package com.ryantenney.zookeeper.spring;

import static org.springframework.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class ZooKeeperAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

  private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperAnnotationBeanPostProcessor.class);

  private static final FieldFilter FIELD_FILTER = new FieldFilter() {
    @Override
    public boolean matches(Field field) {
      return COPYABLE_FIELDS.matches(field) && field.isAnnotationPresent(ZooKeeper.class);
    }
  };

  private static final MethodFilter METHOD_FILTER = new MethodFilter() {
    @Override
    public boolean matches(Method method) {
      return USER_DECLARED_METHODS.matches(method) && method.isAnnotationPresent(ZooKeeper.class);
    }
  };

  private int order = Ordered.LOWEST_PRECEDENCE - 2;

  private CuratorFramework curatorFramework;

  private TypeConverter typeConverter;

  public void setCuratorFramework(CuratorFramework curator) {
    this.curatorFramework = curator;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ConfigurableBeanFactory) {
      typeConverter = ((ConfigurableBeanFactory) beanFactory).getTypeConverter();
    }

    if (typeConverter == null) {
      LOG.info("Unable to obtain a TypeConverter, will attempt to make do without one");
    }
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
	Class<?> targetClass = AopUtils.getTargetClass(bean);

    doWithFields(targetClass, new FieldCallback() {
      @Override
      public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
        makeAccessible(field);
        String path = field.getAnnotation(ZooKeeper.class).value();
        final Class<?> requiredType = field.getType();
        lookup(path, new Setter() {
          public void setValue(Object value) {
            setField(field, bean, convert(value, requiredType, null));
          }
        });
      }
    }, FIELD_FILTER);

    doWithMethods(targetClass, new MethodCallback() {
      @Override
      public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
        makeAccessible(method);
        String path = method.getAnnotation(ZooKeeper.class).value();
        final MethodParameter param = MethodParameter.forMethodOrConstructor(method, 0);
        final Class<?> requiredType = param.getParameterType();
        lookup(path, new Setter() {
          public void setValue(Object value) {
            invokeMethod(method, bean, convert(value, requiredType, param));
          }
        });
      }
    }, METHOD_FILTER);
    
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  protected void lookup(final String path, final Setter setter) {
    lookup(path, setter, new Watcher() {
      @Override
      public void process(WatchedEvent event) {
        // check that path and event.getPath match?
        LOG.info("Watcher for '{}' received watched event: {}", path, event);
        if (event.getType() == EventType.NodeDataChanged) {
          lookup(path, setter, this);
        }
      }
    });
  }

  protected void lookup(String path, Setter setter, Watcher watcher) {
    try {
      byte[] data = curatorFramework.getData().usingWatcher(watcher).forPath(path);
      setter.setValue(new String(data, "UTF-8"));
    }
    catch (Exception ex) {
      rethrowRuntimeException(ex);
    }
  }

  protected Object convert(Object value, Class<?> requiredType, MethodParameter param) {
    if (typeConverter != null) {
      return typeConverter.convertIfNecessary(value, requiredType, param);
    } else {
      if (requiredType.isInstance(value)) {
        return requiredType.cast(value);
      } else {
        throw new TypeMismatchException(value, requiredType);
      }
    }
  }

  @Override
  public int getOrder() {
    return order;
  }

  public void setOrder(final int order) {
    this.order = order;
  }

  private interface Setter {
    void setValue(Object value);
  }

}

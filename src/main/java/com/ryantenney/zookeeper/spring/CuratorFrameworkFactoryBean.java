package com.ryantenney.zookeeper.spring;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class CuratorFrameworkFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

  private CuratorFramework curator;
  private String connectString;
  private RetryPolicy retryPolicy;
  private Integer sessionTimeout;

  public void afterPropertiesSet() throws Exception {
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

    builder.connectString(connectString);

    if (retryPolicy == null) {
      retryPolicy = new ExponentialBackoffRetry(1000, 3);
    }
    builder.retryPolicy(retryPolicy);

    if (sessionTimeout != null) {
      builder.sessionTimeoutMs(sessionTimeout);
    }

    curator = builder.build();
    curator.start();
  }

  public void destroy() throws Exception {
    curator.close();
  }

  public CuratorFramework getObject() throws Exception {
    return curator;
  }

  public Class<?> getObjectType() {
    return CuratorFramework.class;
  }

  public boolean isSingleton() {
    return true;
  }

  public String getConnectString() {
    return connectString;
  }

  public void setConnectString(String connectString) {
    this.connectString = connectString;
  }

  public Integer getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(Integer sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }
  
  public void setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

}
package com.ryantenney.zookeeper.spring.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.ryantenney.zookeeper.spring.ZooKeeperAnnotationBeanPostProcessor;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

public class AnnotationDrivenBeanDefinitionParser extends AbstractBeanDefinitionParser {

  @Override
  protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
    final BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(ZooKeeperAnnotationBeanPostProcessor.class);
    beanDefBuilder.setRole(ROLE_INFRASTRUCTURE);
    beanDefBuilder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
    beanDefBuilder.addPropertyReference("curatorFramework", element.getAttribute("curator-framework"));
    return beanDefBuilder.getBeanDefinition();
  }

  @Override
  protected boolean shouldGenerateId() {
    return true;
  }

}

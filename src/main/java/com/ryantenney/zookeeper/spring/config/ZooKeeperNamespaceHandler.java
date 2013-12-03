package com.ryantenney.zookeeper.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ZooKeeperNamespaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
    registerBeanDefinitionParser("curator-framework", new CuratorFrameworkBeanDefinitionParser());
  }

}

package com.ryantenney.zookeeper.spring.config;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ryantenney.zookeeper.spring.CuratorFrameworkFactoryBean;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_APPLICATION;

public class CuratorFrameworkBeanDefinitionParser extends AbstractBeanDefinitionParser {

  @Override
  protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
    final BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.rootBeanDefinition(CuratorFrameworkFactoryBean.class);
    beanDefBuilder.setRole(ROLE_APPLICATION);
    beanDefBuilder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
    
    beanDefBuilder.addPropertyValue("connectString", element.getAttribute("connect-string"));
    
    Element retryPolicyElement = DomUtils.getChildElementByTagName(element, "retry-policy");
    if (retryPolicyElement != null) {
      Element retryPolicyBeanElement = DomUtils.getChildElements(retryPolicyElement).get(0);
      BeanDefinitionHolder retryPolicy = parserContext.getDelegate().parseBeanDefinitionElement(retryPolicyBeanElement, beanDefBuilder.getBeanDefinition());
      beanDefBuilder.addPropertyValue("retryPolicy", retryPolicy);
    }

    Node namespace = element.getAttributeNode("namespace");
    if (namespace != null) {
      beanDefBuilder.addPropertyValue("namespace", namespace.getNodeValue());
    }

    return beanDefBuilder.getBeanDefinition();
  }

}

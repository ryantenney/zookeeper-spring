package com.ryantenney.zookeeper.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ryantenney.zookeeper.spring.ZooKeeper;

public class ZooKeeperAnnotationTarget implements InitializingBean, DisposableBean {

  @ZooKeeper("/field")
  private String field = "initial";

  private String method = "initial";

  @ZooKeeper("/method")
  public void setMethod(final String method) {
    this.method = method;
    print("setter");
  }
  
  public ZooKeeperAnnotationTarget() {
    print("ctor");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    print("init");
  }
  
  @Override
  public void destroy() throws Exception {
    print("destroy");
  }
  
  private void print(String phase) {
    System.out.printf("phase '%s': field=%s, method=%s\n", phase, field, method);
  }

}

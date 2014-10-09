package com.ryantenney.zookeeper.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ryantenney.zookeeper.spring.ZooKeeper;

public class ZooKeeperAnnotationTarget implements InitializingBean, DisposableBean {

	public static final String FIELD_PATH = "/field";
	public static final String METHOD_PATH = "/method";
	public static final String NONEXISTENT_PATH = "/nonexistent";

  private @ZooKeeper(FIELD_PATH) String field;
  private String method;
  private String nonexistent;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMethod() {
		return method;
	}

  @ZooKeeper(METHOD_PATH)
  public void setMethod(final String method) {
    this.method = method;
    print("setter");
  }

	public String getNonexistent() {
		return nonexistent;
	}

  @ZooKeeper(NONEXISTENT_PATH)
  public void setNonexistent(final String nonexistent) {
    this.nonexistent = nonexistent;
    print("nonexistent");
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

  public void print(String phase) {
    System.out.printf("phase '%s': field=%s, method=%s, nonexistent=%s\n", phase, field, method, nonexistent);
  }

}

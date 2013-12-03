package com.ryantenney.zookeeper.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ZooKeeperAnnotationTest {

  public static void main(String[] args) {
    ClassPathXmlApplicationContext ctx = null;
    try {
      ctx = new ClassPathXmlApplicationContext("test-context.xml");
      ctx.start();
      
      System.out.println("Press any key to end...");
      System.in.read();
    }
    catch (Throwable t) {
      t.printStackTrace();
      if (ctx != null) {
        ctx.stop();
        ctx.close();
      }
    }
  }

}

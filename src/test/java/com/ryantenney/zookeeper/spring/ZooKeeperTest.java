package com.ryantenney.zookeeper.spring;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static com.ryantenney.zookeeper.spring.ZooKeeperAnnotationTarget.*;

public class ZooKeeperTest {

  private static final Logger LOG = LoggerFactory.getLogger(ZooKeeperTest.class);

  private static ClassPathXmlApplicationContext ctx;
  private static TestingServer zkTestServer;
  private static CuratorFramework cli;

  private @Autowired ZooKeeperAnnotationTarget annotationTarget;

  @Test
  public void test() throws Exception {
  	cli.setData().forPath(FIELD_PATH, "field 2".getBytes());
  	annotationTarget.print("test 1");

  	cli.setData().forPath(METHOD_PATH, "method 2".getBytes());
  	annotationTarget.print("test 2");

  	cli.create().forPath(NONEXISTENT_PATH, "now existent".getBytes());
  	annotationTarget.print("test 3");
  }

  @Before
  public void start() throws Exception {
    zkTestServer = new TestingServer();
    cli = CuratorFrameworkFactory.newClient(zkTestServer.getConnectString(), new RetryOneTime(2000));
    cli.start();

    cli = cli.usingNamespace("test");

  	cli.create().forPath(FIELD_PATH, "field initial".getBytes());
  	cli.create().forPath(METHOD_PATH, "method initial".getBytes());

    System.setProperty("zk.connectString", zkTestServer.getConnectString());

    ctx = new ClassPathXmlApplicationContext("test-context.xml");
    ctx.start();

    annotationTarget = ctx.getBean(ZooKeeperAnnotationTarget.class);
  }

  @After
  public void stop() throws IOException {
    //cli.close();
    zkTestServer.stop();

    if (ctx != null) {
      ctx.stop();
      ctx.close();
    }
  }

}

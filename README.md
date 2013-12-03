#ZooKeeper Spring [![Build Status](https://secure.travis-ci.org/ryantenney/zookeeper-spring.png)](http://travis-ci.org/ryantenney/zookeeper-spring)

Annotation-driven injection of values from ZooKeeper into Spring Beans

###Maven

```xml
<dependency>
    <groupId>com.ryantenney.zookeeper</groupId>
    <artifactId>zookeeper-spring</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

###Basic Usage

Include in your application context:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:c="http://www.springframework.org/schema/c"
  xmlns:p="http://www.springframework.org/schema/p"
  xmlns:zk="http://www.ryantenney.com/schema/zookeeper"
  xsi:schemaLocation="http://www.ryantenney.com/schema/zookeeper http://www.ryantenney.com/schema/zookeeper/zookeeper-1.0.xsd
                      http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.2.xsd">

  <zk:annotation-driven curator-framework="curatorFramework" />

  <zk:curator-framework id="curatorFramework" connect-string="127.0.0.1:2181">
    <zk:retry-policy>
      <bean class="org.apache.curator.retry.ExponentialBackoffRetry">
        <constructor-arg index="0" type="int" value="1000" />
        <constructor-arg index="1" type="int" value="3" />
      </bean>
    </zk:retry-policy>
  </zk:curator-framework>

</beans>
```

And annotate away:

```java
@Component
public class SpringBean {

    @ZooKeeper("foo")
    private String bar;

}
```

---

### License

Copyright (c) 2013 Ryan Tenney

Published under Apache Software License 2.0, see LICENSE

[![Rochester Made](http://rochestermade.com/media/images/rochester-made-dark-on-light.png)](http://rochestermade.com)

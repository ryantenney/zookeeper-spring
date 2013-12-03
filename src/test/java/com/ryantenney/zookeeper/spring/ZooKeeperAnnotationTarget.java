package com.ryantenney.zookeeper.spring;

import com.ryantenney.zookeeper.spring.ZooKeeper;

public class ZooKeeperAnnotationTarget {

	@ZooKeeper("/say/my/name")
	public void setName(final String name) {
		System.out.println("My name is " + name);
	}

}

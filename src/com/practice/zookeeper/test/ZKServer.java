package com.practice.zookeeper.test;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

//模拟服务器上线 在zookeeper集群进行注册 即在zookeeper管理服务器的节点生成临时子节点
//服务器与集群失去联系之后 子节点消失
public class ZKServer {

	private ZooKeeper zooKeeper;

	private String connectString = "hadoop101:2181,hadoop102:2181,hadoop103:2181";

	private int sessionTimeout = 5000;

	private String parentPath = "/Servers";

	private void connect() throws Exception {

		zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {

			@Override
			public void process(WatchedEvent event) {

				System.out.println(event);

			}
		});

		System.out.println(zooKeeper.getState());
	}

	private void init() throws KeeperException, InterruptedException {

		if (null == zooKeeper.exists(parentPath, null)) {
			zooKeeper.create(parentPath, " ".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}

	private void bussiness() throws InterruptedException {

		System.out.println("其他的一些工夫！");

		Thread.sleep(Integer.MAX_VALUE);
	}

	public static void main(String[] args) throws Exception {

		ZKServer server = new ZKServer();

		server.connect();

		server.init();

		// 注册
		server.regist(args[0]);

		server.bussiness();
	}

	private void regist(String string) throws KeeperException, InterruptedException {
		
		String create = zooKeeper.create(parentPath+"/"+string,string.getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("已经成功注册到了："+create);
		
	}

}

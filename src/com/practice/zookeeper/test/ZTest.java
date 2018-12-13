package com.practice.zookeeper.test;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

public class ZTest {

	ZooKeeper zooKeeper;
	String connectString = "hadoop101:2181,hadoop102:2181,hadoop103:2181";
	int sessionTimeout = 30000;

	@Before
	public void test() throws IOException {
		zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {

				System.out.println(event);

			}
		});
		System.out.println(zooKeeper.getState());
	}

	// create [-s] [-e] path value
	@Test
	public void create() throws KeeperException, InterruptedException {

		String path = zooKeeper.create("/fruit", "水果".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println(path + " has been created !");
	}

	// delete path [dataversion]
	@Test
	public void delete() throws InterruptedException, KeeperException {

		zooKeeper.delete("/test", -1);
	}

	// 查看当前znode是否存在
	@Test
	public void exsits() throws KeeperException, InterruptedException {

		Stat stat = zooKeeper.exists("/fruit", null);
		System.out.println(stat == null ? "不存在该目录" : "存在该目录");
	}

	// 查看：查看znode的子节点 ls path
	@Test
	public void list() throws Exception, InterruptedException {

		List<String> children = zooKeeper.getChildren("/fruit", null);
		for (String child : children) {
			System.out.println(child);
		}
	}

	// 设置某个节点的值
	@Test
	public void testSet() throws KeeperException, InterruptedException {

		Stat setData = zooKeeper.setData("/fruit/apple", "apple".getBytes(), -1);
		System.out.println(setData);
	}

	// 监听节点的值是否发改变（一次性监听）
	@Test
	public void getData() throws Exception, InterruptedException {

		String path = "/fruit";
		Stat stat = new Stat();
		byte[] data = zooKeeper.getData(path, new Watcher() {
			@Override
			public void process(WatchedEvent event) {

				System.out.println("已经监听到了：" + event.getPath() + "====>发生了：" + event.getType());
				byte[] data2 = null;
				try {
					data2 = zooKeeper.getData(path, false, null);
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("新的结果是：" + new String(data2));
			}
		}, stat);
		System.out.println("节点的数据是：" + new String(data));
		System.out.println("节点的状态信息是：" + stat);

		while (true) {

			Thread.sleep(10000);

		}
	}

	// 循环监听一个节点的值是否发生改变（循环监听）
	@Test
	public void alwaysWatch() throws Exception {

		String data = getData2("/fruit");
		System.out.println("获取的数据是" + data);
		while (true) {
			Thread.sleep(10000);
		}
	}

	public String getData2(String path) throws Exception, Exception {

		byte[] data = zooKeeper.getData(path, new Watcher() {
			@Override
			public void process(WatchedEvent event) {

				System.out.println("已经监听到了：" + event.getPath() + "===>发生了：" + event.getType());
				try {
					String data2 = getData2(path);
					System.out.println("结果是" + data2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, null);
		return new String(data);
	}

}

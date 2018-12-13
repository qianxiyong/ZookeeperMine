package com.practice.zookeeper.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

//模拟 路由对zookeeper管理服务器的节点进行监听 ，一旦此节点
//添加或减少了子节点（服务器上线，下线），及时更新
public class ZKRouter {

	private ZooKeeper zooKeeper;

	private String connectString = "hadoop101:2181,hadoop102:2181,hadoop103:2181";

	private int sessionTimeout = 5000;

	private String parentPath = "/Servers";

	public void connect() throws IOException {

		zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {

			// 回调函数
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event);
			}
		});

		System.out.println(zooKeeper.getState());

	}

	public List<Object> getNewServers() throws KeeperException, InterruptedException {

		List<String> servers = new ArrayList<>();
		List<Object> info = new ArrayList<>();

		servers = zooKeeper.getChildren("/Servers", new Watcher() {
			@Override
			public void process(WatchedEvent event) {

				try {
					List<Object> newServers = getNewServers();
					System.out.println("检测到了节点的变化，最新的可用节点是" + newServers);
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		for (String string : servers) {

			byte[] data = zooKeeper.getData(parentPath + "/" + string, null, null);
			info.add(new String(data));

		}
		return info;
	}

	public void otherBusiness() {

		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		ZKRouter zkRouter = new ZKRouter();
		zkRouter.connect();
		// 获取信息
		List<Object> servers = zkRouter.getNewServers();

		System.out.println("可以访问的节点信息是：" + servers);

		// 其他逻辑
		zkRouter.otherBusiness();
	}

}

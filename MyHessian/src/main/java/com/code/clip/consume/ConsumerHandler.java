package com.code.clip.consume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.Type;
import com.code.clip.command.PubSub;
import com.code.utils.JsonUtil;

public class ConsumerHandler extends PubSub {
	private Log log = LogFactory.getLog(ConsumerHandler.class);

	private static final Map<String,String> nodes = new ConcurrentHashMap<String,String>();
	private static final Map<String, List<Describer>> serviceCache = new ConcurrentHashMap<String, List<Describer>>();

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(String channel, String message) {
		log.debug(String.format("onMessage,channel:%s,message:%s", channel, message));
		Describer desc = JsonUtil.json2java(message, Describer.class);
		// 节点下线通知
		if (desc.getType() == Type.NODE_DOWN) {
			serviceCache.forEach(new BiConsumer<String, List<Describer>>() {

				@Override
				public void accept(String t, List<Describer> u) {
					for (Describer describer : u) {
						if (desc.getNode().getHost().equals(describer.getNode().getHost())) {
							describer.getNode().setStatus(2);// 节点不对外提供服务
						}
					}
				}
			});
		}
		// 节点上线通知
		if (desc.getType() == Type.NODE_UP) {
			serviceCache.forEach(new BiConsumer<String, List<Describer>>() {

				@Override
				public void accept(String t, List<Describer> u) {
					for (Describer describer : u) {
						if (desc.getNode().getHost().equals(describer.getNode().getHost())) {
							describer.getNode().setStatus(1);// 节点下所有服务上线
						}
					}
				}
			});
		}
		// 服务上线通知
		else if (desc.getType() == Type.SERVICE_ACTIVE) {
			String node = desc.getNode().getHost();
			String uri = desc.getStub().getServiceUri();

			if (!nodes.containsKey(node)) {
				nodes.put(node, node);
			}

			Object serviceListObject = serviceCache.get(desc.getStub().getServiceName());
			if (serviceListObject != null) {
				List<Describer> serviceList = (List<Describer>) serviceListObject;
				boolean toAdd = true;
				for (Describer object : serviceList) {
					String node_ = object.getNode().getHost();
					String uri_ = object.getStub().getServiceUri();
					if (node.equals(node_) && uri.equals(uri_)) {
						toAdd = false;
						return;
					}
				}
				if (toAdd)
					serviceList.add(desc);
			} else {
				List<Describer> serviceList = new ArrayList<Describer>();
				serviceList.add(desc);
				serviceCache.put(desc.getStub().getServiceName(), serviceList);
			}
		}
	}

	public static List<Describer> getHessianProxy(String api) {
		List<Describer> proxies = serviceCache.get(api);
		if (proxies == null || proxies.isEmpty()) {
			return null;
		}

		return proxies;
	}

	public static Set<String> getNodes() {
		return nodes.keySet();
	}

	/**
	 * 节点客观下线，因为心跳检测没有成功
	 * 
	 * @param host
	 */
	public void nodeDown(String host) {
		synchronized (nodes) {

			nodes.remove(host);

			serviceCache.forEach(new BiConsumer<String, List<Describer>>() {

				@Override
				public void accept(String t, List<Describer> u) {
					List<Describer> newServices = new ArrayList<Describer>();
					for (Describer describer : u) {
						if (!describer.getNode().getHost().equals(host)) {
							newServices.add(describer);
						}
					}
					serviceCache.put(t, newServices);
				}

			});
		}
	}

}

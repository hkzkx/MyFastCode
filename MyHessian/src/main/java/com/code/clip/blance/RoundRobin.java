package com.code.clip.blance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Coordinator;
import com.code.clip.ServiceStub;

public class RoundRobin implements Blance {
	private static Log	log	= LogFactory.getLog(RoundRobin.class);

	@Override
	public ServiceStub getNextNode(List<ServiceStub> stubs, Map<String, Integer> statistics) {
		log.debug("服务存根数：" + stubs.size());
		for (ServiceStub stub : stubs) {
			if (!stub.isInvoked()) {
				// 整个节点暂停服务
				if (stub.getNode().getStatus() == 2) {
					log.info(stub.getNode().getHost()+" 已经暂停，查找下一节点");
					continue;
				}

				// 服务暂停服务
				if (stub.getStatus() != 1) {
					log.info(stub.getNode().getHost()+stub.getServiceUri()+" 服务已被暂停");
					continue;
				}

				String host = stub.getNode().getHost();
				String uri = stub.getServiceUri();
				String path = host + uri;
				// 某节点上的服务连续出错
				Integer errorCount = statistics.get(path);
				if (!statistics.isEmpty() && ((errorCount != null) && errorCount > Coordinator.retries))
					continue;

				stub.setInvoked(true);
				return stub;
			}
		}

		List<ServiceStub> candidates = new ArrayList<ServiceStub>();
		for (ServiceStub stub : stubs) {
			// 整个节点暂停服务
			if (stub.getNode().getStatus() == 2) {
				log.info(stub.getNode().getHost()+" 已经暂停，查找下一节点");
				continue;
			}

			// 服务暂停服务
			if (stub.getStatus() != 1) {
				log.info(stub.getNode().getHost()+stub.getServiceUri()+" 服务已被暂停");
				continue;
			}

			String host = stub.getNode().getHost();
			String uri = stub.getServiceUri();
			String path = host + uri;
			// 某节点上的服务连续出错
			Integer errorCount = statistics.get(path);
			if (!statistics.isEmpty() && ((errorCount != null) && errorCount > Coordinator.retries))
				continue;

			stub.setInvoked(false);
			candidates.add(stub);
		}

		if (candidates.isEmpty())
			return null;

		log.info("最终可用节点数："+candidates.size());
		ServiceStub stub = candidates.get(0);
		stub.setInvoked(true);
		return stub;

	}
}

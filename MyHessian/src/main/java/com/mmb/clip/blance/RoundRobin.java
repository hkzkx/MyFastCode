package com.mmb.clip.blance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mmb.clip.Coordinator;
import com.mmb.clip.Describer;

public class RoundRobin implements Blance {

	@Override
	public Describer getNextNode(List<Describer> nodes, Map<String, Integer> statistics) {

		for (Describer desc : nodes) {
			if (!desc.isInvoked()) {
				// 整个节点暂停服务
				if (desc.getNode().getStatus() == 2) {
					continue;
				}

				// 服务暂停服务
				if (desc.getStub().getStatus() == 3) {
					continue;
				}

				String host = desc.getNode().getHost();
				String uri = desc.getStub().getServiceUri();
				String path = host + uri;
				// 某节点上的服务连续出错
				Integer errorCount = statistics.get(path);
				if (!statistics.isEmpty() && ((errorCount != null) && errorCount > Coordinator.retries))
					continue;

				desc.setInvoked(true);
				return desc;
			}
		}

		List<Describer> candidates = new ArrayList<Describer>();
		for (Describer desc : nodes) {
			// 整个节点暂停服务
			if (desc.getNode().getStatus() == 2) {
				continue;
			}

			// 服务暂停服务
			if (desc.getStub().getStatus() == 3) {
				continue;
			}
			
			String host = desc.getNode().getHost();
			String uri = desc.getStub().getServiceUri();
			String path = host + uri;
			// 某节点上的服务连续出错
			Integer errorCount = statistics.get(path);
			if (!statistics.isEmpty() && ((errorCount != null) && errorCount > Coordinator.retries))
				continue;
			
			desc.setInvoked(false);
			candidates.add(desc);
		}

		Describer desc = candidates.get(0);
		desc.setInvoked(true);
		return desc;

	}

}

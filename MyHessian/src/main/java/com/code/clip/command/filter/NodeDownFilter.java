package com.code.clip.command.filter;

import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.ServiceStub;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

/**
 * 手动下线服务节点
 * @author Administrator
 *
 */
public class NodeDownFilter implements Filter {
	private static Log	log	= LogFactory.getLog(NodeDownFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		Describer desc = JsonUtil.json2java(message, Describer.class);

		// 节点下线通知,用于通过控制台手工暂停某节点的全部服务
		if (desc.getType() == Type.NODE_DOWN) {

			// 节点下线
			Context.getInstance().getNodes().get(desc.getNode().getHost()).setStatus(2);

			// 节点下服务下线
			Context.getInstance().getServiceCache().forEach(new BiConsumer<String, List<ServiceStub>>() {

				@Override
				public void accept(String t, List<ServiceStub> u) {
					for (ServiceStub stub : u) {
						if (desc.getNode().getHost().equals(stub.getNode().getHost())) {
							stub.setStatus(4);
						}
					}
				}
			});
		}else{
			chain.doFilter(channel, message);
		}
	}

}

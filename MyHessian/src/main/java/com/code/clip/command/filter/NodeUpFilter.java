package com.code.clip.command.filter;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.ServiceStub;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

/**
 * 手动上线服务节点
 * @author Administrator
 *
 */
public class NodeUpFilter implements Filter {
	private static Log	log	= LogFactory.getLog(NodeUpFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		Describer desc = JsonUtil.json2java(message, Describer.class);

		// 节点上线通知
		if (desc.getType() == Type.NODE_UP) {

			// 节点上线
			Context.getInstance().getNodes().get(desc.getNode().getHost()).setStatus(1);

			// 节点下所有服务上线
			Context.getInstance().getServiceCache().forEach(new BiConsumer<String, List<ServiceStub>>() {

				@Override
				public void accept(String t, List<ServiceStub> u) {
					for (ServiceStub stub : u) {
						if (desc.getNode().getHost().equals(stub.getNode().getHost())) {
							stub.setStatus(1);
						}
					}
				}

			});

			Context.getInstance().activateNode();
		}else{
			chain.doFilter(channel, message);
		}
	}

	
}

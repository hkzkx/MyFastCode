package com.code.clip.command.filter;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

/**
 * 服务提供端主动通知特定服务上线，消费端接收到此消息后，应该保存服务信息
 * @author Administrator
 *
 */
public class ServiceActiveFilter implements Filter {
	private static Log	log	= LogFactory.getLog(ServiceActiveFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		log.info("服务上线filter");
		Describer desc = JsonUtil.json2java(message, Describer.class);
		// 服务上线通知
		if (desc.getType() == Type.SERVICE_ACTIVE) {
			log.info("服务上线通知处理");
			// 节点上线
			Node node = desc.getNode();
			String nodeName = node.getHost();
			Map<String, Node> nodes = Context.getInstance().getNodes();
			synchronized (nodes) {
				if (!nodes.containsKey(nodeName)) {
					// 储存节点信息
					nodes.put(nodeName, node);
					Context.getInstance().add(Context.nodes, nodes);
				}
			}

			Context.getInstance().saveHessianProxy(desc);
		}else{
			chain.doFilter(channel, message);
		}
	}

}

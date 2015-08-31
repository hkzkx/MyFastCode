package com.code.clip.command.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.Report;
import com.code.clip.ServiceStub;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

/**
 * clip_admin服务管理界面中，拉取所有服务提供者信息，用于节点管理
 * @author Administrator
 *
 */
public class CollectRegitryFilter implements Filter {
	private static Log	log	= LogFactory.getLog(CollectRegitryFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		Describer desc = JsonUtil.json2java(message, Describer.class);
		if (desc.getType() == Type.COLLECT) {
			/*
			 * 收集注册信息，包括节点信息/服务信息
			 * 
			 * 暂时只用于节点的up and down 操作
			 */
			JedisFace jedis = ((PooledJedis) Context.getInstance().getAttribute(Context.poolJedis)).getJedisProxy();
			Report report = new Report();
			report.setNode((Node) Context.getInstance().getAttribute(Context.node));
			List<ServiceStub> list = new ArrayList<ServiceStub>();
			((List<Object>) Context.getInstance().getAttribute(Context.serviceCache)).forEach(new Consumer<Object>() {

				@Override
				public void accept(Object t) {
					list.add(((Describer) t).getStub());
				}
			});
			report.setStubs(list);
			jedis.publish(Coordinator.channel_collect_push, JsonUtil.java2json(report));
		} else {
			chain.doFilter(channel, message);
		}
	}

}

package com.code.clip.command.filter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

/**
 * 服务消费端拉取服务存根，通常情况只能消费端重动过程还才触发此消息
 * @author Administrator
 *
 */
public class RequestServicesFilter implements Filter {
	private static Log		log			= LogFactory.getLog(RequestServicesFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		Describer desc = JsonUtil.json2java(message, Describer.class);
		// 客户端拉取所有服务存根
		if (desc.getType() == Type.CONSUMER_PULL) {
			JedisFace jedis = ((PooledJedis) Context.getInstance().getAttribute(Context.poolJedis)).getJedisProxy();
			log.debug("客户端拉取服务列表");
			jedis.batchPublish(Coordinator.channel_push, (List<Object>) Context.getInstance().getAttribute(Context.serviceCache));
		}else{
			chain.doFilter(channel, message);
		}
	}

}

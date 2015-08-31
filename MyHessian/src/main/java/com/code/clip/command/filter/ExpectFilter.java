package com.code.clip.command.filter;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

public class ExpectFilter implements Filter {
	private static Log	log	= LogFactory.getLog(ExpectFilter.class);
	
	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		log.info("期盼ExpectFilter");
		Describer desc = JsonUtil.json2java(message, Describer.class);

		// 接收消息时，是否只关心自己期望的服务节点
		Object expect = Context.getInstance().getAttribute(Context.expect);
		@SuppressWarnings("unchecked")
		Set<String> expects = (Set<String>) expect;
		if (expect != null && !expects.isEmpty()) {
			if (expects.contains(desc.getNode().getHost())) {
				chain.doFilter(channel, message);
			}else{
				return;
			}
		}else{
			chain.doFilter(channel, message);
		}
	}

}

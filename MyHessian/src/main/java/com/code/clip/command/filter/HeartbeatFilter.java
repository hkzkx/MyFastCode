package com.code.clip.command.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.Type;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

public class HeartbeatFilter implements Filter {
	private static Log	log	= LogFactory.getLog(HeartbeatFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		Describer desc = JsonUtil.json2java(message, Describer.class);
		if (desc.getType() == Type.NODE_HEARTBEAT) {
			// do nothing
		}else{
			chain.doFilter(channel, message);
		}
	}

}

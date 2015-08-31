package com.code.clip.command.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import com.code.clip.Coordinator;
import com.code.clip.KV;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.utils.JsonUtil;

/**
 * 运行时设置应用日志级别
 * @author Administrator
 *
 */
public class LogSettingsFilter implements Filter {
	private static Log	log	= LogFactory.getLog(LogSettingsFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		// 变更log4j中某个logger的级别
		if (channel.equals(Coordinator.channel_collect_logger_set)) {
			KV kv = JsonUtil.json2java(message, KV.class);
			Object attach = chain.getContext().getAttribute(Context.attach);
			if(kv.getAttach().equals(attach)){
				LogManager.getLogger(kv.getK().toString()).setLevel(Level.toLevel(kv.getV().toString()));
			}
			return;
		}else{
			chain.doFilter(channel, message);
		}
	}

}

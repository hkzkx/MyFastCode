package com.code.clip.command.filter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.code.clip.Coordinator;
import com.code.clip.KV;
import com.code.clip.Node;
import com.code.clip.Report;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

/**
 * 收集log4j设置信息，用于在clip_admin中管理日志级别
 * @author Administrator
 *
 */
public class CollectLogSettingsFilter implements Filter {
	private static Log	log	= LogFactory.getLog(CollectLogSettingsFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		// 收集当前节点中所有的logger和级别，汇报给clip_admin
		if (channel.equals(Coordinator.channel_collect_pull_logger)) {
			/*
			 * 处理log4j设置上报
			 */
			Enumeration<?> loggers = LogManager.getCurrentLoggers();
			if (loggers != null) {
				List<KV> list = new ArrayList<KV>();
				while (loggers.hasMoreElements()) {
					Logger logger = (Logger) loggers.nextElement();
					String name = logger.getName();
					Level level = logger.getLevel();
					if (level == null)
						continue;

					String levelStr = level.toString();
					KV kv = new KV();
					kv.setK(name);
					kv.setV(levelStr);
					kv.setAttach(Context.getInstance().getAttribute(Context.attach));
					list.add(kv);
					
					log.info(name + " --- " + levelStr);
				}
				
				/*
				 * 收集root logger级别
				 */
				Logger rootLogger = LogManager.getRootLogger();
				KV kv = new KV();
				kv.setK(rootLogger.getName());
				kv.setV(rootLogger.getLevel().toString());
				kv.setAttach(Context.getInstance().getAttribute(Context.attach));
				list.add(kv);
				log.info(kv.getK() + " --- " + kv.getV());
				
				Report report = new Report();
				report.setNode((Node) Context.getInstance().getAttribute(Context.node));
				report.setList(list);

				JedisFace jedis = ((PooledJedis)Context.getInstance().getAttribute(Context.poolJedis)).getJedisProxy();
				Long client = jedis.publish(Coordinator.channel_collect_push_logger, JsonUtil.java2json(report));
				log.info(client + " 个客户端接收到日志收集信息");
			}
			return;
		} else {
			chain.doFilter(channel, message);
		}
	}

}

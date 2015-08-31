package com.code.clip.command.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.code.clip.Coordinator;
import com.code.clip.KV;
import com.code.clip.Node;
import com.code.clip.Report;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.spring.context.SpringApplicationContext;
import com.code.utils.JsonUtil;

/**
 * clip_admin中，收集web应用于静态文件域名设置，用于域名切换
 * @author Administrator
 *
 */
public class CollectStaticDomainFilter implements Filter {
	private static Log	log	= LogFactory.getLog(CollectStaticDomainFilter.class);

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		// 收集当前节点中静态文件域
		if (channel.equals(Coordinator.channel_collect_pull_static)) {
			Object vr = SpringApplicationContext.getBean("viewResolver");
			if (vr != null) {
				UrlBasedViewResolver viewResolver = (UrlBasedViewResolver) vr;
				Map<String, Object> map = viewResolver.getAttributesMap();
				List<KV> list = new ArrayList<KV>();
				if (map != null && !map.isEmpty()) {
					Object staticDomain = map.get("staticDomain");
					log.info(staticDomain);
					KV kv = new KV();
					kv.setK("staticDomain");
					kv.setV(staticDomain);
					kv.setAttach(chain.getContext().getAttribute(Context.attach));
					list.add(kv);

					Object libsDomain = map.get("libsDomain");
					log.info(libsDomain);
					kv = new KV();
					kv.setK("libsDomain");
					kv.setV(libsDomain);
					kv.setAttach(chain.getContext().getAttribute(Context.attach));
					list.add(kv);
					
					Object staticVersion = map.get("staticVersion");
					log.info(staticVersion);
					kv = new KV();
					kv.setK("staticVersion");
					kv.setV(staticVersion);
					kv.setAttach(chain.getContext().getAttribute(Context.attach));
					list.add(kv);
				}

				Report report = new Report();
				report.setNode((Node) chain.getContext().getAttribute(Context.node));
				report.setList(list);

				JedisFace jedis = ((PooledJedis)Context.getInstance().getAttribute(Context.poolJedis)).getJedisProxy();
				Long client = jedis.publish(Coordinator.channel_collect_push_static, JsonUtil.java2json(report));
				log.info(client + " 个客户端接收到静态域信息");
			}
		}else{
			chain.doFilter(channel, message);
		}
	}
}

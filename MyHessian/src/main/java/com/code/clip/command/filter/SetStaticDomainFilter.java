package com.code.clip.command.filter;

import java.util.Map;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.code.clip.Coordinator;
import com.code.clip.KV;
import com.code.clip.command.Context;
import com.code.clip.command.Filter;
import com.code.clip.command.FilterChain;
import com.code.spring.context.SpringApplicationContext;
import com.code.utils.JsonUtil;

/**
 * 设置静态文件域名
 * @author Administrator
 *
 */
public class SetStaticDomainFilter implements Filter {

	@Override
	public void doFilter(String channel, String message, FilterChain chain) {
		// 变更静态文件域名
		if (channel.equals(Coordinator.channel_collect_static_set)) {
			KV kv = JsonUtil.json2java(message, KV.class);
			Object vr = SpringApplicationContext.getBean("viewResolver");
			// 有些客户端没有配置UrlBasedViewResolver，所以要判断是否为NULL
			Object attach = chain.getContext().getAttribute(Context.attach);
			if (vr != null && kv.getAttach().equals(attach)) {
				UrlBasedViewResolver viewResolver = (UrlBasedViewResolver) vr;

				Map<String, Object> map = viewResolver.getAttributesMap();
				String key = kv.getK().toString();
				if (map.containsKey(key))
					map.put(key, kv.getV());

				// 清除org.springframework.web.servlet.view.freemarker.FreeMarkerView类缓存
				viewResolver.clearCache();
			}
		}else{
			chain.doFilter(channel, message);
		}
	}

}

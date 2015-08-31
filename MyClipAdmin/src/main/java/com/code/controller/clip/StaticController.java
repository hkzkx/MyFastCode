package com.code.controller.clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.KV;
import com.code.clip.Type;
import com.code.utils.JsonUtil;

@Controller
public class StaticController {

	@Autowired
	private Kernel kernel;

	@RequestMapping(value = "/static/list")
	public ModelAndView loggerList() {
		/*
		 * 管理端索要各节点的log4j信息
		 */
		Describer desc = new Describer();
		desc.setType(Type.COLLECT);
		kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_collect_pull_static, JsonUtil.java2json(desc));

		ModelAndView mv = new ModelAndView("/staticList");
		mv.addObject("statics", kernel.statics);
		return mv;
	}

	@RequestMapping(value = "/static/set")
	@ResponseBody
	public String loggerSet(String key, String domain,String attach) {
		KV kv = new KV();
		kv.setK(key);
		kv.setV(domain);
		kv.setAttach(attach);
		kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_collect_static_set, JsonUtil.java2json(kv));

		return "ok";
	}
}

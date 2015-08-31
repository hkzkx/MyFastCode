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
public class LoggerController {

	@Autowired
	private Kernel kernel;

	@RequestMapping(value = "/logger/list")
	public ModelAndView loggerList() {
		/*
		 * 管理端索要各节点的log4j信息
		 */
		Describer desc = new Describer();
		desc.setType(Type.COLLECT);
		kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_collect_pull_logger, JsonUtil.java2json(desc));

		ModelAndView mv = new ModelAndView("/loggerList");
		mv.addObject("loggers", kernel.loggers);
		return mv;
	}

	@RequestMapping(value = "/logger/set")
	@ResponseBody
	public String loggerSet(String logger, String level) {
		KV kv = new KV();
		kv.setK(logger);
		kv.setV(level);
		kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_collect_logger_set, JsonUtil.java2json(kv));

		return "ok";
	}
}

package com.code.controller.clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.Type;
import com.code.utils.JsonUtil;

@Controller
public class ServiceController {

	@Autowired
	private Kernel	kernel;

	@RequestMapping(value = "/node/list")
	public ModelAndView listNode() {
		ModelAndView mv = new ModelAndView("/nodeList");
		mv.addObject("stubs", kernel.stubs);
		mv.addObject("nodes", kernel.pooledJedis.getJedisProxy().hvals(Coordinator.nodesKey));

		return mv;
	}

	@RequestMapping(value = "/node/start")
	@ResponseBody
	public String start(String host) {
		Describer desc = new Describer();
		desc.setType(Type.NODE_UP);
		Node node = new Node();
		node.setHost(host);
		node.setStatus(1);
		desc.setNode(node);
		boolean success = kernel.pooledJedis.getJedisProxy().hset(Coordinator.nodesKey, node.getHost(), node);
		if (success)
			kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_push, JsonUtil.java2json(desc));

		return "ok";
	}

	@RequestMapping(value = "/services/refresh")
	@ResponseBody
	public String refresh() {
		pull();

		return "ok";
	}

	@RequestMapping(value = "/node/pause")
	@ResponseBody
	public String pause(String host) {
		Describer desc = new Describer();
		desc.setType(Type.NODE_DOWN);
		Node node = new Node();
		node.setHost(host);
		node.setStatus(2);
		desc.setNode(node);

		boolean success = kernel.pooledJedis.getJedisProxy().hset(Coordinator.nodesKey, node.getHost(), node);
		if (success)
			kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_push, JsonUtil.java2json(desc));

		return "ok";
	}

	private void pull() {

		/*
		 * 管理端索要各节点信息
		 */
		Describer desc = new Describer();
		desc.setType(Type.COLLECT);
		kernel.pooledJedis.getJedisProxy().publish(Coordinator.channel_collect_pull, JsonUtil.java2json(desc));

	}
}

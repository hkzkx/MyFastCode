package com.code.controller.clip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@Autowired
	private Kernel	kernel;

	@RequestMapping(value = "/index")
	public ModelAndView listNode() {
		ModelAndView mv = new ModelAndView("/index");
		return mv;
	}
}

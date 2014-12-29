package com.code.controller.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.code.controller.base.BaseController;
import com.code.common.MessageBean;

import com.code.model.user.SitUser;
import com.code.service.user.ISitUserService;

@Controller
public class SitUserController extends BaseController {
	
	@Autowired
	private ISitUserService sitUserService;
	
	
	@RequestMapping(value="/user/situser/form")
	public void form() {}
	
	@RequestMapping(value="/user/situser/save")
	@ResponseBody
	public MessageBean save(@Valid SitUser userModel, BindingResult result) {
		if (result.hasErrors()) {
			return buildValidataMsg(result);
		}
		return null;
	}
}

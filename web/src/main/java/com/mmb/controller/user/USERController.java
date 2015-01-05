package com.mmb.controller.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmb.controller.base.BaseController;
import com.mmb.common.MessageBean;

import com.mmb.model.user.USER;
import com.mmb.service.user.IUSERService;

@Controller
public class USERController extends BaseController {
	
	@Autowired
	private IUSERService uSERService;
	
	
	@RequestMapping(value="/user/user/form")
	public void form() {}
	
	@RequestMapping(value="/user/user/save")
	@ResponseBody
	public MessageBean save(@Valid USER userModel, BindingResult result) {
		if (result.hasErrors()) {
			return buildValidataMsg(result);
		}
		userModel = uSERService.insert(userModel);
		return null;
	}
}

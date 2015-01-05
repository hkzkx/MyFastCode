package com.mmb.controller.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.mmb.common.MessageBean;
import com.mmb.common.ValidateMessage;

@Controller
public class BaseController {
	private Log log = LogFactory.getLog(getClass());

	@Autowired
	private Validator validator;

	@Autowired
	private MessageSource messageSource;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	public MessageBean buildValidataMsg(BindingResult result) {
		List<ObjectError> list = result.getAllErrors();
		List<ValidateMessage> message = new ArrayList<ValidateMessage>();
		for (ObjectError objectError : list) {
			String hint = messageSource.getMessage(objectError.getDefaultMessage(), null, null);
			String field = objectError.getDefaultMessage().split("\\.")[1];
			ValidateMessage msg = new ValidateMessage(field, hint);
			
			log.debug(msg);
			
			message.add(msg);
		}

		return new MessageBean(Boolean.FALSE, message);
	}
}

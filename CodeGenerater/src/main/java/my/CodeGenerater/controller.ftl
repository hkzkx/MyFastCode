package ${controller.package_};

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.code.controller.base.BaseController;
import com.code.common.MessageBean;

import ${controller.service.dao.dto.package_}.${controller.service.dao.dto.className};
import ${controller.service.package_}.${controller.service.iClassName};

@Controller
public class ${controller.service.dao.dto.className}Controller extends BaseController {
	
	@Autowired
	private ${controller.service.iClassName} ${controller.service.instanceName};
	
	
	@RequestMapping(value="/${controller.module?lower_case}/${controller.service.dao.dto.className?lower_case}/form")
	public void form() {}
	
	@RequestMapping(value="/${controller.module?lower_case}/${controller.service.dao.dto.className?lower_case}/save")
	@ResponseBody
	public MessageBean save(@Valid ${controller.service.dao.dto.className} userModel, BindingResult result) {
		if (result.hasErrors()) {
			return buildValidataMsg(result);
		}
		userModel = ${controller.service.instanceName}.insert(userModel);
		return null;
	}
}

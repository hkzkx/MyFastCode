package com.code.test;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.code.common.Constant;
import com.code.common.RemotePage;
import com.code.controller.user.SitUserController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-beans.xml")
public class ServiceProductInfoControllerTest {
	private MockHttpServletRequest request = new MockHttpServletRequest();
	private MockHttpServletResponse response = new MockHttpServletResponse();
	private XmlWebApplicationContext context;
	private MockServletContext msc;
	private SitUserController sitUserController;
	private HandlerAdapter handlerAdapter;

	@Before
	public void setUp() throws Exception {
		String[] contexts = new String[] { "classpath:spring/applicationContext-beans.xml",
				"classpath:mvc/mySpringMVC-servlet.xml" };
		context = new XmlWebApplicationContext();
		context.setConfigLocations(contexts);
		msc = new MockServletContext("file://D:/java/workspace/luna/MyWeb/src/main/webapp");
		context.setServletContext(msc);
		context.refresh();
		msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		sitUserController = (SitUserController) context.getBean("sitUserController");


	}

	@Test
	public void testInsertServiceProductInfo() {
		request.setRequestURI("/user/situser/save");

//		// 主类信息
//		request.addParameter("title", "测试增加商品/信息");
//		// 商品详细类初始化
//		request.addParameter("serviceProductDetail.introduction", "这是商品详细");
//		// 主类关联的列表值初始化
//		request.addParameter("serviceProductSkuCatList[0].productId", "1");
//		request.addParameter("serviceProductSkuCatList[0].skuCatId", "1");
//
//		request.addParameter("deliveryType", Constant.PRODUCT_DELIVERY_TYPE.DOOR2DOOR.code().toString());
//		request.addParameter("featureInfo", "k:v&k:v");
//		request.addParameter("isAutomaticShelves", Constant.PRODUCT_AUTOMATIC_SHELVES.NO.code().toString());
//		request.addParameter("isProduct", Constant.SERVICE_TYPE.PRODUCT.code().toString());
//		request.addParameter("isStoreHome", Constant.COMMON_TYPE.NO.code().toString());
//		request.addParameter("productImgs", "http://news.baidu.com/z/resource/r/image/2014-02-27/c35e6b70385a63bdd578dc30dd77876b.jpg");
//		request.addParameter("servicerCatId", "2");
//		request.addParameter("servicerId", "1");
//
//		request.addParameter("serviceProductBasePrice.lease", "3");
//		request.addParameter("serviceProductBasePrice.price", "33.33");
//		request.addParameter("serviceProductBasePrice.rentBeginTime", "2014/11/11");

		HttpSession session = request.getSession();
		try {
			ModelAndView mv = new AnnotationMethodHandlerAdapter().handle(request, response, sitUserController);
			System.out.println(mv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

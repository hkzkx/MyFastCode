package com.mmb.interceptor;

import java.lang.reflect.Method;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mmb.annotation.Token;

/**
 * @ClassName: TokenInterceptor
 * @Description: 重复提交拦截，主要依赖Token,<br/>
 * 
 *               1：请求某表单准备增加数据时，服务端拦截请求，生成token(UUID)存储到session中<br/>
 *               2：客户端收到Token字符串，保存到表单 hidden 区域<br>
 *               3：客户端提交单，连同 hidden token 一同提交<br>
 *               4：服务端收到post数据，根据 注解Token是否绑定到当前方法判断，是否需要进行Token拦截<br>
 *               5：如果客户端提交数据没有token hidden参数或提交的token与服务端session中的不一致，视为无效提交<br>
 *
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

	private static final String token_flag = "token";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			Token annotation = method.getAnnotation(Token.class);
			if (annotation != null) {
				if (isRepeatSubmit(request)) {
					return false;
				}

				request.getSession(false).removeAttribute(token_flag);
			}
			return true;
		} else {
			return super.preHandle(request, response, handler);
		}
	}

	private boolean isRepeatSubmit(HttpServletRequest request) {
		String serverToken = (String) request.getSession(false).getAttribute(token_flag);
		if (serverToken == null) {
			return true;
		}
		String clientToken = request.getParameter(token_flag);
		if (clientToken == null) {
			return true;
		}
		if (!serverToken.equals(clientToken)) {
			return true;
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
		request.getSession().setAttribute(token_flag, UUID.randomUUID().toString());
	}
}

package com.remote.clip;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.caucho.hessian.io.HessianRemoteObject;
import com.code.common.IBaseService;
import com.code.hessian.MyHessianProxy;


public class MMBHessianProxyFactoryBean implements FactoryBean<Object>, InitializingBean{
	private Class<?> serviceInterface;
	private Object serviceProxy;
	
	private String serviceUrl;

	public Class<?> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	@Override
	public Object getObject() throws Exception {
		return serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		InvocationHandler handler = new MyHessianProxy(serviceInterface,serviceUrl);

		this.serviceProxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
	                                  new Class[] { serviceInterface,
	                                                HessianRemoteObject.class,
	                                                IBaseService.class},
	                                  handler);
	    
		
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}


	
}

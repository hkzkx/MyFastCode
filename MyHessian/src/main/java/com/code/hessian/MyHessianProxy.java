package com.code.hessian;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.code.clip.Coordinator;
import com.code.clip.ServiceStub;
import com.code.clip.blance.RoundRobin;
import com.code.clip.consume.ConsumerHandler;

public class MyHessianProxy implements InvocationHandler {
	private static Log							log				= LogFactory.getLog(MyHessianProxy.class);

	public static final Map<String, Integer>	statistics		= new ConcurrentHashMap<String, Integer>();

	private static final Map<String, Object>	hessianProxies	= new ConcurrentHashMap<String, Object>();

	private String								serviceUrl;

	private AtomicInteger						counter			= new AtomicInteger();

	private Class<?>							api;

	private RoundRobin							rr				= new RoundRobin();

	private HessianProxyFactory					factory			= new HessianProxyFactory();

	public MyHessianProxy(Class<?> api) {
		this.api = api;
		factory.setOverloadEnabled(true);// 允许hessian重载方法
	}

	public MyHessianProxy(Class<?> api, String serviceUrl) {
		this(api);
		this.serviceUrl = serviceUrl;

		if (StringUtils.isNotBlank(serviceUrl)) {
			ServiceStub stub = new ServiceStub();
			stub.setServiceName(api.getName());
			stub.setServiceUri(this.serviceUrl);
			stub.setInvoked(false);
			stub.setStatus(1);
			// stub.setNode(node);
			ConsumerHandler.addHessianProxy(stub);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("equals")) {
			return (proxy == args[0]);
		} else if (method.getName().equals("hashCode")) {
			return System.identityHashCode(proxy);
		}
		List<ServiceStub> stubs = ConsumerHandler.getHessianProxy(api.getName());
		if (stubs != null) {
			ServiceStub stub = rr.getNextNode(stubs, statistics);
			String host = stub.getNode().getHost();
			String uri = stub.getServiceUri();
			String path = host + uri;

			/*
			 * 同名服务，代理类可能不同，如： com.code.service.siteuser.ISiteUserService 和
			 * com.code.security.UserService
			 * 远程uri相同（/service/siteuser/SiteUserService），但是服务接口不同,所以在此拼接服务关键key
			 */
			String key = api.getName() + ":" + path;

			Object hessianProxy = hessianProxies.get(key);
			if (hessianProxy == null) {
				synchronized (hessianProxies) {
					hessianProxy = hessianProxies.get(key);
					if (hessianProxy == null) {
						hessianProxy = factory.create(api, path);
						hessianProxies.put(key, hessianProxy);
					}
				}

			}

			try {
				Object val = method.invoke(hessianProxy, args);
				counter.set(0);
				statistics.put(path, 0);// 统计接口调用失败次数
				return val;
			} catch (Exception ex) {
				log.warn(String.format("调用服务失败[%s][%s]。", method, path));
				ex.printStackTrace();
				Throwable cause = ex.getCause();
				log.error(cause.getClass());

				if (cause != null) {
					Throwable cause2 = cause.getCause();
					if (cause instanceof HessianConnectionException || (cause2 != null && cause2 instanceof ConnectException)) {

						// 本地调用不记录失败次数，并且不重试远程调用
						// if (!isLoopback(stub.getNode().getHost())) {

						int count = counter.incrementAndGet(); // 远程服务调用允许重试的最大次数
						Integer errorCount = statistics.get(path);
						statistics.put(path, errorCount == null ? 1 : errorCount + 1);// 统计接口调用失败次数
						if (count <= Coordinator.retries) {
							log.warn(String.format("重试服务%s,第%s次", uri, count));
							return invoke(hessianProxy, method, args);
						} else {
							stub.setStatus(3);// 重试后仍不成功，标识服务已经下线，下次不再使用此服务
							log.error(String.format("调用服务失败[%s][%s]。", method, path));
						}
						// }
					}
				}

				/*
				 * 把异常信息抛到业务层面处理
				 */
				throw ex;
			}
		}

		return null;
	}

	static {
		new Timer("error nodes clean", true).schedule(new TimerTask() {

			@Override
			public void run() {
				log.debug(Thread.currentThread().getName() + " starting ...");
				try {
					statistics.forEach(new BiConsumer<String, Integer>() {

						@Override
						public void accept(String t, Integer u) {
							if (u == 2) {
								log.debug("复原服务：" + t);
								statistics.put(t, 0);
							}
						}

					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000 * 60 * 1, 1000 * 60 * 1);
	}

//	private boolean isLoopback(String host) {
//		return host.startsWith("http://localhost") || host.startsWith("http://127.0.0.1");
//	}
}

package com.mmb.hessian;

import java.io.FileNotFoundException;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.mmb.clip.Coordinator;
import com.mmb.clip.Describer;
import com.mmb.clip.blance.RoundRobin;
import com.mmb.clip.consume.ConsumerHandler;

public class MyHessianProxy implements InvocationHandler {
	private Log log = LogFactory.getLog(MyHessianProxy.class);

	private static final Map<String, Integer> statistics = new ConcurrentHashMap<String, Integer>();
	private static final Map<String, Object> hessianProxies = new ConcurrentHashMap<String, Object>();

	private AtomicInteger counter = new AtomicInteger();

	private Class<?> api;
	private RoundRobin rr = new RoundRobin();

	private HessianProxyFactory factory = new HessianProxyFactory();

	public MyHessianProxy(Class<?> api) {
		this.api = api;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("equals")) {
			return (proxy == args[0]);
		} else if (method.getName().equals("hashCode")) {
			return System.identityHashCode(proxy);
		}
		List<Describer> stubs = ConsumerHandler.getHessianProxy(api.getName());
		if (stubs != null) {
			Describer desc = rr.getNextNode(stubs, statistics);
			String host = desc.getNode().getHost();
			String uri = desc.getStub().getServiceUri();
			String path = host + uri;

			Object hessianProxy = hessianProxies.get(path);
			if (hessianProxy == null) {
				synchronized (hessianProxies) {
					hessianProxy = hessianProxies.get(path);
					if (hessianProxy == null) {
						hessianProxy = factory.create(api, path);
						hessianProxies.put(path, hessianProxy);
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
				Throwable cause = ex.getCause();
				if (cause != null) {
					Throwable cause2 = cause.getCause();
					if (cause instanceof HessianConnectionException || ( cause2 != null && cause2 instanceof ConnectException)) {
						int count = counter.incrementAndGet(); // 远程服务调用允许重试的最大次数
						Integer errorCount = statistics.get(path);
						statistics.put(path, errorCount == null ? 1 : errorCount + 1);// 统计接口调用失败次数
						if (count <= Coordinator.retries)
							return invoke(hessianProxy, method, args);
						else {
							desc.getStub().setStatus(3);// 重试后仍不成功，标识服务已经下线，下次不再使用此服务
							log.error(String.format("调用服务失败[%s][%s]。", method, path));
						}
					}
				}
				log.warn(cause.getClass());
				cause.printStackTrace();

			}
		}

		return null;
	}

	static {
		new Timer("error nodes clean", true).schedule(new TimerTask() {

			@Override
			public void run() {

				statistics.forEach(new BiConsumer<String, Integer>() {

					@Override
					public void accept(String t, Integer u) {
						if (u == 2) {
							statistics.put(t, 0);
						}
					}

				});

			}
		}, 1000 * 60 * 5, 1000 * 60 * 5);
	}
}

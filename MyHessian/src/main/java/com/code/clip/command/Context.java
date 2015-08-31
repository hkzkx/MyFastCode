package com.code.clip.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.ServiceStub;

public class Context {
	private static Log							log					= LogFactory.getLog(Context.class);

	public static final String					attach				= "host.attach";

	public static final String					node				= "host.node";

	public static final String					expect				= "host.expect";

	public static final String					nodes				= "host.nodes";

	public static final String					serviceCache		= "host.serviceCache";

	public static final String					serviceCacheTemp	= "host.serviceCacheTemp";

	public static final String					poolJedis			= "jedis.pool";

	private volatile static Map<Object, Object>	attributes			= new HashMap<Object, Object>();

	private static Context						context				= new Context();

	public static Context getInstance() {
		return context;
	}

	public Object getAttribute(Object k) {
		return attributes.get(k);
	}

	public void add(Object key, Object value, boolean override) {
		if (override)
			attributes.put(key, value);
		else {
			if (!attributes.containsKey(key)) {
				attributes.put(key, value);
			}
		}
	}

	public void activateNode() {
		Context.getInstance().getServiceCacheTemp().forEach(new Consumer<ServiceStub>() {

			@Override
			public void accept(ServiceStub stub) {
				Context.getInstance().getNodes().forEach(new BiConsumer<String, Node>() {

					@Override
					public void accept(String t, Node u) {
						Describer desc = new Describer();
						ServiceStub stubnew = new ServiceStub();
						stubnew.setNode(u);
						stubnew.setServiceName(stub.getServiceName());
						stubnew.setServiceUri(stub.getServiceUri());
						stubnew.setStatus(stub.getStatus());
						desc.setNode(u);
						desc.setStub(stubnew);

						saveHessianProxy(desc);
					}

				});
			}

		});
	}

	public void saveHessianProxy(Describer desc) {
		Node node = desc.getNode();
		String nodeName = node.getHost();
		String uri = desc.getStub().getServiceUri();
		String api = desc.getStub().getServiceName();

		Object serviceListObject = Context.getInstance().getServiceCache().get(desc.getStub().getServiceName());
		if (serviceListObject != null) {

			@SuppressWarnings("unchecked")
			List<ServiceStub> serviceList = (List<ServiceStub>) serviceListObject;
			boolean toAdd = true;
			synchronized (serviceList) {
				for (ServiceStub stub : serviceList) {
					String node_ = stub.getNode().getHost();
					String uri_ = stub.getServiceUri();
					String api_ = stub.getServiceName();
					if (nodeName.equals(node_) && uri.equals(uri_) && api.equals(api_)) {
						toAdd = false;
						break;
					}
				}
			}
			if (toAdd) {
				ServiceStub stub = desc.getStub();
				stub.setNode(Context.getInstance().getNodes().get(nodeName));
				serviceList.add(stub);

				Map<String, List<ServiceStub>> services = Context.getInstance().getServiceCache();
				services.put(stub.getServiceName(), serviceList);
				Context.getInstance().add(Context.serviceCache, services, true);
			} else {
				log.info(String.format("无意向的服务消息:%s%s", nodeName, uri));
			}
		} else {
			List<ServiceStub> serviceList = new ArrayList<ServiceStub>();
			ServiceStub stub = desc.getStub();
			stub.setNode(Context.getInstance().getNodes().get(nodeName));
			serviceList.add(stub);

			Map<String, List<ServiceStub>> services = Context.getInstance().getServiceCache();
			services.put(stub.getServiceName(), serviceList);
			Context.getInstance().add(Context.serviceCache, services, true);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Node> getNodes() {
		Object o = getAttribute(Context.nodes);
		if (o == null) {
			return new HashMap<String, Node>();
		} else {
			return (Map<String, Node>) o;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<ServiceStub>> getServiceCache() {
		Object o = getAttribute(Context.serviceCache);
		if (o == null) {
			return new HashMap<String, List<ServiceStub>>();
		} else {
			return (Map<String, List<ServiceStub>>) o;
		}
	}

	@SuppressWarnings("unchecked")
	public Set<ServiceStub> getServiceCacheTemp() {
		Object o = getAttribute(Context.serviceCacheTemp);
		if (o == null) {
			return new HashSet<ServiceStub>();
		} else {
			return (Set<ServiceStub>) o;
		}
	}

	public void add(Object key, Object val) {
		this.add(key, val,true);
	}
}

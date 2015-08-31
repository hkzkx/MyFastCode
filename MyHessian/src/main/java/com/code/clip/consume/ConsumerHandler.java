package com.code.clip.consume;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.clip.Node;
import com.code.clip.ServiceStub;
import com.code.clip.command.Context;
import com.code.clip.command.FilterChain;
import com.code.clip.command.PubSub;
import com.code.clip.command.RedisFilterChain;
import com.code.clip.command.filter.CollectLogSettingsFilter;
import com.code.clip.command.filter.CollectRegitryFilter;
import com.code.clip.command.filter.DefaultFilter;
import com.code.clip.command.filter.ExpectFilter;
import com.code.clip.command.filter.LogSettingsFilter;
import com.code.clip.command.filter.NodeDownFilter;
import com.code.clip.command.filter.NodeUpFilter;
import com.code.clip.command.filter.ServiceActiveFilter;
import com.code.clip.command.filter.SetStaticDomainFilter;
import com.code.clip.command.filter.CollectStaticDomainFilter;
import com.code.hessian.MyHessianProxy;
import com.code.redis.PooledJedis;

public class ConsumerHandler extends PubSub {
	private static Log	log		= LogFactory.getLog(ConsumerHandler.class);

	private PooledJedis	pool;

	private FilterChain	chain	= buildFilterChain();

	public RedisFilterChain buildFilterChain() {
		DefaultFilter tailFilter = new DefaultFilter();
		CollectStaticDomainFilter firstFilter = new CollectStaticDomainFilter();
		SetStaticDomainFilter secondaryFilter = new SetStaticDomainFilter();
		ExpectFilter thirdlyFilter = new ExpectFilter();

		ServiceActiveFilter serviceActiveFilter = new ServiceActiveFilter();
		NodeDownFilter nodeDownFilter = new NodeDownFilter();
		NodeUpFilter nodeUpFilter = new NodeUpFilter();
		
		LogSettingsFilter logSettingsFilter = new LogSettingsFilter();
		CollectLogSettingsFilter collectLogSettingsFilter = new CollectLogSettingsFilter();

		RedisFilterChain chain9 = new RedisFilterChain(tailFilter, null);
		RedisFilterChain chain8 = new RedisFilterChain(nodeUpFilter, chain9);
		RedisFilterChain chain7 = new RedisFilterChain(nodeDownFilter, chain8);
		RedisFilterChain chain6 = new RedisFilterChain(serviceActiveFilter, chain7);
		RedisFilterChain chain5 = new RedisFilterChain(thirdlyFilter, chain6);
		
		RedisFilterChain chain4 = new RedisFilterChain(collectLogSettingsFilter, chain5);
		RedisFilterChain chain3 = new RedisFilterChain(logSettingsFilter, chain4);
		RedisFilterChain chain2 = new RedisFilterChain(secondaryFilter, chain3);
		RedisFilterChain chain1 = new RedisFilterChain(firstFilter, chain2);

		log.info("ConsumerHandler 处理链初始完毕");
		return chain1;
	}

	@Override
	public void onMessage(String channel, String message) {
		try {
			log.info(String.format("onMessage,channel:%s,message:%s", channel, message));
			chain.doFilter(channel, message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<ServiceStub> getHessianProxy(String api) {
		List<ServiceStub> proxies = Context.getInstance().getServiceCache().get(api);
		if (proxies == null || proxies.isEmpty()) {
			synchronized (ConsumerHandler.class) {
				Context.getInstance().activateNode();
				proxies = Context.getInstance().getServiceCache().get(api);
				return proxies;
			}

		}

		return proxies;
	}

	public Set<String> getNodes() {
		Set<String> nodeSet = Context.getInstance().getNodes().keySet();
		if (nodeSet != null && !nodeSet.isEmpty())
			return nodeSet;

		return new HashSet<String>();
	}

	/**
	 * 节点客观下线，因为心跳检测没有成功
	 * 
	 * @param host
	 */
	public void nodeDown(String host) {
		Map<String, Node> nodes = Context.getInstance().getNodes();
		synchronized (nodes) {
			log.debug("下线节点：" + host);
			// 节点下线
			nodes.get(host).setStatus(2);

			// serviceCache.put(host, new ArrayList<ServiceStub>());
		}
	}

	public void setExpect(String expect) {
		Set<String> expects = new HashSet<String>();
		if (StringUtils.isNotBlank(expect)) {
			String[] array = expect.split(",");
			for (String string : array) {
				expects.add(string);
			}
		}

		Context.getInstance().add(Context.expect, expects);
	}

	public static void addHessianProxy(ServiceStub stub) {
		Set<ServiceStub> serviceCacheTemp = Context.getInstance().getServiceCacheTemp();
		serviceCacheTemp.add(stub);
		Context.getInstance().add(Context.serviceCacheTemp, serviceCacheTemp);	
	}

	public static String getStatus() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table align='center' width='70%'>");
		buffer.append("<caption><h1>节点信息</h1></caption>");
		buffer.append("<tr><td><b>节点</b></td><td><b>状态</b></td></tr>");
		Context.getInstance().getNodes().forEach(new BiConsumer<String, Node>() {

			@Override
			public void accept(String t, Node u) {
				buffer.append("<tr>");
				buffer.append("<td>");
				buffer.append(u.getHost());
				buffer.append("</td>");
				buffer.append("<td>");
				buffer.append(u.getStatus());
				buffer.append("</td>");
				buffer.append("</tr>");
			}
		});
		buffer.append("</table>");

		buffer.append("<table align='center' width='70%'>");
		buffer.append("<caption><h1>统计信息</h1></caption>");
		buffer.append("<tr><td><b>节点服务</b></td><td><b>错误阀值</b></td></tr>");
		MyHessianProxy.statistics.forEach(new BiConsumer<String, Integer>() {

			@Override
			public void accept(String t, Integer u) {
				buffer.append("<tr>");
				buffer.append("<td>");
				buffer.append(t);
				buffer.append("</td>");
				buffer.append("<td>");
				buffer.append(u);
				buffer.append("</td>");
				buffer.append("</tr>");

			}

		});
		buffer.append("</table>");

		buffer.append("<table align='center' width='90%'>");
		buffer.append("<caption><h1>服务信息</h1></caption>");
		buffer.append("<tr><td><b>服务名称</b></td><td><b>远程地址</b></td><td><b>状态</b></td></tr>");
		Context.getInstance().getServiceCache().forEach(new BiConsumer<String, List<ServiceStub>>() {

			@Override
			public void accept(String t, List<ServiceStub> u) {
				buffer.append("<tr>");
				buffer.append("<td>");
				buffer.append(t);
				buffer.append("</td>");
				buffer.append("<td>");
				for (ServiceStub serviceStub : u) {
					buffer.append(serviceStub.getNode().getHost() + serviceStub.getServiceUri()).append("<br>");
				}
				buffer.append("</td>");
				buffer.append("<td>");
				for (ServiceStub serviceStub : u) {
					buffer.append(serviceStub.getStatus()).append("<br>");
				}
				buffer.append("</td>");
				buffer.append("</tr>");
			}
		});
		buffer.append("</table>");
		return buffer.toString();
	}

	public void setPool(PooledJedis pool) {
		this.pool = pool;
		Context.getInstance().add(Context.poolJedis, this.pool);
	}

	public void setNode(Node node) {
		Context.getInstance().add(Context.node, node);
	}

	public void setAttach(Object attach) {
		Context.getInstance().add(Context.attach, attach);
	}

}

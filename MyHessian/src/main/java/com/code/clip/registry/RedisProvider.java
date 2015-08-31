package com.code.clip.registry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.Type;
import com.code.clip.command.Context;
import com.code.clip.command.FilterChain;
import com.code.clip.command.PubSub;
import com.code.clip.command.RedisFilterChain;
import com.code.clip.command.filter.CollectLogSettingsFilter;
import com.code.clip.command.filter.CollectRegitryFilter;
import com.code.clip.command.filter.DefaultFilter;
import com.code.clip.command.filter.HeartbeatFilter;
import com.code.clip.command.filter.LogSettingsFilter;
import com.code.clip.command.filter.RequestServicesFilter;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

public class RedisProvider implements Provider, BeanFactoryPostProcessor {

	private Log				log			= LogFactory.getLog(RedisProvider.class);

	private PooledJedis		pool;

	private Node			node;

	private List<Object>	services	= new ArrayList<Object>();

	public RedisProvider(PooledJedis pool) {
		this.pool = pool;
	}

	public RedisProvider() {
	}

	private FilterChain	chain	= buildFilterChain();

	public RedisFilterChain buildFilterChain() {
		DefaultFilter tailFilter = new DefaultFilter();

		HeartbeatFilter heartbeatFilter = new HeartbeatFilter();
		RequestServicesFilter requestServicesFilter = new RequestServicesFilter();
		LogSettingsFilter logSettingsFilter = new LogSettingsFilter();
		CollectLogSettingsFilter collectLogSettingsFilter = new CollectLogSettingsFilter();
		CollectRegitryFilter collectRegitryFilter = new CollectRegitryFilter();

		RedisFilterChain chain6 = new RedisFilterChain(tailFilter, null);
		RedisFilterChain chain5 = new RedisFilterChain(logSettingsFilter, chain6);
		RedisFilterChain chain4 = new RedisFilterChain(requestServicesFilter, chain5);
		RedisFilterChain chain3 = new RedisFilterChain(heartbeatFilter, chain4);
		RedisFilterChain chain2 = new RedisFilterChain(collectRegitryFilter, chain3);
		RedisFilterChain chain1 = new RedisFilterChain(collectLogSettingsFilter, chain2);

		return chain1;
	}

	public void register(Describer desc) {
		desc.setNode(this.node);
		services.add(desc);
		Context.getInstance().add(Context.serviceCache, this.services, false);

		JedisFace jedis = pool.getJedisProxy();
		Long reply = jedis.publish(Coordinator.channel_push, JsonUtil.java2json(desc));
		log.info(String.format("服务[%s]通知，%d个客户端接收到", desc.getStub().getServiceName(), reply.intValue()));
	}

	@Override
	public void subscribe(String... channel) {
		StringBuffer channelKey = new StringBuffer();
		for (String chnl : channel) {
			channelKey.append(chnl);
			channelKey.append(",");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {

				pool.getJedisProxy().subscribe(new PubSub() {

					public void onMessage(String channel, String message) {
						log.debug(String.format("Provider onMessage,channel:%s,message:%s", channel, message));
						try {
							chain.doFilter(channel, message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}, channel);
			}
		}, "soa subscribe " + channelKey).start();
	}

	@Override
	public void down() {
		Describer downMsg = new Describer();
		downMsg.setType(Type.NODE_DOWN);
		downMsg.setNode(node);
		pool.getJedisProxy().publish(Coordinator.channel_push, JsonUtil.java2json(downMsg));
	}

	@Override
	public void up() {
		Describer downMsg = new Describer();
		downMsg.setType(Type.NODE_UP);
		downMsg.setNode(node);
		pool.getJedisProxy().publish(Coordinator.channel_push, JsonUtil.java2json(downMsg));
	}

	public PooledJedis getPool() {
		return pool;
	}

	public void setPool(PooledJedis pool) {
		this.pool = pool;
		Context.getInstance().add(Context.poolJedis, this.pool, true);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
		this.node.setStatus(2);
		Context.getInstance().add(Context.node, this.node, true);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] channels = { Coordinator.channel_pull, Coordinator.channel_heartbeat + node.getHost(), Coordinator.channel_collect_pull,

		Coordinator.channel_collect_pull_logger, Coordinator.channel_collect_logger_set };
		subscribe(channels);
		// subscribe(Coordinator.channel_pull);
		// subscribe(Coordinator.channel_heartbeat+node.getHost());
		// subscribe(Coordinator.channel_collect_pull);
	}

	@Override
	public List<Object> getServices() {
		return services;
	}
}

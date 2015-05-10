package com.code.clip.registry;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.code.clip.ServiceStub;
import com.code.clip.Type;
import com.code.clip.command.PubSub;
import com.code.redis.JedisFace;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

public class RedisProvider implements Provider, BeanFactoryPostProcessor {

	private Log log = LogFactory.getLog(RedisProvider.class);
	private PooledJedis pool;
	private Node node;
	private static final List<Object> services = new ArrayList<Object>();

	public RedisProvider(PooledJedis pool) {
		this.pool = pool;
	}

	public RedisProvider() {
	}

	public void register(Describer desc) {
		desc.setNode(this.node);
		services.add(desc);

		JedisFace jedis = pool.getJedisProxy();
		Long reply = jedis.publish(Coordinator.channel_push, desc.toString());
		log.info(String.format("服务[%s]通知，%d个客户端接收到", desc.getStub().getServiceName(), reply.intValue()));
	}

	@Override
	public void subscribe(String channel) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				pool.getJedisProxy().subscribe(new PubSub() {

					public void onMessage(String channel, String message) {
						log.debug(String.format("Provider onMessage,channel:%s,message:%s", channel, message));
						Describer desc = JsonUtil.json2java(message, Describer.class);
						if(desc.getType() == Type.CONSUMER_PULL){
							JedisFace jedis = pool.getJedisProxy();
							jedis.batchPublish(Coordinator.channel_push, services);
						}
						if(desc.getType() == Type.NODE_HEARTBEAT){
							// do nothing
						}
					}

				}, channel);
			}
		},"soa subscribe " +channel).start();
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
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		String uri = "/service/siteuser/SiteUserService";
		String name = "com.mmb.service.siteuser.SiteUserService";

		Describer desc = new Describer();
		desc.setType(Type.SERVICE_ACTIVE);

		ServiceStub message = new ServiceStub();
		message.setServiceName(name);
		message.setServiceUri(uri);
		message.setStatus(1);
		message.setWeight(10);
		desc.setStub(message);

		URI confuri = new URI("file:///F:/guangqun/trunk/remote/src/main/resources/env/me/common.properties");
		PooledJedis pool = PooledJedis.getPooledJedis(confuri);
		pool.init();

		Provider provider = new RedisProvider(pool);
		while (true) {
			provider.register(desc);
			Thread.currentThread().sleep(1000l);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		subscribe(Coordinator.channel_pull);
		subscribe(Coordinator.channel_heartbeat+node.getHost());
	}

	

	

}

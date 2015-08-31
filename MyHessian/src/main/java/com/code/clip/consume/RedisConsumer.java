package com.code.clip.consume;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Node;
import com.code.clip.Type;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

public class RedisConsumer implements Consumer, InitializingBean {
	private Log						log		= LogFactory.getLog(RedisConsumer.class);

	private ConsumerHandler			monitor	= new ConsumerHandler();

	private Object					attach;

	private Node					node;

	private PooledJedis				pool;

	private String					expect;

	public void subscribe(String... host) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (host != null) {
					StringBuffer sb = new StringBuffer();
					for (String string : host) {
						sb.append(string);
						sb.append(",");
					}
					log.info("RedisConsumer 订阅：" + sb);
				} else {
					log.error("无订阅通道，请检查代码");
				}
				pool.getJedisProxy().subscribe(monitor, host);
			}
		}).start();
	}

	public RedisConsumer() {
		heartbeat();
	}

	public void heartbeat() {
		new Timer("soa nodes monitor", true).schedule(new TimerTask() {

			@Override
			public void run() {

				for (String host : monitor.getNodes()) {

					Describer desc = new Describer();
					desc.setType(Type.NODE_HEARTBEAT);
					/*
					 * 心跳发启方节点
					 */

					desc.setNode(new Node(Coordinator.getLocalIp()));

					Long replay = pool.getJedisProxy().publish(Coordinator.channel_heartbeat + host, JsonUtil.java2json(desc));
					log.debug(String.format("节点[%s] 服务检测  %s", Coordinator.channel_heartbeat + host, (replay > 0 ? "成功" : "下线")));
					if (replay < 1) {
						monitor.nodeDown(host);
					}
				}

			}
		}, 10000, 1000);
	}

	@Override
	public void publish(Describer desc, String channel) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info(channel);
				log.info(desc);
				pool.getJedisProxy().publish(channel, JsonUtil.java2json(desc));
			}
		}).start();

	}

	public void setPool(PooledJedis pool) {
		this.pool = pool;
	}

	public String getExpect() {
		return expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
		monitor.setExpect(expect);
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setAttach(Object attach) {
		this.attach = attach;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		monitor.setPool(pool);
		monitor.setNode(node);
		monitor.setAttach(attach);
	}
}

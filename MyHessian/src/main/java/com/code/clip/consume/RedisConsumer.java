package com.code.clip.consume;

import java.util.Timer;
import java.util.TimerTask;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.Type;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

public class RedisConsumer implements Consumer {

	private ConsumerHandler monitor = new ConsumerHandler();
	private PooledJedis pool;

	public void subscribe(String... host) {
		new Thread(new Runnable() {
			@Override
			public void run() {
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

				for (String host : ConsumerHandler.getNodes()) {

					Describer desc = new Describer();
					desc.setType(Type.NODE_HEARTBEAT);

					Long replay = pool.getJedisProxy().publish(Coordinator.channel_heartbeat+host, JsonUtil.java2json(desc));
					if (replay < 1) {
						monitor.nodeDown(host);
					}
				}

			}
		}, 1000, 1000);
	}

	@Override
	public void publish(Describer desc, String channel) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pool.getJedisProxy().publish(channel, JsonUtil.java2json(desc));
			}
		}).start();

	}

	// public static void main(String[] args) throws URISyntaxException {
	//
	// URI confuri = new
	// URI("file:///F:/guangqun/trunk/remote/src/main/resources/env/me/common.properties");
	// PooledJedis pool = PooledJedis.getPooledJedis(confuri);
	// pool.init();
	//
	// pool.getJedisProxy().subscribe(monitor, "http://192.168.23.17:9090");
	// }

	public void setPool(PooledJedis pool) {
		this.pool = pool;
	}

}

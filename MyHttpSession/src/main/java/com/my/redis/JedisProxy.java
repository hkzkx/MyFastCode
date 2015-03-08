package com.my.redis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import redis.clients.jedis.Jedis;

public class JedisProxy implements InvocationHandler, JedisFace {
	private static Logger log = Log.getLogger(JedisProxy.class);

	private Jedis jedis;
	private PooledJedis pool;

	public JedisProxy(PooledJedis pool) {
		this.pool = pool;
	}

	public static String calcKey(String system, String app, String module, String key) {
		String str = system + ":" + app + "^" + module + "&" + key;
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#setStr(java.lang.String, java.lang.String,
	 * int, boolean)
	 */
	@Override
	public boolean setStr(String key, String value, int seconds, boolean override) {
		boolean ok = false;
		if (override == false && jedis.exists(key)) {
			ok = false;
		} else {
			if (seconds < 1) {
				jedis.set(key, value);
			} else {
				jedis.setex(key, seconds, value);
			}
			ok = true;
		}
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#getStr(java.lang.String)
	 */
	@Override
	public String getStr(String key) {
		return jedis.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#setObj(java.lang.String, java.lang.Object,
	 * int, boolean)
	 */
	@Override
	public boolean setObj(String key, Object value, int seconds, boolean override) {
		byte[] data = SerialUtil.encode(value);
		if (data == null) {
			log.info("setObj SerialUtil.encode error");
			return false;
		}
		boolean ok = false;
		if (!override && jedis.exists(key)) {
			ok = false;
		} else {
			if (seconds < 1) {
				jedis.set(key.getBytes(), data);
			} else {
				jedis.setex(key.getBytes(), seconds, data);
			}
			ok = true;
		}
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#getObj(java.lang.String)
	 */
	@Override
	public Object getObj(String key) {
		byte[] data = jedis.get(key.getBytes());
		if (data == null || data.length == 0) {
			log.debug("redis data is empty " + key);
		}
		Object obj = null;
		if (data != null) {
			obj = SerialUtil.decode(data);
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#del_(java.lang.String)
	 */
	@Override
	public boolean del(String key) {
		boolean ok = false;
		try {
			jedis.del(key);
			ok = true;
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#hmset(java.lang.String, java.util.Map, int)
	 */
	@Override
	public boolean hmset(String key, Map<String, Object> values, int timeout) {
		String ok = "";
		Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();
		values.forEach(new BiConsumer<String, Object>() {
			@Override
			public void accept(String key, Object value) {
				byte[] data = SerialUtil.encode(value);
				hash.put(key.getBytes(), data);
			}
		});
		ok = jedis.hmset(key.getBytes(), hash);
		jedis.expire(key.getBytes(), timeout);

		return ok.equals("ok");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#hset(java.lang.String, java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public boolean hset(String key, String field, Object obj) {
		long ok = 0;
		byte[] data = SerialUtil.encode(obj);
		ok = jedis.hset(key.getBytes(), field.getBytes(), data);
		return ok == 0 || ok == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#hget_(java.lang.String, java.lang.String)
	 */
	@Override
	public Object hget(String key, String field) {
		byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
		;
		Object obj = null;
		if (bytes != null) {
			obj = SerialUtil.decode(bytes);
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.my.redis.JedisFace#hgetAll_(java.lang.String)
	 */
	@Override
	public Map<String, Object> hgetAll(String key) {
		Map<byte[], byte[]> values = null;
		Map<String, Object> data = new HashMap<String, Object>();
		values = jedis.hgetAll(key.getBytes());
		values.forEach(new BiConsumer<byte[], byte[]>() {
			@Override
			public void accept(byte[] key, byte[] val) {
				data.put(new String(key), SerialUtil.decode(val));
			}
		});

		return data;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			this.jedis = pool.getJedis();
			result = method.invoke(this, args);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pool != null) {
				if (jedis != null)
					pool.returnResource(jedis);
			}
		}
		return result;
	}

	@Override
	public boolean exists(String id) {
		return jedis.exists(id);
	}
}

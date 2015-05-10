package com.code.redis;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisPubSub;

public interface JedisFace {

	/**
	 * 保存字符串
	 * 
	 * @param key
	 *            关键字
	 * @param value
	 *            值
	 * @param seconds
	 *            过期时间，-1永不过期
	 * @param override
	 *            如果存在，是否覆盖
	 * @return 成功/失败
	 */
	public abstract boolean setStr(String key, String value, int seconds, boolean override);

	/**
	 * 获取字符串
	 * 
	 * @param key
	 *            关键字
	 * @return 值
	 */
	public abstract String getStr(String key);

	/**
	 * 保存对象
	 * 
	 * @param key
	 *            关键字
	 * @param value
	 *            值
	 * @param seconds
	 *            过期时间，-1永不过期
	 * @param override
	 *            如果存在，是否覆盖
	 * @return 成功/失败
	 */
	public abstract boolean setObj(String key, Object value, int seconds, boolean override);

	/**
	 * 获取对象
	 * 
	 * @param key
	 *            关键字
	 * @return 对象值
	 */
	public abstract Object getObj(String key);

	public abstract boolean del(String key);

	public abstract boolean hmset(String key, Map<String, Object> values, int timeout);

	public abstract boolean hset(String key, String field, Object obj);

	public abstract Object hget(String key, String field);

	public abstract Map<String, Object> hgetAll(String key);
	public abstract Map<String, Object> hgetAll(String key,Integer dbIdx);

	public boolean exists(String id);
	
	public long incr(String key, int seconds);
	
	public Long expire(String key, int seconds);

	Long publish(String channel, String message);

	void subscribe(JedisPubSub pubsub, String... channel);

	void batchPublish(String channel,List<Object> services);

}
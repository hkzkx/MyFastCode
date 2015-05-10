package com.code.redis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

public class PooledJedis {
	private static Log log = LogFactory.getLog(PooledJedis.class);

	private static PooledJedis pooledJedis;

	private static URI conf;
	private Pool<Jedis> pool;

	private String host;
	private Integer port;

	private String sentinels;// 主从模式
	private String master;// 主节点名字

	private Integer dbIdx;
	private String app;

	private Integer soTimeout = 5000;// jedis socket 超时设置
	private Integer poolMaxActive = 50;
	private Integer poolMaxIdle = 10;// pool最多有多少个状态为idle(空闲的)的jedis实例。
	private Long poolMaxWait = 10L * 1000L;// 10 秒等待,引用jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
	private boolean poolTestOnBorrow = true;// 引用jedis时，测试是否可用
	private boolean poolTestOnReturn = true; // 返还资源时，是否检查已经返还过

	private Integer retries = 4;

	public static PooledJedis getPooledJedis(URI conf_) {
		if (pooledJedis == null) {
			synchronized (PooledJedis.class) {
				conf = conf_;
				pooledJedis = new PooledJedis();
			}
		}
		return pooledJedis;
	}

	public static PooledJedis getPooledJedis(String conf_) throws URISyntaxException {
		if (pooledJedis == null) {
			synchronized (PooledJedis.class) {
				conf = PooledJedis.class.getResource(conf_).toURI();
				pooledJedis = new PooledJedis();
			}
		}
		return pooledJedis;
	}

	public synchronized void init() {
		if (conf == null || conf.equals(""))
			throw new IllegalArgumentException("未指定redis配置文件");

		Path path = Paths.get(conf);
		boolean fileExists = Files.exists(path);
		if (!fileExists) {
			log.warn("文件不存在：" + conf);
			throw new IllegalArgumentException("文件不存在：" + conf);
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(conf));
			Properties file = new Properties();
			file.load(fis);
			String poolMaxActive = file.getProperty("pool.MaxActive");
			if (poolMaxActive != null && poolMaxActive.trim().length() > 0)
				this.poolMaxActive = Integer.valueOf(poolMaxActive);

			String poolMaxIdle = file.getProperty("pool.MaxIdle");
			if (poolMaxIdle != null && poolMaxIdle.trim().length() > 0)
				this.poolMaxIdle = Integer.valueOf(poolMaxIdle);

			String poolMaxWait = file.getProperty("pool.MaxWait");
			if (poolMaxWait != null && poolMaxWait.trim().length() > 0)
				this.poolMaxWait = Long.valueOf(poolMaxWait);

			String poolTestOnBorrow = file.getProperty("pool.TestOnBorrow");
			if (poolTestOnBorrow != null && poolTestOnBorrow.trim().length() > 0)
				this.poolTestOnBorrow = Boolean.valueOf(poolTestOnBorrow);

			String poolTestOnReturn = file.getProperty("pool.TestOnReturn");
			if (poolTestOnReturn != null && poolTestOnReturn.trim().length() > 0)
				this.poolTestOnReturn = Boolean.valueOf(poolTestOnReturn);

			this.host = file.getProperty("host");
			String portObj = file.getProperty("port");
			this.port = portObj == null ? null : Integer.valueOf(portObj);
			this.dbIdx = Integer.valueOf(file.getProperty("db"));
			this.sentinels = file.getProperty("sentinels");
			this.master = file.getProperty("master.name");

			this.app = file.getProperty("app");
			makePool();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public JedisFace getJedisProxy() {
		return new JedisProxy(this);
	}

	private static AtomicInteger counter = new AtomicInteger();

	protected Jedis getJedis() {
		try {
			Jedis jedis = this.pool.getResource();
			if(counter.get()>0)
				counter.set(0);
			return jedis;
		} catch (Exception ex) {
			int i = counter.incrementAndGet();
			if (i < retries){
				log.warn("尝试获取 jedis 链接，尝试次数："+i);
				return getJedis();
			}
			else
				return null;
		}
	}

	public void returnResource(Jedis resource) {
		pool.returnResource(resource);
	}

	/**
	 * 构建redis连接池
	 * 
	 * @param ip
	 * @param port
	 * @return JedisPool
	 */
	private void makePool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(poolMaxActive);
			config.setMaxIdle(poolMaxIdle);
			config.setMaxWaitMillis(poolMaxWait);
			config.setTestOnBorrow(poolTestOnBorrow);
			config.setTestOnReturn(poolTestOnReturn);

			if (host == null) {
				Set<String> sentinelSet = new HashSet<String>();
				sentinelSet.add(sentinels);
				pool = new JedisSentinelPool(this.master, sentinelSet, config, Protocol.DEFAULT_TIMEOUT, null, dbIdx);
			} else {
				pool = new JedisPool(config, host, port, soTimeout, null, dbIdx, "data-cache-client-" + app);
			}
		}
	}

	public Integer getDbIdx() {
		return dbIdx;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public Integer getPoolMaxActive() {
		return poolMaxActive;
	}

	public void setPoolMaxActive(Integer poolMaxActive) {
		this.poolMaxActive = poolMaxActive;
	}

	public Integer getPoolMaxIdle() {
		return poolMaxIdle;
	}

	public void setPoolMaxIdle(Integer poolMaxIdle) {
		this.poolMaxIdle = poolMaxIdle;
	}

	public Long getPoolMaxWait() {
		return poolMaxWait;
	}

	public void setPoolMaxWait(Long poolMaxWait) {
		this.poolMaxWait = poolMaxWait;
	}

	public boolean isPoolTestOnBorrow() {
		return poolTestOnBorrow;
	}

	public void setPoolTestOnBorrow(boolean poolTestOnBorrow) {
		this.poolTestOnBorrow = poolTestOnBorrow;
	}

	public boolean isPoolTestOnReturn() {
		return poolTestOnReturn;
	}

	public void setPoolTestOnReturn(boolean poolTestOnReturn) {
		this.poolTestOnReturn = poolTestOnReturn;
	}

	public void setDbIdx(Integer dbIdx) {
		this.dbIdx = dbIdx;
	}

	public void setSentinels(String sentinels) {
		this.sentinels = sentinels;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public static void main(String[] args) throws InterruptedException, URISyntaxException {
		URI uri = new URI("file:///F:/guangqun/trunk/remote/src/main/resources/env/me/common.properties");
		PooledJedis pool = PooledJedis.getPooledJedis(uri);
		pool.init();
		while (true) {
			try {
				JedisFace jedis = pool.getJedisProxy();
				String key = jedis.hashCode() + "";
				jedis.setStr(key, key, -1, false);
				System.out.println(jedis.getStr(key));

				Thread.currentThread().sleep(1000l);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}

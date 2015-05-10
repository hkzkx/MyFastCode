package com.my.redis;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class PooledJedis {
	private static Logger log = Log.getLogger(PooledJedis.class);

	private static PooledJedis pooledJedis;
	
	private static String conf;
	private JedisPool pool;
	
	private String host;
	private Integer port;
	private Integer dbIdx;
	private String app;
	
	private Integer soTimeout = 5000;//jedis socket 超时设置
	private Integer poolMaxActive = 50;
	private Integer poolMaxIdle = 10;//pool最多有多少个状态为idle(空闲的)的jedis实例。
	private Long poolMaxWait = 10L*1000L;//10 秒等待,引用jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
	private boolean poolTestOnBorrow = true;//引用jedis时，测试是否可用
	private boolean poolTestOnReturn = true; // 返还资源时，是否检查已经返还过
	
	public static PooledJedis getPooledJedis(String conf_) {
		if(pooledJedis == null) {
			synchronized (PooledJedis.class) {
				conf = conf_;
				pooledJedis = new PooledJedis();
			}
		}
		return pooledJedis;
	}
	
	private PooledJedis(){
		init();
		
	}
	private synchronized void init() {
		if (conf == null || conf.equals(""))
			throw new IllegalArgumentException("未指定http session redis配置文件");

		Path path = Paths.get(conf);
		boolean fileExists = Files.exists(path);
		if (!fileExists) {
			log.warn("文件不存在：" + conf);
			throw new IllegalArgumentException("文件不存在：" + conf);
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(conf);
			Properties file = new Properties();
			file.load(fis);
			String poolMaxActive = file.getProperty("pool.MaxActive");
			if(poolMaxActive != null && poolMaxActive.trim().length()>0)
				this.poolMaxActive = Integer.valueOf(poolMaxActive);
			
			String poolMaxIdle = file.getProperty("pool.MaxIdle");
			if(poolMaxIdle != null && poolMaxIdle.trim().length()>0)
				this.poolMaxIdle = Integer.valueOf(poolMaxIdle);
			
			String poolMaxWait = file.getProperty("pool.MaxWait");
			if(poolMaxWait != null && poolMaxWait.trim().length()>0)
				this.poolMaxWait = Long.valueOf(poolMaxWait);
			
			String poolTestOnBorrow = file.getProperty("pool.TestOnBorrow");
			if(poolTestOnBorrow != null && poolTestOnBorrow.trim().length()>0)
				this.poolTestOnBorrow = Boolean.valueOf(poolTestOnBorrow);
			
			String poolTestOnReturn = file.getProperty("pool.TestOnReturn");
			if(poolTestOnReturn != null && poolTestOnReturn.trim().length()>0)
				this.poolTestOnReturn = Boolean.valueOf(poolTestOnReturn);
			
			this.host = file.getProperty("host");
			this.port = Integer.valueOf(file.getProperty("port"));
			this.dbIdx = Integer.valueOf(file.getProperty("db"));
			
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

	public JedisProxy getJedisProxy() {
		return new JedisProxy(this);
//		JedisProxy proxy = new JedisProxy(this);
//		return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),  
//				proxy.getClass().getInterfaces(), proxy);   
	}
	
	protected Jedis getJedis() {
		return this.pool.getResource();
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
			
			pool = new JedisPool(config,host,port,soTimeout,null,dbIdx,"http-session-client-"+app);
		}
	}

	
	public Integer getDbIdx() {
		return dbIdx;
	}

	public static void main(String[] args) {
		String conf = "D:/work/MyFastCode-master/http_session/src/main/resources/http_session.redis";
		PooledJedis pool = PooledJedis.getPooledJedis(conf);
//		Jedis jedis = pool.getJedis();
		
//		ScanParams p = new ScanParams();
//		p.match("*2*");
//		p.count(999999999);
//		ScanResult<String> rs = jedis.scan("0", p);
//		List<String> list = rs.getResult();
//		System.out.println(list.size());
		while(true){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true) {
						JedisFace face = (JedisFace) pool.getJedisProxy();
						face.getStr("a");
					}
					
				}
			},"read").start();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		for (int i = 0; i < 10; i++) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					while(true) {
//						JedisFace face = (JedisFace) pool.getJedisProxy();
//						face.getStr("a");
//					}
//					
//				}
//			},"read").start();
//		}
	}
}

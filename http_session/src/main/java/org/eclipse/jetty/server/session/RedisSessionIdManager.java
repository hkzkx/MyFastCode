package org.eclipse.jetty.server.session;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.mmb.redis.JedisFace;
import com.mmb.redis.PooledJedis;

public class RedisSessionIdManager extends AbstractSessionIdManager {
	private static Logger log = Log.getLogger(RedisSessionIdManager.class);


	private Server _server;
	private String redisConfig;
	private PooledJedis pool;
	
	private boolean brisk = false;
	
	public boolean isBrisk() {
		return brisk;
	}

	public void setBrisk(boolean brisk) {
		this.brisk = brisk;
	}
	
	public Integer getDbIdx(){
		return pool.getDbIdx();
	}
	
	public RedisSessionIdManager(Server server) {
		super();
		log.info("RedisSessionIdManager init ...");
		_server = server;
	}

	public RedisSessionIdManager(Server server, Random random) {
		super(random);
		_server = server;
	}

	private void initializeRedis() {
		pool = PooledJedis.getPooledJedis(redisConfig);
		try{
			this.getRedis().exists("test");
			log.info("http redis test done.");
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public JedisFace getRedis() {
		return (JedisFace)pool.getJedisProxy();
	}

	public void addSession(HttpSession session) {
		// do nothing
	}

	public void removeSession(HttpSession session) {
		// do nothing
	}

	/**
	 * Get the session id without any node identifier suffix.
	 * 
	 * @see org.eclipse.jetty.server.SessionIdManager#getClusterId(java.lang.String)
	 */
	public String getClusterId(String nodeId) {
		int dot = nodeId.lastIndexOf('.');
		return (dot > 0) ? nodeId.substring(0, dot) : nodeId;
	}

	/**
	 * Get the session id, including this node's id as a suffix.
	 * 
	 * @see org.eclipse.jetty.server.SessionIdManager#getNodeId(java.lang.String,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public String getNodeId(String clusterId, HttpServletRequest request) {
		if (_workerName != null)
			return clusterId + '.' + _workerName;

		return clusterId;
	}

	public boolean idInUse(String id) {
		if (id == null)
			return false;

		String clusterId = getClusterId(id);
		boolean inUse = false;

		synchronized (id) {
			inUse = exists(clusterId);
		}

		return inUse;
	}

	/**
	 * Invalidate the session matching the id on all contexts.
	 * 
	 * @see org.eclipse.jetty.server.SessionIdManager#invalidateAll(java.lang.String)
	 */
	public void invalidateAll(String id) {

		// tell all contexts that may have a session object with this id to
		// get rid of them
		Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);
		for (int i = 0; contexts != null && i < contexts.length; i++) {
			SessionHandler sessionHandler = (SessionHandler) ((ContextHandler) contexts[i])
					.getChildHandlerByClass(SessionHandler.class);
			if (sessionHandler != null) {
				SessionManager manager = sessionHandler.getSessionManager();

				if (manager != null && manager instanceof RedisSessionManager) {
					((RedisSessionManager) manager).invalidateSession(id);
				}
			}
		}
	}

	@Override
	public void doStart() {
		initializeRedis();
		try {
			super.doStart();
			log.debug("RedisSessionIdManager stared");
		} catch (Exception e) {
			log.warn("Problem initialising RedisSessionIdManager", e);
		}
	}

	@Override
	public void doStop() throws Exception {
		super.doStop();
	}

	private boolean exists(String id) {
		return getRedis().exists(id);
	}

	public String getRedisConfig() {
		return redisConfig;
	}

	public void setRedisConfig(String redisConfig) {
		this.redisConfig = redisConfig;
	}

	@Override
	public String newSessionId(HttpServletRequest request, long created) {
		String sessionId = super.newSessionId(request, created);
		String dbId = pool.getDbIdx().toString();
		if(dbId.length()<2)
			dbId = "0"+dbId;
		
		return sessionId + dbId;
		
	}

	
}

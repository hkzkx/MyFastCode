package org.eclipse.jetty.server.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.my.redis.JedisFace;

public class RedisSessionManager extends AbstractSessionManager {
	private static Logger log = Log.getLogger(RedisSessionManager.class);

	/**
	 * Session
	 *
	 * Session instance in memory of this node.
	 */
	public class Session extends AbstractSession implements java.io.Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1465261009774690711L;

		/**
		 * If dirty, session needs to be (re)persisted
		 */
		private boolean _dirty = true;

		/**
		 * Time in msec since the epoch that a session cookie was set for this
		 * session
		 */
		private long _cookieSet;

		/**
		 * Time in msec since the epoch that the session will expire
		 */
		private long _expiryTime;

		/**
		 * Time in msec since the epoch that the session was last persisted
		 */
		private long _lastSaved;

		/**
		 * Unique identifier of the last node to host the session
		 */
		private String _lastNode;

		/**
		 * Virtual host for context (used to help distinguish 2 sessions with
		 * same id on different contexts)
		 */
		private String _virtualHost;

		/**
		 * Mangled context name (used to help distinguish 2 sessions with same
		 * id on different contexts)
		 */
		private String _canonicalContext;

		/**
		 * Session from a request.
		 *
		 * @param request
		 */
		protected Session(HttpServletRequest request) {

			super(RedisSessionManager.this, request);
			_virtualHost = RedisSessionManager.getVirtualHost(_context);
			_canonicalContext = canonicalize(_context.getContextPath());
			_lastNode = getSessionIdManager().getWorkerName();
		}

		/**
		 * Session restored from redis
		 * 
		 * @param sessionId
		 * @param rowId
		 * @param created
		 * @param accessed
		 */
		protected Session(String sessionId, long created, long accessed) {
			super(RedisSessionManager.this, created, accessed, sessionId);
		}

		public synchronized void setVirtualHost(String vhost) {
			_virtualHost = vhost;
		}

		public synchronized String getVirtualHost() {
			return _virtualHost;
		}

		public synchronized long getLastSaved() {
			return _lastSaved;
		}

		public synchronized void setLastSaved(long time) {
			_lastSaved = time;
		}

		public synchronized void setExpiryTime(int time) {
			_expiryTime = time;
		}

		public synchronized long getExpiryTime() {
			return _expiryTime;
		}

		public synchronized void setCanonicalContext(String str) {
			_canonicalContext = str;
		}

		public synchronized String getCanonicalContext() {
			return _canonicalContext;
		}

		public void setCookieSet(long ms) {
			_cookieSet = ms;
		}

		public synchronized long getCookieSet() {
			return _cookieSet;
		}

		public synchronized void setLastNode(String node) {
			_lastNode = node;
		}

		public synchronized String getLastNode() {
			return _lastNode;
		}

		@Override
		public void setAttribute(String name, Object value) {
			super.setAttribute(name, value);
			_dirty = true;
		}

		@Override
		public void removeAttribute(String name) {
			super.removeAttribute(name);
			_dirty = true;
		}

		@Override
		protected void cookieSet() {
			_cookieSet = getAccessed();
		}

		/**
		 * Entry to session. Called by SessionHandler on inbound request and the
		 * session already exists in this node's memory.
		 *
		 * @see org.eclipse.jetty.server.session.AbstractSession#access(long)
		 */
		@Override
		protected boolean access(long time) {
			synchronized (this) {
				if (super.access(time)) {
					int maxInterval = getMaxInactiveInterval();
					_expiryTime = (maxInterval <= 0 ? 0 : (time + maxInterval));
					return true;
				}
				return false;
			}
		}

		/**
		 * Exit from session
		 * 
		 * @see org.eclipse.jetty.server.session.AbstractSession#complete()
		 */
		@Override
		protected void complete() {
			synchronized (this) {
				super.complete();
				try {
					if (isValid()) {
						if (_dirty) {
							// The session attributes have changed, write to the redis, ensuring
							// http passivation/activation listeners called
							willPassivate();
							updateSession(this);
							didActivate();
						}
					}
				} catch (Exception e) {
					log.warn("Problem persisting changed session data id=" + getId(), e);
				} finally {
					_dirty = false;
				}
			}
		}

		@Override
		protected void timeout() throws IllegalStateException {
			if (log.isDebugEnabled())
				log.info("Timing out session id=" + getClusterId());
			super.timeout();
		}

		@Override
		public String toString() {
			return "Session id=" + getId() + ",lastNode=" + _lastNode + ",created="
					+ getCreationTime() + ",accessed=" + getAccessed() + ",lastAccessed="
					+ getLastAccessedTime() + ",cookieSet=" + _cookieSet + ",lastSaved="
					+ _lastSaved + ",expiry=" + _expiryTime;
		}

		public Map<String,Object> copy() {
			Map<String,Object> sessionCopy = new HashMap<String,Object>();
			sessionCopy.put(SessionKeys.canonicalContext.name(), _canonicalContext);
			sessionCopy.put(SessionKeys.cookieSet.name(), _cookieSet);
			sessionCopy.put(SessionKeys.dirty.name(), _dirty);
			sessionCopy.put(SessionKeys.lastNode.name(), _lastNode);
			sessionCopy.put(SessionKeys.lastSaved.name(), _lastSaved);
			sessionCopy.put(SessionKeys.virtualHost.name(), _virtualHost);
			sessionCopy.put(SessionKeys._session_Attributes_.name(), getAttributeMap());
			sessionCopy.put(SessionKeys.accessed.name(), getAccessed());
			sessionCopy.put(SessionKeys.lastAccessedTime.name(), getLastAccessedTime());
			sessionCopy.put(SessionKeys.isNew.name(), isNew());
			sessionCopy.put(SessionKeys.isValid.name(), isValid());
			sessionCopy.put(SessionKeys.id.name(), getId());
			sessionCopy.put(SessionKeys.creationTime.name(), getCreationTime());
			sessionCopy.put(SessionKeys.maxInactiveInterval.name(), getMaxInactiveInterval());
			
			return sessionCopy;
		}
		
	}

	/**
	 * A session has been requested by it's id on this node.
	 * 
	 * Load the session by id AND context path from the database. Multiple
	 * contexts may share the same session id (due to dispatching) but they
	 * CANNOT share the same contents.
	 * 
	 * Check if last node id is my node id, if so, then the session we have in
	 * memory cannot be stale. If another node used the session last, then we
	 * need to refresh from the db.
	 * 
	 * NOTE: this method will go to the database, so if you only want to check
	 * for the existence of a Session in memory, use _sessions.get(id) instead.
	 * 
	 * @see org.eclipse.jetty.server.session.AbstractSessionManager#getSession(java.lang.String)
	 */
	@Override
	public Session getSession(String idInCluster) {
		
		Session session = loadSession(idInCluster);
		
		synchronized (this) {
			long now = System.currentTimeMillis();

			// If we have a session
			if (session != null) {
				if (!session.getLastNode().equals(getSessionIdManager().getWorkerName())) {
					// if session doesn't expire, or has not already expired,
					// update it and put it in this nodes' memory
					if (session._expiryTime <= 0 || session._expiryTime > now) {
						if (log.isDebugEnabled())
							log.debug("getSession(" + idInCluster + "): lastNode="
									+ session.getLastNode() + " thisNode="
									+ getSessionIdManager().getWorkerName());

						session.setLastNode(getSessionIdManager().getWorkerName());

						// update in db: if unable to update, session will be
						// scavenged later
						try {
							updateSessionNode(session);
							session.didActivate();
						} catch (Exception e) {
							log.warn("Unable to update freshly loaded session " + idInCluster, e);
							return null;
						}
					} else {
						log.info(String.format("getSession (%s): Session has expired", idInCluster));
						session = null;
					}

				} else {
					log.info(String.format("getSession(%s): Session not stale %s", idInCluster, session));
				}
			} else {
				// No session in db with matching id and context path.
				log.info(String.format("getSession(%s): No session in database matching id=%s", idInCluster,
						idInCluster));
			}

			return session;
		}
	}

	/**
	 * Start the session manager.
	 * 
	 * @see org.eclipse.jetty.server.session.AbstractSessionManager#doStart()
	 */
	@Override
	public void doStart() throws Exception {
		if (_sessionIdManager == null)
			throw new IllegalStateException("No session id manager defined");

		super.doStart();
	}

	/**
	 * Stop the session manager.
	 * 
	 * @see org.eclipse.jetty.server.session.AbstractSessionManager#doStop()
	 */
	@Override
	public void doStop() throws Exception {
		super.doStop();
	}

	/**
	 * Invalidate a session.
	 * 
	 * @param idInCluster
	 */
	protected void invalidateSession(String idInCluster) {
		Session session = loadSession(idInCluster);

		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * Delete an existing session, both from the in-memory map and the database.
	 * 
	 * @see org.eclipse.jetty.server.session.AbstractSessionManager#removeSession(java.lang.String)
	 */
	@Override
	protected boolean removeSession(String idInCluster) {
		boolean removed = false;
		synchronized (this) {
			removed = getRedis().del(idInCluster);
		}
		log.info("removed session with id "+idInCluster);
		return removed;
	}

	/**
	 * Add a newly created session to our in-memory list for this node and
	 * persist it.
	 * 
	 * @see org.eclipse.jetty.server.session.AbstractSessionManager#addSession(org.eclipse.jetty.server.session.AbstractSessionManager.RedisSession)
	 */
	@Override
	protected void addSession(AbstractSession session) {
		if (session == null)
			return;

		// TODO or delay the store until exit out of session? If we crash before
		// we store it
		// then session data will be lost.
		try {
			session.willPassivate();
			RedisSessionManager.Session managedSession = ((RedisSessionManager.Session) session);
			storeSession(managedSession);
			session.didActivate();
		} catch (Exception e) {
			log.warn("Unable to store new session id=" + session.getId(), e);
		}
	}

	/* ------------------------------------------------------------ */
	/**
	 * Remove session from manager
	 * 
	 * @param session
	 *            The session to remove
	 * @param invalidate
	 *            True if
	 *            {@link HttpSessionListener#sessionDestroyed(HttpSessionEvent)}
	 *            and {@link SessionIdManager#invalidateAll(String)} should be
	 *            called.
	 */
	@Override
	public void removeSession(AbstractSession session, boolean invalidate) {
		// Remove session from context and global maps
		boolean removed = removeSession(session.getClusterId());

		

		if (removed) {

			if (invalidate)
				_sessionIdManager.invalidateAll(session.getClusterId());

			if (invalidate && _sessionListeners != null) {
				HttpSessionEvent event = new HttpSessionEvent(session);
				for (int i = LazyList.size(_sessionListeners); i-- > 0;)
					((HttpSessionListener) LazyList.get(_sessionListeners, i))
							.sessionDestroyed(event);
			}
			if (!invalidate) {
				session.willPassivate();
			}
		}
	}

	/**
	 * Load a session from the redis
	 * 
	 * @param id
	 * @return the session data that was loaded
	 * @throws Exception
	 */
	protected Session loadSession(final String id) {

		Map<String,Object> data = (Map<String,Object>) getRedis().hgetAll(id);
		if (data == null || data.size()==0) {
			return null;
		}

		Session session = new Session(
					(String)data.get(SessionKeys.id.name()), 
					(long)data.get(SessionKeys.creationTime.name()),
				 	(long)data.get(SessionKeys.lastAccessedTime.name()));
		
		session.setLastNode((String)data.get(SessionKeys.lastNode.name()));
		session.setLastSaved((long)data.get(SessionKeys.lastSaved.name()));
		Map<String, Object> attrs = (Map<String, Object>)data.get(SessionKeys._session_Attributes_.name());
		session.addAttributes(attrs);
		
		log.info("LOADED session " + session);

		return session;
	}

	protected void storeSession(Session data) {
		
		if (data == null)
			return;

		long now = System.currentTimeMillis();
		data.setLastNode(getSessionIdManager().getWorkerName());
		data.setLastSaved(now);

		Map<String,Object> sessionCopy = data.copy();
		sessionCopy.put(SessionKeys.lastNode.name(), data.getLastNode());
		sessionCopy.put(SessionKeys.lastSaved.name(), now);
		
		getRedis().hmset(data.getId(), sessionCopy,data.getMaxInactiveInterval());
		
		log.info("Stored session " + data);
	}

	protected void updateSession(Session data) {
		if (data == null)
			return;

		long now = System.currentTimeMillis();
		data.setLastNode(getSessionIdManager().getWorkerName());
		data.setLastSaved(now);

		Map<String,Object> sessionCopy = data.copy();
		sessionCopy.put(SessionKeys.lastNode.name(), data.getLastNode());
		sessionCopy.put(SessionKeys.lastSaved.name(), now);


		sessionCopy.put(SessionKeys.accessed.name(), data.getAccessed());
		sessionCopy.put(SessionKeys.lastAccessedTime.name(), data.getLastAccessedTime());
		
		getRedis().hmset(data.getId(), sessionCopy,data.getMaxInactiveInterval());
		
		log.info("Updated session " + data);
	}

	protected void updateSessionNode(Session data) throws Exception {
		String nodeId = getSessionIdManager().getWorkerName();
		try {
			data.setLastNode(nodeId);
			long now = System.currentTimeMillis();
			data.setLastSaved(now);

			getRedis().hset(data.getId(), SessionKeys.lastNode.name(), nodeId);
			
			log.info("Updated last node for session id=" + data.getId() + ", lastNode = "
						+ nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void deleteSession(Session data) throws Exception {
		getRedis().del(data.getId());
		log.info("Deleted Session " + data);
	}
	
	private JedisFace getRedis() {
		return ((RedisSessionIdManager) getSessionIdManager()).getRedis();
	}

	/**
	 * Get the first virtual host for the context.
	 * 
	 * Used to help identify the exact session/contextPath.
	 * 
	 * @return 0.0.0.0 if no virtual host is defined
	 */
	private static String getVirtualHost(ContextHandler.Context context) {
		String vhost = "0.0.0.0";

		if (context == null)
			return vhost;

		String[] vhosts = context.getContextHandler().getVirtualHosts();
		if (vhosts == null || vhosts.length == 0 || vhosts[0] == null)
			return vhost;

		return vhosts[0];
	}

	/**
	 * Make an acceptable file name from a context path.
	 * 
	 * @param path
	 * @return
	 */
	private String canonicalize(String path) {
		if (path == null)
			return "";

		return path.replace('/', '_').replace('.', '_').replace('\\', '_');
	}

	@Override
	protected AbstractSession newSession(HttpServletRequest request) {
		return new Session(request);
	}

	@Override
	public void setMaxInactiveInterval(int seconds) {
		super.setMaxInactiveInterval(seconds);
	}
	
	@Override
	public int getMaxInactiveInterval(){
		return super.getMaxInactiveInterval();
	}

	@Override
	protected void invalidateSessions() throws Exception {
		
	}
}

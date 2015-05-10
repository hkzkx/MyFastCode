//
//  ========================================================================
//  Copyright (c) 1995-2014 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.server.session;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SessionManager;

/* ------------------------------------------------------------ */
/**
 * SessionHandler.
 */
public class MySessionHandler extends SessionHandler {

	private static Set<String> excludeMatchedUri = new HashSet<String>();
	static {
		excludeMatchedUri.add(".css");
		excludeMatchedUri.add(".js");

		excludeMatchedUri.add(".icon");
		excludeMatchedUri.add(".jpg");
		excludeMatchedUri.add(".jpeg");
		excludeMatchedUri.add(".gif");
		excludeMatchedUri.add(".png");
		excludeMatchedUri.add(".bmp");
		excludeMatchedUri.add(".woff");

	}

	@Override
	public void doScope(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		boolean beHandle = false;
		String type = null;
		
		String uri = request.getRequestURI();
		if(uri != null && uri.trim().length()>0){
			int slashIdx = uri.lastIndexOf("/");
			uri = uri.substring(slashIdx,uri.length());
			if(uri != null && uri.trim().length()>0){
				int lastDotIdx = uri.lastIndexOf(".");
				if(lastDotIdx != -1)
					type = uri.substring(lastDotIdx, uri.length());
			}
			
			
		}
		if(type != null)
			beHandle = excludeMatchedUri.contains(type);

		SessionManager old_session_manager = null;
		HttpSession old_session = null;
		HttpSession access = null;
		
		try {
			if (!beHandle) {

				old_session_manager = baseRequest.getSessionManager();
				old_session = baseRequest.getSession(false);

				if (old_session_manager != getSessionManager()) {
					// new session context
					baseRequest.setSessionManager(getSessionManager());
					baseRequest.setSession(null);
					checkRequestedSessionId(baseRequest, request);
				}

				// access any existing session
				HttpSession session = null;
				if (getSessionManager() != null) {
					session = baseRequest.getSession(false);
					if (session != null) {
						if (session != old_session) {
							access = session;
							HttpCookie cookie = getSessionManager().access(session, request.isSecure());
							if (cookie != null) // Handle changed ID or max-age refresh
								baseRequest.getResponse().addCookie(cookie);
						}
					} else {
						session = baseRequest.recoverNewSession(getSessionManager());
						if (session != null)
							baseRequest.setSession(session);
					}
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("sessionManager=" + getSessionManager());
					LOG.debug("session=" + session);
				}
			}
			// start manual inline of
			// nextScope(target,baseRequest,request,response);
			if (_nextScope != null)
				_nextScope.doScope(target, baseRequest, request, response);
			else if (_outerScope != null)
				_outerScope.doHandle(target, baseRequest, request, response);
			else
				doHandle(target, baseRequest, request, response);
			// end manual inline (pathentic attempt to reduce stack depth)

		} finally {
			if (!beHandle) {
				if (access != null)
					getSessionManager().complete(access);

				HttpSession session = baseRequest.getSession(false);
				if (session != null && old_session == null && session != access)
					getSessionManager().complete(session);

				if (old_session_manager != null && old_session_manager != getSessionManager()) {
					baseRequest.setSessionManager(old_session_manager);
					baseRequest.setSession(old_session);
				}
			}
		}

	}

	/* ------------------------------------------------------------ */
	/**
	 * Look for a requested session ID in cookies and URI parameters
	 *
	 * @param baseRequest
	 * @param request
	 */
	@Override
	protected void checkRequestedSessionId(Request baseRequest, HttpServletRequest request) {
		String requested_session_id = request.getRequestedSessionId();

		SessionManager sessionManager = getSessionManager();

		if (requested_session_id != null && sessionManager != null) {
			HttpSession session = sessionManager.getHttpSession(requested_session_id);
			if (session != null && sessionManager.isValid(session))
				baseRequest.setSession(session);
			return;
		} else if (!DispatcherType.REQUEST.equals(baseRequest.getDispatcherType()))
			return;

		boolean requested_session_id_from_cookie = false;
		HttpSession session = null;

		// Look for session id cookie
		if (getSessionManager().isUsingCookies()) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				final String sessionCookie = sessionManager.getSessionCookieConfig().getName();
				for (int i = 0; i < cookies.length; i++) {
					if (sessionCookie.equalsIgnoreCase(cookies[i].getName())) {
						requested_session_id = cookies[i].getValue();
						requested_session_id_from_cookie = true;

						LOG.debug("Got Session ID {} from cookie", requested_session_id);

						if (requested_session_id != null) {
							session = sessionManager.getHttpSession(requested_session_id);

							if (session != null && sessionManager.isValid(session)) {
								break;
							}
						} else {
							LOG.warn("null session id from cookie");
						}
					}
				}
			}
		}

		if (requested_session_id == null || session == null) {
			requested_session_id = request.getParameter("jsessionid");
			if (requested_session_id != null && !requested_session_id.trim().equals("")) {
				requested_session_id_from_cookie = false;
				session = sessionManager.getHttpSession(requested_session_id);
				if (LOG.isDebugEnabled())
					LOG.debug("Got Session ID {} from URL", requested_session_id);
			}
		}

		baseRequest.setRequestedSessionId(requested_session_id);
		baseRequest.setRequestedSessionIdFromCookie(requested_session_id != null && requested_session_id_from_cookie);
		if (session != null && sessionManager.isValid(session))
			baseRequest.setSession(session);
	}
}

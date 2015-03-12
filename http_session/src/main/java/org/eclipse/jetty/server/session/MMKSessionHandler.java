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

import javax.servlet.DispatcherType;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SessionManager;

/* ------------------------------------------------------------ */
/**
 * SessionHandler.
 */
public class MMKSessionHandler extends SessionHandler
{

    /* ------------------------------------------------------------ */
    /**
     * Look for a requested session ID in cookies and URI parameters
     *
     * @param baseRequest
     * @param request
     */
	@Override
    protected void checkRequestedSessionId(Request baseRequest, HttpServletRequest request)
    {
        String requested_session_id = request.getRequestedSessionId();

        SessionManager sessionManager = getSessionManager();

        if (requested_session_id != null && sessionManager != null)
        {
            HttpSession session = sessionManager.getHttpSession(requested_session_id);
            if (session != null && sessionManager.isValid(session))
                baseRequest.setSession(session);
            return;
        }
        else if (!DispatcherType.REQUEST.equals(baseRequest.getDispatcherType()))
            return;

        boolean requested_session_id_from_cookie = false;
        HttpSession session = null;

        // Look for session id cookie
        if (getSessionManager().isUsingCookies())
        {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0)
            {
                final String sessionCookie=sessionManager.getSessionCookieConfig().getName();
                for (int i = 0; i < cookies.length; i++)
                {
                    if (sessionCookie.equalsIgnoreCase(cookies[i].getName()))
                    {
                        requested_session_id = cookies[i].getValue();
                        requested_session_id_from_cookie = true;

                        LOG.debug("Got Session ID {} from cookie",requested_session_id);

                        if (requested_session_id != null)
                        {
                            session = sessionManager.getHttpSession(requested_session_id);

                            if (session != null && sessionManager.isValid(session))
                            {
                                break;
                            }
                        }
                        else
                        {
                            LOG.warn("null session id from cookie");
                        }
                    }
                }
            }
        }

        if (requested_session_id == null || session == null)
        {
        	requested_session_id = request.getParameter("jsessionid");
        	if(requested_session_id != null && !requested_session_id.trim().equals("")){
        	 requested_session_id_from_cookie = false;
             session = sessionManager.getHttpSession(requested_session_id);
             if (LOG.isDebugEnabled())
                 LOG.debug("Got Session ID {} from URL",requested_session_id);
             }
        }

        baseRequest.setRequestedSessionId(requested_session_id);
        baseRequest.setRequestedSessionIdFromCookie(requested_session_id != null && requested_session_id_from_cookie);
        if (session != null && sessionManager.isValid(session))
            baseRequest.setSession(session);
    }
}

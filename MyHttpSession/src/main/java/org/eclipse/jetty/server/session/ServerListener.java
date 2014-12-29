package org.eclipse.jetty.server.session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.AppLifeCycle;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerListener implements LifeCycle.Listener {
	private Set<String> apps = new HashSet<String>();
	private SessionManager sessionManager;

	public ServerListener(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void lifeCycleStarting(LifeCycle event) {
		if (event instanceof Server) {
			Server server = (Server) event;
			Collection<Object> beans = server.getBeans();
			
			/*
			 * 处理直接将WAR放到webapps目录下的情况
			 */
			if (beans != null && beans.size() > 0) {
				beans.forEach(new Consumer<Object>() {

					@Override
					public void accept(Object t) {
						if (t instanceof DeploymentManager) {
							DeploymentManager deploymentManager = (DeploymentManager) t;
							deploymentManager.addLifeCycleBinding(new AppLifeCycle.Binding() {

								@Override
								public String[] getBindingTargets() {
									return new String[] { "starting" ,"deploying"};
								}

								public void processBinding(Node node, App app) throws Exception {
									ContextHandler handler = app.getContextHandler();
									if (handler instanceof WebAppContext) {
										synchronized (apps) {
											WebAppContext webapp = (WebAppContext) handler;
											String appId = webapp.getContextPath()+webapp.getWar();
											if(!apps.contains(appId) && !webapp.isStarted()) {
											
												SessionHandler sessionHandler = new SessionHandler();
												sessionHandler.setSessionManager(sessionManager);
												webapp.setSessionHandler(sessionHandler);
												apps.add(appId);
											}
										}
									}
								}

							});
						}
					}
				});
			}

			/*
			 * 
			 * 虚拟主机 （在Jetty配置文件中配置WebAppContext的情况）
			 */
			Handler handler = server.getHandler();
			if (handler != null) {
				if (handler instanceof WebAppContext) {
					synchronized (apps) {
						WebAppContext webapp = (WebAppContext) handler;
						String appId = webapp.getContextPath()+webapp.getWar();
						if(!apps.contains(appId) && !webapp.isStarted()) {
							SessionHandler sessionHandler = new SessionHandler();
							sessionHandler.setSessionManager(sessionManager);
							webapp.setSessionHandler(sessionHandler);
							apps.add(appId);
						}
					}
				}
			}
		}
	}

	@Override
	public void lifeCycleStarted(LifeCycle event) {
	}

	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {

	}

	@Override
	public void lifeCycleStopping(LifeCycle event) {

	}

	@Override
	public void lifeCycleStopped(LifeCycle event) {

	}

}

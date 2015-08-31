package com.code.spring.context;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

public class SpringApplicationContext extends ContextLoaderListener {
	protected Log					logger	= LogFactory.getLog(getClass());

	private static ServletContext	sc;

	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		sc = event.getServletContext();

		WebApplicationContext context = (WebApplicationContext) sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		Properties properties = (Properties) context.getBean("settings");

		String sArg = properties.getProperty("log.level");
		logger.warn("log level changed to " + sArg + " at [com.code.spring.context.SpringApplicationContext]");
		LogManager.getRootLogger().setLevel(Level.toLevel(sArg));

		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.error("error");
	}

	public static Object getBean(String name) {
		Enumeration<String> names = sc.getAttributeNames();
		if (names != null) {
			while (names.hasMoreElements()) {
				String name_ = names.nextElement();
				if (name_.startsWith(FrameworkServlet.SERVLET_CONTEXT_PREFIX)) {
					return ((XmlWebApplicationContext) sc.getAttribute(name_)).getBean(name);
				}
			}
		}
		return null;
	}

}

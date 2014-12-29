package com.remote.hession;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.ReflectionUtils;

/**
 * 远程Service扫描器
 * 
 * 
 * 客户端
 * 
 * <pre>
 * <b>自动扫描并加载远程服务，</b>
 * <ul>
 * 	<li>数据库相关的远程服务必须依Service结尾</li>
 *  <li>非数据库的远程服务依Srvc</li>
 * </ul>
 * </pre>
 * 
 */

public class RemoteServiceScanner extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryPostProcessor, ApplicationContextAware {

	private static Log log = LogFactory.getLog(RemoteServiceScanner.class);

	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	private static final String PACKAGE_SEPARATER = ".";
	private static final String root = "com.code";
	private static final String domain = ".soa.url";
	private String[] scanPackages;
	private String[] remoteServiceSuffix;
	private String[] excludeRemoteServiceSuffix;
	private String[] defaultServiceSuffix = new String[] {"Service","Srvc"};
	private String soaUrl;
	private String soaNosqlUrl;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	private DefaultListableBeanFactory beanFactory;

	private boolean pakageFilter(Package pakage) {
		if(scanPackages == null)
			return true;
		for (String package_ : scanPackages) {
			if(pakage.getName().startsWith(package_))
				return true;
		}
		return false;
	}
	
	private boolean canAutowired(String fieldName) {
		if(excludeRemoteServiceSuffix!=null) {
			for(String suffix : excludeRemoteServiceSuffix) {
				if(suffix.trim().length()==0)
					break;
				if(fieldName.endsWith(suffix))
					return false;
			}
		}
		
		if(remoteServiceSuffix != null) {
			for(String suffix : remoteServiceSuffix) {
				if(fieldName.endsWith(suffix))
					return true;
			}
		}
		
		for(String suffix : defaultServiceSuffix) {
			if(fieldName.endsWith(suffix))
				return true;
		}
			
		return false;
	}
	
	@Override
	public boolean postProcessAfterInstantiation(final Object bean, String beanName) throws BeansException {
		if(!pakageFilter(bean.getClass().getPackage()))
			return true;
		ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				Autowired cfg = field.getAnnotation(Autowired.class);
				if (cfg != null && canAutowired(field.getName())) {

					//检查指定的bean name 是否在容器中已经存在，如果存在，不进行覆盖
					boolean beanExists = beanFactory.isBeanNameInUse(field.getName());
					if (beanExists){
						return;
					}

					//按优安java规范，将接口类转换成实现类
					String beanClassName = field.getType().getCanonicalName();
					String simpleClassName = beanClassName.replace(root + PACKAGE_SEPARATER, "");
					String beanName = null;
					if (simpleClassName.contains(PACKAGE_SEPARATER)) {
						beanName = simpleClassName.replace(PACKAGE_SEPARATER, "/");
					} else {
						beanName = simpleClassName;
					}

					String[] parts = beanName.split("/");
					String serviceName = parts[parts.length - 1];
					if (serviceName.startsWith("I")) {
						serviceName = serviceName.substring(1, serviceName.length());
					}
					parts[parts.length - 1] = serviceName;
					
					//组装远程服务地址
					String holderSOAURL = parserSOAURL(field);
					if(holderSOAURL == null) {
						if(field.getName().endsWith("Srvc")){
							holderSOAURL = soaNosqlUrl;
						}else{
							holderSOAURL = soaUrl;;
						}
					}
					serviceName = holderSOAURL + "/" + org.apache.commons.lang.StringUtils.join(parts, "/");

					log.info("Mapping instance " + field.getName() + " of class " + beanClassName + " at " + serviceName);

					GenericBeanDefinition bd = new GenericBeanDefinition();
					bd.setBeanClassName(HessianProxyFactoryBean.class.getName());
					bd.setBeanClass(HessianProxyFactoryBean.class);
					bd.getPropertyValues().add("serviceInterface", beanClassName);
					bd.getPropertyValues().add("serviceUrl", serviceName);
					bd.getPropertyValues().add("overloadEnabled", true);
					
					Reger scanner = new Reger((BeanDefinitionRegistry) beanFactory);
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(bd, field.getName());
					scanner.registerBeanDefinition(definitionHolder);

				}
			}
		});
		return true;
	}


	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	/**
	 * 系统未拆分之前采用这个方法构建代理服务，系统拆分完成之后，此方法需要重新调整
	 */
	@Deprecated
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory) beanFactory;
		this.soaUrl = (String) ((java.util.Properties) beanFactory.getBean("settings")).get("soa.url");
		this.soaNosqlUrl = (String) ((java.util.Properties) beanFactory.getBean("settings")).get("soa.nosql.url");
	}
	
	/**
	 * 系统拆分后，根据成员变量的包路径构建代理服务
	 * 例：com.code.account.service包，构建服务路径为
	 * http://${account.soa.url}/xxx.依account为子服务，后缀.sao.url(account.soa.url),在配置文件中查找
	 * @param field
	 * @return
	 */
	private String parserSOAURL(Field field) {
		String system = field.getGenericType().getTypeName();
		system = system.substring(10,system.length());
		system = system.substring(0, system.indexOf("."));
		return (String) ((java.util.Properties) beanFactory.getBean("settings")).get(system+domain);
	}
	
	private class Reger extends ClassPathBeanDefinitionScanner {

		private BeanDefinitionRegistry registry;

		public Reger(BeanDefinitionRegistry registry) {
			super(registry);
			this.registry = registry;
		}

		public void registerBeanDefinition(BeanDefinitionHolder definitionHolder) {
			registerBeanDefinition(definitionHolder, registry);
		}
	}

	public String[] getRemoteServiceSuffix() {
		return remoteServiceSuffix;
	}

	public void setRemoteServiceSuffix(String[] remoteServiceSuffix) {
		this.remoteServiceSuffix = remoteServiceSuffix;
	}

	public String[] getExcludeRemoteServiceSuffix() {
		return excludeRemoteServiceSuffix;
	}

	public void setExcludeRemoteServiceSuffix(String[] excludeRemoteServiceSuffix) {
		this.excludeRemoteServiceSuffix = excludeRemoteServiceSuffix;
	}
	
	
}

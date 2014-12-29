package com.remote.hession;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.stereotype.Service;

/**
 * HessianService扫描器
 * 
 * 
 * 服务端
 * 
 * <pre>
 * <b>自动扫描并加载HessianService</b>
 * </pre>
 * 
 * 
 */
public class HessianServiceScanner implements BeanFactoryPostProcessor, ApplicationContextAware {

	private static Log log = LogFactory.getLog(HessianServiceScanner.class);

	private static final String PACKAGE_SEPARATER = ".";
	private ApplicationContext applicationContext;
	private String root;
	private String[] scanPackage;
	private Set<String> excludeClass;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
		scanner.setResourceLoader(this.applicationContext);

		//scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
		scanner.scan(scanPackage);

	}

	private final class HessianRpcBeanNameGenerator extends AnnotationBeanNameGenerator {

		protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {

			AnnotationMetadata amd = annotatedDef.getMetadata();
			Set<String> types = amd.getAnnotationTypes();
			String beanName = null;
			for (String type : types) {

				if (type.equals(Service.class.getName()) || type.equals(HessianService.class.getName()) ) {

					String beanClassName = annotatedDef.getBeanClassName();
					String simpleClassName = beanClassName.replace(root + PACKAGE_SEPARATER, "");
					if (simpleClassName.contains(PACKAGE_SEPARATER)) {
						beanName = simpleClassName.replace(PACKAGE_SEPARATER, "/");
					} else {
						beanName = simpleClassName;
					}
				}
			}

			return beanName;
		}

	}

	private final class Scanner extends ClassPathBeanDefinitionScanner {

		private BeanNameGenerator exporterBeanNameGenerator = new HessianRpcBeanNameGenerator();
		private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

		private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
		private BeanDefinitionRegistry registry;

		public Scanner(BeanDefinitionRegistry registry) {
			super(registry);
			this.registry = registry;
		}

		private boolean excludeClassFiler(String beanClassName) {
			if(excludeClass == null || excludeClass.isEmpty())
				return false;
			
			return excludeClass.contains(beanClassName); 
		}
		
		@Override
		protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
			Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
			for (String basePackage : basePackages) {
				Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
				for (BeanDefinition candidate : candidates) {
					
					//排除特定的服务，不进行暴露
					if(excludeClassFiler(candidate.getBeanClassName())) {
						log.warn("Exclude service expose for class "+candidate.getBeanClassName());
						continue;
					}
					
					ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
					candidate.setScope(scopeMetadata.getScopeName());
					String originalBeanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
					String beanName = this.exporterBeanNameGenerator.generateBeanName(candidate, this.registry);
					
					ScannedGenericBeanDefinition bd = (ScannedGenericBeanDefinition) candidate;
					bd.setBeanClassName(HessianServiceExporter.class.getName());
					bd.setBeanClass(HessianServiceExporter.class);
					bd.getPropertyValues().add("service", applicationContext.getBean(originalBeanName));
					String[] interfaces = bd.getMetadata().getInterfaceNames();
					if (interfaces == null || interfaces.length == 0)
						continue;
					Class<?> interf = null;
					try {
						interf = Class.forName(interfaces[0]);
					} catch (ClassNotFoundException e) {
						continue;
					}
					bd.getPropertyValues().add("serviceInterface", interf);
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, "/" + beanName);
					definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
					beanDefinitions.add(definitionHolder);
					registerBeanDefinition(definitionHolder, this.registry);
				}
			}
			if (beanDefinitions.isEmpty()) {
				log.debug("not service be scaned");
			} else {
				for (BeanDefinitionHolder holder : beanDefinitions) {
					AnnotatedBeanDefinition definition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
					log.debug(holder.getBeanName());
					log.debug(applicationContext.getBean(holder.getBeanName()));
					log.debug(definition.getMetadata().getAnnotationTypes());
				}
			}

			return beanDefinitions;

		}

		@Override
		protected void registerDefaultFilters() {

			addIncludeFilter(new AnnotationTypeFilter(Service.class));
		}

		BeanDefinitionHolder applyScopedProxyMode(ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {

			ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
			if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
				return definition;
			}
			boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
			return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
		}

	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String[] getScanPackage() {
		return scanPackage;
	}

	public void setScanPackage(String[] scanPackage) {
		this.scanPackage = scanPackage;
	}

	public Set<String> getExcludeClass() {
		return excludeClass;
	}

	public void setExcludeClass(Set<String> excludeClass) {
		this.excludeClass = excludeClass;
	}
}

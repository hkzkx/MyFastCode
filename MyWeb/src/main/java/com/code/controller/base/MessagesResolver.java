package com.code.controller.base;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class MessagesResolver implements BeanFactoryPostProcessor {

	private ReloadableResourceBundleMessageSource messageSource;

	private String messageRootPath;

	public ReloadableResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessageRootPath() {
		return messageRootPath;
	}

	public void setMessageRootPath(String messageRootPath) {
		this.messageRootPath = messageRootPath;
	}

	private void listFiles(File dir, List<String> basenames) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String path = file.getAbsolutePath();	
				path = "file:"+path;
				basenames.add(path.split("\\.")[0]);
			}
			else
				listFiles(file, basenames);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		URL root = this.getClass().getResource(messageRootPath);
		if(root == null)
			return;
		
		File dir = new File(root.getFile());
		List<String> basenames = new ArrayList<String>();
		listFiles(dir, basenames);

		String[] names = new String[0];
		messageSource.setBasenames(basenames.toArray(names));

	}

}

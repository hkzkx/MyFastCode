package my.CodeGenerater;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MyGenrater {
	static Configuration configuration = null;
	static {
		configuration = new Configuration();
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setTemplateLoader(new ClassTemplateLoader(MyGenrater.class, ""));
	}

	enum BoType {
		iservice, 
		dto, 
		dao, 
		service, 
		mapper, 
		controller,
		messages,
		form;
	}

	private static void generate(Project project, BoType boType, String suffix) throws IOException,
			TemplateException {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(boType.name(), project.getBo());

		Template iserviceTemplate = configuration.getTemplate(boType.name() + ".ftl");
		StringWriter writer = new StringWriter();

		iserviceTemplate.process(context, writer);
		String content = writer.toString();

		new CodeWriter(project).write(content, suffix);
	}

	private static void generate(Project project, BoType boType) throws IOException,
			TemplateException {
		generate(project, boType, null);
	}

	class RemoteGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.iservice);
		}

	}

	class DtoGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.dto);
		}

	}

	class DaoGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.dao);
		}

	}

	class ServiceGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.service);
		}

	}

	class MapperGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.mapper, ".xml");
		}

	}

	class AppGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.controller);
		}

	}
	
	class FormGenerater implements Generater {
		private Project project;

		public Generater getProject(Project project) {
			this.project = project;

			return this;
		}

		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.form, ".ftl");
		}

	}
	class MessageGenerater implements Generater {
		private Project project;
		
		public Generater getProject(Project project) {
			this.project = project;
			
			return this;
		}
		
		@Override
		public void genarate() throws IOException, TemplateException {
			MyGenrater.generate(project, BoType.messages, ".properties");
		}
		
	}
}

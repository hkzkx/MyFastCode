package my.CodeGenerater;

import java.io.IOException;

import freemarker.template.TemplateException;

public interface Generater {
	public void genarate() throws IOException, TemplateException;
}

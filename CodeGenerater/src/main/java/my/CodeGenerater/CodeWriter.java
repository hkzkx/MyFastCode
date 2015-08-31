package my.CodeGenerater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
	private static String default_suffix = ".java";
	private Project project;

	public CodeWriter(Project project) {
		this.project = project;
	}

	public void write(String content, String suffix) throws IOException {
		String pakage = project.getBo().getPackage_();
		String fileName = project.getBo().getClassName();

		String absPath = project.getPath() + pakage.replace(".", "/");
		File dir = new File(absPath);
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(absPath + "/" + fileName + (suffix == null ? default_suffix : suffix));
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.flush();
		fw.close();
	}

	public void write(String content) throws IOException {
		write(content, null);
	}
}

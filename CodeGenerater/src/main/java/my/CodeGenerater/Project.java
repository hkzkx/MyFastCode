package my.CodeGenerater;

import com.my.bo.Bo;

public class Project {

	private String path;
	private String name;
	private Bo bo;

	public Bo getBo() {
		return bo;
	}

	public void setBo(Bo bo) {
		this.bo = bo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Project getProject(String path, Bo bo) {
		Project project = new Project();
		project.setPath(path);
		project.setBo(bo);
		return project;
	}
}

package com.code.supports;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class UploadFileInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2503424784036903370L;

	// 文件名,用于存储在数据中
	private String				path;

	// 原始文件名
	private String				orgName;

	// html 中的field
	private String				field;

	private String				msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

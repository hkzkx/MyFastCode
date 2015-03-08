package com.mmk.supports;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class UploadMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int status;
	
	private String message;
	
	private List<UploadFileInfo> uploadFiles;

	public int getStatus() {
		return status;
	}

	/**
	 * 0 成功
	 * 1 失败
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<UploadFileInfo> getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(List<UploadFileInfo> uploadFiles) {
		this.uploadFiles = uploadFiles;
	}
	
}

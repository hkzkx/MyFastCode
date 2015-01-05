package com.mmb.common;

public class MessageBean implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6491288122912189087L;

	public MessageBean(Boolean success,Object msg) {
		this.success = success;
		this.msg = msg;
	}
	
	private Boolean success;
	private Object msg;
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
	
	
	
}

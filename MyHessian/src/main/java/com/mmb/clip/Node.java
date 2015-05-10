package com.mmb.clip;

public class Node implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5240824887130870010L;

	private String host;
	private int delay;
	/**
	 * 当前节点状态<br/>
	 * 1：正常服务<br/>
	 * 2：节点存活，但暂时不接受新的请求或暂时不对外服务<br/>
	 */
	private int status;
	private String remark;

	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	

}

package com.code.clip;

public class ServiceStub {
	/**
	 * 服务名称：如com.code.service.ISiteUserService,为服务的接口名称
	 */
	private String serviceName;

	/**
	 * 服务地址， 如：/service/SiteUserService
	 */
	private String serviceUri;

	/**
	 * 服务权重
	 */
	private int weight;

	/**
	 * 当前服务状态<br/>
	 * 1：正常服务<br/>
	 * 2：降半权<br/>
	 * 3：服务下线<br/>
	 * 4：服务存活，但不接收请求
	 */
	private int status;

	private Node node;

	private boolean invoked = false;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getServiceUri() {
		return serviceUri;
	}

	public void setServiceUri(String serviceUri) {
		this.serviceUri = serviceUri;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return this.node;
	}

	public boolean isInvoked() {
		return invoked;
	}

	public void setInvoked(boolean invoked) {
		this.invoked = invoked;
	}

	@Override
	public String toString() {
		return "ServiceStub [serviceName=" + serviceName + ", serviceUri=" + serviceUri + ", weight=" + weight + ", status=" + status + ", node="
				+ node + ", invoked=" + invoked + "]";
	}

	

}

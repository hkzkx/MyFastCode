package com.mmb.clip;

import com.code.utils.JsonUtil;

public class Describer implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7654402585908952947L;

	private Type type;
	private ServiceStub stub;
	private Node node;
	private boolean invoked = false;
	
	
	public boolean isInvoked() {
		return invoked;
	}


	public void setInvoked(boolean invoked) {
		this.invoked = invoked;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	public ServiceStub getStub() {
		return stub;
	}


	public void setStub(ServiceStub stub) {
		this.stub = stub;
	}


	@Override
	public String toString() {
		return JsonUtil.java2json(this);
	}


	public Node getNode() {
		return node;
	}


	public void setNode(Node node) {
		this.node = node;
	}
	
	
	
}

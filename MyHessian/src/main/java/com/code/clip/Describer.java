package com.code.clip;


public class Describer implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7654402585908952947L;

	private Type type;
	private ServiceStub stub;
	private Node node;

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
		return "Describer [type=" + type + ", stub=" + stub + ", node=" + node + "]";
	}


	public Node getNode() {
		return node;
	}


	public void setNode(Node node) {
		this.node = node;
	}
	
	
	
}

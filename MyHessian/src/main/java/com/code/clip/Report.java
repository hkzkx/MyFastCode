package com.code.clip;

import java.util.List;

import com.code.utils.JsonUtil;

public class Report implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7654402585908952947L;

	private Type type;
	private List<KV> list;
	private List<ServiceStub> stubs;
	private Node node;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}


	public List<KV> getList() {
		return list;
	}

	public void setList(List<KV> list) {
		this.list = list;
	}

	public List<ServiceStub> getStubs() {
		return stubs;
	}

	public void setStubs(List<ServiceStub> stubs) {
		this.stubs = stubs;
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

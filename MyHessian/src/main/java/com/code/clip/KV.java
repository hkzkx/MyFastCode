package com.code.clip;

import java.io.Serializable;

public class KV implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -708233927149257293L;

	public Object				k;

	public Object				v;

	public Object				attach;

	public Object getK() {
		return k;
	}

	public void setK(Object k) {
		this.k = k;
	}

	public Object getV() {
		return v;
	}

	public void setV(Object v) {
		this.v = v;
	}

	public Object getAttach() {
		return attach;
	}

	public void setAttach(Object attach) {
		this.attach = attach;
	}

}

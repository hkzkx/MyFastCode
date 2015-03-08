package com.code.common;

import java.io.Serializable;
import java.util.List;

public class RemotePage implements Serializable {

	private static final long serialVersionUID = 5756345873158652812L;

	private List<?> data;

	private YnPageInfo page;

	public RemotePage(List<?> data, YnPageInfo page) {
		this.data = data;
		this.page = page;
	}

	public RemotePage() {

	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public YnPageInfo getPage() {
		return page;
	}

	public void setPage(YnPageInfo page) {
		this.page = page;
	}

}

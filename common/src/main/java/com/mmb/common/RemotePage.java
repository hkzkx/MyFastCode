package com.mmb.common;

import java.io.Serializable;
import java.util.List;

public class RemotePage implements Serializable {

	private static final long serialVersionUID = 5756345873158652812L;

	private List<?> data;

	private PageInfo page;

	public RemotePage(List<?> data, PageInfo page) {
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

	public PageInfo getPage() {
		return page;
	}

	public void setPage(PageInfo page) {
		this.page = page;
	}

}

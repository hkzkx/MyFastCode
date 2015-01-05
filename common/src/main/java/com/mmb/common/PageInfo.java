package com.mmb.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import org.apache.ibatis.session.RowBounds;

public class PageInfo implements Serializable {
	private static final long serialVersionUID = -6830864636737268480L;
	
	private static final Integer DEAFULT_PAGE_SIZE = 10; //每页默认记录数
	private static final Integer DEAFULT_PAGENO_STEP = 3; //默认页码步长长度
	
	private Integer pageNoStep; //页码步长长度
	private Integer reqeustPage; //请求页号，临时变量
	private Integer recordCount; //记录总数
	private Integer pageSize; //每页记录数
	private Integer pageNum; //页数
	private Integer curPage; //当前页号，从1开始

	private Integer showPreNo; //ui显示：上一页
	private Integer showNextNo; //ui显示：下一页
	private Integer showFirstNo; //ui显示：第一页
	private Integer showLastNo; //ui显示：最后一页
	private List<Integer> showPageNoList; //页码列表，如"1, 2, 3, 4, 5"
	
	public PageInfo() {
		super();
		initPageInfo(0, DEAFULT_PAGE_SIZE, DEAFULT_PAGENO_STEP);
	}
	public PageInfo(Integer reqeustPage) {
		initPageInfo(reqeustPage, DEAFULT_PAGE_SIZE, DEAFULT_PAGENO_STEP);
	}
	public PageInfo(Integer reqeustPage, Integer pageSize) {
		initPageInfo(reqeustPage, pageSize, DEAFULT_PAGENO_STEP);
	}
	public PageInfo(Integer reqeustPage, Integer pageSize, Integer pageNoStep) {
		initPageInfo(reqeustPage, pageSize, pageNoStep);
	}
	
	public void setRecordCount(Integer recordCount) {
		if(recordCount != null){
			this.recordCount = recordCount;
			pageNum = (recordCount + pageSize - 1) / pageSize;
			jumpPage(reqeustPage);
		}else{
			this.recordCount = 0;
			pageNum = 1;
			jumpPage(0);
		}
	}
	/*public RowBounds getPageInfo() {
		int offset = (curPage - 1) * pageSize;
		if(offset < 0) {
			offset = 0;
		}
		RowBounds pageInfo = new RowBounds(offset, pageSize);
		return pageInfo;
	}*/
	
	public Integer getShowPreNo() {
		return showPreNo;
	}
	public Integer getShowNextNo() {
		return showNextNo;
	}
	public Integer getShowFirstNo() {
		return showFirstNo;
	}
	public Integer getShowLastNo() {
		return showLastNo;
	}
	public List<Integer> getShowPageNoList() { //获取页码列表
		return showPageNoList;
	}
	
	public Integer getRecordCount() {
		return recordCount;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public Integer getPageNum() {
		return pageNum;
	}
	public Integer getCurPage() {
		return curPage;
	}

	public void jumpPage(Integer curPage) { //跳转页
		curPage = curPage==null?0:curPage;
		if(curPage < 1) {
			curPage = 1;
		} else if (curPage > pageNum&&pageNum>0) {
			curPage = pageNum;
		}
		this.curPage = curPage;
		setPageNavigation();
	}
	
	private void initPageInfo(Integer reqeustPage, Integer pageSize, Integer pageNoStep) {
		this.reqeustPage = reqeustPage;
		this.pageSize = pageSize;
		this.pageNoStep = pageNoStep;
	}
	
	private void setPageNavigation() {
		if(curPage <= 1) { //上一页
			showPreNo = null;
		} else {
			showPreNo = curPage - 1;
		}
		
		if(curPage >= pageNum) { //下一页
			showNextNo = null;
		} else {
			showNextNo = curPage + 1;
		}
		
		if(pageNum > 0) { //首尾页
			showFirstNo = 1;
			showLastNo = pageNum;
		} else {
			showFirstNo = null;
			showLastNo = null;
		}
		
		showPageNoList = new ArrayList<Integer>();
		for(int i = pageNoStep; i > 0; i--) { //左边导航
			if(curPage - i > 1) {
				showPageNoList.add(curPage - i);
			}
		}
		
		if(curPage > 1 && curPage < pageNum) { //当前页
			showPageNoList.add(curPage);
		}
		
		for(int i = 1; i < pageNoStep; i++) { //右边导航
			if(curPage + i < pageNum) {
				showPageNoList.add(curPage + i);
			}
		}
	}
	public Integer getReqeustPage() {
		return reqeustPage;
	}
	public void setReqeustPage(Integer reqeustPage) {
		this.reqeustPage = reqeustPage;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	
	
}

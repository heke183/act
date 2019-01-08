package com.xianglin.act.common.dal.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageReq {

	private int startPage = 0;  //起始索引
	
	private int curPage = 1;// 当前页

	private int pageSize = 100; // 每页多少行

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getPageSize() {

		return pageSize;
	}

	public void setPageSize(int pageSize) {

		this.pageSize = pageSize;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
		this.startPage = (curPage -1) * this.getPageSize();
	}

}

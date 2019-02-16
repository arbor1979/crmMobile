package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.List;


public class Page implements Serializable{
	private int pagecurr = 1;
	private int pagesize = 20;
	private int totalcount;
	private List resultlist;
	
	public Page(){}

	public int getPagecurr() {
		return pagecurr;
	}

	public void setPagecurr(int pagecurr) {
		this.pagecurr = pagecurr;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public List getResultlist() {
		return resultlist;
	}

	public void setResultlist(List resultlist) {
		this.resultlist = resultlist;
	}
	
}

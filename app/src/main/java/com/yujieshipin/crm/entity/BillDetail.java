package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 成绩详情
 * 
 *  <br/>创建说明: 2014-4-17 下午5:07:31 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class BillDetail {
	private String title;
	private String opertionType;
	private String opertype;
	private String rightbottomBtn;
	private String newUrl;
	private List<BillDetailItem> items;
	private String huizong1;
	private String huizong2;
	private boolean shouzhekouall;
	private JSONObject allColor;
	public String getOpertionType() {
		return opertionType;
	}




	public void setOpertionType(String opertionType) {
		this.opertionType = opertionType;
	}




	public String getNewUrl() {
		return newUrl;
	}




	public void setNewUrl(String newUrl) {
		this.newUrl = newUrl;
	}


	public String getOpertype() {
		return opertype;
	}

	public String getRightbottomBtn() {
		return rightbottomBtn;
	}

	public boolean isShouzhekouall() {
		return shouzhekouall;
	}

	public JSONObject getAllColor() {
		return allColor;
	}

	public BillDetail(JSONObject jo) {
		title = jo.optString("标题显示");
		opertionType=jo.optString("操作类型");
		opertype=jo.optString("opertype");
		rightbottomBtn=jo.optString("右下按钮");
		newUrl=jo.optString("新增URL");
		items = new ArrayList<BillDetailItem>();
		JSONArray joa = jo.optJSONArray("单据明细");
		for (int i = 0; i < joa.length(); i++) {
			BillDetailItem a = new BillDetailItem(joa.optJSONObject(i));
			items.add(a);
		}
		huizong1= jo.optString("汇总1");
		huizong2= jo.optString("汇总2");
		shouzhekouall=jo.optBoolean("showzhekouall");
		allColor=jo.optJSONObject("allcolor");
	}



	public String getHuizong1() {
		return huizong1;
	}




	public void setHuizong1(String huizong1) {
		this.huizong1 = huizong1;
	}




	public String getHuizong2() {
		return huizong2;
	}




	public void setHuizong2(String huizong2) {
		this.huizong2 = huizong2;
	}




	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public List<BillDetailItem> getItems() {
		return items;
	}




	public void setProducts(List<BillDetailItem> products) {
		this.items = products;
	}


	
}

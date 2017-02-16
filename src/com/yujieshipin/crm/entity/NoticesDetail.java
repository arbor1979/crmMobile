package com.yujieshipin.crm.entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class NoticesDetail {
	private String title;
	private String time;
	private String imageUrl;
	private String content;
	private JSONArray fujian;
	private JSONArray tupian;
	private String rightBtn;
	private String rightBtnUrl;
	private String newWindowTitle;
	public NoticesDetail(JSONObject jo) {
		this.title = jo.optString("标题");
		this.time = jo.optString("第二行");
		this.imageUrl = jo.optString("第二行图片区URL");
		this.content = jo.optString("通知内容");
		fujian=jo.optJSONArray("附件");
		tupian=jo.optJSONArray("图片数组");
		rightBtn=jo.optString("右上按钮");
		rightBtnUrl=jo.optString("右上按钮URL");
		newWindowTitle=jo.optString("新窗口标题");
	}

	public String getNewWindowTitle() {
		return newWindowTitle;
	}

	public void setNewWindowTitle(String newWindowTitle) {
		this.newWindowTitle = newWindowTitle;
	}

	public String getRightBtn() {
		return rightBtn;
	}

	public void setRightBtn(String rightBtn) {
		this.rightBtn = rightBtn;
	}

	public String getRightBtnUrl() {
		return rightBtnUrl;
	}

	public void setRightBtnUrl(String rightBtnUrl) {
		this.rightBtnUrl = rightBtnUrl;
	}

	public JSONArray getTupian() {
		return tupian;
	}

	public void setTupian(JSONArray tupian) {
		this.tupian = tupian;
	}

	public JSONArray getFujian() {
		return fujian;
	}

	public void setFujian(JSONArray fujian) {
		this.fujian = fujian;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

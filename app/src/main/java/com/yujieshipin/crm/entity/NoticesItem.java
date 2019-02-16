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
 *  功能说明: 校内通知
 * 
 *  <br/>创建说明: 2014-4-16 下午2:05:53 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class NoticesItem {
	private String TemplateName;
	private String title;
	private List<Notice> notices;
	
	public NoticesItem() {
		super();
	}
	
	public NoticesItem(JSONObject jo) {
		TemplateName = jo.optString("适用模板");
		title = jo.optString("标题显示");
		JSONArray joArr = jo.optJSONArray("通知项");
		notices = getNotices(joArr);
	}

	private List<Notice> getNotices(JSONArray joArr) {
		List<Notice> notices = new ArrayList<Notice>();
		for (int i = 0; i < joArr.length(); i++) {
			Notice notice = new Notice(joArr.optJSONObject(i));
			notices.add(notice);
		}
		return notices;
	}
	

	public String getTemplateName() {
		return TemplateName;
	}

	public void setTemplateName(String templateName) {
		TemplateName = templateName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Notice> getNotices() {
		return notices;
	}

	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}

}

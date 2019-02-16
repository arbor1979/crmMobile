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
 *  功能说明: 调查问卷
 * 
 *  <br/>创建说明: 2014-4-16 下午6:55:15 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class QuestionnaireItem {
	private String templateName;
	private String title;
	private List<Question> questions;
	public QuestionnaireItem(JSONObject jo){
		templateName=jo.optString("适用模板");
		title=jo.optString("标题显示");
		questions=new ArrayList<Question>();
		JSONArray joq=jo.optJSONArray("调查问卷数值");
		for (int i = 0; i < joq.length(); i++) {
			Question q=new Question(joq.optJSONObject(i));
			questions.add(q);
		}
	}
	public class Question {
		private String id;
		private String icon;
		private String title;
		private String status;
		private String date;
		private String detailUrl;
		private String autoClose;
		public String getAutoClose() {
			return autoClose;
		}

		public void setAutoClose(String autoClose) {
			this.autoClose = autoClose;
		}

		public Question(JSONObject jo) {
			id = jo.optString("编号");
			icon = jo.optString("图标");
			title = jo.optString("第一行");
			status = jo.optString("第二行之状态");
			date = jo.optString("第二行之日期");
			detailUrl = jo.optString("内容项URL");
			autoClose=jo.optString("保存后关闭");
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getDetailUrl() {
			return detailUrl;
		}

		public void setDetailUrl(String detailUrl) {
			this.detailUrl = detailUrl;
		}
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}

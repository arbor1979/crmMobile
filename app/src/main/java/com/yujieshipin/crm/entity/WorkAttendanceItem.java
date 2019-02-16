package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 校内考勤
 * 
 * <br/>
 * 创建说明: 2014-4-16 下午2:06:15 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class WorkAttendanceItem {
	private String selectShortCutType[] = { "最近一周", "最近一月" };
	private String selectByWeekType[] = { "开始周", "结束周" };

	private String templateName;
	private String userPic;
	private String userName;
	private String sno;// 学号
	private String sClass;// 班级
	private List<WorkAttendance> WorkAttendances;
	private List<SelectShortCut> SelectShortCuts;
	private List<SelectByWeek> SelectByWeeks;
	
	@SuppressWarnings("unchecked")
	public WorkAttendanceItem(JSONObject jo) {
		templateName = jo.optString("适用模板");
		userPic = jo.optString("用户头像");
		userName = jo.optString("用户姓名");
		sno = jo.optString("用户姓名下第一行");// 学号
		sClass = jo.optString("用户姓名下第二行");// 班级
		String order=jo.optString("顺序");
		String[] keyOrder=order.split(",");
		WorkAttendances = new ArrayList<WorkAttendance>();
		SelectShortCuts = new ArrayList<SelectShortCut>();
		SelectByWeeks = new ArrayList<SelectByWeek>();
		JSONObject jowa = jo.optJSONObject("考勤数值");
		
		for(int i=0;i<keyOrder.length;i++)
		{
			WorkAttendance wa = new WorkAttendance(jowa.optJSONObject(keyOrder[i]));
			WorkAttendances.add(wa);
		}
		
		JSONObject jossc = jo.optJSONObject("快捷查询");
		for (int i = 0; i < 2; i++) {
			JSONObject josscitem = jossc.optJSONObject(selectShortCutType[i]);
			SelectShortCut ssc = new SelectShortCut(josscitem);
			SelectShortCuts.add(ssc);
		}
		JSONObject josbw = jo.optJSONObject("按周查询");
		for (int i = 0; i < 2; i++) {
			JSONObject josbwitem = josbw.optJSONObject(selectByWeekType[i]);
			SelectByWeek sbw = new SelectByWeek(josbwitem);
			SelectByWeeks.add(sbw);
		}
	}

	/**
	 * 考勤
	 */
	public class WorkAttendance {
		private String value;// 数量
		private String name;// 名称
		private String background;// 背景图片地址
		private String icon;// 图标地址
		private String contentUrl;// 内容项地址

		public WorkAttendance(JSONObject jo) {
			value = jo.optString("值");// 数量
			name = jo.optString("名称");// 名称
			background = jo.optString("图片背景");
			icon = jo.optString("考勤图标");
			contentUrl = jo.optString("内容项URL");
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


		public String getBackground() {
			return background;
		}

		public void setBackground(String background) {
			this.background = background;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getContentUrl() {
			return contentUrl;
		}

		public void setContentUrl(String contentUrl) {
			this.contentUrl = contentUrl;
		}
	}

	/**
	 * 快捷查询
	 */
	public class SelectShortCut {
		private String name;
		private String contentUrl;// 内容项地址

		public SelectShortCut(JSONObject jo) {
			this.name = jo.optString("名称");
			this.contentUrl = jo.optString("内容项URL");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContentUrl() {
			return contentUrl;
		}

		public void setContentUrl(String contentUrl) {
			this.contentUrl = contentUrl;
		}

	}

	/**
	 * 按周查询
	 */
	public class SelectByWeek {
		private String name;
		private String defaultValue;// 学号
		private String value;
		private String contentUrl;// 内容项地址

		public SelectByWeek(JSONObject jo) {
			name = jo.optString("名称");
			defaultValue = jo.optString("默认");
			value = jo.optString("值");
			contentUrl = jo.optString("内容项URL");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getContentUrl() {
			return contentUrl;
		}

		public void setContentUrl(String contentUrl) {
			this.contentUrl = contentUrl;
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUserPic() {
		return userPic;
	}

	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	public String getsClass() {
		return sClass;
	}

	public void setsClass(String sClass) {
		this.sClass = sClass;
	}

	public List<WorkAttendance> getWorkAttendances() {
		return WorkAttendances;
	}

	public void setWorkAttendances(List<WorkAttendance> workAttendances) {
		WorkAttendances = workAttendances;
	}

	public List<SelectShortCut> getSelectShortCuts() {
		return SelectShortCuts;
	}

	public void setSelectShortCuts(List<SelectShortCut> selectShortCuts) {
		SelectShortCuts = selectShortCuts;
	}

	public List<SelectByWeek> getSelectByWeeks() {
		return SelectByWeeks;
	}

	public void setSelectByWeeks(List<SelectByWeek> selectByWeeks) {
		SelectByWeeks = selectByWeeks;
	}

}

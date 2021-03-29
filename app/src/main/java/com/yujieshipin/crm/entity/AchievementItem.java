package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 成绩
 * 
 *  <br/>创建说明: 2014-4-16 下午6:41:34 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class AchievementItem {
	
	private String title;
	private List<Achievement> achievements;
	private String rightButton;
	private String rightButtonURL;
	private String huizong;
	private int page;
	private int allnum;
	private JSONArray filterArr;

	public JSONArray getFilterArr() {
		return filterArr;
	}

	public void setFilterArr(JSONArray filterArr) {
		this.filterArr = filterArr;
	}

	public List<String> getFilterParams1() {
		return filterParams1;
	}

	public List<String> getFilterParams2() {
		return filterParams2;
	}

	private List<String> filterParams1;
	private List<String> filterParams2;
	private JSONArray MutiSelArr;
	private JSONArray groupArr;
	private int curGroupId=0;

	public JSONArray getMutiSelArr() {
		return MutiSelArr;
	}


	public JSONArray getGroupArr() {
		return groupArr;
	}

	public int getCurGroup() {
		return curGroupId;
	}

	public AchievementItem(JSONObject jo) {
		
		title = jo.optString("标题显示");
		achievements = new ArrayList<Achievement>();
		JSONArray joa = jo.optJSONArray("成绩数值");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				Achievement a = new Achievement(joa.optJSONObject(i));
				achievements.add(a);
			}
		}
		rightButton=jo.optString("右上按钮");
		rightButtonURL=jo.optString("右上按钮URL");
		huizong=jo.optString("汇总");
		page=jo.optInt("page");
		allnum=jo.optInt("allnum");
		filterParams1=new ArrayList<String>();
		joa=jo.optJSONArray("过滤参数1");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				filterParams1.add(joa.optString(i));
			}
		}
		filterParams2=new ArrayList<String>();
		joa=jo.optJSONArray("过滤参数2");
		if(joa!=null)
		{
			for (int i = 0; i < joa.length(); i++) {
				filterParams2.add(joa.optString(i));
			}
		}
		filterArr=jo.optJSONArray("过滤条件");
		MutiSelArr=jo.optJSONArray("显示复选");
		groupArr=jo.optJSONArray("显示分组");
		if(filterArr==null)
			filterArr=new JSONArray();
		if(MutiSelArr==null)
			MutiSelArr=new JSONArray();
		if(groupArr==null)
			groupArr=new JSONArray();
		curGroupId=jo.optInt("当前分组");
	}

	public int getAllnum() {
		return allnum;
	}

	public void setAllnum(int allnum) {
		this.allnum = allnum;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getHuizong() {
		return huizong;
	}

	public void setHuizong(String huizong) {
		this.huizong = huizong;
	}

	public String getRightButton() {
		return rightButton;
	}

	public void setRightButton(String rightButton) {
		this.rightButton = rightButton;
	}

	public String getRightButtonURL() {
		return rightButtonURL;
	}

	public void setRightButtonURL(String rightButtonURL) {
		this.rightButtonURL = rightButtonURL;
	}

	public class Achievement {
		private String id;// 编号
		private String icon;// 图标
		private String title;// 标题
		private String total;// 总分
		private String rank;// 排名
		private String detailUrl;// 详情地址
		private String thecolor;//总分颜色
		private String templateName;
	    private String templateGrade;
	    private JSONObject extraMenu;
	    private String middle;
	    private String customerId;
		private String headtype;
	    private String thirdline;
	    private boolean ifChecked;

		public int getProgress() {
			return progress;
		}

		public void setProgress(int progress) {
			this.progress = progress;
		}

		private int progress;
		public String getThirdline() {
			return thirdline;
		}

		public void setThirdline(String thirdline) {
			this.thirdline = thirdline;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getTemplateGrade() {
			return templateGrade;
		}

		public void setTemplateGrade(String templateGrade) {
			this.templateGrade = templateGrade;
		}

		public String getHeadtype() {
			return headtype;
		}

		public void setHeadtype(String headtype) {
			this.headtype = headtype;
		}

		public boolean isIfChecked() {
			return ifChecked;
		}

		public void setIfChecked(boolean ifChecked) {
			this.ifChecked = ifChecked;
		}

		public Achievement(JSONObject jo) {
			id = jo.optString("编号");
			icon = jo.optString("图标");
			customerId= jo.optString("客户ID");
			headtype=jo.optString("头像类型");
			title = jo.optString("第一行");
			total = jo.optString("第二行左");
			rank = jo.optString("第二行右");
			detailUrl = jo.optString("内容项URL");
			thecolor=jo.optString("颜色");
			templateName = jo.optString("模板");
			templateGrade = jo.optString("模板级别");
			extraMenu= jo.optJSONObject("附加菜单");
			thirdline = jo.optString("第三行");
			if(jo.optString("进度条").length()>0)
				progress=jo.optInt("进度条");
			else
				progress=-1;
		}

		
		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public String getMiddle() {
			return middle;
		}

		public void setMiddle(String middle) {
			this.middle = middle;
		}

		public JSONObject getExtraMenu() {
			return extraMenu;
		}

		public void setExtraMenu(JSONObject extraMenu) {
			this.extraMenu = extraMenu;
		}

		public String getThecolor() {
			return thecolor;
		}

		public void setThecolor(String thecolor) {
			this.thecolor = thecolor;
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

		public String getTotal() {
			return total;
		}

		public void setTotal(String total) {
			this.total = total;
		}

		public String getRank() {
			return rank;
		}

		public void setRank(String rank) {
			this.rank = rank;
		}

		public String getDetailUrl() {
			return detailUrl;
		}

		public void setDetailUrl(String detailUrl) {
			this.detailUrl = detailUrl;
		}
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}
	
}

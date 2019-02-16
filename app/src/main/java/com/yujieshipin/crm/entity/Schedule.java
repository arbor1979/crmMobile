package com.yujieshipin.crm.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 课表规则
 * 
 * @Title Schedule.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-11-7 下午4:53:59
 * @version V1.0
 * 
 */
@DatabaseTable(tableName = "Schedule")
public class Schedule {
	@DatabaseField
	private String weeks;
	@DatabaseField
	private String sections;
	@DatabaseField
	private String rests;
	@DatabaseField
	private String sectionsTime;
	@DatabaseField
	private String WeekBeginDay;
	@DatabaseField
	private String WeekEndDay;

	public Schedule() {
	}
	
	public Schedule(JSONObject jo) {
//		weeks = getResult(jo, "星期显示");
//		sections = getResult(jo, "节次显示");
//		rests = getResult(jo, "属于休息时间");
		weeks = jo.optString("星期显示");
		sections = jo.optString("节次显示");
		rests = jo.optString("属于休息时间");
		sectionsTime = jo.optString("节次时间");
		WeekBeginDay = jo.optString("周开始日期");
		WeekEndDay = jo.optString("周结束日期");
	}
	
	public Schedule(net.minidev.json.JSONObject jo) {
//		weeks = getResult(jo, "星期显示");
//		sections = getResult(jo, "节次显示");
//		rests = getResult(jo, "属于休息时间");
		weeks = jo.get("星期显示").toString();
		sections = jo.get("节次显示").toString();
		rests = jo.get("属于休息时间").toString();
		sectionsTime = jo.get("节次时间").toString();
		if(jo.get("周开始日期")!=null)
			WeekBeginDay = jo.get("周开始日期").toString();
		if(jo.get("周结束日期")!=null)
			WeekEndDay = jo.get("周结束日期").toString();
	}
	

	public String getWeekBeginDay() {
		return WeekBeginDay;
	}

	public void setWeekBeginDay(String weekBeginDay) {
		WeekBeginDay = weekBeginDay;
	}

	public String getWeekEndDay() {
		return WeekEndDay;
	}

	public void setWeekEndDay(String weekEndDay) {
		WeekEndDay = weekEndDay;
	}

	private String getResult(JSONObject jo, String key) {
		JSONArray ja = jo.optJSONArray(key);
		String[] result = null;
		if (ja != null) {
			result = toStrArray(ja);
		}
		StringBuffer strbuff = new StringBuffer();

		for (int i = 0; i < result.length; i++) {
			strbuff.append(",").append(result[i]);
		}

		String str = strbuff.deleteCharAt(0).toString();
		return str;
	}

	private String[] toStrArray(JSONArray ja) {
		String[] strArray = new String[ja.length()];
		for (int i = 0; i < ja.length(); i++) {
			strArray[i] = ja.optString(i);
		}
		return strArray;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public String getSections() {
		return sections;
	}

	public void setSections(String sections) {
		this.sections = sections;
	}

	public String getRests() {
		return rests;
	}

	public void setRests(String rests) {
		this.rests = rests;
	}

	public String getSectionsTime() {
		return sectionsTime;
	}

	public void setSectionsTime(String sectionsTime) {
		this.sectionsTime = sectionsTime;
	}

}

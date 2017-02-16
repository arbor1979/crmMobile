package com.yujieshipin.crm.entity;

import java.io.Serializable;
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
 *  功能说明: 检测分数
 * 
 *  <br/>创建说明: 2013-11-30 下午4:11:38 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
//@DatabaseTable(tableName = "StudentTestItem")
public class StudentTestItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	@DatabaseField(id = true)
	private String id;
//	@DatabaseField
	private String studentID; //学号
//	@DatabaseField
	private String name; //测验名称
//	@DatabaseField
	private String date; //测验时间
//	@DatabaseField
	private String score; //分数
//	@DatabaseField
	private String avgScore; //平均分
//	@DatabaseField
	private String highestScore; //最高分
	
	public StudentTestItem() {
		
	}

	public StudentTestItem(JSONObject jo) {
		this.studentID = jo.optString("学号");
		this.name = jo.optString("测验名称");
		this.date = jo.optString("测验时间");
		this.score = jo.optString("测验分值");
		this.avgScore = jo.optString("平均分");
		this.highestScore = jo.optString("最高分");
	}
	
	public static List<StudentTestItem> toList(JSONArray ja) {
		List<StudentTestItem> result = new ArrayList<StudentTestItem>();
		StudentTestItem info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentTestItem(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentTestItem数据");
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(String avgScore) {
		this.avgScore = avgScore;
	}

	public String getHighestScore() {
		return highestScore;
	}

	public void setHighestScore(String highestScore) {
		this.highestScore = highestScore;
	}

	
}

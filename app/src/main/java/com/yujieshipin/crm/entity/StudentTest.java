package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 测验统计
 * 
 *  <br/>创建说明: 2013-11-30 下午4:40:06 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName = "StudentTest")
public class StudentTest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String studentID; //学号
	@DatabaseField
	private String scoreTitle; //成绩标题
	@DatabaseField
	private String testItem;
	
//	private List<StudentTestItem> studentTestItemList;
	
	public StudentTest() {

	}

	public StudentTest(JSONObject jo) {
		this.studentID = jo.optString("学号");
		this.scoreTitle = jo.optString("标题");
		this.testItem = jo.optString("数据");
		//studentTestItemList = StudentTestItem.toList(jo.optJSONArray("数据"));
	}
	
	public StudentTest(net.minidev.json.JSONObject jo) {
		this.studentID = String.valueOf(jo.get("学号"));
		this.scoreTitle = String.valueOf(jo.get("标题"));
		this.testItem = String.valueOf(jo.get("数据"));
		//studentTestItemList = StudentTestItem.toList(jo.optJSONArray("数据"));
	}
	
	public static List<StudentTest> toList(JSONArray ja) {
		List<StudentTest> result = new ArrayList<StudentTest>();
		StudentTest info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentTest(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentScore数据");
			return null;
		}
	}
	
	public static List<StudentTest> toList(net.minidev.json.JSONArray ja) {
		List<StudentTest> result = new ArrayList<StudentTest>();
		StudentTest info = null;
		
		if (ja != null && ja.size() > 0) {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				info = new StudentTest(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentScore数据");
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

	public String getScoreTitle() {
		return scoreTitle;
	}

	public void setScoreTitle(String scoreTitle) {
		this.scoreTitle = scoreTitle;
	}

//	public List<StudentTestItem> getStudentTestItemList() {
//		return studentTestItemList;
//	}
//
//	public void setStudentTestItemList(List<StudentTestItem> studentTestItemList) {
//		this.studentTestItemList = studentTestItemList;
//	}

	public String getTestItem() {
		return testItem;
	}

	public void setTestItem(String testItem) {
		this.testItem = testItem;
	}

}

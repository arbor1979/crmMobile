package com.yujieshipin.crm.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = "AttendanceOfStudent")
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 学生考勤统计
 * 
 *  <br/>创建说明: 2013-11-22 下午3:57:30 linrr  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class AttendanceOfStudent {
	@DatabaseField
private String data;//数据
	@DatabaseField
private String title;//标题
	@DatabaseField
private String attendance;//出勤率
	@DatabaseField
	
private String color;//颜色
	public AttendanceOfStudent(){}
	public AttendanceOfStudent(JSONObject jo) {
		data = jo.optString("数据");
		title = jo.optString("标题");
		attendance = jo.optString("出勤率");
		color= jo.optString("颜色");
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAttendance() {
		return attendance;
	}
	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}

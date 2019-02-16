package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 修改考勤信息
 * 
 *  <br/>创建说明: 2013-12-3 下午1:12:48 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName = "StudentSubject")
public class Changekaoqininfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 编号; 
	private String 授课内容;
	private String 作业布置;
	private String 课堂情况;
	private String 课堂纪律;
	private String 教室卫生;
	private String 班级人数;
	private String 实到人数;
	private String 缺勤情况登记;
	private String 填写时间;
	@SuppressWarnings("rawtypes")
	private List<Map> 缺勤情况登记JSON;
	
	public Changekaoqininfo() {
		
	}

	public String get编号() {
		return 编号;
	}

	public void set编号(String 编号) {
		this.编号 = 编号;
	}

	public String get授课内容() {
		return 授课内容;
	}

	public void set授课内容(String 授课内容) {
		this.授课内容 = 授课内容;
	}

	public String get作业布置() {
		return 作业布置;
	}

	public void set作业布置(String 作业布置) {
		this.作业布置 = 作业布置;
	}

	public String get课堂情况() {
		return 课堂情况;
	}

	public void set课堂情况(String 课堂情况) {
		this.课堂情况 = 课堂情况;
	}

	public String get课堂纪律() {
		return 课堂纪律;
	}

	public void set课堂纪律(String 课堂纪律) {
		this.课堂纪律 = 课堂纪律;
	}

	public String get教室卫生() {
		return 教室卫生;
	}

	public void set教室卫生(String 教室卫生) {
		this.教室卫生 = 教室卫生;
	}

	public String get班级人数() {
		return 班级人数;
	}

	public void set班级人数(String 班级人数) {
		this.班级人数 = 班级人数;
	}

	public String get实到人数() {
		return 实到人数;
	}

	public void set实到人数(String 实到人数) {
		this.实到人数 = 实到人数;
	}

	public String get缺勤情况登记() {
		return 缺勤情况登记;
	}

	public void set缺勤情况登记(String 缺勤情况登记) {
		this.缺勤情况登记 = 缺勤情况登记;
	}

	public String get填写时间() {
		return 填写时间;
	}

	public void set填写时间(String 填写时间) {
		this.填写时间 = 填写时间;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> get缺勤情况登记JSON() {
		return 缺勤情况登记JSON;
	}

	@SuppressWarnings("rawtypes")
	public void set缺勤情况登记JSON(List<Map> 缺勤情况登记json) {
		缺勤情况登记JSON = 缺勤情况登记json;
	}

	
	
}

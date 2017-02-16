package com.yujieshipin.crm.entity;
/**
 * 课程排期表-课程排期数据
 * @author hfthink
 *
 */
public class SubjectInfoEntity {
	/**
	 * 课程节数安排
	 */
	private String numSubject;
	/**
	 * 每节课时间
	 */
	private String Time;
	/**
	 * 周一到周五
	 */
	private String date;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNumSubject() {
		return numSubject;
	}
	public void setNumSubject(String numSubject) {
		this.numSubject = numSubject;
	}
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
}

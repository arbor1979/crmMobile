package com.yujieshipin.crm.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: classdetail中的课程
 * 
 * <br/>
 * 创建说明: 2014-4-26 上午11:06:31 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class Curriculum {
	private String teacherName;
	private String teacherPhoto;
	private String teacherCourses;
	private String teacherClasses;
	private String teacherRank;
	private String courseRating;
	private String summaryContent;
	private String homeWork;
	private String attendanceValues;
	private String curriculums;
	private String classes;
	private String classRoom;
	private String kecheng;
	private String jieci;
	private String shangkeriqi;
	private String teacherpingjiashu;
	private String kechengpingjiashu;
	private String ketangjilv;
	private String jiaoshiweisheng;
	private ArrayList<DownloadSubject> imagePaths,imagePaths1;

	public Curriculum() {
	}

	public String getShangkeriqi() {
		return shangkeriqi;
	}

	public void setShangkeriqi(String shangkeriqi) {
		this.shangkeriqi = shangkeriqi;
	}

	public String getClassRoom() {
		return classRoom;
	}

	public void setClassRoom(String classRoom) {
		this.classRoom = classRoom;
	}

	public String getKecheng() {
		return kecheng;
	}

	public void setKecheng(String kecheng) {
		this.kecheng = kecheng;
	}

	public String getJieci() {
		return jieci;
	}

	public void setJieci(String jieci) {
		this.jieci = jieci;
	}

	public String getTeacherpingjiashu() {
		return teacherpingjiashu;
	}

	public void setTeacherpingjiashu(String teacherpingjiashu) {
		this.teacherpingjiashu = teacherpingjiashu;
	}

	public String getKechengpingjiashu() {
		return kechengpingjiashu;
	}

	public void setKechengpingjiashu(String kechengpingjiashu) {
		this.kechengpingjiashu = kechengpingjiashu;
	}

	public String getKetangjilv() {
		return ketangjilv;
	}

	public void setKetangjilv(String ketangjilv) {
		this.ketangjilv = ketangjilv;
	}

	public String getJiaoshiweisheng() {
		return jiaoshiweisheng;
	}

	public void setJiaoshiweisheng(String jiaoshiweisheng) {
		this.jiaoshiweisheng = jiaoshiweisheng;
	}

	public Curriculum(JSONObject jo) throws JSONException {
		JSONObject teatherJo=jo.optJSONObject("老师介绍");
		teacherName=teatherJo.optString("姓名");
		teacherPhoto=teatherJo.optString("用户头像");
		teacherCourses=teatherJo.optString("所带课程");
		teacherClasses=teatherJo.optString("所带班级");
		
		teacherRank = jo.optString("老师评分");
		courseRating = jo.optString("课程评分");
		summaryContent = jo.optString("授课内容");
		homeWork = jo.optString("课后作业");
		attendanceValues = jo.optString("个人出勤");

		curriculums = jo.optString("所带课程");
		classes = jo.optString("所带班级");
		kecheng=jo.optString("课程名称");
		classRoom=jo.getString("教室");
		jieci=jo.getString("节次");
		shangkeriqi=jo.getString("上课日期");
		JSONArray joimg = jo.optJSONArray("课堂笔记图片");
		JSONArray joimg1 = jo.optJSONArray("课堂作业图片");
		teacherpingjiashu=jo.optString("老师评分数");
		kechengpingjiashu=jo.optString("课程评分数");
		ketangjilv=jo.optString("课堂纪律");
		jiaoshiweisheng=jo.optString("教室卫生");
		imagePaths = new ArrayList<DownloadSubject>();
		for (int i = 0; i < joimg.length(); i++) {
			DownloadSubject downsub=new DownloadSubject(joimg.getJSONObject(i));
			imagePaths.add(downsub);
			
		}
		imagePaths1 = new ArrayList<DownloadSubject>();
		for (int i = 0; i < joimg1.length(); i++) {
			DownloadSubject downsub=new DownloadSubject(joimg1.getJSONObject(i));
			imagePaths1.add(downsub);
			
		}
	}

	public ArrayList<DownloadSubject> getImagePaths1() {
		return imagePaths1;
	}

	public void setImagePaths1(ArrayList<DownloadSubject> imagePaths1) {
		this.imagePaths1 = imagePaths1;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeacherPhoto() {
		return teacherPhoto;
	}

	public void setTeacherPhoto(String teacherPhoto) {
		this.teacherPhoto = teacherPhoto;
	}

	public String getTeacherCourses() {
		return teacherCourses;
	}

	public void setTeacherCourses(String teacherCourses) {
		this.teacherCourses = teacherCourses;
	}

	public String getTeacherClasses() {
		return teacherClasses;
	}

	public void setTeacherClasses(String teacherClasses) {
		this.teacherClasses = teacherClasses;
	}

	public String getTeacherRank() {
		return teacherRank;
	}

	public void setTeacherRank(String teacherRank) {
		this.teacherRank = teacherRank;
	}

	public String getCourseRating() {
		return courseRating;
	}

	public void setCourseRating(String courseRating) {
		this.courseRating = courseRating;
	}

	public String getSummaryContent() {
		return summaryContent;
	}

	public void setSummaryContent(String summaryContent) {
		this.summaryContent = summaryContent;
	}

	public String getHomeWork() {
		return homeWork;
	}

	public void setHomeWork(String homeWork) {
		this.homeWork = homeWork;
	}

	public String getAttendanceValues() {
		return attendanceValues;
	}

	public void setAttendanceValues(String attendanceValues) {
		this.attendanceValues = attendanceValues;
	}

	public String getCurriculums() {
		return curriculums;
	}

	public void setCurriculums(String curriculums) {
		this.curriculums = curriculums;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public ArrayList<DownloadSubject> getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(ArrayList<DownloadSubject> imagePaths) {
		this.imagePaths = imagePaths;
	}

	
}

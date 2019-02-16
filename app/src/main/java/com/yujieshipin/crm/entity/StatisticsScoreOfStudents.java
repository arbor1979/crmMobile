package com.yujieshipin.crm.entity;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName="StatisticsScoreOfStudents")
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 学生测验统计
 * 
 *  <br/>创建说明: 2013-11-22 下午3:55:46 linrr  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class StatisticsScoreOfStudents {
	@DatabaseField
	private String data;//数据
		@DatabaseField
	private String title;//标题
		@DatabaseField
	private String averageScore;//平均分
		@DatabaseField
		private String test;//测验
		@DatabaseField
	private String highestScore;//最高分
		public StatisticsScoreOfStudents(){}
		public StatisticsScoreOfStudents(JSONObject jo) {
			data = jo.optString("数据");
			title = jo.optString("标题");
			averageScore = jo.optString("平均分");
			highestScore= jo.optString("最高分");
			test=jo.optString("测验");
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
		public String getAverageScore() {
			return averageScore;
		}
		public void setAverageScore(String averageScore) {
			this.averageScore = averageScore;
		}
		public String getTest() {
			return test;
		}
		public void setTest(String test) {
			this.test = test;
		}
		public String getHighestScore() {
			return highestScore;
		}
		public void setHighestScore(String highestScore) {
			this.highestScore = highestScore;
		}
}

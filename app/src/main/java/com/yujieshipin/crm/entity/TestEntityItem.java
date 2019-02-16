package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 课堂测试题库列表数据
 * 
 * @author hfthink
 * 
 */
public class TestEntityItem {
	private String id;// 编号
	private String termName;// 学期名称
	private String subjectId; // 上课记录编号
	private String testName; // 测验名称
	private String topicName; // 题目名称
	private String answerStatus;// 答题状态
	private String aAnswer;
	private String bAnswer;
	private String cAnswer;
	private String dAnswer;
	private String eAnswer;
	private String fAnswer;
	private String answer; // 正确答案
	private String remark; // 备注
	private String correctRate;// 正确率
	private String errorRate;// 错误率
	private String studentAnswerStatus; // 学生答题状态
	private String studentAnswerResult; // 学生答题结果
	private String CFS;//题目分类统计

	public TestEntityItem() {

	}

	private TestEntityItem(JSONObject jo) {
		id = jo.optString("编号");
		termName = jo.optString("学期名称");
		subjectId = jo.optString("老师上课记录编号");
		testName = jo.optString("测验名称");
		topicName = jo.optString("题目名称");
		answerStatus = jo.optString("答题状态");
		aAnswer = jo.optString("A");
		bAnswer = jo.optString("B");
		cAnswer = jo.optString("C");
		dAnswer = jo.optString("D");
		eAnswer = jo.optString("E");
		fAnswer = jo.optString("F");
		answer = jo.optString("正确答案");
		remark = jo.optString("备注");
		correctRate = jo.optString("正确率");
		errorRate = jo.optString("错误率");
		studentAnswerStatus = jo.optString("学生答题状态");
		studentAnswerResult = jo.optString("学生答题结果");
		CFS = jo.optString("题目分类统计");
	}

	public static List<TestEntityItem> toList(JSONArray ja) {
		List<TestEntityItem> result = new ArrayList<TestEntityItem>();
		TestEntityItem info = null;

		for (int i = 0; i < ja.length(); i++) {
			JSONObject jo = ja.optJSONObject(i);
			info = new TestEntityItem(jo);
			result.add(info);
		}
		return result;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getAnswerStatus() {
		return answerStatus;
	}

	public void setAnswerStatus(String answerStatus) {
		this.answerStatus = answerStatus;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getaAnswer() {
		return aAnswer;
	}

	public void setaAnswer(String aAnswer) {
		this.aAnswer = aAnswer;
	}

	public String getbAnswer() {
		return bAnswer;
	}

	public void setbAnswer(String bAnswer) {
		this.bAnswer = bAnswer;
	}

	public String getcAnswer() {
		return cAnswer;
	}

	public void setcAnswer(String cAnswer) {
		this.cAnswer = cAnswer;
	}

	public String getdAnswer() {
		return dAnswer;
	}

	public void setdAnswer(String dAnswer) {
		this.dAnswer = dAnswer;
	}

	public String geteAnswer() {
		return eAnswer;
	}

	public void seteAnswer(String eAnswer) {
		this.eAnswer = eAnswer;
	}

	public String getfAnswer() {
		return fAnswer;
	}

	public void setfAnswer(String fAnswer) {
		this.fAnswer = fAnswer;
	}

	public String getCorrectRate() {
		return correctRate;
	}

	public void setCorrectRate(String correctRate) {
		this.correctRate = correctRate;
	}

	public String getErrorRate() {
		return errorRate;
	}

	public void setErrorRate(String errorRate) {
		this.errorRate = errorRate;
	}

	public String getStudentAnswerStatus() {
		return studentAnswerStatus;
	}

	public void setStudentAnswerStatus(String studentAnswerStatus) {
		this.studentAnswerStatus = studentAnswerStatus;
	}

	public String getStudentAnswerResult() {
		return studentAnswerResult;
	}

	public void setStudentAnswerResult(String studentAnswerResult) {
		this.studentAnswerResult = studentAnswerResult;
	}

	public String getCFS() {
		return CFS;
	}

	public void setCFS(String cFS) {
		CFS = cFS;
	}
}

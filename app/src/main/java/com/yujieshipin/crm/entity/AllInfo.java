package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.PrefUtility;

public class AllInfo {
	private String TAG = "AllInfo";
	private Schedule schedule; // 课表规则
	private List<TestEntity> testEntitys;// 课堂测验

	private List<DownloadSubject> downloadSubjects;// 课件下载

	private List<TeacherInfo> teacherInfos; // 教师上课记录

	private Map<String,List<Student>> studentList; // 学生列表

	private List<StudentAttence> studentAttenceList; // 学生考勤统计

	private List<Dictionary> studentAttenceColorList; // 考勤统计颜色

	private List<StudentScore> studentScoreList; // 学生成绩查询

	private List<StudentTest> studentTestList; // 学生测验查询

	private List<Dictionary> studentTestColorList; // 测验统计颜色

	private List<Dictionary> studentInfoList;// 学生详情显示

	private Dictionary studentTab; // 学生信息卡

	Dictionary dictionary = null;

	private List<TestStartEntity> startTestList;// 测验时间统计

	// List<Map> childList;
	int[] array;

	private String curriculums;// 所带课程
	private String classesStr;// 所带班级
	private String companyName;// 单位名称
	private String workAttendances;// 考勤名称
	private String workAttendanceValues;// 考勤分值
	private String allowSchoolrecordkeysStr;// 允许教师修改教师上课记录信息字段
	private String allowSchoolrecordSummaryKeysStr;// "允许教师修改教师上课记录信息字段_总结
	private String allowSchoolrecordWorkAttendanceKeysStr;// "允许教师修改教师上课记录信息字段_考勤
	private int currentWeek;// 当前周次，
	private int selectedWeek;// 选择周次
	private int maxWeek;// "最大周次
	private String curXueQi;//当前学期
	private List<WorkAttendanceRule> workAttendanceRules;// 考勤规则
	private List<AddScoresRule> addScoresRule;// 加分规则
	private List<ReduceScoresRule> reduceScoresRule;// 减分规则
	// private List<WorkAttendanceRule> workAttendanceRules;//
	private List<TeacherSchoolRecords> teacherSchoolRecords;// 教师上课记录
	private List<MyClassSchedule> futureClassSchedule;// 未来上课记录
	// private List<StudentPic> studentPicList; //学生头像

	// private Teacher
	@SuppressWarnings("unchecked")
	public AllInfo(JSONObject jo) {
		curriculums = jo.optString("所带课程");
		companyName = jo.optString("单位名称");
		workAttendances = jo.optString("考勤名称");
		workAttendanceValues = jo.optString("考勤分值");
		allowSchoolrecordkeysStr = jo.optString("允许教师修改教师上课记录信息字段");
		allowSchoolrecordSummaryKeysStr = jo.optString("允许教师修改教师上课记录信息字段_总结");
		allowSchoolrecordWorkAttendanceKeysStr = jo
				.optString("允许教师修改教师上课记录信息字段_考勤");
		currentWeek = jo.optInt("当前周次");
		selectedWeek = jo.optInt("选择周次");
		maxWeek = jo.optInt("最大周次");
		curXueQi=jo.optString("当前学期");
		PrefUtility.put(Constants.PREF_CURRICULUMS, curriculums);
		PrefUtility.put(Constants.PREF_CLASSES, classesStr);
		PrefUtility.put(Constants.PREF_COMPANY_NAME, companyName);
		PrefUtility.put(Constants.PREF_WORK_ATTENDANCES, workAttendances);
		PrefUtility.put(Constants.PREF_WORK_ATTENDANCE_VALUES,
				workAttendanceValues);
		PrefUtility.put(Constants.PREF_ALLOW_SCHOOL_RECORDKEYS_STR,
				allowSchoolrecordkeysStr);
		PrefUtility.put(Constants.PREF_ALLOW_SCHOOL_RECORD_SUMMARYKEYS_STR,
				allowSchoolrecordSummaryKeysStr);
		PrefUtility.put(
				Constants.PREF_ALLOW_SCHOOL_RECORDWORK_ATTENDANCEKEYS_STR,
				allowSchoolrecordWorkAttendanceKeysStr);
		 PrefUtility.put(Constants.PREF_CURRENT_WEEK, currentWeek);
		 PrefUtility.put(Constants.PREF_SELECTED_WEEK, selectedWeek);
		 PrefUtility.put(Constants.PREF_MAX_WEEK, maxWeek);
		 PrefUtility.put(Constants.PREF_CUR_XUEQI, curXueQi);
		Log.d(TAG, "currentWeek:" + currentWeek + ",selectedWeek:"
				+ selectedWeek + ",maxWeek:" + maxWeek);
		// Constants.setCurrentWeek(currentWeek);
		// Constants.setMaxWeek(maxWeek);
		// Constants.setSelectedWeek(selectedWeek);
		// TabHostActivity.currentWeek=currentWeek;
		// TabHostActivity.maxWeek=maxWeek;
		// TabHostActivity.selectedWeek=selectedWeek;

		Log.d(TAG, "------------------>jo:" + jo);
		Log.d(TAG, "------------------>开始准备初始化：" + new Date());
		classesStr = jo.optString("所带班级");
		Log.d(TAG, "------------------>所带班级：" + classesStr);
		if (classesStr != null) {
			String[] classes = classesStr.split(",");
			studentList = new HashMap<String,List<Student>>();
			for (int i = 0; i < classes.length; i++) {
				JSONArray stuArray = jo.optJSONArray(classes[i]);
				if (stuArray != null) {
					List<Student> students = Student.toList(stuArray);
					if (students != null && students.size() > 0) {
						studentList.put(classes[i],students);
					}
				}
			}
		}

		JSONObject jowasr = jo.optJSONObject("考勤规则");
		workAttendanceRules = new ArrayList<WorkAttendanceRule>();
		if (jowasr != null) {
			Log.d(TAG, "------------------>jowasr.length()：" + jowasr.length());
			Iterator<String> keys = jowasr.keys();
			Log.d(TAG, "------------------>keys：" + keys);
			String name, values;
			while (keys.hasNext()) {
				name = String.valueOf(keys.next());
				values = jowasr.optString(name);
				WorkAttendanceRule wrsr = new WorkAttendanceRule(name, values);
				workAttendanceRules.add(wrsr);
			}
		}
		JSONArray joasr = jo.optJSONArray("加分规则");
		if (joasr != null) {
			Log.d(TAG, "------------------>joasr.length()：" + joasr.length());
			addScoresRule = new ArrayList<AddScoresRule>();
			for (int i = 0; i < joasr.length(); i++) {
				AddScoresRule asr = new AddScoresRule(joasr.optJSONObject(i));
				addScoresRule.add(asr);
			}
		}
		JSONArray jorsr = jo.optJSONArray("减分规则");

		if (jorsr != null) {
			Log.d(TAG, "------------------>jorsr.length()：" + jorsr.length());
			reduceScoresRule = new ArrayList<ReduceScoresRule>();
			for (int i = 0; i < jorsr.length(); i++) {
				ReduceScoresRule rsr = new ReduceScoresRule(
						jorsr.optJSONObject(i));
				reduceScoresRule.add(rsr);
			}
		}
		JSONArray jotsr = jo.optJSONArray("教师上课记录");
		if (jotsr != null) {
			Log.d(TAG, "------------------>jotsr.length()：" + jotsr.length());
			teacherSchoolRecords = new ArrayList<TeacherSchoolRecords>();
			for (int i = 0; i < jotsr.length(); i++) {
				TeacherSchoolRecords tsr = new TeacherSchoolRecords(
						jotsr.optJSONObject(i));
				teacherSchoolRecords.add(tsr);
			}
		}
		// JSONObject jObject = jo.optJSONObject("课堂测验_收卷");
		// Log.d(TAG, "------------------>课堂测验_收卷：" + jObject);
		// if (jObject != null) {
		// String timeKey;
		// int timeValues;
		// startTestList = new ArrayList<TestStartEntity>();
		// Iterator<Object> keys = jObject.keys();
		// Log.d(TAG, "--------------课堂测验_收卷--->keys：" + keys);
		// while (keys.hasNext()) {
		// timeKey = String.valueOf(keys.next());
		// timeValues = jObject.optInt(timeKey);
		// // map.put("key", timeKey);
		// // map.put("values", timeValues);
		// TestStartEntity testStartEntity = new
		// TestStartEntity(timeKey,timeValues);
		// startTestList.add(testStartEntity);
		// }
		//
		// for (int i = 0; i < startTestList.size() - 1; i++) {
		// for (int j = i + 1; j < startTestList.size(); j++) {
		// if (startTestList.get(i).getTimeValues() > startTestList
		// .get(j).getTimeValues()) {
		// TestStartEntity temp = new TestStartEntity(
		// startTestList.get(i).getTimeKey(),
		// startTestList.get(i).getTimeValues());
		// startTestList.set(i, startTestList.get(j));
		// startTestList.set(j, temp);
		// }
		// }
		// }
		// array = new int[childList.size()];
		// for (int i = 0; i < childList.size(); i++) {
		// array[i] = (Integer) childList.get(i).get("values");
		// }
		//

		// /**
		// * 字符串排序
		// */
		// for (int i = 0; i < array.length - 1; i++) {
		// for (int j = i + 1; j < array.length; j++) {
		// if (array[i].compareTo(array[j]) > 0) {
		// String temp = array[i];
		// array[i] = array[j];
		// array[j] = temp;
		// }
		// }
		// }
		//
		// /**
		// * 排序后的解析回来添加到list
		// */
		// for(int i = 0; i< array.length; i++){
		// int value = innerJObject.optInt(array[i]);
		// TestStartEntity mTestStartEntity = new TestStartEntity(array[i],
		// value);
		// startTestList.add(mTestStartEntity);
		// }
		// }

		JSONArray jotest = jo.optJSONArray("课堂测验_收卷2");
		startTestList = new ArrayList<TestStartEntity>();
		if (jotest != null) {
			Log.d(TAG, "------------------>jotest.length()：" + jotest.length());
			for (int i = 0; i < jotest.length() - 1; i++) {
				TestStartEntity temp = new TestStartEntity(
						jotest.optJSONObject(i));
				startTestList.add(temp);
			}
		}

		JSONObject sdObj = jo.optJSONObject("课表规则");
		Log.d(TAG, "------------------>课表规则：" + sdObj);
		if (sdObj != null) {
			schedule = new Schedule(sdObj);
			Log.d(TAG, "------------------>schedule：" + schedule);
		}
		// }
		JSONObject sdObj2 = jo.optJSONObject("课件下载");
		Log.d(TAG, "------------------>课件下载：" + sdObj2);
		// if (sdObj2 != null) {
		// JSONArray jArray = sdObj2.optJSONArray("数据");
		// downloadSubjects = DownloadSubject.toList(jArray);
		// }

		JSONArray tiArray = jo.optJSONArray("教师上课记录");

		if (tiArray != null) {
			Log.d(TAG,
					"------------------>tiArray.length()：" + tiArray.length());
			teacherInfos = TeacherInfo.toList(tiArray);
		}

		JSONArray jo1Array = jo.optJSONArray("课堂测验_内容");

		if (jo1Array != null) {
			Log.d(TAG,
					"------------------>jo1Array.length()：" + jo1Array.length());
			testEntitys = TestEntity.toList(jo1Array);
		}

		JSONObject joStuInfo = jo.optJSONObject("学生详情信息卡");
		Log.d(TAG, "------------------>学生详情信息卡：" + joStuInfo);
		studentTab = new Dictionary();
		studentTab.setParentCode("studentTab");
		studentTab.setParentName("学生详情信息卡");
		studentTab.setItemCode("学生详情信息卡");
		if (joStuInfo != null) {
			String studata = joStuInfo.optString("数据");
			if (AppUtility.isNotEmpty(studata)) {
				studentTab.setItemValue(studata);
			}
		}

		/**
		 * 考勤统计
		 */
		JSONObject joAttence = jo.optJSONObject("学生考勤统计");
		Log.d(TAG, "------------------------->学生考勤统计:" + joAttence);
		// 考勤统计颜色
		studentAttenceColorList = new ArrayList<Dictionary>();
		dictionary = new Dictionary();
		dictionary.setParentCode("studentColor");
		dictionary.setParentName("学生详情中用到的颜色");
		dictionary.setItemCode("考勤统计颜色");
		if (joAttence != null) {
			JSONArray stuArray = joAttence.optJSONArray("数据");
			Log.d(TAG, "------------数据----------");
			if (stuArray != null) {
				Log.d(TAG, "------------------------->数据:" + stuArray.length());
				studentAttenceList = StudentAttence.toList(stuArray);
			}
			dictionary.setItemValue(joAttence.optString("颜色"));
		}
		studentAttenceColorList.add(dictionary);

		/**
		 * 学生成绩查询
		 */
		Log.d(TAG, "---------学生成绩查询--------");
		JSONObject joScore = jo.optJSONObject("学生成绩查询");
		if (joScore != null) {
			JSONArray scoreArray = joScore.optJSONArray("数据");
			if (scoreArray != null) {
				Log.d(TAG, "---------------scoreArray.length()--------"
						+ scoreArray.length());
				studentScoreList = StudentScore.toList(scoreArray);
			}
		}

		/**
		 * 学生测验统计
		 */
		JSONObject joTest = jo.optJSONObject("学生测验统计");
		Log.d(TAG, "------------------>学生测验统计：" + joTest);
		// 学生测验统计颜色
		studentTestColorList = new ArrayList<Dictionary>();
		dictionary = new Dictionary();
		dictionary.setParentCode("studentColor");
		dictionary.setParentName("学生详情中用到的颜色");
		dictionary.setItemCode("测验统计颜色");
		if (joTest != null) {
			JSONArray testArray = joTest.optJSONArray("数据");
			Log.d(TAG, "------------数据-------------");
			if (testArray != null) {
				Log.d(TAG,
						"------------testArray.length():" + testArray.length());
				studentTestList = StudentTest.toList(testArray);
			}
			dictionary.setItemValue(joTest.optString("颜色"));
		}
		studentTestColorList.add(dictionary);
		
		JSONArray scheduleArray = jo.optJSONArray("未来两周课程");

		if (scheduleArray != null) {
			Log.d(TAG,
					"------------------>tiArray.length()：" + tiArray.length());
			futureClassSchedule = MyClassSchedule.toList(scheduleArray);
		}
		Log.d(TAG, "------------------>结束准备初始化：" + new Date());
	}

	public AllInfo(net.minidev.json.JSONObject jo) {
		curriculums = (jo.get("所带课程")==null?"":jo.get("所带课程").toString());
		companyName = jo.get("单位名称").toString();
		workAttendances = jo.get("考勤名称").toString();
		workAttendanceValues = jo.get("考勤分值").toString();
		allowSchoolrecordkeysStr = jo.get("允许教师修改教师上课记录信息字段").toString();
		allowSchoolrecordSummaryKeysStr = jo.get("允许教师修改教师上课记录信息字段_总结").toString();
		allowSchoolrecordWorkAttendanceKeysStr = jo
				.get("允许教师修改教师上课记录信息字段_考勤").toString();
		currentWeek = Integer.parseInt(jo.get("当前周次").toString());
		selectedWeek = Integer.parseInt(jo.get("选择周次").toString());
		maxWeek = Integer.parseInt(jo.get("最大周次").toString());
		curXueQi=jo.get("当前学期").toString();
		PrefUtility.put(Constants.PREF_CURRICULUMS, curriculums);
		PrefUtility.put(Constants.PREF_CLASSES, classesStr);
		PrefUtility.put(Constants.PREF_COMPANY_NAME, companyName);
		PrefUtility.put(Constants.PREF_WORK_ATTENDANCES, workAttendances);
		PrefUtility.put(Constants.PREF_WORK_ATTENDANCE_VALUES,
				workAttendanceValues);
		PrefUtility.put(Constants.PREF_ALLOW_SCHOOL_RECORDKEYS_STR,
				allowSchoolrecordkeysStr);
		PrefUtility.put(Constants.PREF_ALLOW_SCHOOL_RECORD_SUMMARYKEYS_STR,
				allowSchoolrecordSummaryKeysStr);
		PrefUtility.put(
				Constants.PREF_ALLOW_SCHOOL_RECORDWORK_ATTENDANCEKEYS_STR,
				allowSchoolrecordWorkAttendanceKeysStr);
		 PrefUtility.put(Constants.PREF_CURRENT_WEEK, currentWeek);
		 PrefUtility.put(Constants.PREF_SELECTED_WEEK, selectedWeek);
		 PrefUtility.put(Constants.PREF_MAX_WEEK, maxWeek);
		 PrefUtility.put(Constants.PREF_CUR_XUEQI, curXueQi);
		classesStr = (jo.get("所带班级")==null?"":jo.get("所带班级").toString());
		Log.d(TAG, "------------------>所带班级：" + classesStr);
		if (classesStr != null) {
			String[] classes = classesStr.split(",");
			studentList = new HashMap<String,List<Student>>();
			for (int i = 0; i < classes.length; i++) {
				net.minidev.json.JSONArray stuArray = (net.minidev.json.JSONArray) jo.get(classes[i]);
				if (stuArray != null) {
					List<Student> students = Student.toList(stuArray);
					if (students != null && students.size() > 0) {
						studentList.put(classes[i],students);
					}
				}
			}
		}

		net.minidev.json.JSONObject jowasr = (net.minidev.json.JSONObject) jo.get("考勤规则");
		
		workAttendanceRules = new ArrayList<WorkAttendanceRule>();
		
		if (jowasr != null) {
			Log.d(TAG, "------------------>jowasr.length()：" + jowasr.size());
			
			Set<String> keys = jowasr.keySet();
			Iterator<String> it=keys.iterator();
			String name, values;
			for (; it.hasNext();) {
				 
				name = String.valueOf(it.next());
				values =  String.valueOf(jowasr.get(name));
				WorkAttendanceRule wrsr = new WorkAttendanceRule(name, values);
				workAttendanceRules.add(wrsr);
			}
		}
		net.minidev.json.JSONObject joasr = (net.minidev.json.JSONObject)jo.get("加分规则");
		if (joasr != null) {
			Log.d(TAG, "------------------>joasr.length()：" + joasr.size());
			addScoresRule = new ArrayList<AddScoresRule>();
			
			Set<String> keys = jowasr.keySet();
			Iterator<String> it=keys.iterator();
			String name, values;
			for (; it.hasNext();) {
				 
				name = String.valueOf(it.next());
				values =  String.valueOf(jowasr.get(name));
				AddScoresRule wrsr = new AddScoresRule(name, values);
				addScoresRule.add(wrsr);
			}
			
			
		}
		net.minidev.json.JSONObject jorsr = (net.minidev.json.JSONObject)jo.get("减分规则");

		if (jorsr != null) {
			Log.d(TAG, "------------------>jorsr.length()：" + jorsr.size());
			reduceScoresRule = new ArrayList<ReduceScoresRule>();
			
			Set<String> keys = jowasr.keySet();
			Iterator<String> it=keys.iterator();
			String name, values;
			for (; it.hasNext();) {
				 
				name = String.valueOf(it.next());
				values =  String.valueOf(jowasr.get(name));
				ReduceScoresRule wrsr = new ReduceScoresRule(name, values);
				reduceScoresRule.add(wrsr);
			}
			
		}
		net.minidev.json.JSONArray jotsr =(net.minidev.json.JSONArray) jo.get("教师上课记录");
		if (jotsr != null) {
			Log.d(TAG, "------------------>jotsr.length()：" + jotsr.size());
			teacherSchoolRecords = new ArrayList<TeacherSchoolRecords>();
			for (int i = 0; i < jotsr.size(); i++) {
				TeacherSchoolRecords tsr = new TeacherSchoolRecords((net.minidev.json.JSONObject)jotsr.get(i));
				teacherSchoolRecords.add(tsr);
			}
		}
		

		net.minidev.json.JSONArray jotest = (net.minidev.json.JSONArray)jo.get("课堂测验_收卷2");
		startTestList = new ArrayList<TestStartEntity>();
		if (jotest != null) {
			Log.d(TAG, "------------------>jotest.length()：" + jotest.size());
			
			for (int i = 0; i < jotest.size(); i++) {
				net.minidev.json.JSONObject item=(net.minidev.json.JSONObject)jotest.get(i);
				
				TestStartEntity temp = new TestStartEntity(item.get("名称").toString(),Integer.parseInt(item.get("值").toString()));
				startTestList.add(temp);
			}
		}

		net.minidev.json.JSONObject sdObj = (net.minidev.json.JSONObject)jo.get("课表规则");
		Log.d(TAG, "------------------>课表规则：" + sdObj);
		if (sdObj != null) {
			schedule = new Schedule(sdObj);
			Log.d(TAG, "------------------>schedule：" + schedule);
		}
		// }
		net.minidev.json.JSONObject sdObj2 = (net.minidev.json.JSONObject)jo.get("课件下载");
		Log.d(TAG, "------------------>课件下载：" + sdObj2);
		if (sdObj2 != null) {
			net.minidev.json.JSONArray jArray = (net.minidev.json.JSONArray)sdObj2.get("数据");
			downloadSubjects = DownloadSubject.toList(jArray);
		}

		net.minidev.json.JSONArray tiArray = (net.minidev.json.JSONArray)jo.get("教师上课记录");

		if (tiArray != null) {
			Log.d(TAG,
					"------------------>tiArray.length()：" + tiArray.size());
			teacherInfos = TeacherInfo.toList(tiArray);
		}

		net.minidev.json.JSONArray jo1Array = (net.minidev.json.JSONArray)jo.get("课堂测验_内容");

		if (jo1Array != null) {
			Log.d(TAG,
					"------------------>jo1Array.length()：" + jo1Array.size());
			testEntitys = TestEntity.toList(jo1Array);
		}

		net.minidev.json.JSONObject joStuInfo = (net.minidev.json.JSONObject)jo.get("学生详情信息卡");
		Log.d(TAG, "------------------>学生详情信息卡：" + joStuInfo);
		studentTab = new Dictionary();
		studentTab.setParentCode("studentTab");
		studentTab.setParentName("学生详情信息卡");
		studentTab.setItemCode("学生详情信息卡");
		if (joStuInfo != null) {
			String studata = String.valueOf(joStuInfo.get("数据"));
			if (AppUtility.isNotEmpty(studata)) {
				studentTab.setItemValue(studata);
			}
		}

		/**
		 * 考勤统计
		 */
		net.minidev.json.JSONObject joAttence =(net.minidev.json.JSONObject) jo.get("学生考勤统计");
		Log.d(TAG, "------------------------->学生考勤统计:" + joAttence);
		// 考勤统计颜色
		studentAttenceColorList = new ArrayList<Dictionary>();
		dictionary = new Dictionary();
		dictionary.setParentCode("studentColor");
		dictionary.setParentName("学生详情中用到的颜色");
		dictionary.setItemCode("考勤统计颜色");
		if (joAttence != null) {
			net.minidev.json.JSONArray stuArray = (net.minidev.json.JSONArray)joAttence.get("数据");
			Log.d(TAG, "------------数据----------");
			if (stuArray != null) {
				Log.d(TAG, "------------------------->数据:" + stuArray.size());
				studentAttenceList = StudentAttence.toList(stuArray);
			}
			dictionary.setItemValue(String.valueOf(joAttence.get("颜色")));
		}
		studentAttenceColorList.add(dictionary);

		/**
		 * 学生成绩查询
		 */
		Log.d(TAG, "---------学生成绩查询--------");
		net.minidev.json.JSONObject joScore = (net.minidev.json.JSONObject)jo.get("学生成绩查询");
		if (joScore != null) {
			net.minidev.json.JSONArray scoreArray = (net.minidev.json.JSONArray)joScore.get("数据");
			if (scoreArray != null) {
				Log.d(TAG, "---------------scoreArray.length()--------"
						+ scoreArray.size());
				studentScoreList = StudentScore.toList(scoreArray);
			}
		}

		/**
		 * 学生测验统计
		 */
		net.minidev.json.JSONObject joTest = (net.minidev.json.JSONObject)jo.get("学生测验统计");
		Log.d(TAG, "------------------>学生测验统计：" + joTest);
		// 学生测验统计颜色
		studentTestColorList = new ArrayList<Dictionary>();
		dictionary = new Dictionary();
		dictionary.setParentCode("studentColor");
		dictionary.setParentName("学生详情中用到的颜色");
		dictionary.setItemCode("测验统计颜色");
		if (joTest != null) {
			net.minidev.json.JSONArray testArray = (net.minidev.json.JSONArray)joTest.get("数据");
			Log.d(TAG, "------------数据-------------");
			if (testArray != null) {
				Log.d(TAG,
						"------------testArray.length():" + testArray.size());
				studentTestList = StudentTest.toList(testArray);
			}
			dictionary.setItemValue(String.valueOf(joTest.get("颜色")));
		}
		studentTestColorList.add(dictionary);
		
		net.minidev.json.JSONArray joSchedule = (net.minidev.json.JSONArray)jo.get("未来两周课程");
		if (joSchedule != null) {
			Log.d(TAG,
					"------------------>tiArray.length()：" + tiArray.size());
			futureClassSchedule = MyClassSchedule.toList(joSchedule);
		}
		Log.d(TAG, "------------------>结束准备初始化：" + new Date());
	}
	public List<MyClassSchedule> getFutureClassSchedule() {
		return futureClassSchedule;
	}

	public void setFutureClassSchedule(List<MyClassSchedule> futureClassSchedule) {
		this.futureClassSchedule = futureClassSchedule;
	}

	/**
	 * 
	 * 学生信息
	 */

	public List<TestStartEntity> getStartTestList() {
		return startTestList;
	}

	public List<TeacherInfo> getTeacherInfos() {
		return teacherInfos;
	}

	public void setTeacherInfos(List<TeacherInfo> teacherInfos) {
		this.teacherInfos = teacherInfos;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public List<TestEntity> getTestEntitys() {
		return testEntitys;
	}

	public void setTestEntitys(List<TestEntity> testEntitys) {
		this.testEntitys = testEntitys;
	}

	public Map<String,List<Student>> getStudentList() {
		return studentList;
	}

	public void setStudentList(Map<String,List<Student>> studentList) {
		this.studentList = studentList;
	}

	public List<DownloadSubject> getDownloadSubjects() {
		return downloadSubjects;
	}

	public void setDownloadSubjects(List<DownloadSubject> downloadSubjects) {
		this.downloadSubjects = downloadSubjects;
	}

	public List<StudentAttence> getStudentAttenceList() {
		return studentAttenceList;
	}

	public void setStudentAttenceList(List<StudentAttence> studentAttenceList) {
		this.studentAttenceList = studentAttenceList;
	}

	public List<Dictionary> getStudentAttenceColorList() {
		return studentAttenceColorList;
	}

	public void setStudentAttenceColorList(
			List<Dictionary> studentAttenceColorList) {
		this.studentAttenceColorList = studentAttenceColorList;
	}

	public List<StudentScore> getStudentScoreList() {
		return studentScoreList;
	}

	public void setStudentScoreList(List<StudentScore> studentScoreList) {
		this.studentScoreList = studentScoreList;
	}

	public List<StudentTest> getStudentTestList() {
		return studentTestList;
	}

	public void setStudentTestList(List<StudentTest> studentTestList) {
		this.studentTestList = studentTestList;
	}

	public List<Dictionary> getStudentTestColorList() {
		return studentTestColorList;
	}

	public void setStudentTestColorList(List<Dictionary> studentTestColorList) {
		this.studentTestColorList = studentTestColorList;
	}

	public List<Dictionary> getStudentInfoList() {
		return studentInfoList;
	}

	public void setStudentInfoList(List<Dictionary> studentInfoList) {
		this.studentInfoList = studentInfoList;
	}

	public Dictionary getStudentTab() {
		return studentTab;
	}

	public void setStudentTab(Dictionary studentTab) {
		this.studentTab = studentTab;
	}

	public int getCurrentWeek() {
		return currentWeek;
	}

	public void setCurrentWeek(int currentWeek) {
		this.currentWeek = currentWeek;
	}

	public int getSelectedWeek() {
		return selectedWeek;
	}

	public void setSelectedWeek(int selectedWeek) {
		this.selectedWeek = selectedWeek;
	}

	public int getMaxWeek() {
		return maxWeek;
	}

	public void setMaxWeek(int maxWeek) {
		this.maxWeek = maxWeek;
	}

	// public List<StudentPic> getStudentPicList() {
	// return studentPicList;
	// }
	//
	// public void setStudentPicList(List<StudentPic> studentPicList) {
	// this.studentPicList = studentPicList;
	// }

}

package com.yujieshipin.crm.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.TabHostActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.DownloadSubject;
import com.yujieshipin.crm.entity.MyClassSchedule;
import com.yujieshipin.crm.entity.NoticeClass;
import com.yujieshipin.crm.entity.Student;
import com.yujieshipin.crm.entity.StudentPic;
import com.yujieshipin.crm.entity.Suggestions;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;

@SuppressLint("NewApi")
public class Alarmreceiver extends BroadcastReceiver {
	private String TAG = "Alarmreceiver";
	DatabaseHelper database;
	SQLiteDatabase sdb;
	private Dao<MyClassSchedule, Integer> myClassScheduleDao;
	private Dao<TeacherInfo, Integer> teacherInfoDao;
	
	private Dao<Suggestions,Integer> suggestDao;
	private Dao<NoticeClass,Integer> noticeDao;
	private Dao<User, Integer> userDao;
	private Dao<DownloadSubject,Integer> downloadSubjectDao;
	List<TeacherInfo> teacherInfoList;
	List<Student> studentList;
	List<StudentPic> studentPicList;
	User userInfo;
	Suggestions suggestions;
	NoticeClass notices;
	DownloadSubject downloadSubject;
	public static Boolean isdialog=false;
	
	private String actionName,checkCode;;
	private Context context;
	private Location myLocation;
	private LocationManager locationManager;
	
	public static NotificationManager notificationManager;
	
	
	@Override
	public void onReceive(Context context1, Intent intent) {
		this.context = context1;
		
		database = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		sdb = database.getWritableDatabase();
		checkCode = intent.getStringExtra(Constants.PREF_CHECK_CODE);
		Log.d(TAG, "Alarmreceiver-------------checkCode"+checkCode);
		actionName = intent.getAction();
		Log.d(TAG, "Alarmreceiver-------------actionName"+actionName);
		try {
			myClassScheduleDao = database.getMyClassScheduleDao();
			teacherInfoDao=database.getTeacherInfoDao();
			userDao = database.getUserDao();
			suggestDao = database.getSuggestionsDao();
			noticeDao = database.getNoticeClassDao();
			downloadSubjectDao = database.getDownloadSubjectDao();
			
			if("initBaseData".equals(actionName)){
				InitData initData = new InitData(context,database, null,"refreshSubject",checkCode);
				initData.initAllInfo();
			}else if("initContactData".equals(actionName)){
				InitData initData = new InitData(context,database, null,"refreshContact",checkCode);
				initData.initContactInfo();
			}
			else if (actionName.equals(Intent.ACTION_BOOT_COMPLETED) ||
					actionName.equals(Intent.ACTION_USER_PRESENT)) 
			{
				/*
	            Handler handler = new Handler(Looper.getMainLooper());
	            handler.postDelayed(new Runnable() {
	                @Override
	                public void run() {
	                    if(!AppUtility.isServiceRunning(context,"com.yujieshipin.crm.service.UploadPOIService")){
	                    	AppUtility.beginReminder(context);
	                    }
	                }
	            }, 30*1000);
	            */
	        }
			else if ("reportLocation".equals(actionName))
			{
				locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);			
				getNetLocation();
			}else if ("reminderMyClass".equals(actionName)) {
				/****************************** 课程提醒 begin ********************************************/
				String contentTitle = "掌上校园课程提醒";
				String contentText = "";
				//课前提醒
				//boolean booleanReminderBeforeClass = PrefUtility.getBoolean("booleanReminderBeforeClass", false);
				//每日课程提醒
				boolean booleanReminderDayClass = PrefUtility.getBoolean("booleanReminderDayClass", true);
				//每日提醒时间
				
				//开启课前提醒
				/*
				if (booleanReminderBeforeClass) {
					Date now10 = new Date(new Date().getTime() + 600000); //10分钟后的时间
					String begintime = DateHelper.getDateString(now10, "HH:mm");
					TeacherInfo teacherInfo = teacherInfoDao.queryBuilder().where().eq("beginTime", begintime).queryForFirst();
					if (teacherInfo != null) {
						contentText = "你在"+teacherInfo.getBeginTime()+"有"+teacherInfo.getCourseName()+"课,班级："+teacherInfo.getClassGrade()+" 教室："+teacherInfo.getClassroom();
						showDialog(intent, contentTitle, contentText);
					}
					
				}
				*/
				//开启每日课程提醒
				if (booleanReminderDayClass) {
					
					String remindClassTime = PrefUtility.get("remindClassTime", "前一天 20:00");
					Log.d("alarm", remindClassTime);
					String preDay=remindClassTime.split(" ")[0];
					String theDay=""; 
					String dayStr="";
					if(preDay.equals("前一天"))
					{
						theDay=DateHelper.getNextday("yyyy-MM-dd");
						dayStr="明天";
					}
					else
					{
						theDay=DateHelper.getToday("yyyy-MM-dd");
						dayStr="今天";
					}
					Log.d("alarm", dayStr);
					List<MyClassSchedule> dayclassList = myClassScheduleDao.queryBuilder().where().eq("courseDate", theDay).query();
					String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");
					if(hostid.split("_")[1].equals("老师"))
					{
						List<String> banjiList=new ArrayList<String>();
						for (MyClassSchedule teacherInfo : dayclassList) {
							if(!banjiList.contains(teacherInfo.getClassGrade()))
								banjiList.add(teacherInfo.getClassGrade());
							
						}
						if(banjiList.size()>0)
						{
							contentText = "您"+dayStr+"有"+banjiList.size()+"个班的课要上";
							for(String item:banjiList)
								contentText+="\r\n"+item;
						}
						else
							contentText="您"+dayStr+"没有课哦";
					}
					else
					{
						List<String> banjiList=new ArrayList<String>();
						for (MyClassSchedule teacherInfo : dayclassList) {
							if(!banjiList.contains(teacherInfo.getCourseName()))
								banjiList.add(teacherInfo.getCourseName());
						}
						if(hostid.split("_")[1].equals("家长"))
						{
							if(banjiList.size()>0)
								contentText = "您的孩子"+dayStr+"有"+banjiList.size()+"门课要上";
							else
								contentText = "您的孩子"+dayStr+"没有课";
						}
							
						else
						{
							if(banjiList.size()>0)
								contentText = "您"+dayStr+"有"+banjiList.size()+"门课要上";
							else
								contentText = "您"+dayStr+"没有课哦";
						}
						for(String item:banjiList)
							contentText+="\r\n"+item;
					}
					Log.d("alarm", contentText);
					showDialog(intent, contentTitle, contentText);
					
				}
				/****************************** 课程提醒 end ********************************************/
			}else{
				/************************** 修改学生考勤信息 begin ************************************************/
				if ("submitdata".equals(actionName)  || "changekaoqininfo".equals(actionName)) { //提交手机本地数据到服务器
					
					// 根据标识isModify=1查询需要提交服务器的数据
					teacherInfoList = teacherInfoDao.queryBuilder().where()
							.eq("isModify", 1).query();

					/**
					 * 获取学生考勤修改数据
					 */
					String kaoqinJsonStr = getChangekaoqininfo(teacherInfoList);
					Log.d(TAG, "kaoqinJsonStr:" + kaoqinJsonStr);
					// base64加密处理
					if (!"".equals(kaoqinJsonStr) && kaoqinJsonStr != null) {
						String kaoqinBase64 = Base64.encode(kaoqinJsonStr
								.getBytes());
						SubmitChangeinfo(kaoqinBase64, "changekaoqininfo");
					}

				}
				
				/************* 修改学生考勤信息 end ************/
				
				/************************** 修改学生信息 begin ************************************************/
				/*
				if ("submitdata".equals(actionName)  || "changestudentinfo".equals(actionName)) {
					// 根据标识isModify=1查询需要提交服务器的数据
					studentList = studentDao.queryBuilder().where()
							.eq("isModify", 1).query();
					
					String studentJsonStr = getChangestudentinfo(studentList);
					Log.d(TAG, "studentJsonStr:" + studentJsonStr);
					if (!"".equals(studentJsonStr) && studentJsonStr != null) {
						String studentBase64 = Base64.encode(studentJsonStr
								.getBytes());
						SubmitChangeinfo(studentBase64, "changestudentinfo");
					}
				}
				*/
				/************* 修改学生信息 end ************/
				/****************************** 修改教师信息 begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeuser".equals(actionName)) {
					// 根据标识isModify=1查询需要提交服务器的数据
					userInfo = userDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					
					/**
					 * 获取教师信息修改数据
					 */
					String teacherJsonStr = getChangeUserInfo(userInfo);
					Log.d(TAG, "teacherJsonStr:" + teacherJsonStr);
					// base64加密处理
					if (!"".equals(teacherJsonStr) && teacherJsonStr != null) {
						String teacherBase64 = Base64.encode(teacherJsonStr
								.getBytes());
						SubmitChangeinfo(teacherBase64, "changeuser");
					}
				}
				/************* 修改教师信息 end ************/
				/****************************** 修改课堂测验状态 begin ********************************************/
				if("submitdata".equals(actionName) || "changeceyan".equals(actionName)){
					teacherInfoList = teacherInfoDao.queryBuilder().where().eq("isModify", 1).query();
					Log.d(TAG, "--------------testList-------------" + teacherInfoList.size());
					String ceyanJsonStr = getChangeceyanStatus(teacherInfoList);
					System.out.println("ceyanJsonStr:" + ceyanJsonStr);
					if(!"".equals(ceyanJsonStr) && ceyanJsonStr != null){
						String ceyanBase64 = Base64.encode(ceyanJsonStr.getBytes());
						SubmitChangeinfo(ceyanBase64, "changeceyanzhuangtai");
					}
				}
				/************* 修改课堂测验状态 end ************/
				/****************************** 提交意见反馈 begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeSuggestion".equals(actionName)) {
					suggestions = suggestDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					String suggJsonString =  getChangeSuggestInfo(suggestions);
					if (!"".equals(suggJsonString) && suggJsonString != null) {
						String SuggBase64 = Base64.encode(suggJsonString
								.getBytes());
						SubmitFeedback(SuggBase64, "changeSuggestion");
					}
				}
				/************* 提交意见反馈 end ************/
				/****************************** 发送班级通知 begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeNoticeClass".equals(actionName)) {
					notices = noticeDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					String noticeJsonString =  getChangeNoticeClassInfo(notices);
					if (!"".equals(noticeJsonString) && noticeJsonString != null) {
						String NoticeBase64 = Base64.encode(noticeJsonString
								.getBytes());
						SubmitNoticeClass(NoticeBase64, "changeNoticeClass");
					}
				}
				/************* 发送班级通知 end ************/
				/****************************** 上传课件 begin ********************************************/
				if ("submitdata".equals(actionName)  || "changeDownloadSubject".equals(actionName)) {
					downloadSubject = downloadSubjectDao.queryBuilder().where().eq("isModify", 1)
							.queryForFirst();
					if (downloadSubject!=null) {
						SubmitUploadFile("changeDownloadSubject");
					}
				}
				/************* 上传课件 end ************/
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void showDialog(Intent intent,CharSequence contentTitle,CharSequence contentText){
		/*
		String isrunflag = intent.getStringExtra("isStartTabHostActivity");
		if ("true".equals(isrunflag) || "false".equals(isrunflag)) {
			PrefUtility.put("isrunflag", isrunflag);
		}
		String isStartTabHostActivity = PrefUtility.get("isrunflag", "true");
		if ("true".equals(isStartTabHostActivity)) {
			Intent remindintent = new Intent("remindSubject");
			remindintent.putExtra("contentText", contentText);
			context.sendBroadcast(remindintent);
		}else{
			setNotification(contentTitle, contentText,TabHostActivity.class);
		}
		*/
		setNotification(contentTitle, contentText,TabHostActivity.class);
	}
	
	@SuppressWarnings("deprecation")
	public void setNotification(CharSequence contentTitle,CharSequence contentText,Class activity){
		//消息通知栏
		//定义NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		//定义通知栏展现的内容信息
		int icon = R.drawable.ic_logo1;
		CharSequence tickerText = "掌上校园通知";
		long when = System.currentTimeMillis();
		
		Intent notificationIntent = new Intent(context, activity);
		notificationIntent.putExtra("contentText", contentText);
		notificationIntent.putExtra("tab", "1");
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		notificationIntent, 0);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setWhen(when);
        builder.setAutoCancel(true);
        builder.setTicker(tickerText);
        builder.setContentTitle(contentTitle);               
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(false);
        //builder.setSubText("This is subtext...");   //API level 16
        //builder.setNumber(100);
        builder.build();
		
        Notification notification = builder.getNotification();
        mNotificationManager.cancel(2);
        mNotificationManager.notify(2, notification);
        
        /*
		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults=Notification.DEFAULT_SOUND;  
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		//定义下拉通知栏时要展现的内容信息
		//Context context = getApplicationContext();
		
		
		notification.setLatestEventInfo(context, contentTitle, contentText,
		contentIntent);
		mNotificationManager.cancel(2);
		//用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(2, notification);
		*/
		Log.d("alarm", notificationIntent.getExtras().toString());
	}

	/**
	 * 功能描述:加工需要修改的考勤数据
	 * 
	 * @author yanzy 2013-12-3 下午2:51:37
	 * 
	 * @param subjectIdList
	 */
	public String getChangekaoqininfo(List<TeacherInfo> teacherInfoList) {
		try {
			if (teacherInfoList != null && teacherInfoList.size() > 0) {
				Log.d(TAG, "teacherInfoList.size():" + teacherInfoList.size());
				JSONArray jsonArray = new JSONArray();

				for (TeacherInfo teacherInfo : teacherInfoList) {
					Log.d(TAG, "------------------->teacherInfo.getAbsenceJson():"+teacherInfo.getAbsenceJson());
					String absenceJson = teacherInfo.getAbsenceJson();
					JSONObject joAbsence = new JSONObject();
					if (AppUtility.isNotEmpty(absenceJson)) {
						JSONArray ja = new JSONArray(teacherInfo.getAbsenceJson());
						if (ja != null && ja.length() > 0) {
							for (int i = 0; i < ja.length(); i++) {
								JSONObject jo = ja.getJSONObject(i);
								joAbsence.put(jo.optString("学号"), jo.optString("考勤类型"));
							}
						}
					}
					
					JSONObject jo = new JSONObject();
					jo.put("用户较验码", checkCode);
					jo.put("编号", teacherInfo.getId() + "");
					jo.put("授课内容", teacherInfo.getCourseContent() + "");
					jo.put("作业布置", teacherInfo.getHomework() + "");
					jo.put("课堂情况", "");
					jo.put("课堂纪律", teacherInfo.getClassroomDiscipline() + "");
					jo.put("教室卫生", teacherInfo.getClassroomHealth() + "");
					jo.put("班级人数", teacherInfo.getClassSize() + "");
					jo.put("实到人数", teacherInfo.getRealNumber() + "");
					jo.put("缺勤情况登记", "");
					jo.put("填写时间", "");
					jo.put("缺勤情况登记JSON", joAbsence);
					jsonArray.put(jo);
				}
				Log.d(TAG, "----------------------json:"+ jsonArray.toString());
				return jsonArray.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	/**
	 * 功能描述:加工需要修改的学生数据
	 * 
	 * @author yanzy 2013-12-5 上午10:17:27
	 * 
	 * @param studentList
	 * @return
	 */
	public String getChangestudentinfo(List<Student> studentList) {
		if (studentList != null && studentList.size() > 0) {
			Log.d(TAG, "studentList.size():" + studentList.size());
			JSONArray jsonArray = new JSONArray();
			try {
				for (Student student : studentList) {
					JSONObject jo = new JSONObject();
					jo.put("用户较验码", checkCode);
					jo.put("编号", student.getStudentID());
					jo.put("性别", student.getGender());
					jo.put("学生电话", student.getPhone());
					jo.put("学生邮箱", student.getEmail());
					jo.put("家长姓名", student.getParentName());
					jo.put("家长电话", student.getParentPhone());
					jo.put("家庭住址", student.getHomeAddress());
					jo.put("备注", student.getRemark());
					jsonArray.put(jo);
				}
				return jsonArray.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 功能描述:加工需要修改的教师信息
	 * 
	 * @author zhuliang 2013-12-6 下午4:31:06
	 * 
	 * @param userInfo
	 * @return
	 */
	public String getChangeUserInfo(User userInfo) {
		if (userInfo != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("用户较验码", checkCode);
				jo.put("编号", userInfo.getId());
				jo.put("性别", userInfo.getGender());
				jo.put("出生日期", userInfo.getBirthday());
				jo.put("手机", userInfo.getPhone());
				jo.put("电邮", userInfo.getEmail());
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	/**
	 * 功能描述:加工需要修改的班级通知信息
	 *
	 * @author linrr  2013-12-16 下午5:57:23
	 * 
	 * @param suggestion
	 * @return
	 */
	public String getChangeNoticeClassInfo(NoticeClass notices) {
		if (notices != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("用户较验码", checkCode);
				jo.put("action",  "DataDeal");
				jo.put("CONTENT", notices.getNotice());
				jo.put("DATETIME", String.valueOf(new Date().getTime()));
				jo.put("班级名称",notices.getClassName());//找到班级。。。。。。
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	/***
	 * 功能描述:加工需要修改的意见反馈信息
	 *
	 * @author linrr  2013-12-16 上午11:14:57
	 * 
	 * @param userInfo
	 * @return
	 */
	public String getChangeSuggestInfo(Suggestions suggestion) {
		if (suggestion != null) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("用户较验码", checkCode);
				jo.put("action",  "DataDeal");
				jo.put("CONTENT", suggestion.getSuggest());
				jo.put("DATETIME", String.valueOf(new Date().getTime()));
				return jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
		
	}
	private String getChangeceyanStatus(List<TeacherInfo> teacherInfoList) {
		if (teacherInfoList != null && teacherInfoList.size() > 0) {
			JSONArray jaTest = new JSONArray();
			for (TeacherInfo teacherInfo : teacherInfoList) {
				JSONObject jo = new JSONObject();
				try {
					jo.put("用户较验码", checkCode);
					jo.put("教师上课记录编号", teacherInfo.getId());
					jo.put("课堂测验状态", teacherInfo.getTestStatus());
					jaTest.put(jo);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return jaTest.toString();
		}
		return null;
	}
	/**
	 * 
	 * 功能描述:提交班级通知
	 *
	 * @author linrr  2013-12-16 下午5:52:47
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitNoticeClass(String base64Str,final String action){
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.noticeClass(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 2;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * 功能描述:上传文件
	 *
	 * @author linrr  2013-12-18 上午11:48:59
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitUploadFile(final String action){
		CampusParameters params = new CampusParameters();
		params.add("用户较验码", checkCode);
		params.add("文件名称", downloadSubject.getFileName());
		params.add("文件内容", downloadSubject.getFilecontent());
		CampusAPI.uploadFiles(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * 功能描述:功能描述:提交服务器
	 *
	 * @author linrr  2013-12-16 下午2:18:41
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitFeedback(String base64Str,final String action){
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.feedback(params, new RequestListener(){

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Log.d(TAG, "response"+response);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = bundle;
				mHandler.sendMessage(msg);	
				
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	/**
	 * 功能描述:提交服务器
	 * 
	 * @author yanzy 2013-12-4 上午10:10:42
	 * 
	 * @param base64Str
	 */
	public void SubmitChangeinfo(String base64Str, final String action) {
		CampusParameters params = new CampusParameters();
		params.add("action", action);
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.Changeinfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				System.out.println("response.toString()"+response.toString());
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Bundle bundle = new Bundle();
			switch (msg.what) {
			case 0:
				bundle = (Bundle) msg.obj;
				String action = bundle.getString("action");
				String result = bundle.getString("result");
				String resultStr = new String(Base64.decode(result));
				Log.d(TAG, "action:" + action);
				Log.d(TAG, "result"+result);
				Log.d(TAG, "resultStr:" + resultStr);
				
				try {
					JSONObject jo = new JSONObject(resultStr);
					System.out.println("jo.optString(成功):"+jo.optString("成功"));
					if ("1".equals(jo.optString("成功"))) {
						if ("changekaoqininfo".equals(action)) {
							//将标识更新为isModify=0
							PreparedUpdate<TeacherInfo> preparedUpdateStudentSubject = (PreparedUpdate<TeacherInfo>) teacherInfoDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
							teacherInfoDao.update(preparedUpdateStudentSubject);
						}
						
						if("changeceyanzhuangtai".equals(action)){
							// 将标识更新为isModify=0
							PreparedUpdate<TeacherInfo> preparedUpdateStudentSubject = (PreparedUpdate<TeacherInfo>) teacherInfoDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
							teacherInfoDao.update(preparedUpdateStudentSubject);
						}
						if ("changeuser".equals(action)) {
							// 将标识更新为isModify=0
							PreparedUpdate<User> preparedUpdateUser = (PreparedUpdate<User>) userDao
									.updateBuilder().updateColumnValue("isModify", 0)
									.where().eq("isModify", 1).prepare();
							userDao.update(preparedUpdateUser);
						}
						
						if ("submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "保存成功！");
						}
					}else{
						if (!"submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "保存失败！");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			case 1:
				bundle = (Bundle) msg.obj;
				String action1 = bundle.getString("action");
				String result1 = bundle.getString("result");
				Log.d(TAG, "action:" + action1);
				Log.d(TAG, "result2"+result1);
				String resultStr1 = new String(Base64.decode(result1));
				Log.d(TAG, "resultStr2:" + resultStr1);
				try {
					JSONObject jo = new JSONObject(resultStr1);
					System.out.println("jo.optString(消息):"+jo.optString("MSG_STATUS"));
					if("成功".equals(jo.optString("MSG_STATUS"))){
						//将标识更新为isModify=0
						PreparedUpdate<Suggestions> preparedUpdateSuggestion = (PreparedUpdate<Suggestions>) suggestDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						suggestDao.update(preparedUpdateSuggestion);
						if ("submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "发送成功！");
						}
					}else{
						if (!"submitdata".equals(actionName)) {
							DialogUtility.showMsg(context, "发送失败！");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				bundle = (Bundle) msg.obj;
				String action2 = bundle.getString("action");
				String result2 = bundle.getString("result");
				try {
					PreparedUpdate<NoticeClass> preparedUpdateNoticeClass = (PreparedUpdate<NoticeClass>) noticeDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
					noticeDao.update(preparedUpdateNoticeClass);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			case 3:
				bundle = (Bundle) msg.obj;
				String action3 = bundle.getString("action");
				String result3 = bundle.getString("result");
				String resultStr3 = "";
				try {
					resultStr3 = new String(Base64.decode(result3.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "action3:" + action3);
				Log.d(TAG, "resultStr3:" + resultStr3);
				isdialog = true;
				try {
					JSONObject jo = new JSONObject(resultStr3);
					Log.d(TAG, "jo.optString(STATUS):"+jo.optString("STATUS"));
					if("OK".equals(jo.optString("STATUS"))){
						//将标识更新为isModify=0
						PreparedUpdate<DownloadSubject> preparedUpdateDownloadSubject = (PreparedUpdate<DownloadSubject>) downloadSubjectDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						downloadSubjectDao.update(preparedUpdateDownloadSubject);
						DialogUtility.showMsg(context, "上传成功！");
					}else{
						DialogUtility.showMsg(context, "上传失败！");
					}
				}catch (Exception e) {
					e.printStackTrace();
				}	
				break;
			case 4:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result.getBytes("GBK")));
						Log.d(TAG, "----resultStr:"+resultStr);
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					JSONObject jo = null;
					try {
						jo = new JSONObject(resultStr);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(jo!=null){
						
						
					}
				}
				break;
			case 5:
				resultStr = msg.obj.toString();
				
				resultStr=resultStr.substring(resultStr.indexOf("({")+1);
				resultStr=resultStr.substring(0, resultStr.length()-1);
				String cityStr="";
				String addressStr="";
				Bundle bdl=msg.getData();
				double lat=bdl.getDouble("lat");
				double lon=bdl.getDouble("lon");
				
				JSONObject jo = null;
				try {
					jo = new JSONObject(resultStr);
					
					if(jo!=null && jo.getInt("status")==0)
					{
						jo=jo.getJSONObject("result");
						cityStr=jo.getJSONObject("addressComponent").getString("city");
						addressStr=jo.getString("formatted_address");
						JSONArray pois=jo.getJSONArray("pois");
						for(int i=0;i<pois.length();i++)
						{
							JSONObject item=pois.getJSONObject(i);
							addressStr+="\n"+item.getString("name");
							if(i>2) break;
						}
						String user_type = PrefUtility.get(Constants.PREF_CHECK_USERTYPE, "");
						if(user_type.equals("学生"))
							postGPS(cityStr,addressStr,lat,lon);
						User user=((CampusApplication)context.getApplicationContext()).getLoginUserObj();
						if(user!=null)
							user.setLatestAddress(addressStr);
					}
					
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				
				break;

			}
		};
	};
	
	@SuppressWarnings("unused")
	private class cancelStudentPicListener implements DialogInterface.OnClickListener{  
		   
        @Override  
        public void onClick(DialogInterface dialog, int which) {  
        	dialog.dismiss();
        }  
    }
	
	LocationListener locationListener = new LocationListener() {
		
		// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		// Provider被enable时触发此函数，比如GPS被打开
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		// Provider被disable时触发此函数，比如GPS被关闭 
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		//当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发 
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {   
				
				myLocation=location;
				locationManager.removeUpdates(locationListener);
		    	getRealAddress();
				
				Log.d("Map", "Location changed : Lat: "  
				+ location.getLatitude() + " Lng: "  
				+ location.getLongitude());   
				
			}
		}
	};
	
	private void getGPSLocation()
	{
		myLocation=null;
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
 		{
			locationManager.removeUpdates(locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0,locationListener);
			
			new Handler().postDelayed(new Runnable(){  
				public void run() {  
					locationManager.removeUpdates(locationListener);
					if(myLocation==null)
					{
						myLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(myLocation!=null)
							getRealAddress();
					}	
					
				}  
			}, 10000);
 		}
		
	}
	
	private void getNetLocation()
	{
		myLocation=null;
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
 		{
			locationManager.removeUpdates(locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0,locationListener);
			
			new Handler().postDelayed(new Runnable(){  
				public void run() {  
					locationManager.removeUpdates(locationListener);
					if(myLocation==null)
					{
						myLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if(myLocation!=null)
							getRealAddress();
						else
							getGPSLocation();
					}	
					
				}  
			}, 5000);
			
 		}
	
	}
	
	private void postGPS(String city,String address,double latitude,double longitude )
	{
		JSONObject jo = new JSONObject();
		String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		try {
			String datetime = String.valueOf(new Date().getTime());
			jo.put("ACTION", "");
			jo.put("用户较验码", user_code);
			jo.put("DATETIME", datetime);
			jo.put("城市", city);
			jo.put("详细地址", address);
			jo.put("纬度", latitude);
			jo.put("经度", longitude);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA,
				Base64.encode(jo.toString().getBytes()));
		CampusAPI.postGPS(params, new RequestListener() {
			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 4;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void getRealAddress()
	{
		if(myLocation==null) return;
		
		final double latitude=myLocation.getLatitude();
		final double longitude=myLocation.getLongitude();
		
		CampusAPI.getAddressFromBaidu(latitude,longitude, new RequestListener() {
			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 5;
				msg.obj = response;
				Bundle bundle=new Bundle();
				bundle.putDouble("lat", latitude);
				bundle.putDouble("lon", longitude);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(CampusException e) {
				// TODO Auto-generated method stub
				
			}
		});
		/*
		 * Geocoder geocoder=new Geocoder(TabHostActivity.this, Locale.getDefault()); 
		try {  
			
            List<Address> addresses=geocoder.getFromLocation(latitude, longitude, 5);  
            StringBuilder stringBuilder=new StringBuilder();                      
            if(addresses.size()>0){  
                Address address=addresses.get(0);  
                for(int i=0;i<address.getMaxAddressLineIndex();i++){  
                    stringBuilder.append(address.getAddressLine(i)).append("\n");   
                    if(i>3) break;
                }  
                String cityStr=address.getLocality();
                String addressStr=stringBuilder.toString();
                Log.d(TAG,"生命周期:"+addressStr);
                postGPS(cityStr,addressStr,latitude,longitude);
            }  
            
			
			
        } catch (IOException e) {  
        	getRealAddressFromBaidu(latitude,longitude);
            e.printStackTrace();  
        }  
        */
	}
	
	
	
}
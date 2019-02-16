package com.yujieshipin.crm.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.TabHostActivity;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.MyClassSchedule;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.PrefUtility;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by coder80 on 2014/10/31.
 */
public class UploadPOIService extends Service implements Runnable{
    private String TAG = UploadPOIService.class.getSimpleName();
    private Dao<MyClassSchedule, Integer> myClassScheduleDao;
    DatabaseHelper database;
    @Override
    public void onCreate() {
        super.onCreate();
        database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        try {
			myClassScheduleDao = database.getMyClassScheduleDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        uploadPOIInfo();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "UploadPOIService onDestroy here.... ");
    }

    private void uploadPOIInfo() {
    	//simulation HTTP request to server 
    	new Thread(this).start();
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
	
		return null;
	}

	@Override
	public void run() {
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
		if (booleanReminderDayClass && 1==0) {
			
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
			List<MyClassSchedule> dayclassList = null;
			try {
				dayclassList = myClassScheduleDao.queryBuilder().where().eq("courseDate", theDay).query();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			setNotification(contentTitle, contentText,TabHostActivity.class);
			
		}
		stopSelf();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	public void setNotification(CharSequence contentTitle,CharSequence contentText,Class activity){
		//消息通知栏
		//定义NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//定义通知栏展现的内容信息
		int icon = R.drawable.ic_logo1;
		CharSequence tickerText = "掌上校园通知";
		long when = System.currentTimeMillis();
		
		Intent notificationIntent = new Intent(this, activity);
		notificationIntent.putExtra("contentText", contentText);
		notificationIntent.putExtra("tab", "1");
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		notificationIntent, 0);
		
		Notification.Builder builder = new Notification.Builder(this);
		builder.setWhen(when);
        builder.setAutoCancel(true);
        builder.setTicker(tickerText);
        builder.setContentTitle(contentTitle);               
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.ic_launcher1);
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
}

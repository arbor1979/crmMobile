package com.yujieshipin.crm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.apache.http.client.HttpClient;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.yujieshipin.crm.activity.LoginActivity;
import com.yujieshipin.crm.activity.TabHostActivity;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.AllInfo;
import com.yujieshipin.crm.entity.ContactsFriends;
import com.yujieshipin.crm.entity.ContactsInfo;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.Student;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.service.Alarmreceiver;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.ZLibUtils;

public class CampusApplication extends Application {
	private HttpClient httpClient;
	private Map<String,ContactsMember> linkManDic;//所有联系人
	private List<ContactsFriends>  linkGroupList;//联系人组
	private Map<String,List<Student>>  studentDic;//所带学生
	private User loginUserObj; //当前登录用户
	

	private DatabaseHelper database;
	public void reLogin()
	{
		Intent intent = new Intent(this,
				LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//System.exit(0);
	}
	public Map<String, ContactsMember> getLinkManDic() {
		/*
		if(linkManDic==null)
		{
			getLinkManFromPref();
		}
		*/
		if(linkManDic==null)
			reLogin();
		return linkManDic;
	}

	public void setLinkManDic(Map<String, ContactsMember> linkManDic) {
		this.linkManDic = linkManDic;
	}
	private void getLinkManFromPref()
	{
		String str=PrefUtility.get(Constants.PREF_INIT_CONTACT_STR,"");
		byte[] contact64byte = null;
		String resultContact = "";
		try {
			if (AppUtility.isNotEmpty(str)) {
				contact64byte = Base64.decode(str.getBytes("GBK"));
				resultContact = ZLibUtils.decompress(contact64byte);
				Object obj=JSONValue.parseStrict(resultContact);
				ContactsInfo contacts = new ContactsInfo(obj);
				if (contacts != null) {
					
					setLinkManDic(contacts.getLinkManDic());
					setLinkGroupList(contacts.getContactsFriendsList());
					
				}
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getStudentFromPref()
	{
		String str=PrefUtility.get(Constants.PREF_INIT_DATA_STR,"");
		byte[] contact64byte = null;
		String resultContact = "";
		try {
			if (AppUtility.isNotEmpty(str)) {
				contact64byte = Base64.decode(str.getBytes("GBK"));
				resultContact = ZLibUtils.decompress(contact64byte);
				net.minidev.json.JSONObject obj=(net.minidev.json.JSONObject) JSONValue.parseStrict(resultContact);
				AllInfo allInfo = new AllInfo(obj);
				setStudentDic(allInfo.getStudentList());
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<ContactsFriends> getLinkGroupList() {
		/*
		if(linkGroupList==null)
		{
			getLinkManFromPref();
		}
		*/
		if(linkGroupList==null)
			reLogin();
		return linkGroupList;
	}

	public void setLinkGroupList(List<ContactsFriends> linkGroupList) {
		this.linkGroupList = linkGroupList;
	}

	public Map<String, List<Student>> getStudentDic() {
		/*
		if(studentDic==null)
		{
			getStudentFromPref();			
		}
		*/
		if(studentDic==null)
			reLogin();
		return studentDic;
	}

	public void setStudentDic(Map<String, List<Student>> studentDic) {
		this.studentDic = studentDic;
	}

	public User getLoginUserObj() {
		
		if(loginUserObj==null)
		{
			loginUserObj=getUserByDao();
		}
		return loginUserObj;
	}
	public User getLoginUserObjAllowNull() {
		
		if(loginUserObj==null)
		{
			reLogin();
		}
		
		return loginUserObj;
	}
	private User getUserByDao()
	{
		Dao<User, Integer> userDao;
		User user=null;
		 try {
				userDao = getHelper().getUserDao();
				user=userDao.queryBuilder().queryForFirst();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return user;
	}
	

	public void setLoginUserObj(User loginUserObj) {
		this.loginUserObj = loginUserObj;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CrashReport.initCrashReport(getApplicationContext(), "676b741b10", false);
		AppUtility.setContext(this);
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File baseDirFile = getExternalFilesDir(null);
			if(baseDirFile == null) {
				FileUtility.cacheDir = getFilesDir().getAbsolutePath();
			} else {
				FileUtility.cacheDir = baseDirFile.getAbsolutePath();
			}
		}
		else
			FileUtility.cacheDir=this.getFilesDir().getAbsolutePath();

		FileUtility.creatSDDir(FileUtility.SDPATH);
		updateTable();
		UMConfigure.setLogEnabled(false);

		//UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "2125a30891e11a84849dceff9ec03c42");
		UMConfigure.init(this, "5c46cc5ab465f53030000580", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "0ba361e0d1662ec403dd8bf429a233ad");
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
		PushAgent mPushAgent = PushAgent.getInstance(this);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回device token
				Log.d("app_deviceToken",deviceToken);
				PrefUtility.put(Constants.PREF_BAIDU_USERID, deviceToken);
				String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
				if(checkCode.length()>0)
				{
					InitData initData = new InitData(AppUtility.getContext(), OpenHelperManager.getHelper(AppUtility.getContext(), DatabaseHelper.class), null,"postBaiDuUserId",checkCode);
					initData.postBaiduUserId();
				}
			}

			@Override
			public void onFailure(String s, String s1) {
				Log.d("app_deviceToken",s1);
			}
		});
		UmengMessageHandler messageHandler = new UmengMessageHandler() {
			@Override
			public Notification getNotification(Context context, UMessage msg) {
				switch (msg.builder_id) {
					case 1:
						Notification.Builder builder = new Notification.Builder(context);
						RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
								R.layout.notification_view);
						myNotificationView.setTextViewText(R.id.notification_title, msg.title);
						myNotificationView.setTextViewText(R.id.notification_text, msg.text);
						myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
								getLargeIcon(context, msg));
						myNotificationView.setImageViewResource(R.id.notification_small_icon,
								getSmallIconId(context, msg));
						builder.setContent(myNotificationView)
								.setSmallIcon(getSmallIconId(context, msg))
								.setTicker(msg.ticker)
								.setAutoCancel(true);

						return builder.getNotification();
					default:
						//默认为0，若填写的builder_id并不存在，也使用默认。
						Intent intent = new Intent(AppUtility.getContext(), Alarmreceiver.class);
						intent.setAction("getMsgList");
						sendBroadcast(intent);
						return super.getNotification(context, msg);

				}
			}
		};

		mPushAgent.setMessageHandler(messageHandler);
		mPushAgent.setNotificaitonOnForeground(false);
		MiPushRegistar.register(this, "2882303761517938977", "5301793856977");
		HuaWeiRegister.register(this);
		MeizuRegister.register(this, "1003855", "487e283823b44260bee2d9203361532b");

	}

	public static Context getContext() {
		return AppUtility.getContext();
		
	}
	private void updateTable()
	{
		updateColumn(getHelper().getWritableDatabase(), "ChatFriend", "hostid", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "ChatMsg", "hostid", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "ChatMsg", "remoteimage", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "ChatMsg", "sendstate", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "ChatMsg", "msg_id", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "AlbumMsgInfo", "toName", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "Schedule", "WeekBeginDay", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "Schedule", "WeekEndDay", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "Student", "liveSchool", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "Student", "zuohao", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "AccountInfo", "siteUrl", "varchar", "''");
		updateColumn(getHelper().getWritableDatabase(), "ChatMsg", "linkUrl", "varchar", "''");
		/*
		try {
			TableUtils.createTable(getHelper().getConnectionSource(), ChatMsgDetail.class);
		} catch (SQLException e) {
			
		}*/
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}
	private void updateColumn(SQLiteDatabase db, String tableName,
            String columnName, String columnType, Object defaultField) {
    try {
            if (db != null) {
                    Cursor c = db.rawQuery("SELECT * from " + tableName
                                    + " limit 1 ", null);
                    boolean flag = false;

                    if (c != null) {
                            for (int i = 0; i < c.getColumnCount(); i++) {
                                    if (columnName.equalsIgnoreCase(c.getColumnName(i))) {
                                            flag = true;
                                            break;
                                    }
                            }
                            if (flag == false) {
                                    String sql = "alter table " + tableName + " add "
                                                    + columnName + " " + columnType + " default "
                                                    + defaultField;
                                    db.execSQL(sql);
                            }
                            c.close();
                    }
            }
    } catch (Exception e) {
            e.printStackTrace();
    }
}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		AQUtility.cleanCache(AQUtility.getCacheDir(this, AQuery.CACHE_DEFAULT), 0, 0);
		BitmapAjaxCallback.clearCache();
		FileUtility.deleteFileFolder(FileUtility.getCacheDir());
		FileUtility.deleteFileFolder(FileUtility.creatSDDir("相册"));
		FileUtility.deleteFileFolder(FileUtility.creatSDDir("download"));
		Runtime.getRuntime().gc();
		this.shutdownHttpClient();
		
	}
	
	// 关闭连接管理器并释放资源
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	// 对外提供HttpClient实例
	public HttpClient getHttpClient() {
		return httpClient;
	}
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public static String getVersion() {
	    try {
	        PackageManager manager = getContext().getPackageManager();
	        PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
	        return info.versionName;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}

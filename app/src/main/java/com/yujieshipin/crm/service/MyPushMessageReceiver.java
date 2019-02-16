package com.yujieshipin.crm.service;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.ChatMsgActivity;
import com.yujieshipin.crm.activity.LoginActivity;
import com.yujieshipin.crm.activity.TabHostActivity;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.ChatFriend;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.BaiduPushUtility;
import com.yujieshipin.crm.util.PrefUtility;

/**
 * Push消息处理receiver
 */
/*
public class MyPushMessageReceiver extends PushMessageReceiver  {

	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	AlertDialog.Builder builder;

	//private Dao<ChatMsg,Integer> chatMsgDao;
	private Dao<ChatFriend,Integer> chatFriendDao;

	DatabaseHelper database;

	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			BaiduPushUtility.setBind(true);
		}
		PrefUtility.put(Constants.PREF_BAIDU_USERID, userId);

		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		if(checkCode.length()>0)
		{
			InitData initData = new InitData(context, OpenHelperManager.getHelper(context, DatabaseHelper.class), null,"postBaiDuUserId",checkCode);
			initData.postBaiduUserId();
		}
		Log.d(TAG, "--------->baiduuserid:"+userId);
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}


	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
        	BaiduPushUtility.setBind(false);
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        //updateContent(context, responseString);
    }



	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}



	@SuppressLint("NewApi")
	@Override
	public void onMessage(Context context, String message,
            String customContentString) {
		// TODO Auto-generated method stub
		if (AppUtility.isNotEmpty(message)) {
			try {
				Log.d(TAG, "Chatmessage:"+message);
				if(database==null)
					database = OpenHelperManager.getHelper(context, DatabaseHelper.class);
				chatFriendDao = database.getChatFriendDao();
				JSONObject jo = new JSONObject(message);
				Log.d(TAG, "--------------->jo:"+jo.toString());
//					String from_datetime = jo.optString("FROM_DATETIME");
				//String from_timeline = jo.optString("FROM_TIMELINE");
				String from_userid_unique = jo.optString("FROM_USERID_UNIQUE"); //唯一码 toid
				String type = jo.optString("type");
				String description = jo.optString("description"); //消息内容
				//String from_baidu_userid = jo.optString("FROM_BAIDU_USERID");
				String toid = from_userid_unique;
				String toname = jo.optString("FROM_USERID_NAME");
				String userImage = jo.optString("FROM_USERID_IMAGE");
				String msg_type = "";
				String content = description;
				String msg_id=jo.optString("MSG_ID");
				int unreadCnt = 0;
				//判断用户是否在聊天列表中
				chatFriendDao = database.getChatFriendDao();
				String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");
				ChatFriend chatFriend = chatFriendDao.queryBuilder().where().eq("toid", from_userid_unique).and().eq("hostid", hostid).queryForFirst();
				if(chatFriend!=null)
					chatFriend.setUnreadCnt(chatFriend.getUnreadCnt()+1);
				InitData initData = new InitData(context, database, null, null,null);
				initData.sendChatToDatabase(type,toid, toname, 0, content, chatFriend,msg_type,userImage,msg_id);
				Intent intentChat = new Intent("ChatInteract");
				context.sendBroadcast(intentChat);

				List<ChatFriend> chatFriendList = chatFriendDao.queryBuilder().where().eq("hostid", hostid).query();
				for (ChatFriend item:chatFriendList) { //在聊天列表中，更新最后聊天内容，最后聊天时间

					unreadCnt += item.getUnreadCnt();

				}
				User user=((CampusApplication)context.getApplicationContext()).getLoginUserObj();
				if (user==null || AppUtility.isApplicationBroughtToBackground(context) || AppUtility.isLockScreen(context)) {
					//消息通知栏
					//定义NotificationManager
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					//定义通知栏展现的内容信息
					int icon = R.drawable.ic_logo1;
					long when = System.currentTimeMillis();

					if(type.equals("img"))
						content="[图片]";
					else
					{
						if(content.length()>12)
							content=content.substring(0,12);
					}



					//定义下拉通知栏时要展现的内容信息
					//Context context = getApplicationContext();
					CharSequence contentText = String.valueOf(unreadCnt)+"条未读消息";
					Intent notificationIntent=null;
					if(user==null)
						notificationIntent = new Intent(context, LoginActivity.class);
					else
					{

						notificationIntent = new Intent(context, TabHostActivity.class);
						notificationIntent.putExtra("tab", "2");
					}
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					notificationIntent.putExtra("toid", toid);
					notificationIntent.putExtra("toname", toname);
					notificationIntent.putExtra("userImage", userImage);
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

					Notification.Builder builder = new Notification.Builder(context);
					builder.setWhen(when);
			        builder.setAutoCancel(true);
			        builder.setTicker(toname+":"+content);
			        builder.setContentTitle(contentText);
			        builder.setContentText(toname+":"+content);
			        builder.setSmallIcon(R.drawable.ic_launcher1);
			        builder.setContentIntent(contentIntent);
			        builder.setOngoing(false);
			        //builder.setSubText("This is subtext...");   //API level 16
			        //builder.setNumber(100);
			        builder.setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE);
			        builder.build();
			        Notification notification = builder.getNotification();
			        mNotificationManager.notify(1, notification);


					Notification notification = new Notification(icon, toname+":"+content, when);
					notification.setLatestEventInfo(context,contentText, toname+":"+content,
					contentIntent);
					notification.flags|=Notification.FLAG_AUTO_CANCEL;
					notification.defaults = Notification.DEFAULT_SOUND;
					//用mNotificationManager的notify方法通知用户生成标题栏消息通知
					mNotificationManager.notify(1, notification);

				}
				else
				{
					if(ChatMsgActivity.isruning && ChatMsgActivity.toid.equals(toid))
					{
						AppUtility.playSounds(R.raw.tw_touch, context);
					}
					else
						AppUtility.playSounds(R.raw.tweet_sent, context);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
*/

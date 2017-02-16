package com.yujieshipin.crm.db;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONValue;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AllInfo;
import com.yujieshipin.crm.entity.ChatFriend;
import com.yujieshipin.crm.entity.ChatMsg;
import com.yujieshipin.crm.entity.ContactsFriends;
import com.yujieshipin.crm.entity.ContactsInfo;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.Dictionary;
import com.yujieshipin.crm.entity.DownloadInfo;
import com.yujieshipin.crm.entity.MyClassSchedule;
import com.yujieshipin.crm.entity.Schedule;
import com.yujieshipin.crm.entity.StudentAttence;
import com.yujieshipin.crm.entity.StudentPic;
import com.yujieshipin.crm.entity.StudentScore;
import com.yujieshipin.crm.entity.StudentTest;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.TestEntity;
import com.yujieshipin.crm.entity.TestStartEntity;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.SerializableMap;
import com.yujieshipin.crm.util.WifiUtility;
import com.yujieshipin.crm.util.ZLibUtils;
import com.yujieshipin.crm.util.ZipUtility;

public class InitData {
	private static final String TAG = "InitData";
	DatabaseHelper database;
	private Context context;
	private Dialog mLoadingDialog;
	private String ACTION_NAME;

	private Dao<Schedule, Integer> scheduleDao;
	
	private Dao<TeacherInfo, Integer> teacherinfoDao;
	private Dao<TestEntity, Integer> testEntityDao;
	private Dao<TestStartEntity, Integer> startTestDao;

	private Dao<StudentAttence, Integer> studentAttenceDao;
	private Dao<Dictionary, Integer> dictionaryDao;
	private Dao<StudentScore, Integer> studentScoreDao;
	private Dao<StudentTest, Integer> studentTestDao;
	private Dao<StudentPic, Integer> studentPicDao;
	private Dao<MyClassSchedule, Integer> myClassScheduleDao;

	List<StudentPic> studentPicList;
	private int studentClassCnt = 0; // 需要加载头像的班级数量
	private int studentLoadClassCnt = 0; // 已经加载过头像的班级数量

	private Dao<ChatMsg, Integer> chatMsgDao;
	private Dao<ChatFriend, Integer> chatFriendDao;
	private Dao<ContactsMember,Integer> contactsMemberDao;
	private Dao<ContactsFriends,Integer> contactsFriendsDao;
	private Dao<DownloadInfo, Integer> downloadInfoDao;

	Date begindate;
	Date enddate;
	String checkCode;

	public InitData(Context context, DatabaseHelper database,
			Dialog mLoadingDialog, String ACTION_NAME, String checkCode) {
		this.context = context;
		this.database = database;
		this.mLoadingDialog = mLoadingDialog;
		this.ACTION_NAME = ACTION_NAME;
		this.checkCode = checkCode;
		Log.d(TAG, "---------------this.mLoadingDialog：" + this.mLoadingDialog);
	}

	/**
	 * 功能描述:加载Dao
	 * 
	 * @author yanzy 2013-12-26 下午12:07:35
	 * 
	 */
	private void initDao() {
		try {
			
			testEntityDao = getHelper().getTestEntityDao();
			teacherinfoDao = getHelper().getTeacherInfoDao();
			myClassScheduleDao= getHelper().getMyClassScheduleDao();
			startTestDao = getHelper().getStartTestDao();
			studentAttenceDao = getHelper().getStudentAttenceDao();
			dictionaryDao = getHelper().getDictionaryDao();
			studentScoreDao = getHelper().getStudentScoreDao();
			studentTestDao = getHelper().getStudentTestDao();
			studentPicDao = getHelper().getStudentPicDao();
			scheduleDao = getHelper().getScheduleDao();
	
			chatMsgDao = getHelper().getChatMsgDao();
			chatFriendDao = getHelper().getChatFriendDao();
			downloadInfoDao = getHelper().getDownloadInfoDao();
			contactsMemberDao=getHelper().getContactsMemberDao();
			contactsFriendsDao=getHelper().getContactsFriendsDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能描述:清空基础数据
	 * 
	 * @author yanzy 2013-12-26 下午12:07:23
	 * 
	 */
	private void deleteBaseData() {
		try {
			testEntityDao.delete((PreparedDelete<TestEntity>) testEntityDao
					.deleteBuilder().prepare());
			teacherinfoDao.delete((PreparedDelete<TeacherInfo>) teacherinfoDao
					.deleteBuilder().prepare());
			myClassScheduleDao.delete((PreparedDelete<MyClassSchedule>) myClassScheduleDao
					.deleteBuilder().prepare());
			startTestDao.delete((PreparedDelete<TestStartEntity>) startTestDao
					.deleteBuilder().prepare());
			studentAttenceDao
					.delete((PreparedDelete<StudentAttence>) studentAttenceDao
							.deleteBuilder().prepare());
			dictionaryDao.delete((PreparedDelete<Dictionary>) dictionaryDao
					.deleteBuilder().prepare());
			studentScoreDao
					.delete((PreparedDelete<StudentScore>) studentScoreDao
							.deleteBuilder().prepare());
			studentTestDao.delete((PreparedDelete<StudentTest>) studentTestDao
					.deleteBuilder().prepare());
			dictionaryDao.delete((PreparedDelete<Dictionary>) dictionaryDao
					.deleteBuilder().prepare());
			studentPicDao.delete((PreparedDelete<StudentPic>) studentPicDao
					.deleteBuilder().prepare());
			scheduleDao.delete((PreparedDelete<Schedule>) scheduleDao
					.deleteBuilder().prepare());
			//chatMsgDao.deleteBuilder().delete();
			//chatFriendDao.deleteBuilder().delete();
			downloadInfoDao.deleteBuilder().delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	private DatabaseHelper getHelper() {
		return database;
	}

	/**
	 * 功能描述:手动加载基础数据
	 * 
	 * @author yanzy 2013-12-12 上午10:26:10
	 * 
	 */
	public void myInitAllInfo() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"数据初始化中，请稍等...");
		initAllInfo();
	}

	/**
	 * 功能描述:手动加载学生头像
	 * 
	 * @author yanzy 2013-12-12 上午10:26:20
	 * 
	 */
	public void myInitStudentPicDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"初始化头像中，请稍等...");
		// 获取当前wifi网络状态
		WifiUtility wifi = new WifiUtility(context);
		int wifiState = wifi.checkState();
		Log.d(TAG, "-------------->wifiState:" + wifiState);
		if (wifiState == 3) { // wifi连网状态
			initStudentPic();
		} else {
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("信息提示");
			builder.setMessage("您当前非WIFI网络，继续更新将消耗GPRS流量");
			builder.setPositiveButton("继续", new initStudentPicListener());
			builder.setNegativeButton("取消", new cancelStudentPicListener());
			AlertDialog ad = builder.create();
			ad.show();
		}
	}

	public void myInitContactsDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"联系人数据初始化中，请稍等...");
		if (mLoadingDialog != null) {
			mLoadingDialog.show();
		}
		initContactInfo();
	}

	public void myInitStudentPic() {
		// 获取当前wifi网络状态
		WifiUtility wifi = new WifiUtility(context);
		int wifiState = wifi.checkState();
		Log.d(TAG, "-------------->wifiState:" + wifiState);
		if (wifiState == 3) { // wifi连网状态
			initStudentPic();
		} else {
			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("信息提示");
			builder.setMessage("您当前非WIFI网络，初始化联系人头像将消耗3G流量");
			builder.setPositiveButton("继续", new initStudentPicListener());
			builder.setNegativeButton("取消", new cancelStudentPicListener());
			AlertDialog ad = builder.create();
			ad.show();
		}
	}

	// 监听类
	private class initStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			initStudentPic();
		}

	}

	// 监听类
	private class cancelStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

	/**
	 * 功能描述:初始化基础数据
	 * 
	 * @author yanzy 2013-12-11 上午10:57:27
	 * 
	 */
	public void initAllInfo() {
		Log.d(TAG, "开始初始化基础数据");
		initDao(); // 加载Dao
		deleteBaseData(); // 清空表
		if (mLoadingDialog != null) {
			mLoadingDialog.show();
		}
		Log.d(TAG, "--------------->开始初始化数据到本地数据库：" + new Date());
		Log.d(TAG, "--------------->用户校验码" + checkCode);
		String dataResult = "";
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("用户较验码", checkCode);
			
			if(PrefUtility.getInt("weekFirstDay", 0)==0)
				jsonObj.put("周日为第一天", "1");
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "--->  [加密参数] =>" + dataResult);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		final Date dt=new Date();
		CampusAPI.initInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				Log.d(TAG, "--->  " + e);
			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "--->  " + e);
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------初始化耗时:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:获取教师上课记录
	 * 
	 * @author shengguo 2014-5-16 下午5:07:07
	 * 
	 */
	public void getTeacherInfos() {
		mLoadingDialog = DialogUtility.createLoadingDialog(context,
				"正在获取数据，请稍等...");
		mLoadingDialog.show();
		try {
			teacherinfoDao = getHelper().getTeacherInfoDao();
			teacherinfoDao.delete((PreparedDelete<TeacherInfo>) teacherinfoDao
					.deleteBuilder().prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String dataResult = null;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("用户较验码", checkCode);
			dataResult = Base64.encode(jsonObj.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "--->  [加密参数] =>" + dataResult);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, dataResult);
		CampusAPI.initInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "--->  " + response);
				Message msg = new Message();
				msg.what = 4;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:初始化联系人信息
	 * 
	 * @author yanzy 2013-12-11 上午10:57:40
	 * 
	 */
	public void initContactInfo() {
		Log.d(TAG, "开始初始化联系人信息");
		if(mLoadingDialog!=null)
			mLoadingDialog.show();
		initDao();
		
		CampusParameters params = new CampusParameters();

		try {
			JSONObject jo = new JSONObject();
			jo.put("用户较验码", checkCode);
			String datetime = String.valueOf(new Date().getTime());
			jo.put("DATETIME", datetime);
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		final Date dt=new Date();
		CampusAPI.getTeacherInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----------联系人耗时:" + (new Date().getTime()-dt.getTime()));
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:获取最近一次聊天记录
	 * 
	 * @QiaoLin  2014-6-18 上午10:35:40
	 * 
	 */
	public void initContactLastMsg() {
		Log.d(TAG, "开始初始化联系人最近一次聊天记录");
		
		initDao();
		try {
			chatFriendDao.deleteBuilder().delete();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		CampusParameters params = new CampusParameters();

		try {
			JSONObject jo = new JSONObject();
			jo.put("用户较验码", checkCode);
			String datetime = String.valueOf(new Date().getTime());
			jo.put("DATETIME", datetime);
			params.add(Constants.PARAMS_DATA,
					Base64.encode(jo.toString().getBytes()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.getLast_ATOALL(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "--->  " + response);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 功能描述:初始化联系人头像
	 * 
	 * @author yanzy 2013-12-11 下午5:16:53
	 * 
	 */
	public void initStudentPic() {
		Log.d(TAG, "开始初始化联系人头像");
		initDao(); // 加载Dao
		try {
			if (mLoadingDialog != null) {
				mLoadingDialog.show();
			}
			studentPicDao = getHelper().getStudentPicDao();
			studentPicList = studentPicDao.queryForAll();

			if (studentPicList != null && studentPicList.size() > 0) {
				studentClassCnt = studentPicList.size();
				for (StudentPic studentPic : studentPicList) {
					downLoadStudentPic(studentPic);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void postBaiduUserId() {
		
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		String baidu_userid = PrefUtility.get(Constants.PREF_BAIDU_USERID, "");
		String baidu_channelid = PrefUtility.get(Constants.PREF_BAIDU_CHANNELID, "");
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("用户较验码", checkCode);
			jsonObj.put("function", "postBaiduPushId");
			jsonObj.put("baidu_userid", baidu_userid);
			jsonObj.put("baidu_channelid", baidu_channelid);
			// 添加系统信息
			String SerialNumber = android.os.Build.SERIAL+"-"+Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID); 
			/*
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String SerialNumber ="";
			if(tm.getDeviceId()==null)
			{
				
			}
			else
				SerialNumber =tm.getDeviceId()+"-"+tm.getSimSerialNumber();
			*/
			jsonObj.put("设备唯一码", SerialNumber);
			jsonObj.put("设备名", android.os.Build.BRAND+" "+android.os.Build.PRODUCT);
			jsonObj.put("设备类型", "Android");
			jsonObj.put("本地模式", android.os.Build.VERSION.SDK);
			jsonObj.put("系统名", android.os.Build.USER);
			jsonObj.put("系统版本", android.os.Build.VERSION.RELEASE);
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
	        jsonObj.put("分辨率", dm.widthPixels+" * "+dm.heightPixels);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 6);
		
	}
	/**
	 * 功能描述:下载学生头像
	 * 
	 * @author yanzy 2013-12-9 上午10:12:19
	 * 
	 * @param studentPic
	 */
	private void downLoadStudentPic(final StudentPic studentPic) {
		CampusParameters params = new CampusParameters();
		String picUrl = studentPic.getPicUrl();
		params.add("picUrl", picUrl);
		CampusAPI.downLoadStudentPic(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = 3;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("className", studentPic.getClassName());
				bundle.putString("result", response.toString());
				Message msg = new Message();
				msg.what = 2;
				msg.obj = bundle;
				mHandler.sendMessage(msg);

			}
		});
	}

	// "课堂测验""课件下载":"学生详情信息卡""电子0901班_姓名列表"
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mLoadingDialog!=null) {
					mLoadingDialog.dismiss();
				}
				Thread thread=new saveInitData(msg.obj.toString());
				thread.start();
			        
				break;
			case 1:
				if (mLoadingDialog!=null) {
					mLoadingDialog.dismiss();
				}
				
				thread=new saveContractsData(msg.obj.toString());
				thread.start();
				
				break;
			case 2:
				Log.d(TAG, "-----------下载学生头像------------");
				Bundle bundle = (Bundle) msg.obj;
				String className = bundle.getString("className");
				String resultPic = bundle.getString("result");
				if (!"".equals(resultPic) && resultPic != null) {
					byte[] base64bytePic = null;
					try {
						base64bytePic = Base64
								.decode(resultPic.getBytes("GBK"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} // 解密base64字符串得到头像zip二进制数据
					String path = "PocketCampus/";
					String fileName = className + ".zip";
					if (base64bytePic != null) {
						// 将zip包下载到本地
						File file = FileUtility.writeSDFromByte(path, fileName,
								base64bytePic);
						if (file != null) {
							path = file.getPath();
							fileName = path.substring(0, path.indexOf("."))
									+ "/"; // 解压目标路径
							boolean isUnZip = ZipUtility.unZipFile(file,
									fileName); // 解压
							if (isUnZip) { // 解压成功，则删除zip包
								FileUtility.deleteFile(path);
							}
						}
					}

				}
				studentLoadClassCnt++;
				if (studentClassCnt == studentLoadClassCnt
						&& studentClassCnt > 0) {
					if (mLoadingDialog != null) {
						mLoadingDialog.dismiss();
					}

					if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
						Intent intent = new Intent(ACTION_NAME);
						intent.putExtra("initResult", "studentPic");
						context.sendBroadcast(intent);
					}
				}
				break;
			case 3:
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				AppUtility.showErrorToast(context, msg.obj.toString());
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					context.sendBroadcast(intent);
				}
				break;
			case 4:// 教师上课记录列表
				Log.d(TAG, "-------------获取教师上课记录列表---------");
				String resultStr = msg.obj.toString(); // 服务器返回的base64加密后的字符串
				Log.d(TAG, "--------------resultStr:" + resultStr);
				byte[] base64byte = null;
				try {
					base64byte = Base64.decode(resultStr.getBytes("GBK"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} // 解密base64字符串，得到压缩字节流，后台为php，采用gzcompress函数压缩，协议为zlib
				String unZlibStr1 = ZLibUtils.decompress(base64byte); // 采用zlib协议解压缩
				Log.d(TAG, "--------------unZlibStr:" + unZlibStr1);
				try {
					JSONObject jothacher = new JSONObject(unZlibStr1);
					String result = jothacher.optString("结果");
					if (AppUtility.isNotEmpty(result)) {
						AppUtility.showToastMsg(context, result);
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						break;
					}
					AllInfo allInfo = new AllInfo(jothacher);

					List<TeacherInfo> teacherInfosList = allInfo
							.getTeacherInfos();
					if (teacherInfosList != null && teacherInfosList.size() > 0) {
						for (TeacherInfo tt : teacherInfosList) {
							if (tt != null) {
								teacherinfoDao.create(tt);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					context.sendBroadcast(intent);
				}
				break;
			case 5:// 最近一次聊天记录
				Log.d(TAG, "-------------获取最近一次聊天记录---------");
				resultStr = msg.obj.toString(); // 服务器返回的base64加密后的字符串
				Log.d(TAG, "--------------resultStr:" + resultStr);
				base64byte = null;
				
				Map<String,String> lastMsgMap = new HashMap<String,String>();
				try {
					resultStr = new String(
							Base64.decode(resultStr.getBytes("GBK")));
					
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} 
				try {
					JSONObject jowasr = new JSONObject(resultStr);
					Iterator<?> keys =jowasr.keys();
					String name;
					JSONObject values;
					while (keys.hasNext()) {
						name = String.valueOf(keys.next());
						values = jowasr.getJSONObject(name);
						values=values.optJSONObject("最后一次聊天记录");
						String content=values.getString("CONTENT");
						if(!values.getString("TYPE").equals("txt"))
						{
							content="[图片]";
						}
						lastMsgMap.put(name,content);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} 
				
				if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
					Intent intent = new Intent(ACTION_NAME);
					Bundle bdl=new Bundle();
					SerializableMap myMap=new SerializableMap();
					myMap.setMap(lastMsgMap);
					bdl.putSerializable("result", myMap);					
					intent.putExtras(bdl);
					context.sendBroadcast(intent);
				}
				break;
			case 6:// 上传设备信息
				
				resultStr = msg.obj.toString(); // 服务器返回的base64加密后的字符串
				
				try {
					JSONObject jowasr = new JSONObject(resultStr);
					if(!jowasr.optString("result").equals("成功"))
					{
						AppUtility.showErrorToast(context,"可能无法接收即时消息");
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				} 
				break;
				
			default:
				break;
			}
			;
		}
	};

	/**
	 * 功能描述:
	 * 
	 * @author zhuliang 2014-1-15 上午11:41:22
	 * 
	 * @param type
	 * @param toid
	 * @param toname
	 * @param msgFlag
	 * @param content
	 * @param chatFriend
	 * @param msg_type
	 *            添加此字段，消息类型
	 * @return
	 */
	public ChatMsg sendChatToDatabase(String type, String toid, String toname,
			int msgFlag, String content, ChatFriend chatFriend, String msg_type,String userImage,String msg_id) {
		try {
			chatMsgDao = getHelper().getChatMsgDao();
			chatFriendDao = getHelper().getChatFriendDao();
			String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");

			// 判断用户是否在聊天列表中
			if (chatFriend != null) { // 在聊天列表中，更新最后聊天内容，最后聊天时间
				chatFriend.setLastTime(new Date());
				chatFriend.setLastContent(content);
				chatFriend.setType(type);
				chatFriend.setMsgType(msg_type);
				chatFriend.setUserImage(userImage);
				chatFriendDao.update(chatFriend);
			} else { // 不在聊天列表中，则添加到聊天列表中
				
				chatFriend = new ChatFriend();
				chatFriend.setHostid(hostid);
				chatFriend.setToid(toid);
				chatFriend.setLastTime(new Date());
				chatFriend.setLastContent(content);
				if(msgFlag==0)
					chatFriend.setUnreadCnt(1);
				chatFriend.setType(type);
				chatFriend.setUserImage(userImage);
				chatFriend.setMsgType(msg_type);
				chatFriend.setToname(toname);
				chatFriendDao.create(chatFriend);
			}
	
			// 聊天保存内容到本地数据
			ChatMsg entity = null;
			if ("txt".equals(type) || msgFlag == 0) {
				entity = new ChatMsg();
				entity.setType(type);
				entity.setHostid(hostid);
				entity.setToid(toid);
				entity.setToname(toname);
				entity.setTime(new Date());
				entity.setMsgFlag(msgFlag);
				entity.setContent(content);
				entity.setMsg_id(msg_id);
				chatMsgDao.create(entity);
			}
			return entity;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private class saveInitData extends Thread
	{
		private String str;
		public saveInitData(String str)
		{
			this.str = str;
		}
		public void run()
		{
			Log.d(TAG, "-----------初始化基础数据------------");
			Date dt=new Date();
			byte[] base64byte = null;
			try {
				base64byte = Base64.decode(str.getBytes("GBK"));
				//PrefUtility.put(Constants.PREF_INIT_DATA_STR, str);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}catch(Exception e2){
				e2.printStackTrace();
			} // 解密base64字符串，得到压缩字节流，后台为php，采用gzcompress函数压缩，协议为zlib
			String unZlibStr = ZLibUtils.decompress(base64byte); // 采用zlib协议解压缩
			Log.d(TAG, "--------------unZlibStr:" + unZlibStr);
    		
			try {
				/*
				JSONObject jo = null;
				jo = new JSONObject(unZlibStr);
				String result = jo.optString("结果");
				*/
				net.minidev.json.JSONObject obj=(net.minidev.json.JSONObject) JSONValue.parseStrict(unZlibStr);
				if (AppUtility.isNotEmpty(String.valueOf(obj.get("结果")))) {
					
				}
				else
				{
        		
	            	AllInfo allInfo = new AllInfo(obj);
	            	int currentWeek = allInfo.getCurrentWeek();// 当前周次，
					int selectedWeek = allInfo.getSelectedWeek();// 选择周次
					int maxWeek = allInfo.getMaxWeek();// "最大周次
					Log.d(TAG, "currentWeek:" + currentWeek + ",selectedWeek:"
							+ selectedWeek + ",maxWeek:" + maxWeek);
					
					Schedule schedule = allInfo.getSchedule();
					// Log.d(TAG, "----------------------->" +
					// schedule.getSections() + schedule.getWeeks());
					/*
					AttendanceOfStudent attendanceOfStudent = new AttendanceOfStudent(
							jo);
					QueryTheMarkOfStudent markOfStudent = new QueryTheMarkOfStudent(
							jo);
					StatisticsScoreOfStudents statisticsScoreOfStudents = new StatisticsScoreOfStudents(
							jo);
					statisticsScoreOfStudentsDao
							.create(statisticsScoreOfStudents);
					*/
					// 课堂测试题库列表数据
					Log.d(TAG,
							"-----------------课堂测试题库列表数据-----------------------");
					List<TestEntity> testinglist = allInfo.getTestEntitys();
					if (testinglist != null && testinglist.size() > 0) {
						Log.d(TAG, "-----------------testinglist.size():"
								+ testinglist.size());
						for (TestEntity testEntity : testinglist) {
							testEntityDao.create(testEntity);
						}
					}
					// 教师上课记录列表
					Log.d(TAG,
							"-----------------教师上课记录列表-----------------------");
					List<TeacherInfo> teacherInfosList = allInfo
							.getTeacherInfos();
					if (teacherInfosList != null && teacherInfosList.size() > 0) {
						Log.d(TAG, "-----------------teacherInfosList.size():"
								+ teacherInfosList.size());
						for (TeacherInfo tt : teacherInfosList) {
							teacherinfoDao.create(tt);
						}
					} 
					
					// 学生列表
					((CampusApplication)context.getApplicationContext()).setStudentDic(allInfo.getStudentList());
					//PrefUtility.putObject("studentDic", allInfo.getStudentList());
					
					/**
					 * 开始测验计时 by zhuliang
					 */
					Log.d(TAG, "----------开始测验计时-------- ");
					List<TestStartEntity> startTestList = allInfo
							.getStartTestList();
					if (startTestList != null && startTestList.size() > 0) {
						Log.d(TAG,
								"-------------------------startTestList.size():"
										+ startTestList.size());
						for (TestStartEntity testStartEntity : startTestList) {
							startTestDao.create(testStartEntity);
						}
					}
	
					
	
					// 考勤颜色处理
					Log.d(TAG, "---------考勤颜色处理--------");
					List<Dictionary> studentAttenceColorList = allInfo
							.getStudentAttenceColorList();
					if (studentAttenceColorList != null
							&& studentAttenceColorList.size() > 0) {
						Log.d(TAG,
								"-------------------studentAttenceColorList.size():"
										+ studentAttenceColorList.size());
						for (Dictionary dictionary : studentAttenceColorList) {
							dictionaryDao.create(dictionary);
						}
					}
					/**
					 * 学生成绩查询
					 */
					Log.d(TAG, "--------学生成绩查询--------");
					List<StudentScore> studentScoreList = allInfo
							.getStudentScoreList();
					if (studentScoreList != null && studentScoreList.size() > 0) {
						Log.d(TAG,
								"-------------------studentScoreList.size():"
										+ studentScoreList.size());
						for (StudentScore studentScore : studentScoreList) {
							studentScoreDao.create(studentScore);
						}
					}
					/**
					 * 学生测验查询
					 */
					Log.d(TAG, "----------学生测验查询--------");
					List<StudentTest> studentTestList = allInfo
							.getStudentTestList();
					if (studentTestList != null && studentTestList.size() > 0) {
						Log.d(TAG, "-------------------studentTestList.size():"
								+ studentTestList.size());
						for (StudentTest studentTest : studentTestList) {
							studentTestDao.create(studentTest);
						}
					}
					// 测验统计颜色
					Log.d(TAG, "-----------测验统计颜色--------");
					List<Dictionary> studentTestColorList = allInfo
							.getStudentTestColorList();
					if (studentTestColorList != null
							&& studentTestColorList.size() > 0) {
						Log.d(TAG,
								"-------------------studentTestColorList.size():"
										+ studentTestColorList.size());
						for (Dictionary dictionary : studentTestColorList) {
							dictionaryDao.create(dictionary);
						}
					}
	
					// 学生详情显示
					Log.d(TAG, "-------------------------学生详情信息卡");
					List<Dictionary> studentInfoList = allInfo
							.getStudentInfoList();
					if (studentInfoList != null && studentInfoList.size() > 0) {
						Log.d(TAG, "-------------------studentInfoList.size():"
								+ studentInfoList.size());
						for (Dictionary dictionary : studentInfoList) {
							dictionaryDao.create(dictionary);
						}
					}
	
					// 学生详情信息卡
					Log.d(TAG, "-------------------------学生详情信息卡");
					Dictionary studentTab = allInfo.getStudentTab();
					if (studentTab != null) {
						dictionaryDao.create(studentTab);
					}
	
					scheduleDao.create(schedule);
					
					
					// 未来两周课程
					Log.d(TAG,
							"-----------------未来两周课程-----------------------");
					List<MyClassSchedule> classScheduleList = allInfo.getFutureClassSchedule();
					if (classScheduleList != null && classScheduleList.size() > 0) {
						Log.d(TAG, "-----------------classScheduleList.size():"
								+ classScheduleList.size());
						for (MyClassSchedule tt : classScheduleList) {
							myClassScheduleDao.create(tt);
						}
					}
					
					PrefUtility.put(Constants.PREF_INIT_BASEDATE_FLAG,true);
					PrefUtility.put(Constants.PREF_INIT_BASEDATE_DATE,DateHelper.getToday());
					
				}
				
        	}
			
        	catch (SQLException e) {
				e.printStackTrace();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 发送数据初始化完毕广播通知
			Log.d(TAG, "----------------------->发送数据初始化完毕广播通知");
			if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
				Log.d(TAG, "----------------------->ACTION_NAME:"+ ACTION_NAME);
				Intent intent = new Intent(ACTION_NAME);
				context.sendBroadcast(intent);
			}
			AppUtility.beginReminder(context);
			Log.d(TAG, "----------初始化处理耗时:" + (new Date().getTime()-dt.getTime()));
		}
	}
	private class saveContractsData extends Thread
	{
		private String str;
		public saveContractsData(String str)
		{
			this.str = str;
		}
		public void run()
		{
			Date dt=new Date();
			try
			{
				Log.d(TAG, "------------------初始化联系人----------------------");
				
				byte[] contact64byte = null;
				String resultContact = "";
				try {
					if (AppUtility.isNotEmpty(str)) {
						contact64byte = Base64.decode(str.getBytes("GBK"));
						//PrefUtility.put(Constants.PREF_INIT_CONTACT_STR, str);
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					
				}
				// 解密base64字符串，得到压缩字节流，后台为php，采用gzcompress函数压缩，协议为zlib
				resultContact = ZLibUtils.decompress(contact64byte);// 采用zlib协议解压缩
				
				
				if (!AppUtility.isNotEmpty(resultContact)) 
					AppUtility.showToastMsg(context, resultContact);
				else
				{
					//JSONObject jObject = null;
					//jObject = new JSONObject(resultContact);
					//ContactsInfo contacts = new ContactsInfo(jObject);
					
					Object obj=JSONValue.parseStrict(resultContact);
					ContactsInfo contacts = new ContactsInfo(obj);
					
					if (contacts != null) {
						
						((CampusApplication)context.getApplicationContext()).setLinkManDic(contacts.getLinkManDic());
						((CampusApplication)context.getApplicationContext()).setLinkGroupList(contacts.getContactsFriendsList());
						//PrefUtility.putObject("linkManDic", contacts.getLinkManDic());
						//PrefUtility.putObject("linkGroupList", contacts.getContactsFriendsList());
						
					}
					PrefUtility.put(Constants.PREF_INIT_CONTACT_FLAG, true);
				}
			}  
			
			catch (Exception e) {
				e.printStackTrace();
				
			}
		
			// 发送数据初始化完毕广播通知
			Log.d(TAG, "----------------------->发送数据初始化完毕广播通知");
			if (!"".equals(ACTION_NAME) && ACTION_NAME != null) {
				Intent intent = new Intent(ACTION_NAME);
				context.sendBroadcast(intent);
			}
			Log.d(TAG, "----------联系人处理耗时:" + (new Date().getTime()-dt.getTime()));
		}
	}
}

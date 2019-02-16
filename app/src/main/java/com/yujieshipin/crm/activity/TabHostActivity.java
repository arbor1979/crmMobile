package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.entity.ChatFriend;
import com.yujieshipin.crm.entity.Notice;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.lib.SlidingMenu;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.util.BaiduPushUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;
import com.yujieshipin.crm.widget.BottomTabLayout;
import com.yujieshipin.crm.widget.BottomTabLayout.OnCheckedChangeListener;

import static anet.channel.util.Utils.context;

@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity   {
	private String TAG = "TabHostActivity";
	public SlidingMenu menu;
	private BottomTabLayout mainTab;
	private TabHost tabHost;
	private Intent messageIntent;
	private Intent communicationIntent;
	private Intent schoolIntent;
	private Intent albumIntent;
	private boolean detectManul=false;
	private TextView pageTime, pageName, departmentOrClassName;
	private ImageView pagePhoto;
	private Button pageMyInfo, change_Pwd,/* pageRecommend, */versionDetect, pageClearCache,
			pageFeedback, pageExit;
	private Dao<User, Integer> userDao;
	private Dao<ChatFriend,Integer> chatFriendDao;
	private Dao<AlbumMsgInfo,Integer> albumMsgDao;
	private List<ChatFriend> chatFriendList;
	private User userInfo;
	private boolean isIntoBack;
	private final static String TAB_TAG_MESSAGE = "tab_tag_message";
	private final static String TAB_TAG_COMMUNICATION = "tab_tag_communication";
	// private final static String TAB_TAG_SUMMARY = "tab_tag_summary";
	private final static String TAB_TAG_SCHOOL = "tab_tag_school";
	private final static String TAB_TAG_ALBUM = "tab_tag_album";
	private Dao<Notice, Integer> noticeInfoDao;
	// public static int currentWeek = 0,selectedWeek = 0,maxWeek =
	// 0;//当前周次,选择周次,选择周次
	DatabaseHelper database;
	private Dialog clearCacheDialog;
	private final String ACTION_NAME_REMIND = "remindSubject";
	private final String ACTION_CHATINTERACT =  "ChatInteract";
	private final String ACTION_CHANGEHEAD =  "ChangeHead";
	
	public final String STitle = "showmsg_title";
	public final String SMessage = "showmsg_message";
	public final String BAThumbData = "showmsg_thumb_data";
	private final int VERSION_DETECT=1;
	private User user;
	
	public static MenuListener menuListener;
	private Dialog mLoadingDialog;
	
	
	public CallBackInterface callBack;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				AppUtility.showErrorToast(TabHostActivity.this, msg.obj.toString());
				break;
			
			case VERSION_DETECT:
				if(mLoadingDialog!=null)
					mLoadingDialog.dismiss();
				String result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					JSONObject jo = null;
					try {
						jo = new JSONObject(result);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(jo!=null){
						String tips = jo.optString("功能更新");
						String downLoadPath = jo.optString("下载地址");
						String newVer=jo.optString("最新版本号");
						if(AppUtility.isNotEmpty(tips)&&AppUtility.isNotEmpty(downLoadPath)){
							showUpdateTips(tips,downLoadPath,newVer);
						}
						else
						{
							if(detectManul)
							{
								if(jo.optString("result").equals("失败"))
									AppUtility.showErrorToast(TabHostActivity.this, jo.optString("errorMsg"));
								else
									AppUtility.showErrorToast(TabHostActivity.this, "已是最新，无需升级");
							}
						}
					}
				}
				break;
			case 2:
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
				
					JSONObject jo = null;
					try 
					{
						albumMsgDao=database.getAlbumMsgDao();
						jo = new JSONObject(result);
						if(jo!=null)
						{
							JSONArray ja=jo.getJSONArray("result");
							int unreadCount = ja.length();
							if(unreadCount!=0){
								
								try {
									
									for(int i=0;i<ja.length();i++)
									{
										JSONObject item=ja.getJSONObject(i);
										AlbumMsgInfo u=new AlbumMsgInfo(item);
										if(u.getImageObject()!=null && !u.getImageObject().equals("null") && u.getImageObject().length()>0)
											albumMsgDao.create(u);
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								Intent intentChat = new Intent("hasUnreadAlbumMsg");
								TabHostActivity.this.sendBroadcast(intentChat);
							}
						
						}
						updateUnreadCount();
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(context).onAppStart();

		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getApplicationContext());

		isIntoBack=true;
		user=((CampusApplication)getApplicationContext()).getLoginUserObjAllowNull();
		//String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		
		if(user==null)
		{
			finish();
			return;
		}

		setContentView(R.layout.activity_tabhost);
		mainTab = (BottomTabLayout) findViewById(R.id.bottom_tab_layout);
		mainTab.setOnCheckedChangeListener(changeListener);
		menuListener=new MenuListener();
		prepareIntent();
		setupIntent();
		showMenu();
		
		clearCacheDialog = new Dialog(TabHostActivity.this, R.style.dialog);
		//Intent intent = new Intent(TabHostActivity.this,SchoolService.class);
		//bindService(intent, connection, Context.BIND_AUTO_CREATE);
		
		showUnreadCnt();
		
		//UpdateManager.checkUpdate(this);
		//版本检测
		versionDetection(false);
			
		//regToWx(); // 注册微信
		registerBoradcastReceiver();
		
		String toTag = getIntent().getStringExtra("tab");
		if(toTag==null)
			findView();
		/*
		else if(toTag.equals("2"))
		{
			tabHost.setCurrentTabByTag(TAB_TAG_MESSAGE);
			View nearBtn = mainTab.findViewById(R.id.bottom_tab_message);
			nearBtn.setSelected(true);
			
		}
		*/
		
		
		Log.d(TAG,"生命周期:onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
		

		showUnreadCnt();
		updateUnreadCount();
		if(isIntoBack)
		{
			isIntoBack=false;
			getAlbumUnreadCount();
			Intent intentChat = new Intent("reloadNotice");
			TabHostActivity.this.sendBroadcast(intentChat);
			//getNetLocation();
		}
		/*
		//上次登录时间并非当前周则重新获取课表
		int week1=DateHelper.getWeekIndexOfYear(DateHelper.getStringDate(user.getLoginTime(), ""));
		int week2=DateHelper.getWeekIndexOfYear(new Date());
		if(week2>week1)
		{
			PrefUtility.put(Constants.PREF_SELECTED_WEEK, 0);
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			InitData initData = new InitData(TabHostActivity.this,getHelper(), null,"refreshSubject",checkCode);
			initData.initAllInfo();
		}
		*/
		Log.d(TAG,"生命周期:onStart");
	}
	@Override
	protected void onStop() {
		super.onStop();

		if(AppUtility.isApplicationBroughtToBackground(this))
			isIntoBack=true;
		Log.d(TAG,"生命周期:onStop");
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	public void showMenu() {
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(TabHostActivity.this);
		View localView = inflater.inflate(R.layout.view_page_menu, null);
		RelativeLayout leftMenu=(RelativeLayout)localView.findViewById(R.id.left_menu);
		int color=PrefUtility.getInt(Constants.PREF_THEME_MENUCOLOR, 0);
		if(color!=0)
			leftMenu.setBackgroundColor(color);
		initMenu(localView);
		menu.setMenu(localView);
		
		menu.showContent();
	}

	/**
	 * 功能描述: 对menu的操作
	 * 
	 * @author zhuliang 2013-12-5 下午4:55:16
	 * 
	 * @param view
	 */
	private void initMenu(View view) {

		TextView company_name=(TextView)view.findViewById(R.id.company_name);
		company_name.setText(user.getCompanyName());
		pageTime = (TextView) view.findViewById(R.id.page_time);
		pagePhoto = (ImageView) view.findViewById(R.id.page_photo);
		pageName = (TextView) view.findViewById(R.id.page_name);
		departmentOrClassName = (TextView) view
				.findViewById(R.id.department_or_class_name);
		pageMyInfo = (Button) view.findViewById(R.id.page_myinfo);
		change_Pwd= (Button) view.findViewById(R.id.change_Pwd);
		versionDetect = (Button) view.findViewById(R.id.versionDetect);
		pageClearCache = (Button) view.findViewById(R.id.page_clear_cache);
		pageFeedback = (Button) view.findViewById(R.id.page_feedback);
		pageExit = (Button) view.findViewById(R.id.page_exit);
		pageTime.setText(AppUtility.getWeekAndDate(new Date()));

		try {
			

			String photoUrl = user.getUserImage();
            /*
			ImageOptions options = new ImageOptions();
			Bitmap bm=aq.getCachedImage(photoUrl);
			if(bm!=null)
			{
				options.preset=bm;
				options.round=bm.getHeight()/2;
			}
			else
			{
				options.memCache=false;
				options.targetWidth=100;
				options.round = 50;
				
			}
			aq.id(pagePhoto).image(photoUrl, options);
			*/
            ImageLoader.getInstance().displayImage(photoUrl,pagePhoto,AppUtility.headOptions);
			userDao = getHelper().getUserDao();
			chatFriendDao = getHelper().getChatFriendDao();
			noticeInfoDao = getHelper().getNoticeInfoDao();
			String number = user.getId();
			userInfo = userDao.queryBuilder().where().eq("id", number)
					.queryForFirst();
			if (userInfo != null) {
				pageName.setText("您好," + userInfo.getName());
				departmentOrClassName.setText(userInfo.getMainRole());
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		pagePhoto.setOnClickListener(new MenuInfoListener());
		versionDetect.setOnClickListener(new MenuInfoListener());
		// pageMyCourse.setOnClickListener(new MenuInfoListener());
		pageMyInfo.setOnClickListener(new MenuInfoListener());
		pageClearCache.setOnClickListener(new MenuInfoListener());
		pageFeedback.setOnClickListener(new MenuInfoListener());
		// pageRecommend.setOnClickListener(new MenuInfoListener());
		pageExit.setOnClickListener(new MenuInfoListener());
		change_Pwd.setOnClickListener(new MenuInfoListener());
	}

	/**
	 * 准备tab的内容Intent
	 */
	private void prepareIntent() {
		//workIntent = new Intent(this, SubjectActivity.class);
		//messageIntent = new Intent(this, ChatFriendActivity.class);
		communicationIntent = new Intent(this, ContactsActivity.class);
		// summaryIntent = new Intent(this, SummaryActivity.class);
		schoolIntent = new Intent(this, TabSchoolActivtiy.class);
		//albumIntent = new Intent(this, AlbumFlowActivity.class);
		
	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;
		localTabHost.addTab(buildTabSpec(TAB_TAG_SCHOOL, R.string.school,
				R.drawable.ic_launcher1, schoolIntent));
		//localTabHost.addTab(buildTabSpec(TAB_TAG_WORK, R.string.study,R.drawable.ic_launcher1, workIntent));
		//localTabHost.addTab(buildTabSpec(TAB_TAG_MESSAGE, R.string.message,R.drawable.ic_launcher1, messageIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_COMMUNICATION,
				R.string.curriculum, R.drawable.ic_launcher1,
				communicationIntent));
		//localTabHost.addTab(buildTabSpec(TAB_TAG_ALBUM,R.string.album, R.drawable.ic_launcher1,albumIntent));
		// localTabHost.addTab(buildTabSpec(TAB_TAG_SUMMARY, R.string.summary,
		// R.drawable.ic_launcher1, summaryIntent));

	}

	
	private TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.tabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// 设置默认选中项
	private void findView() {
		View nearBtn = mainTab.findViewById(R.id.bottom_tab_school);
		nearBtn.setSelected(true);
	}

	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void OnCheckedChange(View checkview) {
			switch (checkview.getId()) {
			
			case R.id.bottom_tab_message:
				tabHost.setCurrentTabByTag(TAB_TAG_MESSAGE);
				
				break;
			case R.id.bottom_tab_communication:
				tabHost.setCurrentTabByTag(TAB_TAG_COMMUNICATION);
				
				break;
			/*
			 * case R.id.bottom_tab_summary:
			 * tabHost.setCurrentTabByTag(TAB_TAG_SUMMARY);
			 * SummaryActivity.layout_menu.setOnClickListener(new
			 * MenuListener()); break;
			 */
			case R.id.bottom_tab_school:
				tabHost.setCurrentTabByTag(TAB_TAG_SCHOOL);
				
				break;
			
			case R.id.bottom_tab_album:
				tabHost.setCurrentTabByTag(TAB_TAG_ALBUM);
				
				break;
			
			}
			
		}

		@Override
		public void OnCheckedClick(View checkview) {

		}
	};

	/**
	 * 对tab上的四个activity上的menu进行监听 by zhuliang
	 */
	class MenuListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			menu.toggle();
		}
	}

	/**
	 * 对menu界面的监听
	 */
	class MenuInfoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.page_photo:
				String photoUrl = user.getUserImage();
				if (AppUtility.isNotEmpty(photoUrl)) {
					DialogUtility.showImageDialog(TabHostActivity.this,photoUrl);
					//showImageDialog(photoUrl);
				}
				break;
			case R.id.change_Pwd:
				final EditText et=new EditText(TabHostActivity.this);
				new AlertDialog.Builder(TabHostActivity.this).setTitle("请输入旧密码").setView(et)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String oldpwd=et.getText().toString();
						String mPassword=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
						if(!oldpwd.equals(mPassword))
						{
							AppUtility.showToastMsg(TabHostActivity.this, "旧密码不正确！");
						}
						else
						{
							Intent intent = new Intent(TabHostActivity.this,ChangePwdActivity.class);
							intent.putExtra("oldpwd", oldpwd);
							startActivity(intent);
						}
					}
					
				}).setNegativeButton("取消", null).show();
				TimeUtility.popSoftKeyBoard(TabHostActivity.this,et);
				break;
			case R.id.page_myinfo:
				/*
				Intent infoIntent = new Intent(TabHostActivity.this,
						UserInfoActivity.class);
				infoIntent.putExtra("userId", userInfo.getId());
				startActivity(infoIntent);
				*/
				Intent intent = new Intent(getApplicationContext(),
						ShowPersonInfo.class);
				intent.putExtra("studentId", user.getId());
				String myPic = user.getUserImage();
				intent.putExtra("userImage", myPic);
				intent.putExtra("userType", user.getUserType());
				startActivity(intent);
				break;
			// case R.id.page_questions:
			// Intent questionsIntent = new Intent(TabHostActivity.this,
			// WebSiteActivity.class);
			// questionsIntent.putExtra("url", CampusAPI.commonQuestionUrl);
			// questionsIntent.putExtra("title", "常见问题");
			// startActivity(questionsIntent);
			// break;
			// case R.id.page_recommend:
			// String[] data = { "新浪微博", "微信好友", "微信朋友圈" };
			// showDownloadDialog(data);
			// break;
			
			case R.id.versionDetect:
				versionDetection(true);
				break;
			
			case R.id.page_clear_cache:
				showClearCacheDialog();
				break;
			case R.id.page_feedback:
				Intent feedbackIntent = new Intent(TabHostActivity.this,
						FeedbackActivity.class);
				startActivity(feedbackIntent);
				break;
			case R.id.page_exit:
				showExit();
				break;
			default:
				break;
			}
		}
	}
	
	private void versionDetection(boolean manual) {
		detectManul=manual;
		if(manual)
		{
			mLoadingDialog = DialogUtility.createLoadingDialog(this, "正在检测新版本...");
			mLoadingDialog.show();
		}
		String thisVersion = CampusApplication.getVersion();
		String check=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj= new JSONObject();
		try {
			jsonObj.put("当前版本号", thisVersion);
			jsonObj.put("用户较验码", check);
			jsonObj.put("function", "versionDetect");

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, VERSION_DETECT);
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}

	public void showDialog(String contentText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TabHostActivity.this);
		builder.setTitle("课程提醒");
		builder.setMessage(contentText);
		builder.setNegativeButton("知道了", new cancelStudentPicListener());
		AlertDialog ad = builder.create();
		ad.show();
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NAME_REMIND)) {
				Log.d(TAG, "----------->BroadcastReceiver："
						+ ACTION_NAME_REMIND);
				String contentText = intent.getStringExtra("contentText");
				showDialog(contentText);
			}else if(action.equals(ACTION_CHATINTERACT)){
				showUnreadCnt();
			}
			else if(action.equals(ACTION_CHANGEHEAD))
			{
				String newhead=intent.getStringExtra("newhead");
				if(newhead!=null)
				{
                    /*
					AQuery aq = new AQuery(TabHostActivity.this);
					ImageOptions options = new ImageOptions();
					options.memCache=false;
					options.targetWidth=100;
					options.round = 50;
					aq.id(pagePhoto).image(newhead, options);
					*/
                    ImageLoader.getInstance().displayImage(newhead,pagePhoto,AppUtility.headOptions);
				}
			}
		}
	};

	/**
	 * 功能描述:显示消息数量
	 *
	 * @author shengguo  2014-5-29 下午3:07:35
	 *
	 */
	private void showUnreadCnt() {
		int count = 0;
		try {
			chatFriendList = chatFriendDao.queryBuilder().where().eq("hostid", user.getUserNumber()).query();
			for (ChatFriend chatFriend:chatFriendList) {
				count += chatFriend.getUnreadCnt();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TextView unreadCnt = (TextView) mainTab.findViewById(R.id.unreadCnt);
		if(count!=0){
			unreadCnt.setText(String.valueOf(count));
			unreadCnt.setVisibility(View.VISIBLE);
		}else{
			unreadCnt.setVisibility(View.INVISIBLE);
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "--------------注销广播/关闭服务-------------");
		try
		{
			unregisterReceiver(mBroadcastReceiver);
		}
		catch(IllegalArgumentException e)
		{
			
		}
		/*
		if(schoolService != null){
			unbindService(connection);
		}
		*/
		Log.d(TAG,"生命周期:onDestroy");
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME_REMIND);
		myIntentFilter.addAction(ACTION_CHATINTERACT);
		myIntentFilter.addAction(ACTION_CHANGEHEAD);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private class cancelStudentPicListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

	private void showExit() {
		View dialog_view = LayoutInflater.from(TabHostActivity.this).inflate(
				R.layout.dialog_exit, null);
		AlertDialog dialog_exit = new AlertDialog.Builder(TabHostActivity.this)
				.setView(dialog_view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消登陆成功状态
						((CampusApplication)getApplicationContext()).setLoginUserObj(null);
						((CampusApplication)getApplicationContext()).setLinkManDic(null);
						((CampusApplication)getApplicationContext()).setLinkGroupList(null);
						((CampusApplication)getApplicationContext()).setStudentDic(null);
						((CampusApplication)getApplicationContext()).setLoginUserObj(null);
						
						PrefUtility.put(Constants.PREF_INIT_BASEDATE_FLAG,
								false);
						PrefUtility
								.put(Constants.PREF_INIT_CONTACT_FLAG, false);
						PrefUtility.put(Constants.PREF_SELECTED_WEEK, 0);
						PrefUtility.put(Constants.PREF_LOGIN_NAME, "");
						PrefUtility.put(Constants.PREF_LOGIN_PASS, "");
						PrefUtility.put(Constants.PREF_INIT_CONTACT_STR, "");
						PrefUtility.put(Constants.PREF_INIT_DATA_STR, "");
						Intent intent = new Intent(TabHostActivity.this,
								LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						System.exit(0);
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog_exit.show();
	}

	// /**
	// * 功能描述:下载弹出框
	// *
	// * @author zhuliang 2013-12-25 下午1:24:26
	// *
	// */
	// private void showDownloadDialog(String[] data) {
	// View view = getLayoutInflater()
	// .inflate(R.layout.view_exam_dialog, null);
	// Button cancel = (Button) view.findViewById(R.id.cancel);
	// ListView mList = (ListView) view.findViewById(R.id.list);
	// DialogAdapter dialogAdapter = new DialogAdapter(data);
	// mList.setAdapter(dialogAdapter);
	// downloadDialog.setContentView(view);
	// downloadDialog.show();
	// Window window = downloadDialog.getWindow();
	// window.setGravity(Gravity.BOTTOM);// 在底部弹出
	// window.setWindowAnimations(R.style.CustomDialog);
	// cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// downloadDialog.dismiss();
	// }
	// });
	// }

	/**
	 * 功能描述:清除缓存
	 * 
	 * @author shengguo 2014-5-5 下午3:45:04
	 * 
	 */
	private void showClearCacheDialog() {
		View view = getLayoutInflater()
				.inflate(R.layout.view_clear_cache, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		Button ok = (Button) view.findViewById(R.id.ok);
		clearCacheDialog.setContentView(view);
		clearCacheDialog.show();
		Window window = clearCacheDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearCacheDialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				((CampusApplication) getApplication()).onLowMemory();
				try {
					List<Notice> typelist=noticeInfoDao.query(noticeInfoDao.queryBuilder().distinct().selectColumns("newsType").prepare());
					
					noticeInfoDao.delete((PreparedDelete<Notice>)noticeInfoDao.deleteBuilder().prepare());
					for (Notice item:typelist)
					{
						Intent intent = new Intent("refreshUnread");
						intent.putExtra("title", item.getNewsType());
						sendBroadcast(intent);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				clearCacheDialog.dismiss();
				AppUtility.showToastMsg(TabHostActivity.this,
						getString(R.string.cleared_all_cached_images));
			}
		});
	}

	
	/**
	 * 功能描述:询问是否更新
	 *
	 * @author shengguo  2014-6-3 下午4:31:55
	 *
	 */
	private void showUpdateTips(String tips,final String downLoadPath,String newVer) {
		View view = LayoutInflater.from(TabHostActivity.this).inflate(
				R.layout.view_textview, null);
		TextView tvTip = (TextView) view.findViewById(R.id.tv_text);
		tvTip.setText(tips);
		AlertDialog dialog_UpdateTips = new AlertDialog.Builder(TabHostActivity.this)
				.setView(view)
				.setTitle(newVer+"版更新提示")
				.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "-------------downLoadPath:" + downLoadPath);
						//schoolService.downLoadUpdate(downLoadPath, 1001);
						downloadFile(downLoadPath);
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog_UpdateTips.show();
	}
	
	private void downloadFile(String url)
	{
		AppUtility.downloadUrl(url, null, this);
	}
	
	private void getAlbumUnreadCount() {
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action", "相册未读消息");
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php", new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
				
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	private void updateUnreadCount()
	{
		TextView unreadCnt = (TextView) mainTab.findViewById(R.id.unreadCntAlbum);
		List<AlbumMsgInfo> unreadList;
		try {
			albumMsgDao=database.getAlbumMsgDao();
			String hostId=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
			unreadList = albumMsgDao.queryBuilder().where().eq("ifRead",0).and().eq("toId", hostId).query();
			if(unreadList.size()>0)
			{
				unreadCnt.setText(String.valueOf(unreadList.size()));
				unreadCnt.setVisibility(View.VISIBLE);
			}
			else
			{
				unreadCnt.setVisibility(View.INVISIBLE);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(callBack!=null)
			AppUtility.permissionResult(requestCode,grantResults,this,callBack);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	
}

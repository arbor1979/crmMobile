package com.yujieshipin.crm.activity;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.TabHostActivity.MenuListener;
import com.yujieshipin.crm.adapter.SchoolWorkAdapter;
import com.yujieshipin.crm.adapter.SectionedSpanSizeLookup;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Notice;
import com.yujieshipin.crm.entity.NoticesItem;
import com.yujieshipin.crm.entity.SchoolWorkItem;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;

public class TabSchoolActivtiy extends FragmentActivity {
	private String TAG = "TabSchoolActivtiy";
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private Button btnLeft;
	private AQuery aq;
	private RecyclerView myGridView;
	private SchoolWorkAdapter adapter;
	private List<SchoolWorkItem> schoolWorkItems = new ArrayList<SchoolWorkItem>();
	private List<Notice> notices = new ArrayList<Notice>();
	private Dao<Notice, Integer> noticeInfoDao;
	private DatabaseHelper database;
	private User user;
	private boolean isruning,needCount;
	private Timer timer; 
	static LinearLayout layout_menu;

	private final int GETMODULES_CODE=0,GETNOTICEITEM_CODE=1;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(TabSchoolActivtiy.this, msg.obj.toString());
				break;
			case GETMODULES_CODE:
				showProgress(false);
				String result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) 
				{
					JSONArray jo = null;
					try {
						jo = new JSONArray(result);
					
						if(jo!=null && jo.length()>0)
						{
							schoolWorkItems.clear();
							for (int i = 0; i < jo.length(); i++) {
								SchoolWorkItem swItem=new SchoolWorkItem(jo.getJSONObject(i));
								schoolWorkItems.add(swItem);
							}
							adapter.setSchoolWorkItems(schoolWorkItems);
							adapter.notifyDataSetChanged();
							
							/*
							if(timer==null)
							{
								timer=new Timer();
								timer.schedule(new myTask(),0,10000);
							}
							*/
							for(int i=0;i<schoolWorkItems.size();i++)
							{
								SchoolWorkItem item=(SchoolWorkItem)schoolWorkItems.get(i);
								if(item.isHasBadge())
								{
									needCount=true;
									break;
								}
							}

							if(needCount)
								getUnreadCount();

							for(SchoolWorkItem item:schoolWorkItems)
							{
								if(item.getTemplateName().equals("通知"))
								{
									getNoticesItem(item.getInterfaceName(),item.getWorkText());
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						
							JSONObject jo1 = null;
							try {
								jo1 = new JSONObject(result);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(jo1!=null && jo1.optString("result").equals("失败")){
								AppUtility.showToastMsg(TabSchoolActivtiy.this, jo1.optString("errorMsg"));
							}
						
					}
					
					
				}
				break;
			case GETNOTICEITEM_CODE:
				
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						
						if(jo.optString("result").equals("失败")){
							AppUtility.showToastMsg(TabSchoolActivtiy.this, jo.optString("errorMsg"));
						}else{
							NoticesItem noticesItem = new NoticesItem(jo);
							notices = noticesItem.getNotices();
							for(Notice item:notices)
							{
								//item.setIfread("0");
								item.setNewsType(noticesItem.getTitle());
								item.setUserNumber(user.getUserNumber());
								Notice nt=noticeInfoDao.queryBuilder().where().eq("id",item.getId()).and().eq("newsType", item.getNewsType()).and().eq("userNumber",user.getUserNumber()).queryForFirst();
								if(nt==null)
									noticeInfoDao.create(item);
							}
							getUnreadByTitle(noticesItem.getTitle());
							
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
				
			case 2:
				result = msg.obj.toString();
	
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						if(jo!=null)
						{
							boolean bfind=false;
							for(SchoolWorkItem item:schoolWorkItems)
							{
								if(item.isHasBadge())
								{
									item.setUnread(jo.optInt(item.getWorkText()));
									bfind=true;
								}
							}
							if(bfind) {
								//adapter.setSchoolWorkItems(schoolWorkItems);
								adapter.notifyDataSetChanged();
							}
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				}
				break;
			
			}
		}
	};
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "----------------onCreate-----------------------");
		setContentView(R.layout.tab_activity_school);
		aq = new AQuery(this);
		myGridView = (RecyclerView) findViewById(R.id.mygridview);
		btnLeft = (Button) findViewById(R.id.btn_left);
		layout_menu = (LinearLayout) findViewById(R.id.layout_btn_left);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		contentLayout = (LinearLayout) findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) findViewById(R.id.empty);
		aq.id(R.id.tv_title).text(getString(R.string.school));
		btnLeft.setBackgroundResource(R.drawable.bg_title_homepage_back);
		btnLeft.setVisibility(View.VISIBLE);
		adapter=new SchoolWorkAdapter(TabSchoolActivtiy.this, schoolWorkItems);
		int screenWidth=AppUtility.getAndroiodScreenProperty(this);
		int spancount= (int) Math.floor(screenWidth / 75);
		GridLayoutManager manager = new GridLayoutManager(this,spancount);
		//设置header
		manager.setSpanSizeLookup(new SectionedSpanSizeLookup(adapter,manager));
		myGridView.setLayoutManager(manager);
		myGridView.setAdapter(adapter);
		//getSchool();
		//重新加载
		failedLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSchool();
			}
		});
		emptyLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSchool();
			}
		});
		try {
			noticeInfoDao = getHelper().getNoticeInfoDao();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		
		registerBoradcastReceiver();
		
		layout_menu.setOnClickListener(TabHostActivity.menuListener);
		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getApplicationContext());
		
	}
	
	/*
	class myTask extends TimerTask {
		public void run ( ) {
			if(isruning && schoolWorkItems.size()>0)
				getUnreadCount();
		}
	};
	*/
	@Override
	protected void onDestroy() {
		if (timer != null) {
			timer.cancel( );
			timer = null;
		}
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}
	private boolean getUnreadByTitle(String title)
	{
		List<Notice> unreadList=null;
		boolean flag=false;
		try {
			unreadList = noticeInfoDao.queryBuilder().where().eq("newsType",title).and().eq("userNumber", user.getUserNumber()).and().eq("ifread","0").query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(unreadList!=null)
		{
			for(SchoolWorkItem item:schoolWorkItems)
			{
				if(item.getWorkText().equals(title))
				{
					item.setUnread(unreadList.size());
					flag=true;
					break;
				}
			}
		}
		if(flag)
			adapter.notifyDataSetChanged();
		return flag;
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}
	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}

	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}
	//获取校内item选项详情
	public void getSchool() {
		showProgress(true);
		String checkCode=PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", "getModules");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		CampusAPI.httpPost(jo, mHandler, GETMODULES_CODE);
		
	}
	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getNoticesItem(String interfaceName,String showName) {
		
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		
		int lastId=0;
		try {
			Notice nt=noticeInfoDao.queryBuilder().orderBy("id", false).where().eq("newsType", showName).and().eq("userNumber", user.getUserNumber()).queryForFirst();
			if(nt!=null)
				lastId=nt.getId();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", interfaceName.substring(0, interfaceName.length()-4));
			jo.put("LASTID", lastId);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, GETNOTICEITEM_CODE);
	}
	
	public void getUnreadCount() {
		

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
	
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function","getModules" );
			jo.put("action", "getCount");
	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 2);

	}
	@Override
	protected void onStart() {
		super.onStart();
		isruning = true;
		Log.d(TAG, "生命周期:Start");
		
		if(needCount)
			getUnreadCount();
	}

	@Override
	protected void onStop() {
		super.onStop();
		isruning = false;
		Log.d(TAG, "生命周期:Stop");
	}
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("refreshUnread");
		myIntentFilter.addAction("reloadNotice");
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("refreshUnread")) {
				
				String refreshTitle = intent.getStringExtra("title");
				getUnreadByTitle(refreshTitle);
				
			}
			else if(action.equals("reloadNotice"))
			{
				getSchool();
			}
		}
	};
}

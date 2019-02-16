package com.yujieshipin.crm.activity;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.TabHostActivity.MenuListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.base.ExitApplication;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.fragment.SubjectFragment;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;

@SuppressLint("NewApi")
public class SubjectActivity extends FragmentActivity {
	Button bn_refresh;
	static Button bn_menu;
	static LinearLayout layout_menu;
	private TextView tv_title;
	private LinearLayout initlayout;
	boolean selection = true;
	private View view, selectWeekView;
	private TextView tvClose, tvOk, tvRight;
	private NumberPicker nPicker1;
	private PopupWindow popupWindow;
	private final String ACTION_NAME = "refreshSubject";
	private Dialog mLoadingDialog;
	private SubjectFragment subjectFragment;
	private DatabaseHelper database;
	private int currentWeek, selectedWeek, maxWeek;// 当前周次，选择周次 "最大周次
	private static final String TAG = "SubjectActivity";
	private boolean isInitDate = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "----------------onCreate-----------------------");
		setContentView(R.layout.activity_subject);
		ExitApplication.getInstance().addActivity(this);
		mLoadingDialog = DialogUtility.createLoadingDialog(this, "正在获取数据...");
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(new Date());
		currentWeek=PrefUtility.getInt(Constants.PREF_CURRENT_WEEK, 0);
		selectedWeek = PrefUtility.getInt(Constants.PREF_SELECTED_WEEK, 0);
		maxWeek = PrefUtility.getInt(Constants.PREF_MAX_WEEK, 0);
		Log.d(TAG, "currentWeek:" + currentWeek + ",selectedWeek:"
				+ selectedWeek + ",maxWeek:" + maxWeek);
		view = findViewById(R.id.subject);
		subjectFragment = (SubjectFragment) getSupportFragmentManager()
				.findFragmentById(R.id.subjectfragment);
		initTitle();
		
		if(!loadScheduleBg())
			view.setBackgroundResource(R.drawable.subject_bg);
		// 注册广播
		registerBoradcastReceiver();
		
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(2);
	}
	private void initTitle() {
		initlayout = (LinearLayout) findViewById(R.id.initlayout);
		layout_menu = (LinearLayout) findViewById(R.id.layout_back);
		//layoutRefresh = (LinearLayout) findViewById(R.id.layout_goto);
		bn_menu = (Button) findViewById(R.id.btn_back);
		bn_refresh = (Button) findViewById(R.id.btn_goto);
		tv_title = (TextView) findViewById(R.id.tv_title);
		bn_menu.setBackgroundResource(R.drawable.bg_title_homepage_back);
		//bn_refresh.setBackgroundResource(R.drawable.bg_title_homepage_go);
		tv_title.setOnClickListener(new MyListener());
		//layoutRefresh.setOnClickListener(new MyListener());
	
		layout_menu.setOnClickListener(TabHostActivity.menuListener);
		
		isInitDate = PrefUtility.getBoolean(Constants.PREF_INIT_BASEDATE_FLAG, false);
		Log.d(TAG, "----------isInitDate"+isInitDate);
		if (isInitDate) {
			
				String lastdt=PrefUtility.get(Constants.PREF_INIT_BASEDATE_DATE, "");
				if(lastdt==null)
					regetKebiao();
				else
				{
					Date dt=DateHelper.getStringDate(lastdt, "yyyy-MM-dd");
					if(dt!=null)
					{
						int lastweek=DateHelper.getWeekIndexOfYear(dt);
						int nowweek=DateHelper.getWeekIndexOfYear(new Date());
						if(lastweek!=nowweek)
							regetKebiao();
					}
				}
				
				if (currentWeek == selectedWeek) {
					tv_title.setText("第" + selectedWeek + "周(本周)▼");
				} else {
					tv_title.setText("第" + selectedWeek + "周(非本周)▼");
				}
				
			
			
		}else{
			regetKebiao();
			/*
			Intent intent = new Intent(SubjectActivity.this,Alarmreceiver.class);
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			intent.putExtra(Constants.PREF_CHECK_CODE, checkCode);
			intent.setAction("initBaseData");
			sendBroadcast(intent);
			*/
		}
	}
	private void regetKebiao()
	{
		initlayout.setVisibility(View.VISIBLE);
		tv_title.setVisibility(View.INVISIBLE);
		PrefUtility.put(Constants.PREF_SELECTED_WEEK, 0);
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		InitData initData = new InitData(this,getHelper(), mLoadingDialog,ACTION_NAME,checkCode);
		initData.initAllInfo();
	}
	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_title:
				selectedWeeks();
				break;
			case R.id.layout_goto:
				Log.d(TAG, "----------isInitDate"+isInitDate);
				isInitDate = PrefUtility.getBoolean(Constants.PREF_INIT_BASEDATE_FLAG, false);
				if (isInitDate) {
					currentWeek=PrefUtility.getInt(Constants.PREF_CURRENT_WEEK, 0);
					selectedWeek = PrefUtility.getInt(Constants.PREF_SELECTED_WEEK, 0);
					maxWeek = PrefUtility.getInt(Constants.PREF_MAX_WEEK, 0);
					Log.d(TAG, "currentWeek:" + currentWeek + ",selectedWeek:"
							+ selectedWeek + ",maxWeek:" + maxWeek);
					subjectFragment.initTable();

					initlayout.setVisibility(View.INVISIBLE);
					tv_title.setVisibility(View.VISIBLE);
					if (currentWeek == selectedWeek) {
						tv_title.setText("第" + selectedWeek + "周(本周)▼");
					} else {
						tv_title.setText("第" + selectedWeek + "周(非本周)▼");
					}
				} else {
					AppUtility.showToastMsg(SubjectActivity.this, "正在初始化数据");
				}
				break;
			}
		}
	}

	/**
	 * 功能描述:选择第几周的上课记录
	 * 
	 * @author shengguo 2014-5-15 下午5:13:06
	 * 
	 */
	/**
	 * 功能描述:
	 * 
	 * @author shengguo 2014-5-16 下午6:16:12
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void selectedWeeks() {
		selectWeekView = getLayoutInflater()
				.inflate(R.layout.select_week, null);
		nPicker1 = (NumberPicker) selectWeekView
				.findViewById(R.id.numberPicker1);
		
		tvOk = (TextView) selectWeekView.findViewById(R.id.tv_ok);
		tvClose = (TextView) selectWeekView.findViewById(R.id.tv_close);
		tvRight = (TextView) selectWeekView.findViewById(R.id.tv_right);
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedWeek = nPicker1.getValue();
				Log.d(TAG, "第" + selectedWeek + "周 ");
				PrefUtility.put(Constants.PREF_SELECTED_WEEK, selectedWeek);
				String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE,"");
				InitData initData = new InitData(SubjectActivity.this,
						getHelper(), mLoadingDialog, ACTION_NAME, checkCode);
				initData.initAllInfo();
				popupWindow.dismiss();
			}
		});
		tvClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
		if(selectedWeek>maxWeek)
			maxWeek=selectedWeek+1;
		nPicker1.setMaxValue(maxWeek);
		nPicker1.setMinValue(1);
		nPicker1.setValue(selectedWeek);
		nPicker1.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				Log.d(TAG, "oldVal:" + oldVal + ",newVal:" + newVal);
				if (newVal == currentWeek) {
					tvRight.setText("周(本周)");
				} else {
					tvRight.setText("周");
				}
			}
		});

		popupWindow = new PopupWindow(selectWeekView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(R.style.popupAnimation);
		// 点击外部消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(view.findViewById(R.id.subject),
				Gravity.RIGHT | Gravity.BOTTOM, 0, 0);

	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		myIntentFilter.addAction("changeScheduleBg");
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "----------->BroadcastReceiver：" + action);
			if (action.equals(ACTION_NAME)) {
				isInitDate = true;
				selectedWeek=PrefUtility.getInt(Constants.PREF_SELECTED_WEEK, 0);
				currentWeek=PrefUtility.getInt(Constants.PREF_CURRENT_WEEK, 0);
				maxWeek = PrefUtility.getInt(Constants.PREF_MAX_WEEK, 0);
				Log.d(TAG, "----BroadcastReceivercurrentWeek:" + currentWeek + ",selectedWeek:"
						+ selectedWeek + ",maxWeek:" + maxWeek);
				// 初始化成功重新加载课表数据
				subjectFragment.initTable();
				initlayout.setVisibility(View.INVISIBLE);
				tv_title.setVisibility(View.VISIBLE);
				if (currentWeek == selectedWeek) {
					tv_title.setText("第" + selectedWeek + "周(本周)▼");
				} else {
					tv_title.setText("第" + selectedWeek + "周(非本周)▼");
				}
			}
			if(action.equals("changeScheduleBg"))
			{
				if(loadScheduleBg())
					AppUtility.showToastMsg(SubjectActivity.this, "背景已修改");
				else
					AppUtility.showToastMsg(SubjectActivity.this, "背景修改失败，手机系统版本太低");
			}
		}
	};
	private boolean loadScheduleBg()
	{
		String bgname=PrefUtility.get("scheduleBg","default");
		BitmapDrawable drawable;
		Bitmap bitmap;
		if(bgname.equals("default"))
		{
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.subject_bg);  
			//bitmap = Bitmap.createBitmap(100, 20, Config.ARGB_8888);  
		}
		else
		{
			view.setBackground(Drawable.createFromPath(bgname));
			bitmap = BitmapFactory.decodeFile(bgname);  
		}
		if(bitmap!=null)
		{
			bitmap=ImageUtility.zoomBitmap(bitmap, this.getWindowManager().getDefaultDisplay().getWidth());
			drawable = new BitmapDrawable(this.getResources(),bitmap);  
			drawable.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );  
			drawable.setDither(true); 
			view.setBackground(drawable);
			return true;
		}
		return false;
		
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(SubjectActivity.this,
					DatabaseHelper.class);
		}
		return database;
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
}

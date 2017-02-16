package com.yujieshipin.crm.activity;


import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.widget.BottomTabLayout;
import com.yujieshipin.crm.widget.BottomTabLayout.OnCheckedChangeListener;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ClassDetailActivity extends TabActivity {
	private String TAG = "ClassDetailActivity";
	public static String classname = null, subjectid = null, userType;
	private AQuery aq;
	private TabHost tabHost;
	private BottomTabLayout bottomTab;
	public static TeacherInfo teacherInfo;
	
	public static ContactsMember contactsMember;// 老师
	public static String contactsMemberImage = "";// 老师
	private Intent curriculumIntent, rollCallIntent, classroomCourseIntent,
			classroomTestIntent, summaryIntent;

	private final static String TAB_TAG_CURRICULUM = "tab_tag_curriculum";
	private final static String TAB_TAG_ROLL_CALL = "tab_tag_roll_call";
	private final static String TAB_TAG_CLASSROOM_COURSE = "tab_tag_classroom_course";
	private final static String TAB_TAG_CLASSROOM_TEST = "tab_tag_classroom_test";
	private final static String TAB_TAG_SUMMARY = "tab_tag_summary";
	public CallBackInterface callBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_classdetail);
		aq = new AQuery(this);

		initDate();
		initView();
		prepareIntent();
		setupIntent();
		registerBroastcastReceiver();
	}

	/**
	 * 功能描述:
	 * 
	 * @author shengguo 2014-2-20 上午10:02:22
	 * 
	 */
	private void registerBroastcastReceiver() {
		IntentFilter mFilter = new IntentFilter("finish_classdetailactivity");
		registerReceiver(mBroadcastReceiver, mFilter);
		IntentFilter mFilter1 = new IntentFilter("changeTab_classdetailactivity");
		registerReceiver(mBroadcastReceiver, mFilter1);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("finish_classdetailactivity")) 
			{
					finish();
			}
			if (action.equals("changeTab_classdetailactivity")) 
			{
				int tabIndex=intent.getIntExtra("tabIndex", 1);
				switch (tabIndex)
				{
				
				case 4:// 总结
					tabHost.setCurrentTabByTag(TAB_TAG_SUMMARY);
					bottomTab.findViewById(R.id.bottom_tab_roll_call)
					.setSelected(false);
					bottomTab.findViewById(R.id.bottom_tab_summary)
					.setSelected(true);
					break;
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};

	private void initDate() {
		teacherInfo = (TeacherInfo) getIntent().getSerializableExtra(
				"teacherInfo");
		subjectid = teacherInfo.getId();

		classname = teacherInfo.getClassGrade();
		userType = PrefUtility.get(Constants.PREF_CHECK_USERTYPE,"");
		Log.d(TAG, "---classname:" + classname + ",subjectid:" + subjectid
				+ ",userType:" + userType);
		String userName = teacherInfo.getUsername();
		contactsMember=null;
		Set<Map.Entry<String, ContactsMember>> set = ((CampusApplication)getApplicationContext()).getLinkManDic().entrySet();
		for (Iterator<Map.Entry<String, ContactsMember>> it = set.iterator(); it.hasNext();)
		{            
			Map.Entry<String, ContactsMember> entry = (Map.Entry<String, ContactsMember>) it.next();
			ContactsMember item=entry.getValue();
			if(item.getStudentID().equals(userName))
			{
				contactsMember=item;
				break;
			}
		}
		
		if (contactsMember != null) {
			contactsMemberImage = contactsMember.getUserImage();
		}
		else
		{
			//AppUtility.showToastMsg(this, "没有找到对应联系人");
			contactsMember=new ContactsMember();
			contactsMemberImage="";
		}
	}

	private void initView() {
		bottomTab = (BottomTabLayout) findViewById(R.id.bottom_tab_layout);
		bottomTab.setOnCheckedChangeListener(changeListener);
		if (userType.equals("老师")) {
			aq.id(R.id.bottom_tab_curriculum).visibility(View.GONE);
			bottomTab.findViewById(R.id.bottom_tab_roll_call).setSelected(true);
		} else {
			aq.id(R.id.bottom_tab_roll_call).visibility(View.GONE);
			bottomTab.findViewById(R.id.bottom_tab_curriculum)
					.setSelected(true);
		}
	}

	/**
	 * 准备tab的内容Intent
	 */
	private void prepareIntent() {
		curriculumIntent = new Intent(this, CurriculumActivity.class);
		rollCallIntent = new Intent(this, CallClassActivity.class);
		classroomCourseIntent = new Intent(this, CourseClassActivity.class);
		classroomTestIntent = new Intent(this, TestClassActivity.class);
		summaryIntent = new Intent(this, SummaryClassActivity.class);
		summaryIntent.putExtra(subjectid, subjectid);
	}

	private void setupIntent() {
		this.tabHost = getTabHost();
		TabHost localTabHost = this.tabHost;
		if (userType.equals("老师")) {
			localTabHost.addTab(buildTabSpec(TAB_TAG_ROLL_CALL, R.string.roll_call,
							R.drawable.ic_launcher, rollCallIntent));
		} else {
			localTabHost.addTab(buildTabSpec(TAB_TAG_CURRICULUM,R.string.curriculum, 
					R.drawable.ic_launcher,curriculumIntent));
		}
		localTabHost.addTab(buildTabSpec(TAB_TAG_CLASSROOM_COURSE,
				R.string.classroom_course, R.drawable.ic_launcher,
				classroomCourseIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_CLASSROOM_TEST,
				R.string.classroom_test, R.drawable.ic_launcher,
				classroomTestIntent));
		localTabHost.addTab(buildTabSpec(TAB_TAG_SUMMARY, R.string.summary,
				R.drawable.ic_launcher, summaryIntent));
	}

	/**
	 * 构建TabHost的Tab页
	 * 
	 * @param tag 标记
	 * @param resLabel 标签
	 * @param resIcon 图标
	 * @param content 该tab展示的内容
	 * @return 一个tab
	 */
	private TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return tabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}


	OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		@Override
		public void OnCheckedChange(View checkview) {
			switch (checkview.getId()) {
			case R.id.bottom_tab_curriculum:// 课程
				tabHost.setCurrentTabByTag(TAB_TAG_CURRICULUM);
				break;
			case R.id.bottom_tab_roll_call:// 点名
				tabHost.setCurrentTabByTag(TAB_TAG_ROLL_CALL);
				break;
			case R.id.bottom_tab_classroom_course:// 课件
				tabHost.setCurrentTabByTag(TAB_TAG_CLASSROOM_COURSE);
				break;
			case R.id.bottom_tab_classroom_test:// 测验
				tabHost.setCurrentTabByTag(TAB_TAG_CLASSROOM_TEST);
				break;
			case R.id.bottom_tab_summary:// 总结
				tabHost.setCurrentTabByTag(TAB_TAG_SUMMARY);
				break;
			}
		}

		@Override
		public void OnCheckedClick(View checkview) {

		}
	};
	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(callBack!=null)
			AppUtility.permissionResult(requestCode,grantResults,this,callBack);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}

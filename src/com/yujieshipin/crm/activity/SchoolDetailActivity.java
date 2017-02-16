package com.yujieshipin.crm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.yujieshipin.crm.fragment.SchoolAchievementDetailFragment;
import com.yujieshipin.crm.fragment.SchoolBillDetailFragment;
import com.yujieshipin.crm.fragment.SchoolNoticeDetailFragment;
import com.yujieshipin.crm.fragment.SchoolQuestionnaireDetailFragment;
import com.yujieshipin.crm.fragment.SchoolWorkAttendanceDetailFragment;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: school查看各类详情信息
 * 
 * <br/>
 * 创建说明: 2014-4-17 下午2:18:43 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class SchoolDetailActivity extends FragmentActivity {
	private String TAG = "SchoolDetailActivity";
	
	private static List<String> TemplateNameS = new ArrayList<String>();
	private int templateType;
	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	private String interfaceName, templateName, title, status,autoClose;
	Fragment fragment;
	static {
		TemplateNameS.add("通知");
		TemplateNameS.add("考勤");
		TemplateNameS.add("成绩");
		TemplateNameS.add("调查问卷");
		TemplateNameS.add("单据");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = getSupportFragmentManager().findFragmentById(
				android.R.id.content);
		Intent intent = getIntent();
		templateName = intent.getStringExtra("templateName");
		interfaceName = intent.getStringExtra("interfaceName");
		title = intent.getStringExtra("title");
		status = intent.getStringExtra("status");
		autoClose = intent.getStringExtra("autoClose");
		Log.d(TAG, "---templateName" + templateName);
		Log.d(TAG, "---interfaceName" + interfaceName);
		for (int i = 0; i < TemplateNameS.size(); i++) {
			if (TemplateNameS.get(i).equals(templateName)) {
				templateType = i;
			}
		}
		Log.d(TAG, "-------------------savedInstanceState");
		switch (templateType) {
		case 0:
			fragment = SchoolNoticeDetailFragment.newInstance(title, interfaceName);
			break;
		case 1:
			fragment = new SchoolWorkAttendanceDetailFragment(title,interfaceName);
			break;
		case 2:
			fragment = new SchoolAchievementDetailFragment(title, interfaceName);
			break;
		case 3:
			fragment = new SchoolQuestionnaireDetailFragment(title,status,interfaceName,autoClose);
			break;
		case 4:
			fragment = new SchoolBillDetailFragment(title,interfaceName);
			break;
		}
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
	}

	
	

	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("interfaceName", interfaceName);
        savedInstanceState.putString("status", status);
        savedInstanceState.putString("autoClose", autoClose);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString("title");
        interfaceName = savedInstanceState.getString("interfaceName");
        status = savedInstanceState.getString("status");
        autoClose = savedInstanceState.getString("autoClose");
    }
}

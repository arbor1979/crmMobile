package com.yujieshipin.crm.activity;

import java.util.ArrayList;
import java.util.List;

import com.yujieshipin.crm.fragment.SchoolAchievementFragment;
import com.yujieshipin.crm.fragment.SchoolBillFragment;
import com.yujieshipin.crm.fragment.SchoolNoticeFragment;
import com.yujieshipin.crm.fragment.SchoolQuestionnaireFragment;
import com.yujieshipin.crm.fragment.SchoolWorkAttendanceFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: school查看各类数据
 * 
 *  <br/>创建说明: 2014-4-17 下午2:19:08 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class SchoolActivity extends FragmentActivity {
	private String TAG = "SchoolActivity";
	private static List<String> TemplateNameS = new ArrayList<String>();
	private int templateType;
	private String interfaceName, templateName,title;
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

		// Check what fragment is shown, replace if needed.
		fragment = getSupportFragmentManager().findFragmentById(
				android.R.id.content);
		Intent intent =getIntent();
		templateName = intent.getStringExtra("templateName");
		interfaceName = intent.getStringExtra("interfaceName");
		title = intent.getStringExtra("title");
		Log.d(TAG, "---templateName" + templateName);
		Log.d(TAG, "---interfaceName" + interfaceName);
		for (int i = 0; i < TemplateNameS.size(); i++) {
			if (TemplateNameS.get(i).equals(templateName)) {
				templateType = i;
			}
		}
		switch (templateType) {
		case 0:
			fragment = SchoolNoticeFragment.newInstance(title,interfaceName);
			break;
		case 1:
			fragment = new SchoolWorkAttendanceFragment(title,interfaceName);
			break;
		case 2:
			fragment = new SchoolAchievementFragment(title,interfaceName);
			break;
		case 3:
			fragment = new SchoolQuestionnaireFragment(title,interfaceName);
			break;
		case 4:
			fragment = new SchoolBillFragment(title,interfaceName);
			break;
		}
		if(getSupportFragmentManager().findFragmentById(
				android.R.id.content)!=null)
		{
			getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, fragment).commit();
		}
		else
		{
			getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment).commit();
		}
	}

	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("interfaceName", interfaceName);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString("title");
        interfaceName = savedInstanceState.getString("interfaceName");
    }
}

package com.yujieshipin.crm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.ExitApplication;

public class RegisterActivity extends Activity {
	Button bn_back;
	TextView tv_title,bn_start;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_register);
		bn_back = (Button)findViewById(R.id.back);
		
		bn_start = (TextView)findViewById(R.id.goto_text);
		bn_start.setVisibility(View.VISIBLE);
		tv_title = (TextView)findViewById(R.id.setting_tv_title);
		bn_back.setText("取消");
		bn_start.setText("提交");
		tv_title.setText("注册账号");
	}

}

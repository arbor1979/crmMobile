package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;

public class ChangePwdActivity extends Activity implements OnClickListener{

	private Dialog mLoadingDialog;
	InputMethodManager inputManager;
	TextView title;
	private Button back,save;
	String oldpwd;
	AQuery aq=new AQuery(this);
	private RelativeLayout headerlayout;
	private final int ChangePWD_CODE=1;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		headerlayout=(RelativeLayout)findViewById(R.id.headerlayout);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			headerlayout.setBackgroundColor(color);
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		oldpwd=getIntent().getStringExtra("oldpwd");
		String mPassword=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
		if(!oldpwd.equals(mPassword))
		{
			AppUtility.showToastMsg(this, "旧密码不正确！");
			finish();
		}
		initTitle();
	}
	private void initTitle() {
		back = (Button) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.setting_tv_title);
		title.setText("修改密码");
		save= (Button) findViewById(R.id.uploading);
		save.setCompoundDrawables(null, null, null, null); 
		save.setVisibility(View.VISIBLE);
		save.setText("保存");
		//aq.id(R.id.setting_layout_goto).visibility(View.VISIBLE);
		String mUsername=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		aq.id(R.id.editText1).text(mUsername);
		back.setOnClickListener(this);
		save.setOnClickListener(this);
		EditText et=(EditText)findViewById(R.id.editText2);
		TimeUtility.popSoftKeyBoard(this, et);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			//inputManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
			finish();
			break;
		case R.id.uploading:
			String username=aq.id(R.id.editText1).getText().toString();
			String pwd =aq.id(R.id.editText2).getText().toString();
			String pwd1 =aq.id(R.id.editText3).getText().toString();
			if(username.trim().length()==0)
			{
				AppUtility.showToastMsg(this, "用户名不能为空！");
				break;
			}
			if(pwd.trim().length()==0)
			{
				AppUtility.showToastMsg(this, "密码不能为空！");
				break;
			}
			if(!pwd.equals(pwd1)){
				AppUtility.showToastMsg(this, "两次输入密码不一致！");
				break;
			}
			
			SubmitFeedback(username,pwd);

			break;
		default:
			break;
		}
	}

	public void SubmitFeedback(String username,String newpwd){
		showDialog();
		JSONObject jo = new JSONObject();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function",  "changePwd");
			jo.put("密码", newpwd);
			jo.put("旧密码", oldpwd);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, ChangePWD_CODE);
		
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case ChangePWD_CODE:
				
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				String result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					
					JSONObject jo = null;
					try {
						jo = new JSONObject(result);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(jo!=null){
						String tips = jo.optString("result");
						if(tips.equals("成功"))
						{
							PrefUtility.put(Constants.PREF_LOGIN_PASS, jo.optString("新密码"));
							DialogUtility.showMsg(ChangePwdActivity.this, "保存成功！");
							finish();
						}
						else
							DialogUtility.showMsg(ChangePwdActivity.this, jo.optString("errorMsg"));
					}
				}	
				
				break;
			case -1:
				mLoadingDialog.dismiss();
				AppUtility.showErrorToast(ChangePwdActivity.this, msg.obj.toString());
			break;
			}
		};
	};
	public void showDialog(){
		mLoadingDialog = DialogUtility.createLoadingDialog(this, "保存中...");
		mLoadingDialog.show();
	}
	
	public void closeDialog(){
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}
		DialogUtility.showMsg(this, "保存失败！");
	}
}


package com.yujieshipin.crm.activity;

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

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.base.ExitApplication;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;

public class FeedbackActivity extends Activity implements OnClickListener {
	
	private Button back,send;
	private EditText content;
	private TextView title;
	private InputMethodManager inputManager;
	private Dialog mLoadingDialog;
	private RelativeLayout headerlayout;
	private final int SUGGEST_CODE=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_feedback);
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initTitle();
		initContent();
		listener();
	}

	private void initTitle() {
		back = (Button) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.setting_tv_title);
		headerlayout=(RelativeLayout)findViewById(R.id.headerlayout);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			headerlayout.setBackgroundColor(color);
		title.setText("意见反馈");
	}

	private void initContent() {
		send = (Button) findViewById(R.id.send);
		int color=PrefUtility.getInt(Constants.PREF_THEME_TABBARCOLOR, 0);
		if(color!=0)
			send.setBackgroundColor(color);
		content = (EditText) findViewById(R.id.suggest);
	}

	private void listener() {
		back.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			inputManager.hideSoftInputFromWindow(content.getWindowToken(), 0);
			finish();
			break;
		case R.id.send:
			String suggest = content.getText().toString();

			if (suggest != null && !suggest.trim().toString().equals("")) {
				showDialog();
				SubmitFeedback(suggest);
				
			} else {
				AppUtility.showToastMsg(this, "提交意见不能为空！");
			}

			break;
		default:
			break;
		}
	}
	
	/**
	 * 功能描述:功能描述:提交服务器
	 *
	 * @author linrr  2013-12-16 下午2:18:41
	 * 
	 * @param base64Str
	 * @param action
	 */
	public void SubmitFeedback(String suggest){
		
		JSONObject jo = new JSONObject();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function",  "submitSuggest");
			jo.put("CONTENT", suggest);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, SUGGEST_CODE);
	}
		
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case SUGGEST_CODE:
				String result = msg.obj.toString();
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				JSONObject obj=null;
				try {
					obj = new JSONObject(result);
				} catch (JSONException e) { 
					e.printStackTrace();
				}
				if(obj!=null)
				{
					if(obj.optString("result").equals("成功"))
					{
				
						DialogUtility.showMsg(FeedbackActivity.this, "提交成功！");
						finish();
					}
					else
					{
						DialogUtility.showMsg(FeedbackActivity.this, obj.optString("errorMsg"));
					}
				}
				break;
			case -1:
				mLoadingDialog.dismiss();
				AppUtility.showErrorToast(FeedbackActivity.this, msg.obj.toString());
			break;
			}
		};
	};
	
	/**
	 * 功能描述:显示提示框
	 *
	 * @author yanzy  2013-12-21 上午10:54:41
	 *
	 */
	public void showDialog(){
		mLoadingDialog = DialogUtility.createLoadingDialog(FeedbackActivity.this, "数据提交中...");
		mLoadingDialog.show();
	}

	
}

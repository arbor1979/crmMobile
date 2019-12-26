package com.yujieshipin.crm.activity;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotifyClickActivity;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.AccountInfo;
import com.yujieshipin.crm.entity.Equipment;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;

public class LoginActivity extends UmengNotifyClickActivity implements OnClickListener,
		OnDismissListener {
	private static final String TAG = "LoginActivity";
	private EditText mUsernameView, mPasswordView,mSiteUrlView;
	private Button loginButton;
	private TableRow table_item;
	private ImageView logoImageView;
	private LinearLayout mainBackView;
	private TextView titleTextView;
	private String mSiteUrl,mUsername, mPassword;
	private Dao<Equipment, Integer> eqmDao;
	private Dao<User, Integer> userDao;
	private Dao<AccountInfo, Integer> accountInfoDao;
	private Dialog mLoadingDialog, experienceDialog, userTypeDialog;;
	private User user;
	private DatabaseHelper database;
	private ImageButton login_choose;
	private ListView listView;
	private PopupWindow popupWindow;
	private loginHistoryAdapter adapter;
	private String logoPath;
	private final int LOGIN_CODE=0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.moban_color_pink));
		}
		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getApplicationContext());
		//listData = new ArrayList<String>();
		// 初始化视图组件
		buildComponents();
		/*
		logoPath=LoginActivity.this.getFilesDir().getAbsolutePath()+"/logo.png";
		File file = new File(logoPath);  
		if(file.exists())
		{
			Bitmap bm=ImageUtility.getDiskBitmapByPath(logoPath);
			if(bm!=null)
				logoImageView.setImageBitmap(bm);
		}
		*/
		String title=PrefUtility.get(Constants.PREF_SCHOOL_DOMAIN,"");
		if(title!=null && title.length()>0)
			titleTextView.setText(title);
		int backgroundColor=PrefUtility.getInt(Constants.PREF_THEME_BACKGROUNDCOLOR, 0);
		if(backgroundColor!=0)
			mainBackView.setBackgroundColor(backgroundColor);
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		// 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
		// if (!BaiduPushUtility.hasBind()) {
		PrefUtility.put(Constants.PREF_BAIDU_USERID, "");
		PrefUtility.put(Constants.PREF_BAIDU_CHANNELID, "");
		PrefUtility.put(Constants.PREF_CHECK_CODE,"");
		
		try {
			accountInfoDao = getHelper().getAccountInfoDao();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		mSiteUrl=PrefUtility.get(Constants.PREF_LOGIN_URL, "");
		mUsername=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
		mPassword=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
		
		if(AppUtility.isNotEmpty(mSiteUrl) && AppUtility.isNotEmpty(mUsername))
		{
			mSiteUrlView.setText(mSiteUrl);
			mUsernameView.setText(mUsername);
			mPasswordView.setText(mPassword);
			doLogin();
		}
		
	}

	
	private void buildComponents() {
		mainBackView=(LinearLayout) findViewById(R.id.mainBackView);
		logoImageView=(ImageView) findViewById(R.id.user_logo);
		titleTextView=(TextView)findViewById(R.id.titleText);
		table_item = (TableRow) findViewById(R.id.table_item);
		mSiteUrlView = (EditText) findViewById(R.id.login_url);
		mUsernameView = (EditText) findViewById(R.id.login_username);
		mPasswordView = (EditText) findViewById(R.id.login_password);
		loginButton = (Button) findViewById(R.id.btn_login);
		//experienceButton = (Button) findViewById(R.id.btn_experience);
		login_choose = (ImageButton) findViewById(R.id.login_choose);
		mLoadingDialog = DialogUtility.createLoadingDialog(this, "正在登录...");
		
		// 注册按钮点击事件
		loginButton.setOnClickListener(this);
		//experienceButton.setOnClickListener(this);
		login_choose.setOnClickListener(this);
		String thisVersion = CampusApplication.getVersion();
		TextView tv_copyright = (TextView) findViewById(R.id.tv_copyright);
		tv_copyright.setText(tv_copyright.getText()+" v"+thisVersion);
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		case R.id.btn_experience:
			PrefUtility.put(Constants.PREF_CHECK_TEST, true);
			// jumpExperience();
			showUserTypeDialog(userTypes);
			break;
		*/
		case R.id.btn_login:
			PrefUtility.put(Constants.PREF_CHECK_TEST, false);
			attemptLogin();
			break;
		case R.id.login_choose:
			if (adapter == null) {
				listView = new ListView(this);
				int color=PrefUtility.getInt(Constants.PREF_THEME_LISTCOLOR, 0);
				if(color!=0)
					listView.setBackgroundColor(color);
				else
					listView.setBackgroundColor(Color.WHITE);
				adapter = new loginHistoryAdapter();
				listView.setAdapter(adapter);
				popupWindow = new PopupWindow(listView, table_item.getWidth(),
						LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
				// 点击外部消失
				popupWindow.setOutsideTouchable(true);
				
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAsDropDown(table_item);
				login_choose.setImageResource(R.drawable.login_btn_bg_sel);
			} else {
				adapter.notifyDataSetChanged();
				popupWindow = new PopupWindow(listView, table_item.getWidth(),
						LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
				// 点击外部消失
				popupWindow.setOutsideTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAsDropDown(table_item);
				login_choose.setImageResource(R.drawable.login_btn_bg_sel);
			}
			popupWindow.setOnDismissListener(this);
			break;

		}
	}

	private void attemptLogin() {
		// Reset errors.
		mSiteUrlView.setError(null);
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mSiteUrl=mSiteUrlView.getText().toString().trim();
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;
		if (TextUtils.isEmpty(mSiteUrl)) {
			mSiteUrlView.setError(getString(R.string.error_siteurl_required));
			focusView = mSiteUrlView;
			cancel = true;
		}
		else if(!AppUtility.isInt(mSiteUrl))
		{
			mSiteUrlView.setError(getString(R.string.error_siteurl_required_int));
			focusView = mSiteUrlView;
			cancel = true;
		}
		else if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_username_required));
			focusView = mUsernameView;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			// 显示登录提示
			//if(!mSiteUrl.startsWith("http", 0))
			//	mSiteUrl="http://"+mSiteUrl;
			//if(mSiteUrl.endsWith("/") || mSiteUrl.endsWith("\\"))
			//	mSiteUrl=mSiteUrl.substring(0,mSiteUrl.length()-1);

			PrefUtility.put(Constants.PREF_LOGIN_URL, mSiteUrl);
			doLogin();
		}
	}

	/**
	 * 执行登录操作
	 * 
	 * @description 变量:"用户名"和"密码" <br/>
	 *              对变量名进行JSON编码后，再进行Base64编码，然后提交，提交使用的参数名称为"DATA"。<br/>
	 * 
	 * @return 密文，先进行Base64解码,再进行JSON数据解析。
	 * 
	 */
	private void doLogin() {
		mLoadingDialog.show();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("function", "doLogin");
			jsonObj.put("siteUrl", mSiteUrl);
			jsonObj.put("username", mUsername);
			jsonObj.put("password", mPassword);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, LOGIN_CODE);
	}
	private boolean saveColor(String colorStr,String key)
	{
		if(colorStr!=null && colorStr.length()>0)
		{
			int color=0;
			try
			{
				color=Color.parseColor(colorStr);
				PrefUtility.put(key, color);
				return true;
			}
			catch(Exception e)
			{
				AppUtility.showToastMsg(LoginActivity.this,"颜色格式不正确"+colorStr);
			}
			
		}
		return false;
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				mLoadingDialog.dismiss();
				AppUtility.showErrorToast(LoginActivity.this,
						msg.obj.toString());
				break;
			case LOGIN_CODE:
				Date dt=new Date();
				String result = msg.obj.toString();
				try {
		
					JSONObject obj=new JSONObject(result);
					String loginStatus = obj.optString("result");
					if (loginStatus.equals("失败")) {
						AppUtility.showToastMsg(LoginActivity.this, obj.optString("errorMsg"),1);
						if(mLoadingDialog!=null)
							mLoadingDialog.dismiss();
					} else {
						
						/*
						String logo=obj.optString("logo");
						if(logo!=null)
						{
							ImageLoader.getInstance().displayImage(logo,logoImageView, new ImageLoadingListener(){

								@Override
								public void onLoadingCancelled(String arg0,
										View arg1) {
		
								}

								@Override
								public void onLoadingComplete(String arg0,
										View arg1, Bitmap arg2) {
									
									if(arg2!=null)
									{
										ImageUtility.writeTofilesPNG(arg2, logoPath, 90);
									}
								}

								@Override
								public void onLoadingFailed(String arg0,
										View arg1, FailReason arg2) {
						
								}

								@Override
								public void onLoadingStarted(String arg0,
										View arg1) {
							
								}
								
							});
							
							
						}
						JSONObject colorJson=obj.optJSONObject("color");
						if(colorJson!=null)
						{
							String backgroundColor=colorJson.optString("backgroundColor");
							String tabbarColor=colorJson.optString("tabbarColor");
							String navibarColor=colorJson.optString("navibarColor");
							String menuColor=colorJson.optString("menuColor");
							String listColor=colorJson.optString("listColor");
							
							if(saveColor(backgroundColor,Constants.PREF_THEME_BACKGROUNDCOLOR))
								mainBackView.setBackgroundColor(Color.parseColor(backgroundColor));
							saveColor(tabbarColor,Constants.PREF_THEME_TABBARCOLOR);
							saveColor(navibarColor,Constants.PREF_THEME_NAVBARCOLOR);
							saveColor(menuColor,Constants.PREF_THEME_MENUCOLOR);
							saveColor(listColor,Constants.PREF_THEME_LISTCOLOR);
							
						}
						*/
						user = new User(obj);
					    if(AppUtility.isNotEmpty(user.getCompanyName()))
					    {
					    	titleTextView.setText(user.getCompanyName());
					    	PrefUtility.put(Constants.PREF_SCHOOL_DOMAIN,user.getCompanyName());
					    }
						if (AppUtility.isNotEmpty(user.getId())) {
							Log.d(TAG, "--->  登录成功！");

							PrefUtility.put(Constants.PREF_CHECK_CODE,user.getCheckCode());
							PrefUtility.put(Constants.PREF_LOGIN_NAME, mUsername);
							PrefUtility.put(Constants.PREF_LOGIN_PASS, mPassword);
							PrefUtility.put(Constants.PREF_CHECK_USERTYPE,user.getUserType());
							PrefUtility.put(Constants.PREF_INIT_BASEDATE_FLAG,true);
							PrefUtility.put(Constants.PREF_CHECK_HOSTID,user.getUserNumber());

							String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
							((CampusApplication)getApplicationContext()).setLoginUserObj(user);
							user.setPassword(mPassword);
							userDao = getHelper().getUserDao();
							userDao.delete((PreparedDelete<User>) userDao.deleteBuilder().prepare());
							userDao.create(user);
							
							AccountInfo info = accountInfoDao.queryBuilder().where()
									.eq("userName", mUsername).and().eq("siteUrl", mSiteUrl).queryForFirst();
							if (info == null) {
								AccountInfo accountInfo = new AccountInfo();
								long time = new Date().getTime();
								accountInfo.setSiteUrl(mSiteUrl);
								accountInfo.setUserName(mUsername);
								accountInfo.setPassWord(mPassword);
								accountInfo.setLoginTime(time);
								accountInfoDao.create(accountInfo);
							} else {
								long time = new Date().getTime();
								info.setPassWord(mPassword);
								info.setLoginTime(time);
								accountInfoDao.update(info);
							}

							String baiduUserId=PrefUtility.get(Constants.PREF_BAIDU_USERID, "");
							if(baiduUserId.length()>0)
							{
								InitData initData = new InitData(LoginActivity.this, getHelper(), null,"postBaiDuUserId",checkCode);
								initData.postBaiduUserId();
								PushAgent mPushAgent = PushAgent.getInstance(AppUtility.getContext());
								mPushAgent.setAlias(user.getUserNumber(), "唯一码",

										new UTrack.ICallBack() {

											@Override
											public void onMessage(boolean isSuccess, String message) {
												Log.d("app_setAlias",isSuccess+":"+message);
											}

										});
							}
							
							jumpMain();
							
						} else {
							// mLoadingDialog.dismiss(); // 关闭登陆提醒
							AppUtility.showToastMsg(LoginActivity.this,"返回数据为空");
						}
						
						if(mLoadingDialog!=null)
							mLoadingDialog.dismiss(); // 关闭登陆提醒
						
					}
				} catch (Exception e) {
					if(mLoadingDialog!=null)
						mLoadingDialog.dismiss();
					e.printStackTrace();
				}
				Log.d(TAG, "----------登录处理耗时:" + (new Date().getTime()-dt.getTime()));
				break;
			case 2:
				experienceDialog.dismiss();
				break;
			
			}
		};
	};

	/**
	 * 功能描述:跳转到主页
	 * 
	 * @author yanzy 2013-12-30 上午11:34:40
	 * 
	 */
	private void jumpMain() {
		Intent intent = new Intent(this, TabHostActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		if(mLoadingDialog!=null)
			mLoadingDialog.dismiss();
		this.finish();
	}

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (database != null) {
			OpenHelperManager.releaseHelper();
			database = null;
		}
		
	}

	/**
     */
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}

	/**
	 * 功能描述:选择用户类型进入演示
	 * 
	 * @author shengguo 2014-5-10 上午11:05:35
	 * 
	 * @param data
	 */
	private void showUserTypeDialog(String[] data) {
		userTypeDialog = new Dialog(LoginActivity.this, R.style.dialog);
		View view = LayoutInflater.from(getBaseContext()).inflate(
				R.layout.view_exam_login_dialog, null);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(data);
		mList.setAdapter(dialogAdapter);
		Window window = userTypeDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		window.setGravity(Gravity.CENTER);
		userTypeDialog.setContentView(view);
		userTypeDialog.show();
		
	}

	/**
	 * 弹出窗口listview适配器
	 */
	public class DialogAdapter extends BaseAdapter {
		String[] arrayData;

		public DialogAdapter(String[] array) {
			this.arrayData = array;
		}

		@Override
		public int getCount() {
			return arrayData == null ? 0 : arrayData.length;
		}

		@Override
		public Object getItem(int position) {
			return arrayData[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.view_testing_pop, null);
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String text = arrayData[position];
			holder.title.setText(text);
			holder.title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PrefUtility.put(Constants.PREF_CHECK_TEST, false);
					if ("老师".equals(text)) {
						mUsernameView.setText("0038@dandian.net");
						mPasswordView.setText("0038");
						attemptLogin();
					} else if ("家长".equals(text)) {
						mUsernameView.setText("jz1229641397@dandian.net");
						mPasswordView.setText("123456");
						attemptLogin();
					} else if ("学生".equals(text)) {
						mUsernameView.setText("1229641397@dandian.net");
						mPasswordView.setText("123456");
						attemptLogin();
					}
					userTypeDialog.dismiss();
				}
			});
			return convertView;
		}

	}

	class ViewHolder {
		TextView title;
	}

	class loginHistoryAdapter extends BaseAdapter {
		List<AccountInfo> accountInfoList;
		LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public loginHistoryAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
			try {
				accountInfoList = accountInfoDao.queryBuilder()
						.orderBy("loginTime", false).limit(4).query();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getCount() {
			return accountInfoList == null ? 0 : accountInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return accountInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PopHolder holder = null;
			if (convertView == null) {
				holder = new PopHolder();
				convertView = inflater.inflate(
						R.layout.view_login_poplist_item, null);
				holder.userName = (TextView) convertView
						.findViewById(R.id.account);
				holder.deleteButton = (ImageButton) convertView
						.findViewById(R.id.delete_account);
				convertView.setTag(holder);
			} else {
				holder = (PopHolder) convertView.getTag();
			}

			final AccountInfo info = this.accountInfoList.get(position);
			Log.i(TAG, info.getUserName() + info.getPassWord());
			holder.userName.setText(info.getSiteUrl());
			holder.userName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					mSiteUrlView.setText(info.getSiteUrl());
					mUsernameView.setText(info.getUserName());
					mPasswordView.setText(info.getPassWord());
					//attemptLogin();
				}
			});
			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						accountInfoDao.delete(info);
						accountInfoList.remove(info);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					adapter.notifyDataSetChanged();
					popupWindow.update();
					if (accountInfoList.size() == 0) {
						popupWindow.dismiss();
					}
				}
			});
			return convertView;
		}

	}

	class PopHolder {
		TextView userName;
		ImageButton deleteButton;
	}

	@Override
	public void onDismiss() {
		login_choose.setImageResource(R.drawable.login_btn_bg_nor);
	}

	@Override
	public void onMessage(Intent intent) {
		super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
		//String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
		//Log.i(TAG, body);
	}
}

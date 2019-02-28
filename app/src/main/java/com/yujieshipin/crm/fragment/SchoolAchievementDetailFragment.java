package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.ClassDetailActivity;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.SysSettingActivity;
import com.yujieshipin.crm.activity.WebSiteActivity;
import com.yujieshipin.crm.adapter.MyPictureAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AchievementDetail;
import com.yujieshipin.crm.entity.PayResult;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.AchievementDetail.Achievement;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.SignUtils;
import com.yujieshipin.crm.widget.NonScrollableGridView;

/**
 * 成绩详情
 */
public class SchoolAchievementDetailFragment extends Fragment {
	private String TAG = "SchoolAchievementDetailFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private AchievementDetail achievementDetail;
	private String title, interfaceName;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private LayoutParams leftParams, rightParams;
	private Dialog dialog;
	private List<Achievement> achievements = new ArrayList<Achievement>();
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				String resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), res);
						}else{
							achievementDetail = new AchievementDetail(jo);
							Log.d(TAG, "--------noticesItem.getNotices().size():"
									+ achievementDetail.getAchievements().size());
							initDate();
						}
					} catch (JSONException e) {
						//showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					//showFetchFailedView();
				}
				break;
			case 1:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						AppUtility.showToastMsg(getActivity(), "操作"+res);
						if(res.equals("成功"))
							getAchievesItem();
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 2:
				LinearLayout v=(LinearLayout)msg.obj;
				v.setVisibility(View.GONE);
				break;
			case 3:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						
						if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), "操作成功!");
							String autoClose=jo.optString("自动关闭");
							if(autoClose!=null && autoClose.equals("是"))
							{
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
							else
								getAchievesItem();
						}
						else
							AppUtility.showErrorToast(getActivity(), "操作失败:"+res);
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 4: 
				PayResult payResult = new PayResult((String) msg.obj);
				
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				
				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(getActivity(), "支付成功",
							Toast.LENGTH_SHORT).show();
					getAchievesItem();
					Intent aintent = new Intent();
					getActivity().setResult(1,aintent); 
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(getActivity(), "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(getActivity(), "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			
				
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 1:
			getAchievesItem();
		    break;
		default:
		    break;
		}
	}
	public SchoolAchievementDetailFragment() {
		
	}
	public SchoolAchievementDetailFragment(String title, String iunterfaceName) {
		this.title = title;
		this.interfaceName = iunterfaceName;
		leftParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
		rightParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvTitle.setText(title);
		adapter = new AchieveAdapter();
		myListview.setAdapter(adapter);
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getAchievesItem();
			}
		});
		getAchievesItem();
		return view;
	}

	/**
	 * 功能描述:初始化数据
	 * 
	 * @author shengguo 2014-4-17 下午5:18:06
	 * 
	 */
	private void initDate() {
		tvTitle.setText(achievementDetail.getTitle());
		achievements = achievementDetail.getAchievements();
		//设置Weight值
		float leftWeight = achievementDetail.getLeftWeight() / 10.0f;
		float rightWeight = achievementDetail.getRightWeight() / 10.0f;
		Log.d(TAG, "leftWeight:" + leftWeight + ",rightWeight:" + rightWeight);
		leftParams = new LayoutParams(0,LayoutParams.WRAP_CONTENT, leftWeight);
		rightParams = new LayoutParams(0,LayoutParams.WRAP_CONTENT, rightWeight);
		if(achievementDetail.getSubmitBtn()!=null && achievementDetail.getSubmitBtn().length()>0)
		{
			tvRight.setText(achievementDetail.getSubmitBtn());
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(achievementDetail.getSubmitTarget().equals("是"))
					{
						submitButtonClick(achievementDetail.getSubmitBtnUrl());
					}
					else
					{
						Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
						intent.putExtra("templateName", "调查问卷");
						int pos=interfaceName.indexOf("?");
						String preUrl=interfaceName;
						if(pos>-1)
							preUrl=interfaceName.substring(0, pos);
						intent.putExtra("interfaceName", preUrl+achievementDetail.getSubmitBtnUrl());
						intent.putExtra("title", title);
						intent.putExtra("status", "进行中");
						intent.putExtra("autoClose", "是");
						startActivityForResult(intent, 101);
					}
				}
			});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
		adapter.notifyDataSetChanged();
	}
	//submit按钮
	private void submitButtonClick(String url) {

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");

		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				"数据处理中...");
		dialog.show();

		String base64Str = Base64.encode(jo.toString().getBytes());
	
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		CampusAPI.getSchoolItem(params,
				preUrl + url,
				new RequestListener() {

					@Override
					public void onIOException(IOException e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(CampusException e) {
						Log.d(TAG, "----response" + e.getMessage());
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = -1;
						msg.obj = e.getMessage();
						mHandler.sendMessage(msg);
					}

					@Override
					public void onComplete(String response) {
						Log.d(TAG, "----response" + response);
						if(dialog != null){
							dialog.dismiss();
						}
						Message msg = new Message();
						msg.what = 3;
						msg.obj = response;
						mHandler.sendMessage(msg);
					}
				});
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

	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getAchievesItem() {
		showProgress(true);
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());

			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	@SuppressLint("NewApi")
	class AchieveAdapter extends BaseAdapter {

		
		@Override
		public int getCount() {
			return achievements.size();
		}

		@Override
		public Object getItem(int position) {
			return achievements.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				convertView = inflater.inflate(
						R.layout.school_achievement_detail_item, parent, false);
				holder = new ViewHolder();
				holder.celllayout=(LinearLayout)convertView.findViewById(R.id.cell_layout);
				holder.left = (TextView) convertView.findViewById(R.id.thieDescription);
				holder.right = (TextView) convertView.findViewById(R.id.tv_right);
				holder.hiddenBtn=(ImageView)convertView.findViewById(R.id.hiddenBtn);
				holder.ly_hidden=(LinearLayout)convertView.findViewById(R.id.ly_hidden);
				holder.grid_picture=(NonScrollableGridView)convertView.findViewById(R.id.grid_picture);
				holder.right_layout=(LinearLayout)convertView.findViewById(R.id.right_layout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AQuery aq = new AQuery(convertView);
			final Achievement achievement = (Achievement) getItem(position);
			holder.left.setText(achievement.getSubject());
			holder.right.setText(achievement.getFraction());
			if(achievement.getHiddenBtn()!=null && achievement.getHiddenBtn().length()>0)
			{
				
				aq.id(holder.hiddenBtn).image(achievement.getHiddenBtn(),false,true);
				holder.ly_hidden.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						doRequestUrl(achievement.getHiddenBtnURL());
					}
					
				});
			}
			holder.left.setLayoutParams(leftParams);
			holder.right_layout.setLayoutParams(rightParams);
			if(achievement.getImageList()!=null && achievement.getImageList().size()>0)
			{
				holder.grid_picture.setVisibility(View.VISIBLE);
				if(holder.grid_picture.getAdapter()==null)
				{
					MyPictureAdapter myPictureAdapter = new MyPictureAdapter(getActivity(),
						false, achievement.getImageList(), 10,"课堂笔记",0);
					holder.grid_picture.setAdapter(myPictureAdapter);
				}
				else
				{
					MyPictureAdapter myPictureAdapter=(MyPictureAdapter) holder.grid_picture.getAdapter();
					myPictureAdapter.setPicPaths(achievement.getImageList());
				}
			}
			else
				holder.grid_picture.setVisibility(View.GONE);
			
			
			convertView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(achievement.getHiddenBtn()!=null && achievement.getHiddenBtn().length()>0)
					{
						ViewHolder holder = (ViewHolder) v.getTag();
						if(holder.ly_hidden.getVisibility()==View.GONE)
						{
							holder.ly_hidden.setVisibility(View.VISIBLE);
							Timer timer = new Timer(); 
							timer.schedule(new Task(holder), 3 * 1000);
						}
					}
					
					if(achievement.getLat()!=0 && !String.valueOf(achievement.getLat()).equals("NaN"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						String address=achievement.getFraction().split("\n")[0];
						String url=String.format("http://mo.amap.com/?q=%.10f,%.10f&name=%s&dev=1", achievement.getLat(),achievement.getLon(),address);
						contractIntent.putExtra("url",url);
						contractIntent.putExtra("title", achievement.getSubject());
						startActivity(contractIntent);
					}
					if(achievement.getHtmlText()!=null && achievement.getHtmlText().length()>0 && !achievement.getHtmlText().equals("null"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						contractIntent.putExtra("htmlText",achievement.getHtmlText());
						contractIntent.putExtra("title", achievementDetail.getTitle());
						contractIntent.putExtra("loginUrl", achievementDetail.getLoginUrl());
						startActivity(contractIntent);
					}
					
					if(achievement.getUrl()!=null && achievement.getUrl().length()>0 && !achievement.getUrl().equals("null"))
					{
						Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
						intent.putExtra("templateName", "成绩");
						int pos=interfaceName.indexOf("?");
						String preUrl=interfaceName;
						if(pos>-1)
							preUrl=interfaceName.substring(0, pos);
						intent.putExtra("interfaceName", preUrl+achievement.getUrl());
						intent.putExtra("title", achievement.getFraction());
						startActivityForResult(intent,101);
					}
					if(achievement.getKechengId()!=null && achievement.getKechengId().length()>0)
					{
						TeacherInfo ti = new TeacherInfo();
						ti.setId(achievement.getKechengId());
						ti.setUsername(achievement.getTeacherUsername());
						ti.setCourseName(achievementDetail.getTitle());
						// Intent intent = new
						// Intent(getActivity(),ClassRoomActivity.class);
						Intent intent = new Intent(getActivity(),
								ClassDetailActivity.class);
						intent.putExtra("teacherInfo", (Serializable) ti);
						startActivity(intent);
					}
					
				}
				
			});
			
	
			
			return convertView;
		}
		class Task extends TimerTask {
			private ViewHolder holder;
			public Task(ViewHolder h)
			{
				holder=h;
			}
			public void run()
			{
				Message msg = new Message();
				msg.what = 2;
				msg.obj = holder.ly_hidden;
				mHandler.sendMessage(msg);   
			}
		}
		class ViewHolder {
			LinearLayout celllayout;
			TextView left;
			TextView right;
			ImageView hiddenBtn;
			LinearLayout ly_hidden;
			NonScrollableGridView grid_picture;
			LinearLayout right_layout;
		}
	}
	private void doRequestUrl(String url)
	{
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				"数据处理中...");
		dialog.show();
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		int pos=interfaceName.indexOf("?");
		String preUrl=interfaceName;
		if(pos>-1)
			preUrl=interfaceName.substring(0, pos);
		CampusAPI.getSchoolItem(params,preUrl+ url,new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				if(dialog != null){
					dialog.dismiss();
				}
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				if(dialog != null){
					dialog.dismiss();
				}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	
}

package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.adapter.MyPictureAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.Curriculum;
import com.yujieshipin.crm.entity.DownloadSubject;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.NonScrollableGridView;

public class CurriculumActivity extends Activity {
	private String TAG = "CurriculumActivity";
	private Curriculum curriculum;
	private AQuery aq;
	private Button btnLeft;
	private RatingBar rbTeacherRank, rbCourserating;
	private LinearLayout loadingLayout;
	private ScrollView contentLayout;
	private LinearLayout failedLayout;
	private NonScrollableGridView grid_picture,grid_picture1; 
	private MyPictureAdapter myPictureAdapter,myPictureAdapter1;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:// 请求失败
				showFetchFailedView();
				AppUtility.showErrorToast(CurriculumActivity.this, msg.obj.toString());
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
						e.printStackTrace();
					}
				}
				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(CurriculumActivity.this,
									res);
						} else {
							curriculum = new Curriculum(jo);
							initData();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			default:
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curriculum);
		rbTeacherRank = (RatingBar) findViewById(R.id.rb_teacher_rank);
		rbCourserating = (RatingBar) findViewById(R.id.rb_course_rating);
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		contentLayout = (ScrollView) findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) findViewById(R.id.empty_error);
		grid_picture=(NonScrollableGridView)findViewById(R.id.grid_picture);
		grid_picture1=(NonScrollableGridView)findViewById(R.id.grid_picture1);
		aq = new AQuery(this);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);

		//aq.id(R.id.tv_title).textSize(getResources().getDimensionPixelSize(R.dimen.text_size_micro));
		initListener();
		GetCourseAndTeacherInfo();
	};
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
			
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
			loadingLayout.setVisibility(View.VISIBLE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
		}
	}
	private void initData() {

		if (ClassDetailActivity.userType.equals("教师")) {
			aq.id(R.id.tv_title).text(curriculum.getClassRoom());
		} else {
			aq.id(R.id.tv_title).text(curriculum.getKecheng());
		}

		rbTeacherRank.setRating(Float.valueOf(curriculum.getTeacherRank()));
		rbCourserating.setRating(Float.valueOf(curriculum.getCourseRating()));
		aq.id(R.id.tv_teacher_pingjiashu).text(curriculum.getTeacherpingjiashu()+"人评");
		aq.id(R.id.tv_kecheng_pingjiashu).text(curriculum.getKechengpingjiashu()+"人评");

		final String userImage =curriculum.getTeacherPhoto();
		ImageOptions options = new ImageOptions();
		options.memCache=false;
		options.targetWidth=300;
		options.round = 150;
		aq.id(R.id.iv_pic).image(userImage, options);
		aq.id(R.id.tv_name).text(curriculum.getTeacherName());
		aq.id(R.id.tv_brought_course).text(curriculum.getTeacherCourses());
		aq.id(R.id.right_detail).text(curriculum.getTeacherClasses());
		aq.id(R.id.tv_summary_content).text(curriculum.getSummaryContent());
		aq.id(R.id.tv_homework).text(curriculum.getHomeWork());
		aq.id(R.id.course_date).text(curriculum.getShangkeriqi()+" "+curriculum.getJieci()+"节");
		aq.id(R.id.class_room).text(curriculum.getClassRoom());
		String attendanceName =((CampusApplication)getApplicationContext()).getLoginUserObj().getName().replace("[家长]","")+ "的出勤";
		aq.id(R.id.tv_attendance_name).text(attendanceName);
		aq.id(R.id.tv_attendance_values).text(curriculum.getAttendanceValues());
		aq.id(R.id.tv_ketangjilv).text(curriculum.getKetangjilv());
		aq.id(R.id.tv_jiaoshiweisheng).text(curriculum.getJiaoshiweisheng());
		aq.id(R.id.iv_pic).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 查看大图
				if(AppUtility.isNotEmpty(userImage)){
					DialogUtility.showImageDialog(CurriculumActivity.this, userImage);
					
				}
			}
		});
		
		if(curriculum.getImagePaths()!=null && curriculum.getImagePaths().size()>0)
		{
			grid_picture.setVisibility(View.VISIBLE);
			ArrayList<String> picturePaths=new ArrayList<String>();
			for(DownloadSubject down:curriculum.getImagePaths())
			{
				picturePaths.add(down.getDownAddress());
			}
			myPictureAdapter = new MyPictureAdapter(this,
					false, picturePaths, 10,"课堂笔记");
			myPictureAdapter.setFrom(TAG);
			grid_picture.setAdapter(myPictureAdapter);
		}
		if(curriculum.getImagePaths1()!=null && curriculum.getImagePaths1().size()>0)
		{
			grid_picture1.setVisibility(View.VISIBLE);
			ArrayList<String> picturePaths=new ArrayList<String>();
			for(DownloadSubject down:curriculum.getImagePaths1())
			{
				picturePaths.add(down.getDownAddress());
			}
			myPictureAdapter1 = new MyPictureAdapter(this,
					false, picturePaths, 10,"课堂作业");
			myPictureAdapter1.setFrom(TAG);
			grid_picture1.setAdapter(myPictureAdapter1);
		}
	}

	private void initListener() {
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 通知ClassDetailActivity finish();
				Intent intent =new Intent("finish_classdetailactivity");
				sendBroadcast(intent);
				finish();
			}
		});
		//重新加载
		failedLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GetCourseAndTeacherInfo();
			}
		});
	}

	/**
	 * 功能描述:学生身份：点某一节课后的接口数据来源
	 * 
	 * @author shengguo 2014-4-26 上午10:13:15
	 * 
	 */
	private void GetCourseAndTeacherInfo() {
		showProgress(true);
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("老师上课记录编号", ClassDetailActivity.teacherInfo.getId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getCourseAndTeacherInfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
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
}

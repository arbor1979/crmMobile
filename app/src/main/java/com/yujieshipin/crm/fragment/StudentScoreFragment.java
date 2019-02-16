package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.StuInfoBar;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.StuInfoBarGraph;

public class StudentScoreFragment extends Fragment {
	LinearLayout ll_bar_chart;
	TextView total,average;
	ArrayList<StuInfoBar> points;
	private LinearLayout loadingLayout;
	private RelativeLayout contentLayout;
	private LinearLayout failedLayout;
	DatabaseHelper database;
	TeacherInfo teacherInfo;
	TextView testTitle;
	TextView xueqi;
	ScrollView scrollView1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View localView = inflater.inflate(R.layout.view_stuinfo3, container,
				false);
		loadingLayout = (LinearLayout) localView.findViewById(R.id.data_load);
		contentLayout = (RelativeLayout) localView.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) localView.findViewById(R.id.empty_error);
		total = (TextView)localView.findViewById(R.id.totaltext);
		
		average = (TextView)localView.findViewById(R.id.average);
		testTitle = (TextView)localView.findViewById(R.id.stuinfo_info_name);
		
		xueqi = (TextView)localView.findViewById(R.id.stuinfo_studentid);
		scrollView1 = (ScrollView)localView.findViewById(R.id.scrollView1);
		ll_bar_chart = (LinearLayout) localView.findViewById(R.id.ll_bar_chart);
		
		Bundle bundle = getArguments();
		String studentId = bundle.getString("studentId");
		teacherInfo=(TeacherInfo)bundle.getSerializable("teacherInfo");
		if(teacherInfo==null)
		{
			User user=((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj();
			teacherInfo=new TeacherInfo();
			teacherInfo.setUsername(user.getUsername());
			teacherInfo.setCourseName("");
		}
		getTestScoreData(studentId);
		return localView;
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
	public void getTestScoreData(String studentId) {
		showProgress(true);
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("学生编号", studentId);
			jo.put("学期",  PrefUtility.get(Constants.PREF_CUR_XUEQI, ""));
			jo.put("课程名称", teacherInfo.getCourseName());
			jo.put("教师用户名",teacherInfo.getUsername());
			jo.put("ACTION","ceyanResult");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getCeyanInfo(params,  new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = msg.obj.toString();
			String resultStr = "";
			switch (msg.what) {
			case -1:
				showProgress(false);
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						
					} catch (UnsupportedEncodingException e) {
						
						e.printStackTrace();
					}
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(getActivity(), res);
						} else {
							
							initDate(jo);
						}
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				}
				break;
			
			}
		}
	};
	
	private void initDate(JSONObject jo)
	{
		
		total.setText(jo.optString("总分"));
		average.setText(jo.optString("平均分"));
		testTitle.setText(jo.optString("学生姓名")+"的课堂测验成绩");
		xueqi.setText("("+jo.optString("学期")+"课程:"+jo.optString("课程名称")+")");
		
		
		JSONArray json = jo.optJSONArray("测验结果");
		
		points = new ArrayList<StuInfoBar>();
		if(json!=null)
		{
			try {
					
				for(int i=0;i<json.length();i++)
				{
					JSONObject item=(JSONObject)json.get(i);
					 
					String title=item.optString("上课日期").substring(5)+"日  "+item.optString("节次")+"节";
					float value = Float.parseFloat(item.optString("分数"));
					StuInfoBar stuInfoBar = new StuInfoBar();
					stuInfoBar.setGrade(value);
					stuInfoBar.setName(title);
					points.add(stuInfoBar);
				}
				StuInfoBarGraph g = new StuInfoBarGraph(getActivity());
				g.setBars(points);
				ll_bar_chart.addView(g);
				//ll_bar_chart.setBackgroundColor(Color.GRAY);
				
				LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,60*points.size());
				g.setLayoutParams(lp);
				
				
			
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			scrollView1.setVisibility(View.GONE);
			if(getActivity()!=null)
			{
				TextView tv_empty=new TextView(getActivity());
				tv_empty.setLayoutParams(scrollView1.getLayoutParams());
				tv_empty.setTextSize(AppUtility.getPixByDip(getActivity(), 10));
				tv_empty.setText("没有课堂测验成绩");
				tv_empty.setGravity(Gravity.CENTER);
				contentLayout.addView(tv_empty);
			}
			
		}
		
	}
	

}

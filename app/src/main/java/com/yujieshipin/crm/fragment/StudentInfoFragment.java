package com.yujieshipin.crm.fragment;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.Student;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;

public class StudentInfoFragment extends Fragment {
	TextView stuName, stuPhone, stuEmail, paName, paPhone, stuAddress,
			stuRemark,stuBanJi,stuXueHao;
	ImageView stuImage, stuinfoBack;
	Student studentInfo;
	String studentId;
	String userImage;
	Dao<Student, Integer> studentDao;
	DatabaseHelper database;
	AQuery aq;
	private String curPhone;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View localview = inflater.inflate(R.layout.view_stuinfo1, container,
				false);
		Bundle bundle = getArguments();
		studentId = bundle.getString("studentId");
		userImage = bundle.getString("userImage");
		query();
		findView(localview);
		initContent();
		return localview;
	}

	private void query() {
		try {
			
			studentInfo=null;
			Map<String,List<Student>> map=((CampusApplication)getActivity().getApplicationContext()).getStudentDic();
			for (String key : map.keySet()) {
				List<Student> stuList=map.get(key);
				for(Student stu:stuList)
				{
					if(stu.getStudentID().equals(studentId))
					{
						studentInfo=stu;
						break;
					}
				}
				if(studentInfo!=null)
					break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(studentInfo==null)
		{
			User user=((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj();
			String tmp[]=user.getUserNumber().split("_");
			String weiyi="用户_学生_"+studentId+"____"+tmp[tmp.length-1];
			Map<String,ContactsMember> map=((CampusApplication)getActivity().getApplicationContext()).getLinkManDic();
			ContactsMember member=map.get(weiyi);
			if(member!=null)
			{
				studentInfo=new Student();
				studentInfo.setName(member.getName());
				studentInfo.setStudentID(studentId);
				studentInfo.setClassName(member.getClassName());
				studentInfo.setPhone(member.getStuPhone());
				studentInfo.setEmail(member.getStuEmail());
				studentInfo.setHomeAddress(member.getAddress());
				studentInfo.setRemark(member.getRemark());
				studentInfo.setParentName(member.getRelativeName());
				studentInfo.setParentPhone(member.getRelativePhone());
			}
			else
			{
				AppUtility.showToastMsg(getActivity(), "没有找到此学生的资料");
				studentInfo=new Student();
			}
		}

	}

	private void findView(View view) {
		stuName = (TextView) view.findViewById(R.id.stu_name);
		stuPhone = (TextView) view.findViewById(R.id.stuinfo_phone);
		stuEmail = (TextView) view.findViewById(R.id.stuinfo_email);
		paName = (TextView) view.findViewById(R.id.stuinfo_parent_name);
		paPhone = (TextView) view.findViewById(R.id.stuinfo_parent_phone);
		stuAddress = (TextView) view.findViewById(R.id.stuinfo_address);
		stuRemark = (TextView) view.findViewById(R.id.stuinfo_remark);
		stuImage = (ImageView) view.findViewById(R.id.stu_photo);
		stuBanJi=(TextView) view.findViewById(R.id.stu_banji);
		stuXueHao=(TextView) view.findViewById(R.id.stu_xuehao);
	}

	private void initContent() {
		stuName.setText(studentInfo.getName());
		stuPhone.setText(Html.fromHtml("<u>"+studentInfo.getPhone()+"</u>"));
		stuEmail.setText(studentInfo.getEmail());
		paName.setText(studentInfo.getParentName());
		paPhone.setText(Html.fromHtml("<u>"+studentInfo.getParentPhone()+"</u>"));
		stuAddress.setText(studentInfo.getHomeAddress());
		stuRemark.setText(studentInfo.getRemark());
		stuImage.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_launcher1));
		ImageOptions options = new ImageOptions();
		options.memCache=false;
		options.targetWidth=200;
		options.round=100;
		aq.id(stuImage).image(userImage, options);
		stuBanJi.setText(studentInfo.getClassName());
		stuXueHao.setText("学号:"+studentInfo.getStudentID());
		stuPhone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AppUtility.isNotEmpty(stuPhone.getText().toString()))
					showDelOrShowPictureDiaLog(stuPhone.getText());
			}
			
		});
		paPhone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AppUtility.isNotEmpty(paPhone.getText().toString()))
					showDelOrShowPictureDiaLog(paPhone.getText());
			}
			
		});
		stuImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtility.showImageDialog(getActivity(),userImage);
				
			}
			
		});
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(),
					DatabaseHelper.class);

		}
		return database;
	}
private void showDelOrShowPictureDiaLog(final CharSequence charSequence) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_call_or_sendsms_phone, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView call_phone = (TextView) view.findViewById(R.id.tv_call);
		TextView send_sms = (TextView) view.findViewById(R.id.tv_sendsms);
		
		final AlertDialog ad=new AlertDialog.Builder(getActivity()).setView(view).create();

		Window window = ad.getWindow();
		
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		//拨打电话
		call_phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(charSequence!=null && charSequence.length()>0)
				{
					if (Build.VERSION.SDK_INT >= 23) 
					{
						if(AppUtility.checkPermission(getActivity(), 8,Manifest.permission.CALL_PHONE))
							sendCall(charSequence.toString());
						else
							curPhone=charSequence.toString();
					}
					else
						sendCall(charSequence.toString());
					
				}
				ad.dismiss();
			}
		});
		//发送短信
		send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) 
				{
					if(AppUtility.checkPermission(getActivity(), 9,Manifest.permission.SEND_SMS))
						sendMsg(charSequence.toString());
					else
						curPhone=charSequence.toString();
				}
				else
					sendMsg(charSequence.toString());
				
				ad.dismiss();
			}
		});
	}
	private void sendCall(String charSequence)
	{
		Intent phoneIntent = new Intent("android.intent.action.CALL",
				Uri.parse("tel:" + charSequence));
		startActivity(phoneIntent);
		
	}
	private void sendMsg(String charSequence)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto://"+charSequence));
		startActivity(intent);
	}
	public CallBackInterface callBack=new CallBackInterface()
	{

		@Override
		public void getLocation1() {
			// TODO Auto-generated method stub
		
		}

		@Override
		public void getPictureByCamera1() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getPictureFromLocation1() {
			// TODO Auto-generated method stub
			
		}

	};
	
}

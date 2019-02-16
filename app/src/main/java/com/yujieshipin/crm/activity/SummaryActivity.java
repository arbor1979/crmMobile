package com.yujieshipin.crm.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.ExitApplication;
import com.yujieshipin.crm.fragment.AttendSummaryFragment;
import com.yujieshipin.crm.fragment.ExamSummaryFragment;
import com.yujieshipin.crm.fragment.GradeSummaryFragment;

public class SummaryActivity extends FragmentActivity {
	ViewPager mViewPager;
	SectionPagerAdapter mSectionAdapter;
	TextView tv_attend,tv_exam,tv_grade,tv_title;
	Button bn_refresh,bn_title;
	static Button bn_menu;
	static LinearLayout layout_menu;
	static int view_width,view_height;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		ExitApplication.getInstance().addActivity(this);
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		view_width = display.getWidth();
		view_height = display.getHeight()/2;
		initTitle();
		mViewPager = (ViewPager)findViewById(R.id.summary_pager);
		mSectionAdapter = new SectionPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mSectionAdapter);
		mViewPager.setOnPageChangeListener(new PageListener());
	}
	
	private void initTitle(){
		tv_attend = (TextView)findViewById(R.id.attend);
		tv_exam = (TextView)findViewById(R.id.exam);
		tv_grade = (TextView)findViewById(R.id.grade);
		tv_title = (TextView)findViewById(R.id.tv_title);
		bn_title = (Button)findViewById(R.id.bn_title);
		bn_menu = (Button)findViewById(R.id.btn_back);
		bn_refresh = (Button)findViewById(R.id.btn_goto);
		layout_menu = (LinearLayout)findViewById(R.id.layout_back);
		bn_menu.setBackgroundResource(R.drawable.bg_title_homepage_back);
		bn_refresh.setBackgroundResource(R.drawable.bg_title_homepage_go);
		tv_title.setText("电子信息103班");
		bn_title.setVisibility(View.VISIBLE);
//		bn_title.setOnClickListener(new DialogListener());
		tv_attend.setOnClickListener(new TitleListener());
		tv_exam.setOnClickListener(new TitleListener());
		tv_grade.setOnClickListener(new TitleListener());
	}
	public class SectionPagerAdapter extends FragmentPagerAdapter{

		public SectionPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0 :
				AttendSummaryFragment mAttend = new AttendSummaryFragment();
				return mAttend;
			case 1 :
				ExamSummaryFragment mExam = new ExamSummaryFragment();
				return mExam;
			case 2 :
				GradeSummaryFragment mGrade = new GradeSummaryFragment();
				return mGrade;
				default :
					return null;
			}
		}
		
	}
	
	public class PageListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			switch(position){
			case 0 :
				tv_attend.setBackgroundResource(R.drawable.chat_msg_bg_sel);
				tv_exam.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				tv_grade.setBackgroundResource(R.drawable.chat_group_bg_nor);
				tv_attend.setTextColor(Color.parseColor("#27ae56"));
				tv_exam.setTextColor(Color.WHITE);
				tv_grade.setTextColor(Color.WHITE);
				break;
			case 1 :
				tv_attend.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				tv_exam.setBackgroundResource(R.drawable.chat_middle_bg_sel);
				tv_grade.setBackgroundResource(R.drawable.chat_group_bg_nor);
				tv_attend.setTextColor(Color.WHITE);
				tv_exam.setTextColor(Color.parseColor("#27ae56"));
				tv_grade.setTextColor(Color.WHITE);
				break;
			case 2 :
				tv_attend.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				tv_exam.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				tv_grade.setBackgroundResource(R.drawable.chat_group_bg_sel);
				tv_attend.setTextColor(Color.WHITE);
				tv_exam.setTextColor(Color.WHITE);
				tv_grade.setTextColor(Color.parseColor("#27ae56"));
				break;
				default :
					break;
			}
		}
		
	}
	
	public class TitleListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.equals(tv_attend)){
				mViewPager.setCurrentItem(0);
			}else{
				if(v.equals(tv_exam)){
					mViewPager.setCurrentItem(1);
				}else{
					mViewPager.setCurrentItem(2);
				}
			}
		}
		
	}
	
	public class DialogListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			final AlertDialog dialog = new AlertDialog.Builder(SummaryActivity.this).create();
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_summary_dialog, null);
			view.setLayoutParams(new LinearLayout.LayoutParams(view_width, view_height));    //大小没有变化
			GridView grid = (GridView)view.findViewById(R.id.grid);
			ClassAdapter mClass = new ClassAdapter();
			grid.setAdapter(mClass);
			grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					
				}
			});
			Button close = (Button)view.findViewById(R.id.close);
			close.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.setView(view);
			dialog.show();
		}
		
		public class ClassAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				return 12;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout layout = new LinearLayout(getApplicationContext());
				layout.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER_HORIZONTAL);
				ImageView image  = new ImageView(getApplicationContext());
				image.setLayoutParams(new LayoutParams(140, 140));
				image.setBackgroundResource(R.drawable.ic_launcher1);
				TextView tv = new TextView(getApplicationContext());
				tv.setText("电子103班");
				layout.addView(image);
				layout.addView(tv);
				return layout;
			}
		}
	}
	
}

package com.yujieshipin.crm.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.ExitApplication;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Dictionary;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.fragment.StudentInfoFragment;
import com.yujieshipin.crm.fragment.StudentScoreFragment;
import com.yujieshipin.crm.fragment.StudentTestFragment;

public class StudentInfoActivity extends FragmentActivity {
	private String TAG ="StudentInfoActivity";
	private ViewPager mViewpager;
	private StuInfoPagerAdapter mStuInfoPagerAdapter;
	//private ArrayList<View> views;
	//private View viewInfo, viewTable, viewScore, viewReinfo;
	private TextView textViewPoint, textViewBlank, tv_title, bn_go;
	 Button bn_back, menu, refresh;
	private TextView[] textViewPoints, textViewBlanks;
	//private Intent intent;
	//private Student studentInfo;
	private Dao<Dictionary, Integer> dictionaryDao;
	private Dictionary dictionary;
	private DatabaseHelper database;
	private List<String> typeShow;
	private String studentId;
	private String userImage;
	TeacherInfo teacherInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stuinfo);
		ExitApplication.getInstance().addActivity(this);
		studentId = getIntent().getStringExtra("studentId");
		userImage = getIntent().getStringExtra("userImage");
		teacherInfo=(TeacherInfo)getIntent().getSerializableExtra("teacherInfo");
		try {
			dictionaryDao = getHelper().getDictionaryDao();
			dictionary = dictionaryDao.queryBuilder().where()
					.eq("itemCode", "学生详情信息卡").queryForFirst();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (dictionary != null) {
			typeShow = new ArrayList<String>();
			typeShow.add("学生信息");
			typeShow.add("考勤");
			typeShow.add("成绩");
			//typeShow.add("成绩");
			try {
				JSONArray ja = new JSONArray(dictionary.getItemValue());
				for (int j = 0; j < ja.length(); j++) {
					String a = ja.optString(j);
					Log.d(TAG, "------------------>a:" + a);
					typeShow.add(a);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		initTitle();
		initViewPager();
	}

	private void initTitle() {
		tv_title = (TextView) findViewById(R.id.setting_tv_title);
		bn_back = (Button) findViewById(R.id.back);
		bn_go = (TextView) findViewById(R.id.goto_text);
		//bn_go.setVisibility(View.VISIBLE);
		//bn_go.setText("编辑");
		
		tv_title.setText("学生详情");
		menu = (Button) findViewById(R.id.menu);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initViewPager() {
		mViewpager = (ViewPager) findViewById(R.id.stuinfo_pager);
		mStuInfoPagerAdapter = new StuInfoPagerAdapter(
				getSupportFragmentManager(), typeShow);
		mViewpager.setAdapter(mStuInfoPagerAdapter);
		mViewpager.setOnPageChangeListener(new PointListener());
		point();
	}

	private void point() {
		ViewGroup viewGroup = (ViewGroup) findViewById(R.id.points);
		textViewPoints = new TextView[mStuInfoPagerAdapter.getCount()];
		textViewBlanks = new TextView[mStuInfoPagerAdapter.getCount()];
		for (int i = 0; i < mStuInfoPagerAdapter.getCount(); i++) {

			textViewPoint = new TextView(getApplicationContext());
			textViewBlank = new TextView(getApplicationContext());
			textViewPoint.setLayoutParams(new LayoutParams(10, 10));
			textViewBlank.setLayoutParams(new LayoutParams(10, 10));
			textViewPoints[i] = textViewPoint;
			textViewBlanks[i] = textViewBlank;
			if (i == 0) {
				// 默认进入程序后第一张图片被选中;
				textViewPoints[i]
						.setBackgroundResource(R.drawable.stuinfo_radio_sel);
			} else {
				textViewPoints[i]
						.setBackgroundResource(R.drawable.stuinfo_radio_nosel);
			}
			viewGroup.addView(textViewPoints[i]);
			viewGroup.addView(textViewBlanks[i]);

		}
	}

	public class StuInfoPagerAdapter extends FragmentPagerAdapter {
		List<String> list = new ArrayList<String>();

		public StuInfoPagerAdapter(FragmentManager fm, List<String> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = null;
			for (int i = 0; i < list.size(); i++) {
				if (position == i) {
					if (list.get(position).equals("学生信息")) {
						StudentInfoFragment mStuInfoFragment = new StudentInfoFragment();
						bundle = new Bundle();
						bundle.putString("studentId", studentId);
						bundle.putString("userImage", userImage);
						mStuInfoFragment.setArguments(bundle);
						return mStuInfoFragment;
					}
					
					if (list.get(position).equals("成绩")) {
						StudentScoreFragment mTableStuInfoFragment = new StudentScoreFragment();
						bundle = new Bundle();
						bundle.putString("studentId",
								studentId);
						bundle.putSerializable("teacherInfo",teacherInfo);
						mTableStuInfoFragment.setArguments(bundle);
						return mTableStuInfoFragment;
					}
					if (list.get(position).equals("测验")) {
						StudentTestFragment mRemark = new StudentTestFragment();
						bundle = new Bundle();
						bundle.putString("studentId",
								studentId);
						mRemark.setArguments(bundle);
						return mRemark;
					}

				}
			}
			return null;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}
	}

	public class PointListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < textViewPoints.length; i++) {
				textViewPoints[i]
						.setBackgroundResource(R.drawable.stuinfo_radio_sel);
				if (arg0 != i) {
					textViewPoints[i]
							.setBackgroundResource(R.drawable.stuinfo_radio_nosel);
				}
			}

		}

	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}

}

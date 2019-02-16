package com.yujieshipin.crm.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Dictionary;
import com.yujieshipin.crm.entity.Line;
import com.yujieshipin.crm.entity.LinePoint;
import com.yujieshipin.crm.entity.StudentTest;
import com.yujieshipin.crm.entity.StudentTestItem;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.widget.LineGraph;

public class StudentTestFragment extends Fragment {
	LinearLayout chart;
	ListView mList;
	int chart_width,chart_height;
	ExamAdapter mExam;
	
	private Dao<StudentTest, Integer> studentTestDao = null;
	private Dao<Dictionary, Integer> dictionaryDao = null;
	DatabaseHelper database;
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View localView = inflater.inflate(R.layout.view_stuinfo4, container, false);
		
		Bundle bundle = getArguments();
		String studentId = bundle.getString("studentId");
		StudentTest studentTest = null;
		
		
		List<StudentTestItem> studentTestItemList = null;
		try {
			studentTestDao = getHelper().getStudentTestDao();
			dictionaryDao = getHelper().getDictionaryDao();
			//studentTest = studentTestDao.queryBuilder().where().eq("studentID", studentId).queryForFirst();
			studentTest=new StudentTest();
			studentTest.setScoreTitle("本学期测验");
			studentTest.setTestItem("{}");
			JSONArray ja = null;
			String testItem = studentTest.getTestItem();
			if (AppUtility.isNotEmpty(testItem)) {
				try {
					ja = new JSONArray(testItem);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			StudentTestItem testItem1=new StudentTestItem();
			testItem1.setStudentID("01234567");
			testItem1.setName("张三");
			testItem1.setDate("2014-8-1");
			testItem1.setScore("90");
			testItem1.setAvgScore("80");
			testItem1.setHighestScore("95");
			StudentTestItem testItem2=new StudentTestItem();
			testItem2.setStudentID("12345678");
			testItem2.setName("李四");
			testItem2.setDate("2014-8-1");
			testItem2.setScore("90");
			testItem2.setAvgScore("80");
			testItem2.setHighestScore("95");
			studentTestItemList=new ArrayList<StudentTestItem>();
			studentTestItemList.add(testItem1);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (studentTest != null && studentTestItemList != null && studentTestItemList.size() > 0) {
			WindowManager manager = getActivity().getWindowManager();
			Display display = manager.getDefaultDisplay();
			chart_width = display.getWidth();
			chart_height = display.getHeight()/3;
			chart = (LinearLayout)localView.findViewById(R.id.chart);
			chart.setLayoutParams(new LinearLayout.LayoutParams(chart_width, chart_height));
			Line lineHighest = new Line();
			lineHighest.setColor(Color.RED);
			lineHighest.setTitle("最高分");
			Line lineAvg = new Line();
			lineAvg.setColor(Color.BLUE);
			lineAvg.setTitle("平均分");
			LinePoint point = null;
		
		
			for (StudentTestItem studentTestItem : studentTestItemList) {
				point = new LinePoint();
				point.setGrade(AppUtility.parseFloat(studentTestItem.getHighestScore()));
				lineHighest.addPoint(point);
				
				point = new LinePoint();
				point.setGrade(AppUtility.parseFloat(studentTestItem.getAvgScore()));
				lineAvg.addPoint(point);
			}
		
			LineGraph li = new LineGraph(getActivity().getApplicationContext());
			li.addLine(lineHighest);
			li.addLine(lineAvg);
			
			chart.addView(li);
			mList = (ListView)localView.findViewById(R.id.list);
			mExam = new ExamAdapter(studentTestItemList);
			mList.setAdapter(mExam);
		}
		return localView;
	}
	
	public class ExamAdapter extends BaseAdapter{
		private List<StudentTestItem> list = new ArrayList<StudentTestItem>();
		
		public ExamAdapter(List<StudentTestItem> studentTestItemList){
			this.list = studentTestItemList;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = new ViewHolder();
			final StudentTestItem studentTestItem = list.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_list_summary_exam, null);
				holder.img_photo = (ImageView) convertView
						.findViewById(R.id.call_stu_photo);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.exam_tv_name);
				holder.class_high = (TextView) convertView.findViewById(R.id.exam_class_high);
				holder.class_average = (TextView)convertView.findViewById(R.id.exam_class_average);
				holder.class_time = (TextView) convertView.findViewById(R.id.exam_class_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_name.setText((position+1)+"、"+studentTestItem.getName());
			holder.class_average.setText("平均分："+studentTestItem.getAvgScore());
			holder.class_high.setText("最高分："+studentTestItem.getHighestScore());
			holder.class_time.setText("测试时间："+studentTestItem.getDate());
			return convertView;
		}
		
		public class ViewHolder {
			TextView tv_name, class_high, class_average,class_time;
			ImageView img_photo;
		}
		
	}
	
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);

		}
		return database;
	}
}

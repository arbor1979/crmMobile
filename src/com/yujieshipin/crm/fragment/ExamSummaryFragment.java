package com.yujieshipin.crm.fragment;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.Line;
import com.yujieshipin.crm.entity.LinePoint;
import com.yujieshipin.crm.widget.LineGraph;
import com.yujieshipin.crm.widget.LineGraph.OnPointClickedListener;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ExamSummaryFragment extends Fragment {
	LinearLayout chart;
	ListView mList;
	int chart_width,chart_height;
	ExamAdapter mExam;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_summary, container, false);
		WindowManager manager = getActivity().getWindowManager();
		Display display = manager.getDefaultDisplay();
		chart_width = display.getWidth();
		chart_height = display.getHeight()/3;
		chart = (LinearLayout)localView.findViewById(R.id.chart);
		chart.setLayoutParams(new LinearLayout.LayoutParams(chart_width, chart_height));
		Line l = new Line();
		LinePoint p = new LinePoint();
		p.setGrade(60);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(80);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(100);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(50);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(60);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(40);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(50);
		l.addPoint(p);
		p = new LinePoint();
		p.setGrade(50);
		l.addPoint(p);
		l.setColor(Color.parseColor("#FFBB33"));
		l.setTitle("最高分");
		Line l1 = new Line();
		LinePoint p1 = new LinePoint();
		p1.setGrade(30);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(50);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(90);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(50);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(60);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(80);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(20);
		l1.addPoint(p1);
		p1 = new LinePoint();
		p1.setGrade(20);
		l1.addPoint(p1);
		l1.setColor(Color.parseColor("#27ae62"));
		l1.setTitle("平均分");
		LineGraph li = new LineGraph(getActivity().getApplicationContext());
		li.addLine(l);
		li.addLine(l1);
		
		chart.addView(li);
		mList = (ListView)localView.findViewById(R.id.list);
		mExam = new ExamAdapter();
		mList.setAdapter(mExam);
		return localView;
	}
	
	public class ExamAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 5;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_list_summary_exam, null);
			return convertView;
		}
		
	}

}

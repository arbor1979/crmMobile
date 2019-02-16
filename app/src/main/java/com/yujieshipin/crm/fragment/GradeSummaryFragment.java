package com.yujieshipin.crm.fragment;

import java.util.ArrayList;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.Bar;
import com.yujieshipin.crm.widget.BarGraph;
import com.yujieshipin.crm.widget.BarGraph.OnBarClickedListener;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class GradeSummaryFragment extends Fragment {
	LinearLayout chart;
	ListView mList;
	int chart_width,chart_height;
	GradeAdapter mGrade;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_summary, container, false);
		WindowManager manager = getActivity().getWindowManager();
		Display display = manager.getDefaultDisplay();
		chart_width = display.getWidth();
		chart_height = display.getHeight()/3;
		Log.d("Height", String.valueOf(chart_height));
		
		chart = (LinearLayout)localView.findViewById(R.id.chart);
		chart.setLayoutParams(new LinearLayout.LayoutParams(chart_width, chart_height));
		ArrayList<Bar> points = new ArrayList<Bar>();
		Bar d = new Bar();
		d.setColor(Color.parseColor("#58c5d4"));
		d.setName("平均分");
		d.setValue(10);
		Bar d2 = new Bar();
		d2.setColor(Color.parseColor("#27ae62"));
		d2.setName("最高分");
		d2.setValue(100);
		Bar d3 = new Bar();
		d3.setColor(Color.parseColor("#f45e37"));
		d3.setName("最低分");
		d3.setValue(20);
		points.add(d);
		points.add(d2);
		points.add(d3);
		
		BarGraph g = new BarGraph(getActivity().getApplicationContext());
		g.setBars(points);
		
		g.setOnBarClickedListener(new OnBarClickedListener(){

			@Override
			public void onClick(int index) {
				
			}
			
		});
		chart.addView(g);
		mList = (ListView)localView.findViewById(R.id.list);
		mGrade = new GradeAdapter();
		mList.setAdapter(mGrade);
		return localView;
	}
	
	public class GradeAdapter extends BaseAdapter{

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
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_list_summary_grade, null);
			return convertView;
		}
		
	}
}

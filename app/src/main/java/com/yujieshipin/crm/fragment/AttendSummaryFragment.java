package com.yujieshipin.crm.fragment;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.PieSlice;
import com.yujieshipin.crm.widget.PieGraph;
import com.yujieshipin.crm.widget.PieGraph.OnSliceClickedListener;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 汇总---出勤汇总
 * 
 *  <br/>创建说明: 2013-12-5 下午2:33:14 zhuliang  创建文件<br/>
 * 
 *  修改历史:<br/> 
 */
public class AttendSummaryFragment extends Fragment {
	LinearLayout chart;
	ListView mList;
	int chart_width,chart_height;
	AttendAdapter mAttend;
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
		PieGraph pg = new PieGraph(getActivity().getApplicationContext());
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#3c3aab"));
		slice.setValue(17);
		slice.setTitle("迟到");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#f4c637"));
		slice.setValue(10);
		slice.setTitle("请假");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#f45e37"));
		slice.setValue(15);
		slice.setTitle("缺席");
		pg.addSlice(slice);		
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#27ae62"));
		slice.setValue(87);
		slice.setTitle("出勤");
		pg.addSlice(slice);	
		pg.setOnSliceClickedListener(new OnSliceClickedListener(){

			@Override
			public void onClick(int index) {
			
			}
			
		});
		chart.addView(pg);
		mList = (ListView)localView.findViewById(R.id.list);
		mAttend = new AttendAdapter();
		mList.setAdapter(mAttend);
		return localView;
	}
	
	public class AttendAdapter extends BaseAdapter{

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
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_list_summary_attend, null);
			return convertView;
		}
		
	}
	
}

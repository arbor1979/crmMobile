package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yujieshipin.crm.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentCourse extends Fragment {
	ListView listview;
	AdapterCourseList mAdapterCourseList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.fragment_course, container, false);
		listview = (ListView)localView.findViewById(R.id.list_course);
		mAdapterCourseList = new AdapterCourseList();
		listview.setAdapter(mAdapterCourseList);
		return localView;
	}
	
	public class AdapterCourseList extends BaseAdapter {
		private ArrayList<Map<String,Object>> mlist;

		DataHolder holder = null;
		public ArrayList<Map<String,Object>> getData(){
			Map<String,Object> childList = new HashMap<String, Object>();
			mlist = new ArrayList<Map<String,Object>>();
			
			childList.put("image",R.drawable.ic_launcher);
			childList.put("course", "马列政治");
			childList.put("time","AM 08:30");
			childList.put("info","电子1010班" + "/" + "3号教学楼301室");
			for(int i = 0;i < 20;i++){
				mlist.add(childList);
			}
			return mlist;
			
		};
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return getData().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return getData().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			DataHolder holder = new DataHolder();
			convertView = mInflater.inflate(R.layout.list_homepage_course, null); 
			holder.imageview = (ImageView)convertView.findViewById(R.id.image_course);
			holder.tv_course = (TextView)convertView.findViewById(R.id.tv_homepage_course);
			holder.tv_time = (TextView)convertView.findViewById(R.id.tv_homepage_time);
			holder.tv_course_info = (TextView)convertView.findViewById(R.id.tv_homepage_courseinfo);
			holder.imageview.setBackgroundResource((Integer)getData().get(position).get("image"));
			holder.tv_course.setText((String)getData().get(position).get("course"));
			holder.tv_course_info.setText((String)getData().get(position).get("info"));
			holder.tv_time.setText((String)getData().get(position).get("time"));
			convertView.setTag(holder);
			return convertView;
		}
		
		
			
		
		public class DataHolder{
			ImageView imageview;
			TextView tv_course;
			TextView tv_time;
			TextView tv_course_info;
		}
		
	}
	
}

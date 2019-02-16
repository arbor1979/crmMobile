package com.yujieshipin.crm.fragment;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.ClassDetailActivity;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Schedule;
import com.yujieshipin.crm.entity.TeacherInfo;

public class ClassSelectDialogFragment extends DialogFragment implements
		OnItemClickListener,OnClickListener {

	private Dao<TeacherInfo, Integer> teacherInfoDao;
	DatabaseHelper database;
	String schedule;
	
	ScheduleDialogAdapter adapter;
	ListView listView;
	Dialog dialog;
	Button close;
	List<TeacherInfo> teacherInfoList;
	String userType;
	public String idStr;
	public static ClassSelectDialogFragment newInstance(){
		ClassSelectDialogFragment scheduleDialogFragment = new ClassSelectDialogFragment();
		return scheduleDialogFragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		teacherInfoList = new ArrayList<TeacherInfo>();
		userType=((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj().getUserType();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View localView = LayoutInflater.from(getActivity()).inflate(
				R.layout.view_dialog_schedule, null);
		query();
		initView(localView);
		dialog = new Dialog(getActivity(), R.style.dialog);
		dialog.setContentView(localView);
		dialog.getWindow().setGravity(Gravity.CENTER);
		return dialog;
	}

	private void query() {
		try {
			if(idStr==null) return;
			teacherInfoDao = getHelper().getTeacherInfoDao();
			String[] idArray=idStr.split(",");
			for(String id:idArray)
			{
				TeacherInfo item=teacherInfoDao.queryBuilder().where().eq("id", id).queryForFirst();
				if(item!=null && !teacherInfoList.contains(item))
					teacherInfoList.add(item);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initView(View view) {
		close = (Button)view.findViewById(R.id.close);
		listView = (ListView) view.findViewById(R.id.list);
		adapter = new ScheduleDialogAdapter(teacherInfoList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		close.setOnClickListener(this);
	}

	class ScheduleDialogAdapter extends BaseAdapter {
		List<TeacherInfo> list = new ArrayList<TeacherInfo>();

		public ScheduleDialogAdapter(List<TeacherInfo> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
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
			if(convertView==null)
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_schedule_dialog, null);
			TextView section = (TextView) convertView.findViewById(R.id.section);
			section.setVisibility(View.GONE);
			TextView sectionTime = (TextView) convertView.findViewById(R.id.sectiontime);
			
			if(userType.equals("老师"))
				sectionTime.setText(list.get(position).getClassGrade());
			else
				sectionTime.setText(list.get(position).getName());
			convertView.setTag(list.get(position));
			return convertView;
		}

	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(),
					DatabaseHelper.class);
		}
		return database;
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TeacherInfo item=(TeacherInfo)arg1.getTag();
		Intent intent = new Intent(getActivity(),
				ClassDetailActivity.class);
		intent.putExtra("teacherInfo", (Serializable) item);
		startActivity(intent);
		dialog.dismiss();
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.close :
			dialog.dismiss();
			break;
			default :
				break;
		}
	}
}

package com.yujieshipin.crm.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
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
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Schedule;

public class ScheduleDialogFragment extends DialogFragment implements
		OnItemClickListener,OnClickListener {

	Dao<Schedule, Integer> scheduleDao;
	DatabaseHelper database;
	String schedule;
	List<ScheduleTime> listData;
	ScheduleDialogAdapter adapter;
	ListView listView;
	Dialog dialog;
	String title;
	Button close;
	public static ScheduleDialogFragment newInstance(String title){
		ScheduleDialogFragment scheduleDialogFragment = new ScheduleDialogFragment();
		Bundle localBundle = new Bundle();
		localBundle.putString("title", title);
		scheduleDialogFragment.setArguments(localBundle);
		return scheduleDialogFragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getArguments().getString("title");
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View localView = LayoutInflater.from(getActivity()).inflate(
				R.layout.view_dialog_schedule, null);
		query();
		initView(localView);
		dialog = new Dialog(getActivity(), R.style.dialog);
		dialog.setContentView(localView);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		return dialog;
	}

	private void query() {
		try {
			scheduleDao = getHelper().getScheduleDao();
			schedule = scheduleDao.queryBuilder().queryForFirst()
					.getSectionsTime();

			JSONArray ja = new JSONArray(schedule);
			listData = new ArrayList<ScheduleDialogFragment.ScheduleTime>();
			ScheduleTime scheduleTime;
			for (int i = 0; i < ja.length(); i++) {
				scheduleTime = new ScheduleTime();
				JSONObject jo = ja.optJSONObject(i);
				scheduleTime.section = jo.optString("名称");
				System.out.println("-------section------>"
						+ scheduleTime.section);
				scheduleTime.sectionTime = jo.optString("时间");
				System.out.println("---------sectiontime------->"
						+ scheduleTime.sectionTime);
				listData.add(scheduleTime);
			}

			System.out.println("---------listData-------->" + listData.size());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initView(View view) {
		close = (Button)view.findViewById(R.id.close);
		listView = (ListView) view.findViewById(R.id.list);
		adapter = new ScheduleDialogAdapter(listData);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		close.setOnClickListener(this);
	}

	class ScheduleDialogAdapter extends BaseAdapter {
		List<ScheduleTime> list = new ArrayList<ScheduleDialogFragment.ScheduleTime>();

		public ScheduleDialogAdapter(List<ScheduleTime> list) {
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
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_schedule_dialog, null);
			TextView section = (TextView) convertView.findViewById(R.id.section);
			TextView sectionTime = (TextView) convertView.findViewById(R.id.sectiontime);
			section.setText("第" + list.get(position).section + "节");
			sectionTime.setText(list.get(position).sectionTime);
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

	class ScheduleTime {
		String section;
		String sectionTime;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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

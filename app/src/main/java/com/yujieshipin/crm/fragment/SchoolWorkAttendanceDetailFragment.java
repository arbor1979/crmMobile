package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.WorkAttendanceDetail;
import com.yujieshipin.crm.entity.WorkAttendanceDetail.AttendanceValue;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;

/**
 * 考勤
 */
public class SchoolWorkAttendanceDetailFragment extends Fragment {
	private String TAG = "SchoolWorkAttendanceDetailFragment";
	private Button btnLeft;
	private String title,interfaceName;
	private AQuery aq;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private LayoutInflater inflater;
	private WorkAttendanceDetail wAttendance;
	private List<AttendanceValue> attValues = new ArrayList<AttendanceValue>();
	private ListView myListview;
	private AttendanceValueAdapter adapter;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();
				String resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), res);
						}else{
							wAttendance = new WorkAttendanceDetail(jo);
							initDate();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			}
		}
	};
	public SchoolWorkAttendanceDetailFragment() {
		
	}
	public SchoolWorkAttendanceDetailFragment(String title, String iunterfaceName) {
		this.title = title;
		this.interfaceName = iunterfaceName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment, container,
				false);
		aq = new AQuery(view);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		adapter = new AttendanceValueAdapter();
		myListview.setAdapter(adapter);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		aq.id(R.id.tv_title).text(title);
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "正在获取数据");
		getWorkAttendanceItem();
	}

	/**
	 * 功能描述:向界面填充数据
	 * 
	 * @author shengguo 2014-4-16 下午3:15:48
	 * 
	 */
	private void initDate() {
		aq.id(R.id.tv_title).text(wAttendance.getTitle());
		attValues = wAttendance.getAttendanceValues();
		adapter.notifyDataSetChanged();
	}
	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}

	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}
	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getWorkAttendanceItem() {
		showProgress(true);
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, interfaceName, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	class AttendanceValueAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return attValues.size();
		}

		@Override
		public Object getItem(int position) {
			return attValues.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.school_work_attendance_detail_item, parent,
						false);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.value = (TextView) convertView.findViewById(R.id.tv_right);
				holder.title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.timeAndAddress = (TextView) convertView.findViewById(R.id.tv_time_and_address);
				holder.rightIcon=(ImageView) convertView.findViewById(R.id.iv_right);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final AttendanceValue attvalue = (AttendanceValue) getItem(position);
			AQuery aq = new AQuery(convertView);
			aq.id(holder.icon).image(attvalue.getBackground());
			holder.timeAndAddress.setText(attvalue.getTimeAndAddress());
			holder.title.setText(attvalue.getTitle());
			if(attvalue.getRightType().equals("图片")){
				holder.rightIcon.setVisibility(View.VISIBLE);
				holder.value.setVisibility(View.GONE);
				aq.id(holder.rightIcon).image(attvalue.getRightContent());
			}else{
				holder.rightIcon.setVisibility(View.GONE);
				holder.value.setVisibility(View.VISIBLE);
				holder.value.setText(attvalue.getRightContent());
			}
			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView title;
			TextView timeAndAddress;
			TextView value;
			ImageView rightIcon;
		}
	}
}

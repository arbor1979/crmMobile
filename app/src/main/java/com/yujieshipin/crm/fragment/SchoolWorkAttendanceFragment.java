package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.entity.WorkAttendanceItem;
import com.yujieshipin.crm.entity.WorkAttendanceItem.SelectByWeek;
import com.yujieshipin.crm.entity.WorkAttendanceItem.SelectShortCut;
import com.yujieshipin.crm.entity.WorkAttendanceItem.WorkAttendance;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.NonScrollableGridView;

/**
 * 考勤
 */
public class SchoolWorkAttendanceFragment extends Fragment {
	private String TAG = "SchoolWorkAttendanceFragment";
	private Button btnLeft;
	private String interfaceName, title;
	private AQuery aq;
	private LayoutInflater inflater;
	private LinearLayout loadingLayout, failedLayout;
	private Dao<User, Integer> userDao;
	private WorkAttendanceItem wAttendance;
	private List<WorkAttendance> workAttendances = new ArrayList<WorkAttendance>();
	private NonScrollableGridView myGridview;
	private WorkAttendanceAdapter adapter;
	private PopupWindow popupWindow;
	private View view, settingTimeView;
	private TextView tvClose, tvOk;
	private DatabaseHelper database;
	private User user;
	private int startWeek,endWeek;
	private NumberPicker nPicker1, nPicker2;
	

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = msg.obj.toString();
			String resultStr = "";
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else {
					showFetchFailedView();
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						Log.d(TAG, "-------res:" + res);
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(getActivity(), res);
						} else {
							wAttendance = new WorkAttendanceItem(jo);
							initDate();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				} else {
					showFetchFailedView();
				}
				break;
			case 1:
				showProgress(false);
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, "getwwek:" + resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						String res = jo.optString("结果");
						if (AppUtility.isNotEmpty(res)) {
							AppUtility.showToastMsg(getActivity(), res);
						} else {
							wAttendance = new WorkAttendanceItem(jo);
							initDate();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				} else {
					showFetchFailedView();
				}
				break;
			}
		}
	};

	public static final Fragment newInstance(String title, String interfaceName){
		Fragment fragment = new SchoolWorkAttendanceFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("interfaceName", interfaceName);
		fragment.setArguments(bundle);
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		title=getArguments().getString("title");
		interfaceName=getArguments().getString("interfaceName");
		this.inflater = inflater;
		view = inflater.inflate(R.layout.school_work_attendance_fragment,
				container, false);
		aq = new AQuery(view);
		String number =((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj().getId();
		try {
			userDao = getHelper().getUserDao();
			user = userDao.queryBuilder().where().eq("id", number)
					.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		myGridview = (NonScrollableGridView) view
				.findViewById(R.id.my_gridview);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		btnLeft.setVisibility(View.VISIBLE);
		adapter = new WorkAttendanceAdapter();
		myGridview.setAdapter(adapter);
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
		getWorkAttendanceItem();
		//aq.id(R.id.rbtn_left).checked(true);
	}

	/**
	 * 功能描述:向界面填充数据
	 * 
	 * @author shengguo 2014-4-16 下午3:15:48
	 * 
	 */
	private void initDate() {
		// final String imgUrl = wAttendance.getUserPic();
		// if (imgUrl != null) {
		// ImageOptions options = new ImageOptions();
		// options.round = 115;
		// aq.id(R.id.iv_user_pic).image(imgUrl, options);
		// }
		// aq.id(R.id.tv_title).text(wAttendance.getTemplateName());
		// aq.id(R.id.tv_user_name).text(wAttendance.getUserName());
		// aq.id(R.id.tv_user_study_id).text(wAttendance.getSno());
		// aq.id(R.id.tv_class).text(wAttendance.getsClass());

		aq.id(R.id.tv_user_name).text(wAttendance.getUserName());

		if (user.getUserType().equals("老师")) {
			aq.id(R.id.tv_user_type).text("用户类型:老师");
			aq.id(R.id.tv_class_or_department).text("部门:" + wAttendance.getsClass());
		}else{
			aq.id(R.id.tv_user_type).text(wAttendance.getSno());
			aq.id(R.id.tv_class_or_department).text(wAttendance.getsClass());
		}
		final String imgUrl = wAttendance.getUserPic();
		if (imgUrl != null) {
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=200;
			options.round = 100;
			
			aq.id(R.id.iv_user_pic).image(imgUrl, options);
			aq.id(R.id.iv_user_pic).clicked(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogUtility.showImageDialog(getActivity(),imgUrl);
					//showImageDialog(imgUrl);
				}
			});
		}
		// 考勤
		workAttendances = wAttendance.getWorkAttendances();
		adapter.notifyDataSetChanged();
		Log.d(TAG, workAttendances.get(0).getIcon());
		// 快捷查询
		List<SelectShortCut> SelectShortCuts = wAttendance.getSelectShortCuts();
		aq.id(R.id.rbtn_left).text(SelectShortCuts.get(0).getName());
		aq.id(R.id.rbtn_right).text(SelectShortCuts.get(1).getName());
		// 按周查询
		final List<SelectShortCut> selectShortCuts = wAttendance.getSelectShortCuts();
		
		String n=wAttendance.getSelectByWeeks().get(0).getDefaultValue();
		String m=wAttendance.getSelectByWeeks().get(1).getDefaultValue();
		startWeek = Integer.parseInt(n);
		endWeek = Integer.parseInt(m);
		String str = "第" + startWeek + "周 - 第" + endWeek + "周";
		aq.id(R.id.tv_by_week_value).text(str);
		aq.id(R.id.rbtn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "---selectShortCuts.get(0).getContentUrl():"
						+ selectShortCuts.get(0).getContentUrl());
				getWeeks(interfaceName + selectShortCuts.get(0).getContentUrl());
				
			}
		});
		aq.id(R.id.rbtn_right).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "---selectShortCuts.get(1).getContentUrl():"
						+ selectShortCuts.get(1).getContentUrl());
				getWeeks(interfaceName + selectShortCuts.get(1).getContentUrl());
				
			}
		});
		// 按周查询
		aq.id(R.id.lv_by_week_value).clicked(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				settingTimeView = LayoutInflater.from(getActivity()).inflate(
						R.layout.select_time, null);
				nPicker1 = (NumberPicker) settingTimeView
						.findViewById(R.id.numberPicker1);
				nPicker2 = (NumberPicker) settingTimeView
						.findViewById(R.id.numberPicker2);
				tvOk = (TextView) settingTimeView.findViewById(R.id.tv_ok);
				tvClose = (TextView) settingTimeView
						.findViewById(R.id.tv_close);
				tvOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int n = nPicker1.getValue();
						int m = nPicker2.getValue();
						Log.d(TAG, "第" + n + "周 - 第" + m + "周");
						if (m < n) {
							AppUtility.showToastMsg(getActivity(),
									"起始周不能大于结束周!");
						} else {
							String str = "?Week1=" + n + "&Week2=" + m;
							aq.id(R.id.tv_by_week_value).text(
									"第" + n + "周 - 第" + m + "周");
							getWeeks(interfaceName + str);
							
							popupWindow.dismiss();
						}
					}
				});
				tvClose.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});
				List<SelectByWeek> selectByWeeks = wAttendance.getSelectByWeeks();
				int max = Integer.parseInt(selectByWeeks.get(1).getValue());
				int min = Integer.parseInt(selectByWeeks.get(0).getValue());
				nPicker1.setMaxValue(max);
				nPicker1.setMinValue(min);
				nPicker2.setMaxValue(max);
				nPicker2.setMinValue(min);
				nPicker1.setValue(startWeek);
				nPicker2.setValue(endWeek);
				popupWindow = new PopupWindow(settingTimeView,
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
				popupWindow.setAnimationStyle(R.style.popupAnimation);
				// 点击外部消失
				popupWindow.setOutsideTouchable(true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.showAtLocation(
						view.findViewById(R.id.school_work_attendance),
						Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
			}
		});

	}

	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		myGridview.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}

	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			myGridview.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			myGridview.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 功能描述:更具接口获取数据
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
			jo.put("当前学期", PrefUtility.get(Constants.PREF_CUR_XUEQI, ""));
			jo.put("当前周", PrefUtility.getInt(Constants.PREF_CURRENT_WEEK, 1));
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

	/**
	 * 功能描述:获取周的数据
	 * 
	 * @author shengguo 2014-4-19 下午1:47:20
	 * 
	 * @param interfacePath
	 */
	public void getWeeks(String interfacePath) {
		// showProgress(true);
		AppUtility.showToastMsg(getActivity(), "正在获取数据");
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
		CampusAPI.getSchoolItem(params, interfacePath, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Log.d(TAG, "----response" + e.getMessage());
				AppUtility.cancelToast();
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Log.d(TAG, "----response" + response);
				AppUtility.cancelToast();
				Message msg = new Message();
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(),
					DatabaseHelper.class);
		}
		return database;
	}

	/**
	 * 功能描述:显示头像大图
	 * 
	 * @author shengguo 2014-5-9 下午3:04:49
	 * 
	 * @param imagePath
	 */
	private void showImageDialog(String imagePath) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.view_image, null);
		AQuery aq = new AQuery(view);
		final Dialog dialog = DialogUtility.createLoadingDialog(getActivity(),
				"show_image_dialog");
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
		aq.id(R.id.iv_img).image(imagePath).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	class WorkAttendanceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return workAttendances.size();
		}

		@Override
		public Object getItem(int position) {
			return workAttendances.get(position);
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
						R.layout.school_work_attendance_item, parent, false);
				holder = new ViewHolder();
				holder.background = (ImageView) convertView
						.findViewById(R.id.iv_background);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.value = (TextView) convertView
						.findViewById(R.id.tv_count);
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final WorkAttendance workAttendance = (WorkAttendance) getItem(position);
			AQuery aq = new AQuery(convertView);
			aq.id(holder.background).image(workAttendance.getBackground());
			aq.id(holder.icon).image(workAttendance.getIcon());
			holder.value.setText(workAttendance.getValue());
			holder.name.setText(workAttendance.getName());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG,
							"----contentUrl" + workAttendance.getContentUrl());
					Intent intent = new Intent(getActivity(),SchoolDetailActivity.class);
					intent.putExtra("templateName",wAttendance.getTemplateName());
					intent.putExtra("interfaceName", interfaceName+ workAttendance.getContentUrl());
					intent.putExtra("title", title);
					getActivity().startActivity(intent);
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView background;
			ImageView icon;
			TextView value;
			TextView name;
		}
	}
}

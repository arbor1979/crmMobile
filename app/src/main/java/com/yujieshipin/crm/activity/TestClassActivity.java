package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.adapter.TestAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.entity.TestEntityItem;
import com.yujieshipin.crm.entity.TestStartEntity;
import com.yujieshipin.crm.entity.TestStatus;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;

public class TestClassActivity extends Activity {
	private ListView mList;
	private TestAdapter mTestAdapter;
	private Dao<TestStartEntity, Integer> startTestDao;
	private Dao<TeacherInfo, Integer> teacherInfoDao;
	private DatabaseHelper database;
	private List<TestEntityItem> testList;
	private List<TestStartEntity> startTestList;
	private TeacherInfo teacherInfo;
	private Dialog dialog, mLoadingDialog;
	private String classname = null, subjectid = null;
	private CountDownTimer mCountDownTimer;
	private TextView filedTitle, tvRight,none_exam;
	private AQuery aq;
	private Button btnLeft;
	private long startTime, endTime/*, testTime*/;
	public static int mTestStatus;// 记录老师修改的测验状态
	public static final int TESTSSTART = 0, TESTEND = 1;
	private String TAG = "TestClassActivity";
	private TestStatus testStatus;
	private int myRecord;//得分
	private int avgScores;//平均分
	
	LinearLayout loadingLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_classroom_test);
		classname = ClassDetailActivity.classname;
		subjectid = ClassDetailActivity.subjectid;
		Log.d(TAG, "classname:" + classname + ",subjectid:" + subjectid);

		mList = (ListView) findViewById(R.id.list);
		filedTitle = (TextView) findViewById(R.id.tv_fied_title);
		aq = new AQuery(this);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvRight = (TextView) findViewById(R.id.tv_right);
		tvRight.setVisibility(View.VISIBLE);
		if (ClassDetailActivity.userType.equals("老师")) {
			aq.id(R.id.tv_title).text(ClassDetailActivity.classname);
			
		} else {
			
			aq.id(R.id.bottom_tab_roll_call).visibility(View.GONE);
			aq.id(R.id.tv_title).text(
					ClassDetailActivity.teacherInfo.getCourseName());
		}
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		none_exam=(TextView)findViewById(R.id.none_exam);
		if (ClassDetailActivity.userType.equals("家长"))
			tvRight.setVisibility(View.INVISIBLE);
		//aq.id(R.id.tv_right).textColor(R.color.gray);
		// aq.id(R.id.tv_title).textSize(getResources().getDimensionPixelSize(R.dimen.text_size_micro));
		initListener();
		dialog = new Dialog(this, R.style.dialog);
		Log.d(TAG, "----------id-----------" + subjectid);
		try {
			startTestDao = getHelper().getStartTestDao();
			teacherInfoDao = getHelper().getTeacherInfoDao();
			teacherInfo = teacherInfoDao.queryBuilder().where()
					.eq("id", subjectid).queryForFirst();
			startTestList = startTestDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		testList = new ArrayList<TestEntityItem>();
		mTestAdapter = new TestAdapter(this, testList,ClassDetailActivity.userType);
		mList.setAdapter(mTestAdapter);
		mTestAdapter.notifyDataSetChanged();
		showProgress(true);
		getCeyanInfo();
	}
	private void showProgress(boolean progress) {
		if (progress) {
			mList.setVisibility(View.GONE);
			loadingLayout.setVisibility(View.VISIBLE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			mList.setVisibility(View.VISIBLE);
		}
	}
	private void initListener() {
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 通知ClassDetailActivity finish();
				Intent intent = new Intent("finish_classdetailactivity");
				sendBroadcast(intent);
				finish();
			}
		});
		aq.id(R.id.layout_btn_right).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mStatus = testStatus.getTestStatus();
				Log.d(TAG, ClassDetailActivity.userType + "-------");
				Log.d(TAG, "-----------mStatus:" + mStatus);
				if (ClassDetailActivity.userType.equals("老师")) {
					Log.d(TAG, "------------------老师---------------------");
					if (mStatus.equals("未开始") && testList != null
							&& testList.size() > 0) {
						startTest();
					} else if (mStatus.equals("执行中") && testList != null
							&& testList.size() > 0) {
						showEndTest();
					} else if (mStatus.equals("已结束") && testList != null
							&& testList.size() > 0) {
						startTest();
					}
				} else {
					Log.d(TAG, "------------------学生,家长---------------------");
					if(tvRight.getText().equals("保存"))
					{
						if (mStatus.equals("执行中") && testList != null
								&& testList.size() > 0) 
						{
							if (mTestAdapter.getIsConsummation()) {
								saveTestResult();
							} else {
								AppUtility.showToastMsg(TestClassActivity.this,
										"请答完题目在提交答案");
							}
						}
					}
					else
					{
						AppUtility.showToastMsg(TestClassActivity.this,
								"获取测验状态...");
						getCeyanStatus();
					}
					
				}
			}
		});
	}

	/**
	 * 功能描述:获取测验状态
	 * 
	 * @author shengguo 2014-4-28 上午11:08:29
	 * 
	 */
	private void getCeyanStatus() {
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("ACTION", "GetInfo");
			jo.put("班级", ClassDetailActivity.teacherInfo.getClassGrade());
			jo.put("老师上课记录编号", ClassDetailActivity.teacherInfo.getId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getCeyanStatus(params, new RequestListener() {

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
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:获取测验数据
	 * 
	 * @author shengguo 2014-4-28 上午11:08:29
	 * 
	 */
	private void getCeyanInfo() {
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("ACTION", "GetInfo");
			jo.put("班级", ClassDetailActivity.teacherInfo.getClassGrade());
			jo.put("老师上课记录编号", ClassDetailActivity.teacherInfo.getId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getCeyanInfo(params, new RequestListener() {

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
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}

	/**
	 * 功能描述:保存测验结果
	 * 
	 * @author shengguo 2014-4-29 下午4:38:19
	 * 
	 */
	private void saveTestResult() {
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		JSONObject jo = new JSONObject();
		try {
			jo.put("选项记录集", mTestAdapter.getAnswer());
			jo.put("ACTION", "UploadAnswer");
			jo.put("老师上课记录编号", ClassDetailActivity.teacherInfo.getId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getCeyanInfo(params, new RequestListener() {

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
				msg.what = 3;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:显示计时
	 * 
	 * @author zhuliang 2013-11-22 下午2:55:20
	 * 
	 * @param many
	 */
	private void showTime(long totalTimeCountInMilliseconds) {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (totalTimeCountInMilliseconds < 3600000) {
			mCountDownTimer = new CountDownTimer(totalTimeCountInMilliseconds,
					1000) {
				@Override
				public void onTick(long leftTimeInMillsecondes) {
					long seconds = leftTimeInMillsecondes / 1000;
					filedTitle.setText("离结束时间还有"
							+ String.format("%02d", seconds / 60) + "分"
							+ String.format("%02d", seconds % 60) + "秒");
				}

				@Override
				public void onFinish() {
					filedTitle.setText("测验状态：已结束");
					endCountDown();
				}
			}.start();
		} else {
			mCountDownTimer = new CountDownTimer(totalTimeCountInMilliseconds,
					1000) {
				@Override
				public void onTick(long leftTimeInMillsecondes) {
					long seconds = leftTimeInMillsecondes / 1000;
					filedTitle.setText("离结束时间还有"
							+ String.format("%02d", seconds / 3600) + "时"
							+ String.format("%02d", (seconds / 60) % 60) + "分"
							+ String.format("%02d", seconds % 60) + "秒");
				}

				@Override
				public void onFinish() {
					filedTitle.setText("测验状态：已结束");
					endCountDown();
				}
			}.start();
		}
	}
	/**
	 * 功能描述:倒计时结束
	 *
	 * @author shengguo  2014-6-3 上午11:49:44
	 *
	 */
	private void endCountDown(){
		String mStatus = testStatus.getTestStatus();
		Log.d(TAG, ClassDetailActivity.userType + "-------");
		Log.d(TAG, "-----------mStatus:" + mStatus);
		if (ClassDetailActivity.userType.equals("老师")) {
			Log.d(TAG, "------------------老师---------------------");
			endTest();
		} else {
			Log.d(TAG, "------------------学生,家长---------------------");
			saveTestResult();
		}
	}
	/**
	 * 弹出窗口listview适配器
	 */
	public class DialogAdapter extends BaseAdapter {
		List<TestStartEntity> listData = new ArrayList<TestStartEntity>();

		public DialogAdapter(List<TestStartEntity> list) {
			this.listData = list;
		}

		@Override
		public int getCount() {
			return listData == null ? 0 : listData.size();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.view_testing_pop, null);
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TestStartEntity testStartEntity = listData.get(position);
			Log.d(TAG, "-------test-----------" + testStartEntity.getTimeKey());
			holder.title.setText(testStartEntity.getTimeKey());
			return convertView;
		}

	}

	class ViewHolder {
		TextView title;
		RadioGroup group;
		RadioButton type_option;
	}

	/**
	 * 功能描述:提交测验状态
	 * 
	 * @author zhuliang 2013-12-21 下午1:59:49
	 * 
	 * @param teacherInfoList
	 * @return
	 */
	private String getChangeceyanStatus(TeacherInfo teacherInfo) {
		JSONArray jsonArray = new JSONArray();
		if (teacherInfo != null) {
			JSONObject jo = new JSONObject();
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			try {
				jo.put("用户较验码", checkCode);
				jo.put("教师上课记录编号", teacherInfo.getId());
				jo.put("课堂测验状态", teacherInfo.getTestStatus());
				jsonArray.put(jo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonArray.toString();
		}

		return null;
	}

	/**
	 * 功能描述:提交服务器
	 * 
	 * @author yanzy 2013-12-4 上午10:10:42
	 * 
	 * @param base64Str
	 * 
	 *            修改：传递测验状态判断
	 */
	public void SubmitChangeinfo(String base64Str, final String action) {
		CampusParameters params = new CampusParameters();
		params.add("action", action);
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.Changeinfo(params, new RequestListener() {

			@Override
			public void onIOException(IOException e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Bundle bundle = new Bundle();
				bundle.putString("action", action);
				bundle.putString("result", response.toString());
				Message msg = new Message();
				msg.what = 0;
				msg.obj = bundle;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 功能描述:点击开始测验
	 * 
	 * @author zhuliang 2013-12-21 下午2:56:56
	 * 
	 */
	private void startTest() {
		View view = getLayoutInflater()
				.inflate(R.layout.view_exam_dialog, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(startTestList);
		mList.setAdapter(dialogAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (AppUtility.getNetworkIsAvailable(TestClassActivity.this)) {
					showDialog();
					int id = startTestList.get(position).getTimeValues();
					//testTime = id * 60 * 1000;
					startTime = System.currentTimeMillis();
					Log.d(TAG, "------startTime--------" + startTime);

					try {
						teacherInfo.setTestStatus("开始答题:" + id);
						teacherInfo.setIsModify(1);
						teacherInfoDao.update(teacherInfo);
					} catch (SQLException e) {
						e.printStackTrace();
					}

					String ceyanJsonStr = getChangeceyanStatus(teacherInfo);
					Log.d(TAG, "ceyanJsonStr:" + ceyanJsonStr);
					if (!"".equals(ceyanJsonStr) && ceyanJsonStr != null) {
						String ceyanBase64 = Base64.encode(ceyanJsonStr
								.getBytes());
						mTestStatus = TESTSSTART;
						SubmitChangeinfo(ceyanBase64, "changeceyanzhuangtai");
					}
				} else {
					AppUtility.showToastMsg(TestClassActivity.this, "请检查网络连接!");
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 功能描述:点击结束测验
	 * 
	 * @author zhuliang 2013-12-21 下午2:58:15
	 * 
	 */
	private void endTest() {
		showDialog();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		endTime = System.currentTimeMillis();
		Log.i(TAG, String.valueOf(endTime));
		try {
			teacherInfo.setTestStatus("结束答题");
			teacherInfo.setIsModify(1);
			teacherInfoDao.update(teacherInfo);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String ceyanJsonStr = getChangeceyanStatus(teacherInfo);
		Log.d(TAG, "ceyanJsonStr:" + ceyanJsonStr);
		if (!"".equals(ceyanJsonStr) && ceyanJsonStr != null) {
			String ceyanBase64 = Base64.encode(ceyanJsonStr.getBytes());
			mTestStatus = TESTEND;
			SubmitChangeinfo(ceyanBase64, "changeceyanzhuangtai");
		}
	}

	/**
	 * 功能描述:手动结束提示
	 *
	 * @author shengguo  2014-5-28 上午11:12:23
	 *
	 */
	private void showEndTest() {
		AlertDialog ad = new AlertDialog.Builder(TestClassActivity.this)
				.setTitle("测验结束时间未到,是否手动结束?")
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								endTest();
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						}).create();
		//dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		ad.show();
	}

	/**
	 * 功能描述:显示提示框
	 * 
	 * @author yanzy 2013-12-21 上午10:54:41
	 * 
	 */
	public void showDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(
				TestClassActivity.this, "数据保存中...");
		mLoadingDialog.show();
	}

	/**
	 * 功能描述:操作失败，提示
	 * 
	 * @author yanzy 2013-12-21 上午10:37:17
	 * 
	 */
	public void closeDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}
		filedTitle.setText("");
		DialogUtility.showMsg(TestClassActivity.this, "保存失败！");
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(TestClassActivity.this,
					DatabaseHelper.class);
		}
		return database;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle localBundle = new Bundle();
			String resultStr = "";
			String result = "";
			switch (msg.what) {
			case -1:
				showProgress(false);
				if (mLoadingDialog != null)
					mLoadingDialog.dismiss();
				if (dialog != null)
					dialog.dismiss();
				AppUtility.showErrorToast(TestClassActivity.this,
						msg.obj.toString());
				break;
			case 0:// 教师改变测试状态
				localBundle = (Bundle) msg.obj;
				String action = localBundle.getString("action");
				result = localBundle.getString("result");
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				Log.d(TAG, "action:" + action);
				Log.d(TAG, "resultStr:" + resultStr);

				try {
					JSONObject jo = new JSONObject(resultStr);
					if ("1".equals(jo.optString("成功"))) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						JSONObject ret189=jo.optJSONObject("189结果");
						if(ret189!=null && ret189.optString("答题状态").equals("已结束"))
							getCeyanInfo();
						else
							getCeyanStatus();
						
					} else {
						closeDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
					closeDialog();
				}
				break;
			case 1:// 获取测验状态
				result = msg.obj.toString();
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
					Log.d(TAG, "resultStr" + resultStr);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					JSONObject jo = new JSONObject(resultStr);
					parseTestStatus(jo);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case 2:// 获取测验详情
				showProgress(false);
				result = msg.obj.toString();
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
					Log.d(TAG, "resultStr" + resultStr);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				try {
					JSONObject jo = new JSONObject(resultStr);
					JSONArray joarr = new JSONArray(jo.optString("测验数据"));
					testList = TestEntityItem.toList(joarr);
					myRecord=jo.optInt("得分");
					avgScores=jo.optInt("平均分");
					mTestAdapter.setList(testList);
					
					if (testList.size() > 0) {
						aq.id(R.id.tv_title)
								.text(testList.get(0).getTestName());
					}
					if(testList.size()==0)
					{
						none_exam.setVisibility(View.VISIBLE);
						mList.setVisibility(View.GONE);
					}
					else
					{
						none_exam.setVisibility(View.GONE);
						mList.setVisibility(View.VISIBLE);
					}
					parseTestStatus(jo.optJSONObject("状态数据"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 3:// 学生提交答案
				result = msg.obj.toString();
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
					Log.d(TAG, "resultStr" + resultStr);
					try {
						JSONObject jo = new JSONObject(resultStr);
						String mResult = jo.optString("结果");
						if (AppUtility.isNotEmpty(result)) {
							AppUtility.showToastMsg(TestClassActivity.this,
									mResult);
						}
						JSONArray joarr = new JSONArray(jo.optString("测验数据"));
						List<TestEntityItem> testList = TestEntityItem
								.toList(joarr);
						mTestAdapter.setList(testList);
						mTestAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	};
	private void parseTestStatus(JSONObject jo)
	{
		testStatus = new TestStatus(jo);
		String str = testStatus.getTestStatus();
		filedTitle.setText("测试状态:" + str);
		mTestAdapter.setTestStatus(str);
		if (str.equals("执行中")) {
			showTime(testStatus.getRemainingTime() * 1000);
			
			if (ClassDetailActivity.userType.equals("老师")) {
			{
				tvRight.setText("手动结束");
				mTestAdapter.setIsEnable(false);
			}
			}else if(ClassDetailActivity.userType.equals("学生"))
			{
				mTestAdapter.setIsEnable(true);
				tvRight.setText("保存");
			}
			else
			{
				tvRight.setText("刷新");
				mTestAdapter.setIsEnable(false);
			}
		} else {
			mTestAdapter.setIsEnable(false);
			if (ClassDetailActivity.userType.equals("老师")) {
				tvRight.setText("开始");
			}else{
				tvRight.setText("刷新");
				
			}
			if(str.equals("已结束"))
			{
				if(ClassDetailActivity.userType.equals("老师"))
					filedTitle.setText("测试状态:" + str+" 平均分:"+avgScores);
				else
					filedTitle.setText("测试状态:" + str+" 得分:"+myRecord);
					
			}
		}
		mTestAdapter.notifyDataSetChanged();
	}
}

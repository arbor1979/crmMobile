package com.yujieshipin.crm.fragment;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.ClassDetailActivity;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Schedule;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.lib.BaseTableAdapter;
import com.yujieshipin.crm.lib.TableFixHeaders;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.PrefUtility;

public class SubjectFragment extends Fragment {
	public Object[][] table = new Object[13][7];
	WindowManager manager;
	Display display;
	DatabaseHelper database;
	private Dao<TeacherInfo, Integer> teacherInfoDao;
	Dao<Schedule, Integer> scheduleDao;
	List<TeacherInfo> teacherInfoList = new ArrayList<TeacherInfo>();
	Schedule scheduleInfo;
	BaseTableAdapter baseTableAdapter;
	public TableFixHeaders tableFixHeaders;
	String[] colors = { "#eda573", "#55b4ba", "#dee188", "#a7af38", "#9FA4C1",
			"#ee7e8a", "#f7cd83","#67b7d5","#f979b6","#9175f0","#FF3DA1","#ff9875","#968cff","#CE8AFF","#FFDC8A",
			"#bcddf3","#d6abd8","#8694cd","#8ecee7","#BAC0DE" };
	String color;
	private static final String TAG = "SubjectFragment";
	private Map<String, Button> showButtons = new HashMap<String, Button>();
	private String[] weeks, sections,weekdays;

	private JSONObject weekJson;
	private String userType; 
	private ClassSelectDialogFragment dialogFragment;
	public SubjectFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "---------onCreate is running-------------");
		try {
			teacherInfoDao = getHelper().getTeacherInfoDao();
			scheduleDao = getHelper().getScheduleDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		userType=((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj().getUserType();
		weekJson = new JSONObject();
		try {
			
				weekJson.put("0", "星期日");
				weekJson.put("1", "星期一");
				weekJson.put("2", "星期二");
				weekJson.put("3", "星期三");
				weekJson.put("4", "星期四");
				weekJson.put("5", "星期五");
				weekJson.put("6", "星期六");
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		dialogFragment = ClassSelectDialogFragment.newInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "---------onCreateView is running-------------");
		View localView = inflater.inflate(R.layout.table, container, false);
		manager = getActivity().getWindowManager();
		display = manager.getDefaultDisplay();
		tableFixHeaders = (TableFixHeaders) localView.findViewById(R.id.table);
		baseTableAdapter = new SubjectAdapter(getActivity()
				.getApplicationContext());
		tableFixHeaders.setAdapter(baseTableAdapter);
		
		return localView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getSchedulInfo();
		initTable();
	}

	public void initTable() {
		tableFixHeaders.removeAllViews();
		table = new Object[13][7];
		Log.d(TAG, "----------------refresh-----------:running");
		try {
			getSchedulInfo();
			teacherInfoList = teacherInfoDao.queryForAll();
			Log.d(TAG, "---------------teacherInfoList----------"
					+ teacherInfoList.size());
			for (TeacherInfo tt : teacherInfoList) {
				if (tt != null) {
					localTable(tt);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		baseTableAdapter.notifyDataSetChanged();
	}

	/**
	 * 功能描述:获取课表规则
	 * 
	 * @author shengguo 2014-5-16 上午11:55:22
	 * 
	 */
	private void getSchedulInfo() {
		try {
			scheduleInfo = scheduleDao
					.queryForFirst((PreparedQuery<Schedule>) scheduleDao
							.queryBuilder().prepare());
			Log.d(TAG, "---------------scheduleInfo-----------" + scheduleInfo);
			if (scheduleInfo != null) {
				String jaStrSections = scheduleInfo.getSections();
				String jaStrSectionTimes=scheduleInfo.getSectionsTime();
				String jaStrWeeks = scheduleInfo.getWeeks();
				JSONArray jaSections = new JSONArray(jaStrSections);
				JSONArray jaWeeks = new JSONArray(jaStrWeeks);
				JSONArray jaSectionTimes=new JSONArray(jaStrSectionTimes);
				weeks = new String[jaWeeks.length()];
				weekdays = new String[jaWeeks.length()];
				sections = new String[jaSections.length()];
				
				for (int i = 0; i < jaSections.length(); i++) {
					if(i<jaSectionTimes.length())
					{
						String starttime=jaSectionTimes.getJSONObject(i).optString("时间");
						starttime=starttime.split("-")[0];
						if(starttime!=null && starttime.length()>0)
							sections[i]=starttime+"\n";
					}
					sections[i] += jaSections.optString(i);
				}
				
				for (int i = 0; i < jaWeeks.length(); i++) {
					if(PrefUtility.getInt("weekFirstDay", 0)==0)
						weeks[i] = weekJson.getString(String.valueOf(i));
					else
					{
						if(i+1==weekJson.length())
							weeks[i] = weekJson.getString("0");
						else
							weeks[i] = weekJson.getString(String.valueOf(i+1));
					}
				}
				if(scheduleInfo.getWeekBeginDay()!=null && scheduleInfo.getWeekBeginDay().length()>0)
				{
					for (int i = 0; i < jaWeeks.length(); i++) {
						if(i==0)
							weekdays[i]=scheduleInfo.getWeekBeginDay();
						else
							weekdays[i]=DateHelper.getOffsetDay(weekdays[i-1], 2, "yyyy-MM-dd");
						
					}
				}
				
				
			} else {
				weeks = new String[] { "星期日","星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
				sections = new String[] {"1", "2", "3", "4", "5", "6",
						"7", "8", "9", "10", "11", "12" };
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class SubjectAdapter extends BaseTableAdapter {
		private final float density;

		public SubjectAdapter(Context context) {
			density = context.getResources().getDisplayMetrics().density;
		}

		@Override
		public int getRowCount() {
			return sections == null ? 0 : sections.length;
		}

		@Override
		public int getColumnCount() {
			return weeks == null ? 0 : weeks.length;
		}

		@Override
		public View getView(int row, int column, View convertView,
				ViewGroup parent) {
			switch (getItemViewType(row, column)) {
			case 0:
				convertView = getFirstHeader(row, column, convertView, parent);
				break;
			case 1:
				convertView = getHeader(row, column, convertView, parent);
				break;
			case 2:
				convertView = getFirst(row, column, convertView, parent);
				break;
			case 3:
				convertView = getTime(row, column, convertView, parent);
				break;
			case 4:
				convertView = getBody(row, column, convertView, parent);
				break;
			case 5:
				convertView = getTimeNull(row, column, convertView, parent);
				break;
			default:
				throw new RuntimeException("wtf?");
			}
			return convertView;
		}

		private View getFirstHeader(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_single));
				tv.setText("");
			}
			return tv;
		}

		private View getHeader(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText(weeks[column]);
				if(weekdays!=null && weekdays[column]!=null && weekdays[column].length()>0)
				{
					tv.setText(tv.getText()+"\n"+weekdays[column].substring(5));
				}
				tv.setTextSize(13);
				//tv.setPadding(5, 5, 5, 5);
				//tv.setTextColor(Color.parseColor("#fadf8f"));
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
				if (column % 2 == 0) {
					tv.setBackgroundColor(getResources().getColor(
							R.color.subject_double));
				} else {
					tv.setBackgroundColor(getResources().getColor(
							R.color.subject_single));
				}
				if(weekdays[column].equals(DateHelper.getToday()))
				{
					tv.setBackgroundColor(Color.parseColor("#075c1b"));
				}
			}
			return tv;
		}

		private View getFirst(int row, int column, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_subject_sections, parent, false);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.tv_section);
			SpannableString builder = new SpannableString(sections[row]);
            builder.setSpan(new AbsoluteSizeSpan(10,true), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(builder);
			
			/*
			if (AppUtility.isNumeric(sections[row])) {
				
			} else {
				tv.setText(AppUtility.getVerticalText(sections[row]));
			}
			*/
			if (row % 2 == 0) {
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_double));
			} else {
				tv.setBackgroundColor(getResources().getColor(
						R.color.subject_single));
			}
			return convertView;
		}

		private View getTime(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText(sections[row]);
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER);
			}
			return tv;
		}

		private View getTimeNull(int row, int column, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setText("");
			}
			return tv;
		}

		@SuppressLint("NewApi")
		private View getBody(int row, int column, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_subject_body, parent, false);
			}
			final Button bn = (Button) convertView.findViewById(R.id.button);
			
			if (row < table.length && column < table[row].length) {
				TeacherInfo teacherInfo = (TeacherInfo) table[row][column];
				if (teacherInfo != null) {
					int color =0;
					bn.setTag(teacherInfo);
					String teacherInfoStr = teacherInfo.getCourseName() + "("
							+ teacherInfo.getClassroom() + ")";
					if (userType.equals("老师"))
					{
						teacherInfoStr+=teacherInfo.getClassGrade();
						color = Color.parseColor(getColor(teacherInfo.getClassGrade()));
					}
					else
					{
						teacherInfoStr+=teacherInfo.getName();
						color = Color.parseColor(getColor(teacherInfo.getName()));
					}
					bn.setText(teacherInfoStr);
					TeacherInfo lastInfo = null;
					TeacherInfo nextInfo = null;
					if (row - 1 >= 0 && row - 1 <= table.length) {
						lastInfo = (TeacherInfo) table[row - 1][column];
					}
					if (row + 1 > 0 && row + 1 <= table.length) {
						nextInfo = (TeacherInfo) table[row + 1][column];
					}
					if (nextInfo == null && lastInfo == null) {
						teacherInfoStr = teacherInfo.getCourseName() + "("
								+ teacherInfo.getClassroom() + ")"
								+ teacherInfo.getClassGrade();
						bn.setText(teacherInfoStr);
					} else {
						if (nextInfo != null) {
							if (teacherInfo.getSection().equals(
									nextInfo.getSection())) {
								showButtons.put(teacherInfo.getId() + "up", bn);
								
								teacherInfoStr = teacherInfo.getCourseName() + "("
										+ teacherInfo.getClassroom() + ")";
								bn.setText(teacherInfoStr);
								bn.setGravity(Gravity.BOTTOM);
								MarginLayoutParams  layoutParams=(ViewGroup.MarginLayoutParams)bn.getLayoutParams();
								layoutParams.bottomMargin=0;
								
							}
						}

						if (lastInfo != null) {
							if (teacherInfo.getSection().equals(
									lastInfo.getSection())) {
								showButtons.put(teacherInfo.getId() + "down",
										bn);
								
								if (userType.equals("老师"))
									bn.setText(teacherInfo.getClassGrade());
								else
									bn.setText(teacherInfo.getName());
								bn.setGravity(Gravity.TOP);
							}
						}
					}
					
					bn.setBackgroundColor(color);
					
					//bn.setTextSize(TypedValue.COMPLEX_UNIT_PX,display.getWidth()/320*16);
					//bn.getBackground().setAlpha(180);
					bn.setId(color);
					bn.setVisibility(View.VISIBLE);
				}
				else
					bn.setVisibility(View.GONE);

			} else {
				bn.setText("");
			}
			if (!bn.getText().toString().equals("")) {
				bn.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						TeacherInfo ti = (TeacherInfo) v.getTag();
						Button btnDown = showButtons.get(ti.getId() + "down");
						Button btnUp = showButtons.get(ti.getId() + "up");
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							Log.d(TAG, "--------->开始点击");
							if(btnDown!=null && btnUp!=null)
							{
								btnDown.setBackgroundColor(Color
									.parseColor("#eeeeee"));
								btnUp.setBackgroundColor(Color
									.parseColor("#eeeeee"));
							}
							else
								v.setBackgroundColor(Color
									.parseColor("#eeeeee"));
						}
						if (event.getAction() == MotionEvent.ACTION_UP
								|| event.getAction() != MotionEvent.ACTION_DOWN) {
							Log.d(TAG, "--------->结束点击");
							if(btnDown!=null && btnUp!=null)
							{
								btnDown.setBackgroundColor(v.getId());
								btnUp.setBackgroundColor(v.getId());
							//btnDown.getBackground().setAlpha(180);
							//btnUp.getBackground().setAlpha(180);
							}
							else
								v.setBackgroundColor(v.getId());
						}
						return false;
					}
				});

				bn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						TeacherInfo ti = (TeacherInfo) v.getTag();
						// Intent intent = new
						// Intent(getActivity(),ClassRoomActivity.class);
						String[] idArray=ti.getId().split(",");
						if(idArray.length>1)
						{
							if(!dialogFragment.isAdded()){
								dialogFragment.idStr=ti.getId();
								dialogFragment.show(getActivity().getSupportFragmentManager(), "");
							}
						}
						else
						{
							Intent intent = new Intent(getActivity(),
									ClassDetailActivity.class);
							intent.putExtra("teacherInfo", (Serializable) ti);
							startActivity(intent);
						}
					}
				});
			}
			return convertView;
		}

		@SuppressWarnings("deprecation")
		@Override
		public int getWidth(int column) {
			if (column == -1) {
				return Math.round(30 * density);
			} else {
				return Math.round(display.getWidth() / 6
						+ (display.getWidth() / 6 - 30 * density) / 6);
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public int getHeight(int row) {
			if (row == -1 /* || row == 3 || row == 6 */) {
				return Math.round(35 * density);
			} else {
				return Math.round(display.getHeight() / 11);
			}
		}

		@Override
		public int getItemViewType(int row, int column) {
			int itemViewType;
			if (row == -1 && column == -1) {
				itemViewType = 0;
			} else if (row == -1) {
				itemViewType = 1;
			} else if (column == -1) {
				itemViewType = 2;
			} else {
				itemViewType = 4;
			}
			return itemViewType;
		}

		@Override
		public int getViewTypeCount() {
			return 6;
		}

	}

	public String getColor(String seed) {
		int m=0;
		for(int i=0;i<seed.length();i++)
		{
			byte bytes=(byte)seed.charAt(i);
			int result = bytes&0xff;  
			m+=result;
		}
		int i = m % colors.length;
		color = colors[i];
		return color;
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(),
					DatabaseHelper.class);
		}
		return database;
	}

	public void localTable(TeacherInfo li) {

		for (int i = 0; i < sections.length; i++) {
			String[] temparray=sections[i].split("\n");
			String leftText = temparray[temparray.length-1];
			String sectionStr = li.getSection();
			String[] sectionArray = sectionStr.split("-");
			for (int j = 0; j < sectionArray.length; j++) {
				if (leftText.equals(sectionArray[j])) {
					int col=0;
					if(PrefUtility.getInt("weekFirstDay", 0)==0)
					{
						if(li.getWeek()==7)
							li.setWeek(0);
						col=li.getWeek();
					}
					else
						col=li.getWeek()-1;
					if(table[i][col]==null)
						table[i][col] = li;
					else
					{
						TeacherInfo oldli=(TeacherInfo)table[i][col];
						if(oldli.getCourseName().equals(li.getCourseName()))
						{
							String[] idarray=oldli.getId().split(",");
							boolean flag=false;
							for(String id : idarray)
							{
								if(id.equals(li.getId()))
								{
									flag=true;
									break;
								}
							}
							if(flag)
								continue;
							oldli.setId(oldli.getId()+","+li.getId());
							if(userType.equals("老师"))
								oldli.setClassGrade(oldli.getClassGrade()+","+li.getClassGrade());
							else
								oldli.setName(oldli.getName()+","+li.getName());
						}
					}
				}
			}
		}
	}
}

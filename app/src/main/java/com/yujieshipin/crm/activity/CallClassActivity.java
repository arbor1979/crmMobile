package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Student;
import com.yujieshipin.crm.entity.TeacherInfo;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.CharacterParser;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.SegmentedGroup;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 点名界面
 * 
 * <br/>
 * 创建说明: 2013-12-5 下午2:36:14 zhuliang 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
@SuppressLint({ "ValidFragment", "DefaultLocale" })
public class CallClassActivity extends Activity {
	private ListView sortListView;
	//private SideBar sideBar;
	private SortAdapter adapter;
	private CharacterParser characterParser;
	
	//private PinyinComparator pinyinComparator;
	public static String curClassName;
	
	private Button btnLeft;
	private String classname = null;
	private String subjectid = null; // 课程编号
	DatabaseHelper database;

	private Dao<TeacherInfo, Integer> teacherInfoDao;
	TeacherInfo teacherInfo;
	int totalCount;
	public static TextView tv_show;
	private Dialog mLoadingDialog;
	private static final String TAG = "CallClassFragment";
	//private String[] array;
	private SegmentedGroup segmentedGroup2;
	private String jieci;
	AQuery aq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_classroom_call);

		Log.d(TAG, "--------------onCreateView is running----------");
		aq = new AQuery(this);
		tv_show = (TextView) findViewById(R.id.tv);
		tv_show.setVisibility(View.GONE);
		classname = ClassDetailActivity.classname;
		subjectid = ClassDetailActivity.subjectid;
		Log.d(TAG, "classname:"+classname+",subjectid:"+subjectid);
		btnLeft = (Button) findViewById(R.id.btn_left);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		aq.id(R.id.tv_title).text(classname);
		//aq.id(R.id.tv_title).textSize(getResources().getDimensionPixelSize(R.dimen.text_size_micro));
		aq.id(R.id.tv_right).text("保存");
		aq.id(R.id.tv_right).visibility(View.VISIBLE);
		segmentedGroup2=(SegmentedGroup)findViewById(R.id.segmentedGroup2);
		initListener();
		
		try {
			teacherInfoDao = getHelper().getTeacherInfoDao();
			teacherInfo = teacherInfoDao
					.queryForId(Integer.parseInt(subjectid));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String[] sectStr=teacherInfo.getSection().split("-");
		if(sectStr.length>1)
		{
			segmentedGroup2.setVisibility(View.VISIBLE);
			segmentedGroup2.removeAllViews();
			for(int i=0;i<sectStr.length;i++)
			{
				RadioButton rdbtn = (RadioButton) LayoutInflater.from(this).inflate(R.layout.tabmenu_radiobutton, null);  
				rdbtn.setText("第"+sectStr[i]+"节");
				if(i==0)
				{
					rdbtn.setChecked(true);
					jieci=sectStr[i];
				}
				rdbtn.setId(i);
				segmentedGroup2.addView(rdbtn);
			}
			segmentedGroup2.updateBackground();
			segmentedGroup2.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					jieci=sectStr[checkedId];
					adapter.notifyDataSetChanged();
				}
				
			});
		}
		else
			jieci=teacherInfo.getSection();
		Thread thread=new Thread(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
            	refreshData();
            }  
        });  
        thread.start();  
        reloadStudentKaoqin();
	}
	private void refreshData()
	{
		initViews();
		Message message=new Message();  
        message.what=1;  
        mHandler.sendMessage(message); 
	}
	private void reloadStudentKaoqin()
	{
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
			jo.put("subjectid", subjectid);
			jo.put("classname", classname);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		Log.d(TAG, "------->base64Str:" + base64Str);
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getSchoolItem(params, "XUESHENG-KAOQIN-Teacher.php?action=classkaoqin", new RequestListener() {
			

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
				Message msg = new Message();
				msg.what = 2;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		});
	}
	@SuppressLint("CutPasteId")
	private void initViews() {
		Log.d(TAG, "--------------initViews is running----------");
		
		
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		//pinyinComparator = new PinyinComparator();

		//sideBar = (SideBar) findViewById(R.id.sidebar);

		sortListView = (ListView) findViewById(R.id.call_list);
		List<Student> students = ((CampusApplication)getApplicationContext()).getStudentDic().get(classname);
		if (teacherInfo != null && students != null && students.size() > 0) {
			List<Student> studentlist = filledData(students);
			if (studentlist != null && studentlist.size() > 0) {
				JSONArray subjects;
				try {
					String absenceJson = teacherInfo.getAbsenceJson();
					if (AppUtility.isNotEmpty(absenceJson)) {
						subjects = new JSONArray(absenceJson);
						for (Student student : studentlist) 
						{
							
							for (int j = 0; j < subjects.length(); j++) {
								JSONObject jo = subjects.optJSONObject(j);
								if (student.getStudentID().equals(
										jo.optString("学号")) ) {
									JSONObject chuqinJson=student.getChuqinJson();
									
									String kaoqinstr=chuqinJson.get(jo.optString("节次")).toString().replace(jo.optString("考勤类型")+",","");
									kaoqinstr=kaoqinstr.replace(","+jo.optString("考勤类型"),"");
									kaoqinstr=kaoqinstr.replace(jo.optString("考勤类型"),"");
									if(kaoqinstr.equals(""))
										chuqinJson.put(jo.optString("节次"),jo.optString("考勤类型"));
									else
										chuqinJson.put(jo.optString("节次"),kaoqinstr+","+jo.optString("考勤类型"));
								}
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// 根据a-z进行排序源数据
				//Collections.sort(studentlist, pinyinComparator);
				adapter = new SortAdapter(this, studentlist);
				
				// 设置右侧触摸监听
				/*
				List<String> b=new ArrayList<String>();
				for(Student student : studentlist)
				{
					if(!b.contains(student.getStuLetter()))
						b.add(student.getStuLetter());
				}
				array =new String[b.size()];
				b.toArray(array);
				
				sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					@Override
					public void onTouchingLetterChanged(String s) {
						// 该字母首次出现的位置
						tv_show.setVisibility(View.VISIBLE);
						tv_show.setText(s);
						int position = adapter.getPositionForSection(s
								.charAt(0));
						if (position != -1) {
							sortListView.setSelection(position);
						}
					}
				});
				*/
			}
		}

		
	}

	private void initListener() {
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 通知ClassDetailActivity finish();
				Intent intent =new Intent("finish_classdetailactivity");
				sendBroadcast(intent);
				finish();
			}
		});
		//保存数据
		aq.id(R.id.layout_btn_right).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = sortListView.getCount();
				try {
					showDialog();
					Log.d(TAG, "----------------->开始保存数据：" + new Date());
					teacherInfo = teacherInfoDao.queryForId(Integer
							.parseInt(subjectid));
					JSONArray ja = new JSONArray();
					int absenceCnt = 0;
					for (int i = 0; i < count; i++) {
						Student student = (Student) sortListView
								.getItemAtPosition(i);
						if (student.getChuqinJson()!=null) 
						{
							
							Iterator<?> it = student.getChuqinJson().keys();  
							String chuqinStr="出勤";
					        while (it.hasNext()) 
					        {  
					            String key = (String) it.next();  
					            String value = student.getChuqinJson().getString(key); 
					            if(!value.equals("出勤") && value.length()>0)
					            {
					            	JSONObject jo = new JSONObject();
					            	jo.put("学号", student.getStudentID());
					            	jo.put("考勤类型", value);
					            	jo.put("节次", key);
					            	ja.put(jo);
					            }
					            chuqinStr=value;
					        }
							
							if(chuqinStr.endsWith("请假") || chuqinStr.endsWith("事假") 
									|| chuqinStr.endsWith("公假") || chuqinStr.endsWith("病假")
								|| chuqinStr.endsWith("缺勤") || chuqinStr.endsWith("缺课") || chuqinStr.endsWith("旷课"))
								absenceCnt++;
						}
					}
					String json = ja.toString();
					Log.d(TAG, "---------------json:" + json);
					teacherInfo.setClassSize(String.valueOf(count)); // 班级人数
					teacherInfo.setRealNumber(String
							.valueOf(count - absenceCnt)); // 实到人数
					teacherInfo.setAbsenceJson(json);
					teacherInfoDao.update(teacherInfo);

					/**
					 * 获取学生考勤修改数据
					 */
					String kaoqinJsonStr = getChangekaoqininfo(teacherInfo);
					System.out.println("kaoqinJsonStr:" + kaoqinJsonStr);
					// base64加密处理
					if (!"".equals(kaoqinJsonStr) && kaoqinJsonStr != null) {
						String kaoqinBase64 = Base64.encode(kaoqinJsonStr
								.getBytes());
						SubmitChangeinfo(kaoqinBase64, "changekaoqininfo");
					} else {
						closeDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					closeDialog();
				} catch (SQLException e) {
					e.printStackTrace();
					closeDialog();
				}

			}
		});
	}
	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<Student> filledData(List<Student> students) {
		List<Student> mSortList = new ArrayList<Student>();
		final String[] sectStr=teacherInfo.getSection().split("-");
		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			// 汉字转换成拼音

			String pinyin = characterParser.getSelling(student.getName());
			String sortString = pinyin.toUpperCase().substring(0, 1);
			student.setStuLetter(sortString);
			// 正则表达式，判断首字母是否是英文字母
			
			if (sortString.matches("[A-Z]")) {
				student.setStuLetter(sortString.toUpperCase());
			} else {
				student.setStuLetter("#");
			}
			
			JSONObject obj=new JSONObject();
			
			for(int j=0;j<sectStr.length;j++)
			{
				try {
					obj.put(sectStr[j],"");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			student.setChuqinJson(obj);
			mSortList.add(student);
		}
		return mSortList;

	}

	@SuppressLint("DefaultLocale")
	public class SortAdapter extends BaseAdapter implements SectionIndexer {
		public List<Student> list = null;
		 LayoutInflater inflater;
		public SortAdapter(CallClassActivity callClassFragment,
				List<Student> list) {
			this.list = list;
			inflater = LayoutInflater.from(CallClassActivity.this);
		}

		public void updateListView(List<Student> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			final Student mContent = list.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.view_list_classroom_call, null);
				holder.img_photo = (ImageView) convertView
						.findViewById(R.id.call_stu_photo);
				holder.tvLetter = (TextView) convertView
						.findViewById(R.id.datalog);
				holder.tvName = (TextView) convertView.findViewById(R.id.name);
				holder.tvCount = (TextView) convertView
						.findViewById(R.id.count);
				holder.tvPhone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.radio_group = (LinearLayout) convertView
						.findViewById(R.id.radio_group);
				holder.horizontalScrollView1=(HorizontalScrollView)convertView.findViewById(R.id.horizontalScrollView1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			/*
			// 根据position获取分类的首字母的char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(mContent.getStuLetter().substring(0, 1));
			} else {
				holder.tvLetter.setVisibility(View.GONE);
			}
			*/
			holder.img_photo.setBackgroundResource(R.drawable.ic_launcher1);
			holder.tvName.setText(mContent.getName());
			if(mContent.getLiveSchool().equals("走读"))
				holder.tvName.append("*");
			if(mContent.getZuohao()!=null && mContent.getZuohao().length()>0)
			{
				holder.tvPhone.setVisibility(View.VISIBLE);
				holder.tvPhone.setText("座号:"+mContent.getZuohao());
			}
			else
				holder.tvPhone.setVisibility(View.GONE);
			//holder.tvPhone.setText("手机：" + mContent.getPhone());
			holder.img_photo.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_launcher1));
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(holder.img_photo).image(mContent.getPicImage(), options);
			holder.img_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CallClassActivity.this,
							StudentInfoActivity.class);
					intent.putExtra("studentId", mContent.getStudentID());
					intent.putExtra("userImage", mContent.getPicImage());
					intent.putExtra("teacherInfo", teacherInfo);
					startActivity(intent);
				}
			});
			String workAttendances=PrefUtility.get(Constants.PREF_WORK_ATTENDANCES,"出勤,迟到,请假,缺勤");
			workAttendances=workAttendances.replace("出勤,", "");
			String[] workAttendArray=workAttendances.split(",");
			
			holder.radio_group.setTag(position);
			ArrayList<Integer> usedIcon=new ArrayList<Integer>();
			usedIcon.add(R.drawable.class_call_chat);
			usedIcon.add(R.drawable.class_call_eat);
			usedIcon.add(R.drawable.class_call_read);
			for(int m=0;m<holder.radio_group.getChildCount();m++)
			{
				CheckBox rdbtn=(CheckBox)holder.radio_group.getChildAt(m);
				rdbtn.setVisibility(View.GONE);
			}
			String kaoqinStr=mContent.getChuqinJson().optString(jieci);
			String[] kaoqinArray=kaoqinStr.split(",");
			
			for(int m=0;m<workAttendArray.length;m++)
			{
				if(m>=holder.radio_group.getChildCount()) break;
				CheckBox rdbtn=(CheckBox)holder.radio_group.getChildAt(m);
				rdbtn.setVisibility(View.VISIBLE);
				rdbtn.setText(workAttendArray[m]);
				rdbtn.setChecked(false);
				rdbtn.setEnabled(true);
				for(int i=0;i<kaoqinArray.length;i++)
				{
					if(kaoqinArray[i]!=null && kaoqinArray[i].equals(workAttendArray[m]))
						rdbtn.setChecked(true);
				}
				//if(kaoqinStr!=null && kaoqinStr.equals(workAttendArray[m]))
				//	rdbtn.setChecked(true);
				Drawable drawable;
				
				if(workAttendArray[m].equals("出勤") || workAttendArray[m].equals("正常"))
					drawable=getResources().getDrawable(R.drawable.class_call_attend); 
				else if(workAttendArray[m].equals("迟到"))
					drawable=getResources().getDrawable(R.drawable.class_call_late); 
				else if(workAttendArray[m].equals("缺课") || workAttendArray[m].equals("缺勤") || workAttendArray[m].equals("旷课"))
					drawable=getResources().getDrawable(R.drawable.class_call_absence); 
				else if(workAttendArray[m].equals("早退"))
					drawable=getResources().getDrawable(R.drawable.class_call_zaotui); 
				else if(workAttendArray[m].equals("睡觉"))
					drawable=getResources().getDrawable(R.drawable.class_call_shuijiao); 
				else if(workAttendArray[m].equals("玩手机"))
					drawable=getResources().getDrawable(R.drawable.class_call_wanshouji); 
				else if(workAttendArray[m].equals("无着校服"))
					drawable=getResources().getDrawable(R.drawable.class_call_wuzhuoxiaofu); 
				else if(workAttendArray[m].equals("吃东西"))
					drawable=getResources().getDrawable(R.drawable.class_call_eat);
				else if(workAttendArray[m].equals("聊天"))
					drawable=getResources().getDrawable(R.drawable.class_call_chat);
				else if(workAttendArray[m].equals("看课外书"))
					drawable=getResources().getDrawable(R.drawable.class_call_read);
				else
				{
					if(usedIcon.size()>0 && usedIcon.get(0)!=null)
					{
						drawable=getResources().getDrawable(usedIcon.get(0));
						usedIcon.remove(0);
					}
					else
						drawable=getResources().getDrawable(R.drawable.class_call_eat);
				}
				rdbtn.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
				rdbtn.setOnCheckedChangeListener(checkboxListener);
			}
			if(kaoqinStr.equals("请假") || kaoqinStr.equals("事假") || kaoqinStr.equals("公假") || kaoqinStr.equals("病假"))
			{
				for(int m=0;m<holder.radio_group.getChildCount();m++)
				{
					CheckBox rdbtn=(CheckBox)holder.radio_group.getChildAt(m);
					if(m==0)
					{
						rdbtn.setVisibility(View.VISIBLE);
						Drawable drawable=null;
						if(kaoqinStr.equals("请假") || kaoqinStr.equals("事假"))
							drawable=getResources().getDrawable(R.drawable.class_call_leave);
						else if(kaoqinStr.equals("公假"))
							drawable=getResources().getDrawable(R.drawable.class_call_gongjia);	
						else if(kaoqinStr.equals("病假"))
							drawable=getResources().getDrawable(R.drawable.class_call_bingjia);
						if(drawable!=null)
							rdbtn.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
						rdbtn.setText(kaoqinStr);
						rdbtn.setChecked(true);
						rdbtn.setEnabled(false);
					}
					else
						rdbtn.setVisibility(View.GONE);
				}
			}
			
			holder.horizontalScrollView1.scrollTo(0, 0);
			/*
			holder.radio_group.setOnCheckedChangeListener(null);
			holder.radio_group
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {

							RadioButton radioBtn=(RadioButton)group.findViewById(group.getCheckedRadioButtonId());
							String chuqin=radioBtn.getText().toString();
							int index=(Integer) group.getTag();
							JSONObject obj=list.get(index).getChuqinJson();
							try {
								obj.put(jieci, chuqin);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
					});
			*/
			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getStuLetter();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return list.get(position).getStuLetter().charAt(0);
		}

		@Override
		public Object[] getSections() {
			return null;
		}

		public class ViewHolder {
			TextView tvLetter, tvName, tvCount, tvPhone;
			ImageView img_photo;
			LinearLayout radio_group;
			HorizontalScrollView horizontalScrollView1;
			int flag;
		}
	}

	CompoundButton.OnCheckedChangeListener checkboxListener = new CompoundButton.OnCheckedChangeListener() {  
		  
        @Override  
        public void onCheckedChanged(CompoundButton buttonView,  
                boolean isChecked) {  
            // TODO Auto-generated method stub  
	            CheckBox radioBtn = (CheckBox) buttonView;  
	            
				if(radioBtn.isPressed())
				{
					String chuqin=radioBtn.getText().toString();
					LinearLayout parentView=(LinearLayout) radioBtn.getParent();
					int index=(Integer)parentView.getTag();
					JSONObject obj=adapter.list.get(index).getChuqinJson();
					try {
						if(isChecked)
						{
							if(chuqin.endsWith("缺勤") || chuqin.endsWith("缺课") || chuqin.endsWith("旷课"))
							{
								for(int m=0;m<parentView.getChildCount();m++)
								{
									CheckBox rdbtn=(CheckBox)parentView.getChildAt(m);
									if(!rdbtn.getText().equals(chuqin))
										rdbtn.setChecked(false);
								}
								obj.put(jieci, chuqin);
							}
							else
							{
								for(int m=0;m<parentView.getChildCount();m++)
								{
									CheckBox rdbtn=(CheckBox)parentView.getChildAt(m);
									String itemText=rdbtn.getText().toString();
									if(itemText.endsWith("缺勤") || itemText.endsWith("缺课") || itemText.endsWith("旷课"))
									{
										rdbtn.setChecked(false);
										String newChuqin=obj.get(jieci).toString().replace(itemText+",","");
										newChuqin=newChuqin.replace(","+itemText,"");
										newChuqin=newChuqin.replace(itemText,"");
										obj.put(jieci, newChuqin);
									}
								}
								if(obj.get(jieci).equals(""))
									obj.put(jieci, chuqin);
								else
									obj.put(jieci, obj.get(jieci)+","+chuqin);
							}
						}
						else
						{
							String newChuqin=obj.get(jieci).toString().replace(chuqin+",","");
							newChuqin=newChuqin.replace(","+chuqin,"");
							newChuqin=newChuqin.replace(chuqin,"");
							obj.put(jieci, newChuqin);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
             
  
        }  
    };  
	public class PinyinComparator implements Comparator<Student> {

		public int compare(Student o1, Student o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
			if (o2.getStuLetter().equals("#")) {
				return -1;
			} else if (o1.getStuLetter().equals("#")) {
				return 1;
			} else {
				String o1Name=o1.getStuLetter()+o1.getName();
				String o2Name=o2.getStuLetter()+o2.getName();
				return o1Name.compareTo(o2Name);
			}
		}
	}
	public class PinyinComparator1 implements Comparator<Student> {

		public int compare(Student o1, Student o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
			if (o2.getStudentID().equals("#")) {
				return -1;
			} else if (o1.getStudentID().equals("#")) {
				return 1;
			} else {
				String o1Name=o1.getStudentID();
				String o2Name=o2.getStudentID();
				return o1Name.compareTo(o2Name);
			}
		}
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(CallClassActivity.this,
					DatabaseHelper.class);

		}
		return database;
	}

	/**
	 * 功能描述:加工需要修改的考勤数据
	 * 
	 * @author yanzy 2013-12-3 下午2:51:37
	 * 
	 * @param subjectIdList
	 * @throws JSONException
	 */
	public String getChangekaoqininfo(TeacherInfo teacherInfo)
			throws JSONException {
		if (teacherInfo != null) {
			JSONArray jsonArray = new JSONArray();

			Log.d(TAG, "------------------->teacherInfo.getAbsenceJson():"
							+ teacherInfo.getAbsenceJson());
			String absenceJson = teacherInfo.getAbsenceJson();
			JSONObject joAbsence = new JSONObject();
			JSONArray ja = new JSONArray();
			JSONArray ja1 = new JSONArray();
			if (AppUtility.isNotEmpty(absenceJson)) {
				ja = new JSONArray(teacherInfo.getAbsenceJson());
				if (ja != null && ja.length() > 0) {
					for (int i = 0; i < ja.length(); i++) {
						JSONObject jo = ja.getJSONObject(i);
						ja1.put(new JSONObject(jo.toString()));
						jo.put("考勤类型",jo.optString("考勤类型").split(",")[0]);
						joAbsence.put(jo.optString("学号"), jo.optString("考勤类型"));
						
					}
				}
			}

			JSONObject jo = new JSONObject();
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
			jo.put("用户较验码", checkCode);
			jo.put("编号", teacherInfo.getId() + "");
			jo.put("授课内容", teacherInfo.getCourseContent() + "");
			jo.put("作业布置", teacherInfo.getHomework() + "");
			jo.put("课堂情况", "");
			jo.put("课堂纪律", teacherInfo.getClassroomDiscipline() + "");
			jo.put("教室卫生", teacherInfo.getClassroomHealth() + "");
			jo.put("班级人数", teacherInfo.getClassSize() + "");
			jo.put("实到人数", teacherInfo.getRealNumber() + "");
			jo.put("缺勤情况登记", "");
			jo.put("填写时间", "");
			jo.put("缺勤情况登记JSON", joAbsence);
			jo.put("缺勤情况登记JSONArray", ja);
			jo.put("缺勤情况登记JSONArray1", ja1);
			jsonArray.put(jo);

			Log.d(TAG, "----------------------json:" + jsonArray.toString());
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

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			Bundle bundle = new Bundle();
			switch (msg.what) {
			case -1:
				mLoadingDialog.dismiss();
				AppUtility.showErrorToast(CallClassActivity.this, msg.obj.toString());
				break;
			case 0:
				bundle = (Bundle) msg.obj;
				String action = bundle.getString("action");
				String result = bundle.getString("result");
				String resultStr = "";
				try {
					resultStr = new String(
							Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				System.out.println("action:" + action);
				System.out.println("resultStr:" + resultStr);

				try {
					JSONObject jo = new JSONObject(resultStr);
					if ("1".equals(jo.optString("成功"))) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						DialogUtility.showMsg(CallClassActivity.this, "保存成功！");
						Log.d(TAG, "----------------->结束保存数据："
								+ new Date());
						TipInputCourseContent();
					} else {
						closeDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
					closeDialog();
				}

				break;
			case 1:
				sortListView.setAdapter(adapter);
				/*
				if(array!=null)
					sideBar.setB(array);
				*/
				break;
			case 2:
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
						Log.d(TAG, resultStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				if (AppUtility.isNotEmpty(resultStr)) {
					try {
						JSONObject jo = new JSONObject(resultStr);
						JSONObject jsZongjie=jo.getJSONObject("总结");
						if(jsZongjie!=null && jsZongjie.length()>0)
						{
							teacherInfo.setClassroomDiscipline(jsZongjie.optString("课堂纪律"));
							teacherInfo.setClassroomHealth(jsZongjie.optString("教室卫生"));
							teacherInfo.setCourseContent(jsZongjie.optString("授课内容"));
							teacherInfo.setHomework(jsZongjie.optString("作业布置"));
							teacherInfoDao.update(teacherInfo);
						}
						JSONArray joa = jo.getJSONArray("结果");
						if (joa!=null && joa.length()>0) 
						{
							teacherInfo.setAbsenceJson(joa.toString());
							teacherInfoDao.update(teacherInfo);
							refreshData();  
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				break;
			}
		};
	};
	public void TipInputCourseContent()
	{
		if (ClassDetailActivity.userType.equals("老师")) {
			if(ClassDetailActivity.teacherInfo.getCourseContent()==null || ClassDetailActivity.teacherInfo.getCourseContent().length()==0)
			{
				Intent intent =new Intent("changeTab_classdetailactivity");
	        	intent.putExtra("tabIndex", 4);
				sendBroadcast(intent);
				/*
				new AlertDialog.Builder(this)
		         .setTitle("提示")
		         .setMessage("授课内容还未填写，是否现在填写？")
		         .setPositiveButton("是", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	
		        	Intent intent =new Intent("changeTab_classdetailactivity");
		        	intent.putExtra("tabIndex", 4);
					sendBroadcast(intent);
		         }
		         })
		         .setNegativeButton("否", null)
		         .show();
		         */
			}
		}
	}
	/**
	 * 功能描述:显示提示框
	 * 
	 * @author yanzy 2013-12-21 上午10:54:41
	 * 
	 */
	public void showDialog() {
		mLoadingDialog = DialogUtility.createLoadingDialog(CallClassActivity.this,
				"数据保存中...");
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
		DialogUtility.showMsg(CallClassActivity.this, "保存失败！");
	}
}

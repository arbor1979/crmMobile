package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.CallClassActivity;
import com.yujieshipin.crm.activity.CaptureActivity;
import com.yujieshipin.crm.activity.SchoolActivity;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AchievementItem;
import com.yujieshipin.crm.entity.AchievementItem.Achievement;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.PrinterShareUtil;
import com.yujieshipin.crm.util.TimeUtility;
import com.yujieshipin.crm.widget.SegmentedGroup;
import com.yujieshipin.crm.widget.XListView;
import com.yujieshipin.crm.widget.XListView.IXListViewListener;

import static android.view.View.VISIBLE;


/**
 * 成绩
 */
public class SchoolBillFragment extends Fragment implements IXListViewListener{
	private String TAG = "SchoolBillFragment";
	private XListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight,tv_huizong1;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout,ll_multisel;
	private AchievementItem achievementItem;
	private String interfaceName,title;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private List<Achievement> achievements = new ArrayList<Achievement>();
	private Dialog userTypeDialog;
	private JSONObject filterObject=new JSONObject();;
	private final static int SCANNIN_GREQUEST_CODE = 2;
	private Dialog searchDialog;
	private boolean isLoading=false;
	private FloatingActionButton mFab,mFab1;
	private int mPreviousVisibleItem;
	private boolean bShowMutiSel=false;
	private CheckBox cb_selAll;
	private SegmentedGroup segmentedGroup2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(getActivity()==null)
				return;
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				isLoading=false;
				myListview.stopRefresh();
				showProgress(false);
				String result = msg.obj.toString();
			
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), res);
						}
						else{
							achievementItem = new AchievementItem(jo);
							if(achievementItem.getPage()>0 && achievementItem.getAchievements().size()<achievementItem.getAllnum())
								myListview.setPullLoadEnable(true);
							else
								myListview.setPullLoadEnable(false);

							achievements = achievementItem.getAchievements();
							if(achievementItem.getGroupArr().length()>0)
							{
								segmentedGroup2.setVisibility(VISIBLE);
								segmentedGroup2.removeAllViews();
								segmentedGroup2.setOnCheckedChangeListener(null);
								for(int i=0;i<achievementItem.getGroupArr().length();i++)
								{
									String groupname=achievementItem.getGroupArr().getString(i);

									RadioButton rdbtn = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.tabmenu_radiobutton, null);
									rdbtn.setText(groupname);
									if(achievementItem.getCurGroup()==i)
									{
										rdbtn.setChecked(true);
									}
									rdbtn.setId(i);
									segmentedGroup2.addView(rdbtn);
								}
								segmentedGroup2.updateBackground();
								segmentedGroup2.setOnCheckedChangeListener(new OnCheckedChangeListener(){

									@Override
									public void onCheckedChanged(RadioGroup group, int checkedId) {
										// TODO Auto-generated method stub
										getAchievesItem(true);
									}

								});
							}
							adapter.notifyDataSetChanged();
							tvTitle.setText(achievementItem.getTitle());
							if(achievementItem.getHuizong()!=null && achievementItem.getHuizong().length()>0)
							{
								tv_huizong1.setText(achievementItem.getHuizong());
								tv_huizong1.setVisibility(VISIBLE);
							}
							else
							{
								tv_huizong1.setVisibility(View.GONE);
							}
							if(achievementItem.getRightButton()!=null && achievementItem.getRightButton().length()>0)
							{
								tvRight.setText(achievementItem.getRightButton());
								tvRight.setVisibility(VISIBLE);
								lyRight.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										
										JSONObject queryObj=AppUtility.parseQueryStrToJson(achievementItem.getRightButtonURL());
										if(queryObj.optString("search").length()>0)
										{
											if(queryObj.optString("search").equals("customer") || queryObj.optString("search").equals("supply"))
												popSearchDlg(queryObj.optString("search"));
											else if(queryObj.optString("search").equals("bill"))
												popBillFilterDlg();
											else if(queryObj.optString("search").equals("product"))
												popProductFilterDlg();
											else if(queryObj.optString("search").equals("rukubill"))
												popRukuBillFilterDlg();

										}
										else
											gotoWenJuan("");
										
									}
								});
							}
							else
							{
								tvRight.setVisibility(View.GONE);
								lyRight.setOnClickListener(null);
							}
							if(achievementItem.getFilterArr().length()>0)
								mFab.show();
							else
								mFab.hide();
							 if(achievementItem.getMutiSelArr().length()>0)
								 mFab1.show();
							 else
								 mFab1.hide();

								
						}
					} 
					catch (JSONException e) {
						
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}else{
					showFetchFailedView();
					
				}
				break;
			case 1:
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						}
						else 
						{
							int count=jo.optInt("count");
							if(count==1)
							{
								gotoWenJuan(jo.optString("value"));
							}
							else
							{
								JSONArray userArray=jo.optJSONArray("value");
								popSelectList(userArray);
							}
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}
				else
					showFetchFailedView();
				
				break;
			case 2:
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						}
						else if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), jo.optString("msg"));
							getAchievesItem(false);
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}
				else
					showFetchFailedView();
				
				break;
			}
		}
	};
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 101:
			if(resultCode==1)
				getAchievesItem(true);
		    break;
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == Activity.RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				
				String result = bundle.getString("result");
				if(searchDialog!=null && result!=null && result.length()>0)
				{
					EditText et=(EditText)searchDialog.findViewById(R.id.et_productid);
					et.setText(result);
					Button PositiveButton=((AlertDialog) searchDialog).getButton(AlertDialog.BUTTON_POSITIVE);
					PositiveButton.performClick();
				}
				
				//显示
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		default:
		    break;
		}
	}
	public static final Fragment newInstance(String title, String interfaceName){
		Fragment fragment = new SchoolBillFragment();
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
		View view = inflater.inflate(R.layout.school_xlistview_fragment,
				container, false);
		RelativeLayout navibar=(RelativeLayout)view.findViewById(R.id.navibar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			navibar.setBackgroundColor(color);
		myListview = (XListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		tv_huizong1= (TextView) view.findViewById(R.id.tv_huizong1);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		ll_multisel= (LinearLayout) view.findViewById(R.id.ll_multisel);
		cb_selAll=(CheckBox) view.findViewById(R.id.cb_selAll);
		myListview.setEmptyView(emptyLayout);
		myListview.setPullRefreshEnable(true);
		myListview.setPullLoadEnable(false);
		myListview.setXListViewListener(this);
		mFab = (FloatingActionButton) view.findViewById(R.id.fab);
		mFab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
		segmentedGroup2=(SegmentedGroup)view.findViewById(R.id.segmentedGroup2);
		btnLeft.setVisibility(VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvTitle.setText(title);
		adapter = new AchieveAdapter();
		myListview.setAdapter(adapter);
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				getAchievesItem(true);
			}
		});
		getAchievesItem(true);
		mFab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popFilterDlg();
			}
		});
		mFab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showbatchpass();
			}
		});
		myListview.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem > mPreviousVisibleItem) {
					mFab.hide();
				} else if (firstVisibleItem < mPreviousVisibleItem && achievementItem!=null && achievementItem.getFilterArr()!=null && achievementItem.getFilterArr().length()>0)  {
					mFab.show();
				}
				mPreviousVisibleItem = firstVisibleItem;
			}
		});
		return view;
	}


	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(VISIBLE);
	}

	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getAchievesItem(boolean flag) {
		showProgress(flag);
		if(interfaceName==null)
			return;
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			String[] tmpurlarr=interfaceName.split(".php");
			String functionName=tmpurlarr[0];
			jo.put("function", functionName);
			if(segmentedGroup2.getVisibility()==VISIBLE)
			{
				for(int i = 0 ;i < segmentedGroup2.getChildCount();i++) {
					RadioButton rb = (RadioButton) segmentedGroup2.getChildAt(i);
					if (rb.isChecked()){
						jo.put("curGroupId",rb.getId());
						break;
					}
				}
			}
			JSONObject queryJson=AppUtility.parseQueryStrToJson(interfaceName);
			try {
				Iterator it = queryJson.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = queryJson.getString(key);
					jo.put(key, value);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			Iterator it = filterObject.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = filterObject.getString(key);
				jo.put(key, value);
			}
			if(achievementItem!=null && achievementItem.getFilterArr()!=null) {
				for (int i=0;i<achievementItem.getFilterArr().length();i++)
				{
					JSONObject joitem=achievementItem.getFilterArr().optJSONObject(i);
					jo.put(joitem.optString("标题"), joitem.optString("值"));
				}
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		CampusAPI.httpPost(jo, mHandler, 0);
		isLoading=true;
	}

	
	@SuppressLint({ "DefaultLocale", "NewApi" })
	class AchieveAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return achievements.size();
		}

		@Override
		public Object getItem(int position) {
			return achievements.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				convertView = inflater.inflate(
						R.layout.school_billlistview_item, parent,
						false);
				holder = new ViewHolder();

				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.theTotalMoney = (TextView) convertView
						.findViewById(R.id.theTotalMoney);
				holder.total = (TextView) convertView
						.findViewById(R.id.bill_status);
				holder.theShopper = (TextView) convertView
						.findViewById(R.id.theShopper);
				holder.moreMenu = (ImageView) convertView
						.findViewById(R.id.iv_right);
				holder.cb_checkitem=(CheckBox)convertView.findViewById(R.id.cb_checkitem);
				holder.pb_bottom=(ProgressBar)convertView.findViewById(R.id.pb_bottom);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Achievement achievement = (Achievement) getItem(position);
			AQuery aq = new AQuery(convertView);
			String imagurl = achievement.getIcon();
			//Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.equals("")) {
				//aq.id(holder.icon).image(imagurl);
				//ImageLoader.getInstance().displayImage(imagurl,holder.icon,AppUtility.headOptions);
				ImageOptions options = new ImageOptions();
		        options.memCache=true;
		        options.fileCache=false;
		        options.fallback=R.drawable.ic_launcher1;
				options.targetWidth=200;
				//if(achievement.getHeadtype().equals("-1"))
				//	options.round = 100;
				aq.id(holder.icon).image(imagurl,options);
			}
			else
				holder.icon.setImageResource(R.drawable.ic_launcher1);
			holder.icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {

					if(achievement.getCustomerId()!=null && achievement.getCustomerId().length()>0)
					{
						Intent intent=new Intent(getActivity(),ShowPersonInfo.class);
						intent.putExtra("studentId", achievement.getCustomerId());
						intent.putExtra("userImage", achievement.getIcon());
						intent.putExtra("userType", achievement.getHeadtype());
						startActivity(intent);
					}
					else
					{
						// TODO 放大显示图片
						DialogUtility.showImageDialog(getActivity(),achievement.getIcon());
					}
				}
				
			});
			holder.title.setText(achievement.getTitle());
			holder.theTotalMoney.setText(achievement.getThirdline());
			holder.total.setText(achievement.getTotal());
			if(achievement.getTotal().length()>0)
				holder.total.setVisibility(View.VISIBLE);
			else
				holder.total.setVisibility(View.GONE);
			holder.theShopper.setText(achievement.getRank());
			if(achievement.getExtraMenu()==null)
				holder.moreMenu.setVisibility(View.GONE);
			else
				holder.moreMenu.setVisibility(VISIBLE);
			if(achievement.getThecolor()!=null && achievement.getThecolor().length()>0)
			{
				if(achievement.getThecolor().toLowerCase().equals("red"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_red));
				else if(achievement.getThecolor().toLowerCase().equals("blue"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_blue));
				else if(achievement.getThecolor().toLowerCase().equals("brown"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_brown));
				else if(achievement.getThecolor().toLowerCase().equals("pink"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_pink));
				else if(achievement.getThecolor().toLowerCase().equals("gray"))
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_gray));
				else
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_bg));
				holder.total.setTextColor(getActivity().getResources().getColor(R.color.white));
			}
			if(bShowMutiSel) {
				holder.cb_checkitem.setVisibility(VISIBLE);
				holder.cb_checkitem.setChecked(achievement.isIfChecked());
				holder.cb_checkitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
						if(!compoundButton.isPressed())
							return ;
						achievement.setIfChecked(b);
						//achievements.set(position,achievement);
					}
				});
			}
			else
				holder.cb_checkitem.setVisibility(View.GONE);
			if(achievement.getProgress()>-1)
			{
				holder.pb_bottom.setVisibility(VISIBLE);
				holder.pb_bottom.setProgress(achievement.getProgress());
			}
			else
				holder.pb_bottom.setVisibility(View.GONE);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(bShowMutiSel) {
						CheckBox cb_checkitem=(CheckBox)v.findViewById(R.id.cb_checkitem);
						cb_checkitem.setChecked(!cb_checkitem.isChecked());
						achievement.setIfChecked(cb_checkitem.isChecked());
						return;
					}
					String DetailUrl = achievement.getDetailUrl();
					if (AppUtility.isNotEmpty(DetailUrl)) {
						Log.d(TAG,"----notice.getEndUrl():"+ achievement.getDetailUrl());
						
						if(DetailUrl.length()>0 && !DetailUrl.equals("null"))
						{
							Intent intent =null;
							if(achievement.getTemplateName()==null || achievement.getTemplateName().length()==0)
							{
								intent=new Intent(getActivity(),SchoolDetailActivity.class);
								intent.putExtra("templateName", "单据");
							}
							else
							{
								if(achievement.getTemplateGrade().equals("main"))
									intent=new Intent(getActivity(),SchoolActivity.class);
								else
									intent=new Intent(getActivity(),SchoolDetailActivity.class);
								intent.putExtra("templateName", achievement.getTemplateName());
							}
							int pos=interfaceName.indexOf("?");
							String preUrl=interfaceName;
							if(pos>-1)
								preUrl=interfaceName.substring(0, pos);
							intent.putExtra("interfaceName", preUrl+DetailUrl);
							intent.putExtra("title", title);
							startActivityForResult(intent,101);
						}
					}
				}
			});
			holder.moreMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JSONObject popMenu=achievement.getExtraMenu();
					if(popMenu!=null && popMenu.length()>0)
					{
						String[] popMenuStr=new String[popMenu.length()+1];
						Iterator<?> it = popMenu.keys();
						int i=0;
			            while(it.hasNext()){
			                 popMenuStr[i]= (String) it.next().toString();
			                i++;
			            }
						popMenuStr[popMenuStr.length-1]="取消";
						showUserTypeDialog(popMenuStr,achievement);
					}
				}
			
			});
			return convertView;
		}

		class ViewHolder {
			ImageView icon;
			TextView title;
			TextView theTotalMoney;
			TextView total;
			TextView theShopper;
			ImageView moreMenu;
			CheckBox cb_checkitem;
			ProgressBar pb_bottom;
		}
		
	}
	private void showUserTypeDialog(String[] data,Achievement achievement) {
		userTypeDialog = new Dialog(getActivity(), R.style.dialog);
		View view = inflater.inflate(
				R.layout.view_exam_login_dialog, null);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(data,achievement);
		mList.setAdapter(dialogAdapter);
		Window window = userTypeDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		window.setGravity(Gravity.CENTER);
		userTypeDialog.setContentView(view);
		userTypeDialog.show();
		
	}

	/**
	 * 弹出窗口listview适配器
	 */
	public class DialogAdapter extends BaseAdapter {
		String[] arrayData;
		Achievement achievement;
		public DialogAdapter(String[] array,Achievement achievement) {
			this.arrayData = array;
			this.achievement=achievement;
		}

		@Override
		public int getCount() {
			return arrayData == null ? 0 : arrayData.length;
		}

		@Override
		public Object getItem(int position) {
			return arrayData[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.view_testing_pop, arg2,false);
				
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String text = arrayData[position];
			holder.title.setText(text);
			holder.title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PrefUtility.put(Constants.PREF_CHECK_TEST, false);
					if ("删除".equals(text)) {
						new AlertDialog.Builder(getActivity())
					    .setIcon(android.R.drawable.ic_dialog_alert)
					    .setTitle("确认对话框")
					    .setMessage("是否确认删除?")
					    .setPositiveButton("是", new DialogInterface.OnClickListener()
					    {
					    	@Override
					    	public void onClick(DialogInterface dialog, int which) 
					    	{
					    		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
					    		JSONObject queryObj=AppUtility.parseQueryStrToJson(achievement.getExtraMenu().optString(text));
					    		JSONObject jo = new JSONObject();
					    		try {
					    			jo.put("用户较验码", checkCode);
					    			Iterator it = queryObj.keys();
									while (it.hasNext()) {
						                String key = (String) it.next();
						                String value = queryObj.getString(key); 
						                jo.put(key, value);
									}
					    			
					    		} catch (JSONException e1) {
					    			e1.printStackTrace();
					    		}
					    		if(queryObj.optString("templateName").length()>0) {

									Intent intent =null;
									intent.putExtra("templateName", queryObj.optString("templateName"));
									if(queryObj.optString("templateGrade").equals("main"))
										intent=new Intent(getActivity(),SchoolActivity.class);
									else
										intent=new Intent(getActivity(),SchoolDetailActivity.class);
									int pos=interfaceName.indexOf("?");
									String preUrl=interfaceName;
									if(pos>-1)
										preUrl=interfaceName.substring(0, pos);
									intent.putExtra("interfaceName", preUrl+achievement.getExtraMenu().optString(text));
									//intent.putExtra("title", title);
									startActivityForResult(intent,101);
								}
					    		else
					    			CampusAPI.httpPost(jo, mHandler, 2);
					    	}
					    })
					    .setNegativeButton("否", null)
					    .show();
					}
					else if ("取消".equals(text))
					{

					}
					else {

						JSONObject queryObj = AppUtility.parseQueryStrToJson(achievement.getExtraMenu().optString(text));
						String templateName = queryObj.optString("templateName");
						String templateGrade = queryObj.optString("templateGrade");
						String printurl = queryObj.optString("printurl");
						if (templateName != null && templateName.length() > 0) {
							Intent intent = null;
							if (templateGrade != null && templateGrade.equals("main"))
								intent = new Intent(getActivity(), SchoolActivity.class);
							else
								intent = new Intent(getActivity(), SchoolDetailActivity.class);
							intent.putExtra("templateName", templateName);
							int pos = interfaceName.indexOf("?");
							String preUrl = interfaceName;
							if (pos > -1)
								preUrl = interfaceName.substring(0, pos);
							intent.putExtra("interfaceName", preUrl + achievement.getExtraMenu().optString(text));
							startActivityForResult(intent, 101);
						}
						else if(printurl!=null && printurl.length()>0)
						{
							if(PrinterShareUtil.isAppInstalled(getActivity()))
							{
								String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
								String siteUrl = PrefUtility.get(Constants.PREF_LOGIN_URL, "");
								String urlstr="http://119.29.6.239:"+siteUrl+ Uri.decode(printurl)+"&token="+Uri.encode(checkCode);
								PrinterShareUtil.startUrlPrinterShare(getActivity(),urlstr);
							}
							else {
								new AlertDialog.Builder(getActivity())
										.setIcon(android.R.drawable.ic_dialog_alert)
										.setTitle("确认对话框")
										.setMessage("是否安装打印工具PrinterShare?")
										.setPositiveButton("是", new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int which)
											{
												PrinterShareUtil.startInstallApp(getActivity());
											}
										})
										.setNegativeButton("否", null)
										.show();
							}
						}
						else {
							String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
							JSONObject jo = new JSONObject();
							try {
								jo.put("用户较验码", checkCode);
								Iterator it = queryObj.keys();
								while (it.hasNext()) {
									String key = (String) it.next();
									String value = queryObj.getString(key);
									jo.put(key, value);
								}

							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							CampusAPI.httpPost(jo, mHandler, 2);
						}

					}
					userTypeDialog.dismiss();
				}
			});
			return convertView;
		}
		class ViewHolder {
			TextView title;
		}
	}
	private void popProductFilterDlg()
	{
		String title="产品过滤";
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final LinearLayout textEntryView = (LinearLayout) inflater.inflate(R.layout.dialog_product_entry,
			    null);
		final Button bt_scanCode=(Button)textEntryView.findViewById(R.id.bt_scancode);
		final EditText et_productid=(EditText)textEntryView.findViewById(R.id.et_productid);
		final EditText et_producttype=(EditText)textEntryView.findViewById(R.id.et_producttype);
		final EditText et_supplyid=(EditText)textEntryView.findViewById(R.id.et_supplyid);

		bt_scanCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppUtility.checkPermission(getActivity(), 6, Manifest.permission.CAMERA))
					openScanCode();
			}
		});
		
		String productid=filterObject.optString("productid");
		if(productid!=null && productid.length()>0)
			et_productid.setText(productid);
		String producttype=filterObject.optString("producttype");
		if(producttype!=null && producttype.length()>0)
			et_producttype.setText(producttype);
		String supplyid=filterObject.optString("supplyid");
		if(supplyid!=null && supplyid.length()>0)
			et_supplyid.setText(supplyid);

		Spinner sp_filter1 = null;
		String mItems1[] = new String[0];
		if(achievementItem.getFilterParams1()!=null && achievementItem.getFilterParams1().size()>0) {
			sp_filter1 = new Spinner(getActivity());
			mItems1 = new String[achievementItem.getFilterParams1().size()];
			for (int i = 0; i < achievementItem.getFilterParams1().size(); i++) {
				mItems1[i] = achievementItem.getFilterParams1().get(i);
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems1);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_filter1.setAdapter(adapter);
			sp_filter1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100));
			textEntryView.addView(sp_filter1);

		}
		String filterparam1=filterObject.optString("过滤参数1");
		if(filterparam1!=null && filterparam1.length()>0 && sp_filter1!=null) {
			for (int i = 0; i < mItems1.length; i++) {
				if (mItems1[i].equals(filterparam1)) {
					sp_filter1.setSelection(i);
					break;
				}
			}
		}
		Spinner sp_filter2= null;;
		String mItems2[]= new String[0];;
		if(achievementItem.getFilterParams2()!=null && achievementItem.getFilterParams2().size()>0) {
			sp_filter2 = new Spinner(getActivity());
			mItems2 = new String[achievementItem.getFilterParams2().size()];
			for (int i = 0; i < achievementItem.getFilterParams2().size(); i++) {
				mItems2[i] = achievementItem.getFilterParams2().get(i);
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems2);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_filter2.setAdapter(adapter);
			sp_filter2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100));
			textEntryView.addView(sp_filter2);

		}

		String filterparam2=filterObject.optString("过滤参数2");
		if(filterparam2!=null && filterparam2.length()>0 && sp_filter2!=null) {
			for (int i = 0; i < mItems2.length; i++) {
				if (mItems2[i].equals(filterparam2)) {
					sp_filter2.setSelection(i);
					break;
				}
			}
		}
		final Spinner finalSp_filter1 = sp_filter1;
		final Spinner finalSp_filter2 = sp_filter2;
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String productid=et_productid.getText().toString();
				String producttype=et_producttype.getText().toString();
				String supplyid=et_supplyid.getText().toString();
				String filterparam1="";
				if(finalSp_filter1!=null)
					filterparam1= finalSp_filter1.getSelectedItem().toString();
				String filterparam2="";
				if(finalSp_filter2!=null)
					filterparam2= finalSp_filter2.getSelectedItem().toString();
				try {
					filterObject.put("productid", productid);
					filterObject.put("producttype", producttype);
					filterObject.put("supplyid", supplyid);
					filterObject.put("过滤参数1", filterparam1);
					filterObject.put("过滤参数2", filterparam2);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				getAchievesItem(true);
				
			}
			
		}).setNegativeButton("取消", null);
		searchDialog=builder.create();
        searchDialog.show();
		//TimeUtility.popSoftKeyBoard(getActivity(),et_productid);
	}
	private void openScanCode()
	{


		Intent intent = new Intent();
		intent.setClass(getActivity(), CaptureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	}
	private void popBillFilterDlg()
	{
		
		title="单据过滤";
		LinearLayout layout=new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		final Spinner sp_createtime=new Spinner(getActivity());
		String[] mItems=new String[] {"今天","最近三天","最近一周","最近一月","最近半年","全部"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_createtime.setAdapter(adapter);
		layout.addView(sp_createtime);
		final EditText et_billid=new EditText(getActivity());
		et_billid.setHint("输入单号");
		et_billid.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		et_billid.setSingleLine();
		layout.addView(et_billid);
		final EditText et_customerName=new EditText(getActivity());
		et_customerName.setHint("输入客户名称或会员卡");
		et_customerName.setSingleLine();
		layout.addView(et_customerName);
		final EditText et_creater=new EditText(getActivity());
		et_creater.setHint("输入制单人");
		et_creater.setSingleLine();
		layout.addView(et_creater);
		final EditText et_shopper=new EditText(getActivity());
		et_shopper.setHint("输入导购");
		et_shopper.setSingleLine();
		layout.addView(et_shopper);
		
		
			String createtime=filterObject.optString("createtime");
			if(createtime!=null && createtime.length()>0)
			{
				for(int i=0;i<mItems.length;i++)
				{
					if(mItems[i].equals(createtime))
					{
						sp_createtime.setSelection(i);
						break;
					}
				}
			}
			String billid=filterObject.optString("billid");
			if(billid!=null && billid.length()>0)
				et_billid.setText(billid);
			String customerName=filterObject.optString("customerName");
			if(customerName!=null && customerName.length()>0)
				et_customerName.setText(customerName);
			String user_id=filterObject.optString("user_id");
			if(user_id!=null && user_id.length()>0)
				et_creater.setText(user_id);
			String qianyueren=filterObject.optString("qianyueren");
			if(qianyueren!=null && qianyueren.length()>0)
				et_shopper.setText(qianyueren);
			
		
		new AlertDialog.Builder(getActivity()).setTitle(title).setView(layout)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String billid=et_billid.getText().toString();
				String customerName=et_customerName.getText().toString();
				String user_id=et_creater.getText().toString();
				String qianyueren=et_shopper.getText().toString();
				String createtime=sp_createtime.getSelectedItem().toString();
				
				try {
					filterObject.put("billid", billid);
					filterObject.put("customerName", customerName);
					filterObject.put("user_id", user_id);
					filterObject.put("qianyueren", qianyueren);
					filterObject.put("createtime", createtime);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				getAchievesItem(true);
				
			}
			
		}).setNegativeButton("取消", null).show();
		TimeUtility.popSoftKeyBoard(getActivity(),et_billid);
	}
	private void popRukuBillFilterDlg()
	{
		title="单据过滤";
		LinearLayout layout=new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10,10,10,10);
		final Spinner sp_createtime=new Spinner(getActivity());
		String[] mItems=new String[] {"今天","最近三天","最近一周","最近一月","最近半年","全部"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_createtime.setAdapter(adapter);
		layout.addView(sp_createtime);
		sp_createtime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,60));
		Spinner sp_filter1 = null;
		String mItems1[] = new String[0];
		if(achievementItem.getFilterParams1()!=null) {
			sp_filter1 = new Spinner(getActivity());
			mItems1 = new String[achievementItem.getFilterParams1().size()];
			for (int i = 0; i < achievementItem.getFilterParams1().size(); i++) {
				mItems1[i] = achievementItem.getFilterParams1().get(i);
			}
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems1);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_filter1.setAdapter(adapter);
			layout.addView(sp_filter1);
			sp_filter1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,60));
		}
		Spinner sp_filter2= null;;
		String mItems2[]= new String[0];;
		if(achievementItem.getFilterParams2()!=null) {
			sp_filter2 = new Spinner(getActivity());
			mItems2 = new String[achievementItem.getFilterParams2().size()];
			for (int i = 0; i < achievementItem.getFilterParams2().size(); i++) {
				mItems2[i] = achievementItem.getFilterParams2().get(i);
			}
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems2);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_filter2.setAdapter(adapter);
			layout.addView(sp_filter2);
			sp_filter2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,60));
		}
		final EditText et_billid=new EditText(getActivity());
		et_billid.setHint("输入单号");
		et_billid.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		et_billid.setSingleLine();
		layout.addView(et_billid);
		final EditText et_creater=new EditText(getActivity());
		et_creater.setHint("输入制单人");
		et_creater.setSingleLine();
		layout.addView(et_creater);

		String createtime=filterObject.optString("createtime");
		if(createtime!=null && createtime.length()>0)
		{
			for(int i=0;i<mItems.length;i++)
			{
				if(mItems[i].equals(createtime))
				{
					sp_createtime.setSelection(i);
					break;
				}
			}
		}
		String billid=filterObject.optString("billid");
		if(billid!=null && billid.length()>0)
			et_billid.setText(billid);
		String filterparam1=filterObject.optString("过滤参数1");
		if(filterparam1!=null && filterparam1.length()>0 && sp_filter1!=null) {
			for (int i = 0; i < mItems1.length; i++) {
				if (mItems1[i].equals(filterparam1)) {
					sp_filter1.setSelection(i);
					break;
				}
			}
		}
		String filterparam2=filterObject.optString("过滤参数2");
		if(filterparam2!=null && filterparam2.length()>0 && sp_filter2!=null) {
			for (int i = 0; i < mItems2.length; i++) {
				if (mItems2[i].equals(filterparam2)) {
					sp_filter2.setSelection(i);
					break;
				}
			}
		}
		String user_id=filterObject.optString("user_id");
		if(user_id!=null && user_id.length()>0)
			et_creater.setText(user_id);

		final Spinner finalSp_filter1 = sp_filter1;
		final Spinner finalSp_filter2 = sp_filter2;
		new AlertDialog.Builder(getActivity()).setTitle(title).setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String billid=et_billid.getText().toString();
						String filterparam1="";
						if(finalSp_filter1!=null)
							filterparam1= finalSp_filter1.getSelectedItem().toString();
						String filterparam2="";
						if(finalSp_filter2!=null)
							filterparam2= finalSp_filter2.getSelectedItem().toString();
						String user_id=et_creater.getText().toString();
						String createtime=sp_createtime.getSelectedItem().toString();

						try {
							filterObject.put("billid", billid);
							filterObject.put("过滤参数1", filterparam1);
							filterObject.put("过滤参数2", filterparam2);
							filterObject.put("user_id", user_id);
							filterObject.put("createtime", createtime);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						getAchievesItem(true);

					}

				}).setNegativeButton("取消", null).show();
		TimeUtility.popSoftKeyBoard(getActivity(),et_billid);
	}
	private void popSearchDlg(final String searchType)
	{
		String title="";
		final EditText et=new EditText(getActivity());
		if(searchType.equals("customer")) {
			title = "搜索客户";
			et.setHint("输入名称或电话");
		}
		else
		{
			title="搜索供应商";
			et.setHint("输入名称或拼音缩写");
		}
		new AlertDialog.Builder(getActivity()).setTitle(title).setView(et)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String searchValue=et.getText().toString();
				if(AppUtility.isNotEmpty(searchValue))
				{
					searchForValue(searchType,searchValue);
				}
			}
			
		}).setNegativeButton("取消", null).show();
		TimeUtility.popSoftKeyBoard(getActivity(),et);
		
	}

	private void searchForValue(String searchType,String searchValue) {
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", "searchForValue");
			jo.put("searchType", searchType);
			jo.put("searchValue", searchValue);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 1);
	}
	private void gotoWenJuan(String extParam)
	{
		if(extParam!=null && extParam.length()>0)
			extParam="&extParam="+extParam;
		else
			extParam="";
		Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
		intent.putExtra("templateName", "调查问卷");
		intent.putExtra("interfaceName", achievementItem.getRightButtonURL()+extParam);
		intent.putExtra("title", title);
		startActivityForResult(intent,101);
	}
	private void popSelectList(final JSONArray userArray)
	{
		String [] userStr=new String[userArray.length()];
		for(int i=0;i<userArray.length();i++)
		{
			JSONObject obj = null;
			try {
				obj = userArray.getJSONObject(i);
				if(obj!=null)
					userStr[i]=obj.optString("value");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		new AlertDialog.Builder(getActivity()).setTitle("请选择一个")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(userStr, 0, new DialogInterface.OnClickListener() 
		{ 
			public void onClick(DialogInterface dialog, int which) 
			{ 
				try {
					String ID=userArray.getJSONObject(which).getString("key");
					gotoWenJuan(ID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			} 
		} 
		).setNegativeButton("取消", null) .show();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if(!isLoading)
		{
			if(achievementItem!=null && achievementItem.getPage()>0)
				try {
					
					filterObject.put("page", achievementItem.getPage()+1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			getAchievesItem(false);
		}
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getAchievesItem(false);
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		AppUtility.permissionResult(requestCode,grantResults,getActivity(),callBack);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	public AppUtility.CallBackInterface callBack=new AppUtility.CallBackInterface()
	{
		@Override
		public void getLocation1() {

		}

		@Override
		public void getPictureByCamera1() {
			openScanCode();
			if(searchDialog.isShowing())
				searchDialog.dismiss();
		}

		@Override
		public void getPictureFromLocation1() {
			// TODO Auto-generated method stub

		}

	};
	private void popFilterDlg()
	{
		final LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 10, 10, 10);
		for (int i = 0; i < achievementItem.getFilterArr().length(); i++) {
			JSONObject filterObj = achievementItem.getFilterArr().optJSONObject(i);
			if (filterObj != null) {
				if (filterObj.optString("类型").equals("文本框")) {
					final EditText et_billid = new EditText(getActivity());
					et_billid.setContentDescription(filterObj.optString("标题"));
					et_billid.setHint(filterObj.optString("标题"));
					if (filterObj.optString("输入法").equals("数字"))
						et_billid.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
					et_billid.setSingleLine();
					et_billid.setText(filterObj.optString("值"));
					layout.addView(et_billid);
				} else if (filterObj.optString("类型").equals("下拉框")) {
					Spinner sp_filter1 = new Spinner(getActivity());
					sp_filter1.setContentDescription(filterObj.optString("标题"));
					String[] mItems1 = new String[filterObj.optJSONArray("选项").length()];
					int selection = 0;
					for (int j = 0; j < filterObj.optJSONArray("选项").length(); j++) {
						mItems1[j] = filterObj.optJSONArray("选项").optString(j);
						if (filterObj.optString("值").equals(filterObj.optJSONArray("选项").optString(j)))
							selection = j;
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems1);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_filter1.setAdapter(adapter);
					sp_filter1.setSelection(selection);
					layout.addView(sp_filter1);
					sp_filter1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
				}
			}
		}

		new AlertDialog.Builder(getActivity()).setTitle("过滤条件").setView(layout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					for (int i = 0; i < layout.getChildCount(); i++) {
						View view = layout.getChildAt(i);
						String key = "";
						String value = "";
						if (view instanceof EditText) {
							EditText editText = (EditText) view;
							key = (String) editText.getContentDescription();
							value = editText.getText().toString();
						} else if (view instanceof Spinner) {
							Spinner spinner = (Spinner) view;
							key = (String) spinner.getContentDescription();
							value = spinner.getSelectedItem().toString();
						}
						for (int j = 0; j < achievementItem.getFilterArr().length(); j++) {
							JSONObject item = achievementItem.getFilterArr().optJSONObject(j);
							if (item.optString("标题").equals(key)) {
								try {
									item.put("值", value);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
					getAchievesItem(true);
				}
			}).setNegativeButton("取消", null).show();

	}
	private void showbatchpass()
	{
		bShowMutiSel=!bShowMutiSel;
		if(bShowMutiSel) {
			LinearLayout ll_btns=null;
			for(int i=0;i<ll_multisel.getChildCount();i++) {
				View subview = ll_multisel.getChildAt(i);
				if (subview instanceof LinearLayout) {
					ll_btns=(LinearLayout)subview;
					ll_btns.removeAllViews();
					break;
				}
			}
			cb_selAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

					for(int i=0;i<achievements.size();i++)
					{
						Achievement item=achievements.get(i);
						item.setIfChecked(b);
					}
					adapter.notifyDataSetChanged();
				}

			});
			for(int i=0;i<achievementItem.getMutiSelArr().length();i++)
			{
				final JSONObject jo=achievementItem.getMutiSelArr().optJSONObject(i);
				if(jo!=null)
				{
					Button btn=new Button(getActivity());
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(10,0,10,0);//4个参数按顺序分别是左上右下
					layoutParams.height=95;
					btn.setLayoutParams(layoutParams);
					btn.setText(jo.optString("name"));
					ll_btns.addView(btn);
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							String selIdStr="";
							for(int i=0;i<achievements.size();i++)
							{
								Achievement item=achievements.get(i);
								if(item.isIfChecked())
								{
									if(selIdStr.length()>0)
										selIdStr+=","+item.getId();
									else
										selIdStr=item.getId();
								}
							}
							if(selIdStr.length()==0) {
								AppUtility.showToastMsg(getActivity(),"请先勾选记录");
								return;
							}
							String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
							JSONObject queryObj=AppUtility.parseQueryStrToJson(jo.optString("url"));
							if(queryObj.optString("templateName").length()>0) {
								Intent intent = new Intent(getActivity(), SchoolDetailActivity.class);
								intent.putExtra("templateName", queryObj.optString("templateName"));
								intent.putExtra("interfaceName", jo.optString("url")+"&ID="+selIdStr);
								intent.putExtra("title", title);
								startActivityForResult(intent, 101);
							}
							else {
								JSONObject jo = new JSONObject();
								try {
									jo.put("用户较验码", checkCode);
									jo.put("selIdStr", selIdStr);
									Iterator it = queryObj.keys();
									while (it.hasNext()) {
										String key = (String) it.next();
										String value = queryObj.getString(key);
										jo.put(key, value);
									}

								} catch (JSONException e1) {
									e1.printStackTrace();
								}
								CampusAPI.httpPost(jo, mHandler, 2);
							}
						}
					});
					if(jo.optString("color").length()>0)
					{
						if(jo.optString("color").equals("orange"))
							btn.setBackgroundResource(R.drawable.button_round_corner_orange);
						else if(jo.optString("color").equals("blue"))
							btn.setBackgroundResource(R.drawable.button_round_corner_blue);
						else
							btn.setBackgroundResource(R.drawable.button_round_corner_green);

					}
				}
			}
			ll_multisel.setVisibility(VISIBLE);
		}
		else
			ll_multisel.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}
}

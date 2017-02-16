package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.LoginActivity;
import com.yujieshipin.crm.activity.MipcaActivityCapture;
import com.yujieshipin.crm.activity.SchoolActivity;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.activity.LoginActivity.DialogAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AchievementItem;
import com.yujieshipin.crm.entity.AchievementItem.Achievement;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;


/**
 * 成绩
 */
public class SchoolBillFragment extends Fragment {
	private String TAG = "SchoolBillFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle,tvRight,tv_huizong1;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private AchievementItem achievementItem;
	private String interfaceName,title;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private List<Achievement> achievements = new ArrayList<Achievement>();
	private Dialog userTypeDialog;
	private JSONObject filterObject;
	private final static int SCANNIN_GREQUEST_CODE = 2;
	private Dialog searchDialog;
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
			
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), res);
						}
						else{
							achievementItem = new AchievementItem(jo);
							achievements = achievementItem.getAchievements();
							adapter.notifyDataSetChanged();
							tvTitle.setText(achievementItem.getTitle());
							if(achievementItem.getHuizong()!=null && achievementItem.getHuizong().length()>0)
							{
								tv_huizong1.setText(achievementItem.getHuizong());
								tv_huizong1.setVisibility(View.VISIBLE);
							}
							else
							{
								tv_huizong1.setVisibility(View.GONE);
							}
							if(achievementItem.getRightButton()!=null && achievementItem.getRightButton().length()>0)
							{
								tvRight.setText(achievementItem.getRightButton());
								tvRight.setVisibility(View.VISIBLE);
								lyRight.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										
										JSONObject queryObj=AppUtility.parseQueryStrToJson(achievementItem.getRightButtonURL());
										if(queryObj.optString("search")!=null)
										{
											if(queryObj.optString("search").equals("customer"))
												popSearchDlg(queryObj.optString("search"));
											else if(queryObj.optString("search").equals("bill"))
												popBillFilterDlg();
											else if(queryObj.optString("search").equals("product"))
												popProductFilterDlg();
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
							getAchievesItem();
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
			getAchievesItem();
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
	public SchoolBillFragment() {
		
	}
	public SchoolBillFragment(String title,String iunterfaceName) {
		this.interfaceName = iunterfaceName;
		this.title = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment,
				container, false);
		RelativeLayout navibar=(RelativeLayout)view.findViewById(R.id.navibar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			navibar.setBackgroundColor(color);
		myListview = (ListView) view.findViewById(R.id.my_listview);
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
		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
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
				getAchievesItem();
			}
		});
		getAchievesItem();
		return view;
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
	public void getAchievesItem() {
		showProgress(true);
		if(interfaceName==null)
			return;
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			String functionName=interfaceName.substring(0, interfaceName.length()-4);
			jo.put("function", functionName);
			if(filterObject!=null)
			{
				Iterator it = filterObject.keys();
				while (it.hasNext()) {
	                String key = (String) it.next();
	                String value = filterObject.getString(key); 
	                jo.put(key, value);
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 0);
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
		public View getView(int position, View convertView, ViewGroup parent) {
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
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Achievement achievement = (Achievement) getItem(position);
			AQuery aq = new AQuery(convertView);
			String imagurl = achievement.getIcon();
			Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.equals("")) {
				//aq.id(holder.icon).image(imagurl);
				//ImageLoader.getInstance().displayImage(imagurl,holder.icon,AppUtility.headOptions);
				ImageOptions options = new ImageOptions();
		        options.memCache=true;
		        options.fileCache=false;
		        options.fallback=R.drawable.ic_launcher;
				options.targetWidth=200;
				options.round = 100;
				aq.id(holder.icon).image(imagurl,options);
				
			}
			holder.icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(achievement.getCustomerId()!=null && achievement.getCustomerId().length()>0)
					{
						Intent intent=new Intent(getActivity(),ShowPersonInfo.class);
						intent.putExtra("studentId", achievement.getCustomerId());
						intent.putExtra("userImage", achievement.getIcon());
						intent.putExtra("userType", "0");
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
			holder.theShopper.setText(achievement.getRank());
			if(achievement.getExtraMenu()==null)
				holder.moreMenu.setVisibility(View.GONE);
			else
				holder.moreMenu.setVisibility(View.VISIBLE);
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
				else
					holder.total.setBackground(getResources().getDrawable(R.drawable.school_achievement_bg));
				holder.total.setTextColor(getActivity().getResources().getColor(R.color.white));
			}
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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
					    		CampusAPI.httpPost(jo, mHandler, 2);
					    	}
					    })
					    .setNegativeButton("否", null)
					    .show();
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
		final View textEntryView = inflater.inflate(R.layout.dialog_product_entry,
			    null);
		final Button bt_scanCode=(Button)textEntryView.findViewById(R.id.bt_scancode);
		final EditText et_productid=(EditText)textEntryView.findViewById(R.id.et_productid);
		final EditText et_producttype=(EditText)textEntryView.findViewById(R.id.et_producttype);
		final EditText et_standard=(EditText)textEntryView.findViewById(R.id.et_standard);
		final EditText et_supplyid=(EditText)textEntryView.findViewById(R.id.et_supplyid);
		
		bt_scanCode.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	Intent intent = new Intent();
				intent.setClass(getActivity(), MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            	
            }
         });
		if(filterObject!=null)
		{
			String productid=filterObject.optString("productid");
			if(productid!=null && productid.length()>0)
				et_productid.setText(productid);
			String producttype=filterObject.optString("producttype");
			if(producttype!=null && producttype.length()>0)
				et_producttype.setText(producttype);
			String standard=filterObject.optString("standard");
			if(standard!=null && standard.length()>0)
				et_standard.setText(standard);
			String supplyid=filterObject.optString("supplyid");
			if(supplyid!=null && supplyid.length()>0)
				et_supplyid.setText(supplyid);
			
		}
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String productid=et_productid.getText().toString();
				String producttype=et_producttype.getText().toString();
				String standard=et_standard.getText().toString();
				String supplyid=et_supplyid.getText().toString();
				if(filterObject==null)
					filterObject=new JSONObject();
				try {
					filterObject.put("productid", productid);
					filterObject.put("producttype", producttype);
					filterObject.put("standard", standard);
					filterObject.put("supplyid", supplyid);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getAchievesItem();
				
			}
			
		}).setNegativeButton("取消", null);
		searchDialog=builder.create();
        searchDialog.show();
		//TimeUtility.popSoftKeyBoard(getActivity(),et_productid);
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
		
		if(filterObject!=null)
		{
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
			
		}
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
				if(filterObject==null)
					filterObject=new JSONObject();
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
				getAchievesItem();
				
			}
			
		}).setNegativeButton("取消", null).show();
		TimeUtility.popSoftKeyBoard(getActivity(),et_billid);
	}
	
	private void popSearchDlg(final String searchType)
	{
		String title="";
		final EditText et=new EditText(getActivity());
		title="搜索客户";
		et.setHint("输入客户名称或会员卡");
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
}

package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.MipcaActivityCapture;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.BillDetail;
import com.yujieshipin.crm.entity.BillDetailItem;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;

/**
 * 成绩详情
 */
public class SchoolBillDetailFragment extends Fragment {
	private String TAG = "SchoolbillDetailFragment";
	private ListView myListview;
	private Button btnLeft,bt_gotopay;
	private TextView tvTitle,tvRight,tv_huizong1,tv_huizong2,tv_yingshou,tv_zhaoling;
	private LinearLayout lyLeft,lyRight;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	private LinearLayout huizongLayout;
	private BillDetail billDetail;
	private String title, interfaceName;
	private LayoutInflater inflater;
	private AchieveAdapter adapter;
	private Dialog dialog;
	private List<BillDetailItem> details = new ArrayList<BillDetailItem>();
	private int opertype=1;
	private Dialog searchDialog;
	private final static int SCANNIN_GREQUEST_CODE = 2;
	private EditText et_quling,et_shoukuan;
	private RadioButton rb_ifpay0,rb_ifpay1;
	private Spinner spinner;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				if(dialog!=null)
					dialog.dismiss();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;
			case 0:
				showProgress(false);
				String result = msg.obj.toString();

				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(AppUtility.isNotEmpty(res)){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						}else{
							billDetail = new BillDetail(jo);
							initDate(msg.arg1);
						}
					} catch (JSONException e) {
						//showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1:
				if(dialog!=null)
					dialog.dismiss();
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败"))
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						else
							getAchievesItem();
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 2:
				LinearLayout v=(LinearLayout)msg.obj;
				v.setVisibility(View.GONE);
				break;
			case 3:
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						
						if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), "操作成功!");
							String autoClose=jo.optString("自动关闭");
							if(autoClose!=null && autoClose.equals("是"))
							{
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
							else
								getAchievesItem();
						}
						else
							AppUtility.showErrorToast(getActivity(), jo.optString("errorMsg"));
						
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			
			case 5://搜索产品编号或原厂码
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
								insertNewDetail(jo.optString("value"));
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
			case 6://保存一个条新明细记录
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							adapter.notifyDataSetChanged();
						}
						else 
						{
							Message msg1 = new Message();
							msg1.what = 0;
							msg1.obj = jo.optJSONObject("detailList");
							msg1.arg1=jo.optInt("toBottom");
							mHandler.sendMessage(msg1);   
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
						adapter.notifyDataSetChanged();
					}
				}
				else
					showFetchFailedView();
				
				break;
			case 7://获取结算信息
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							adapter.notifyDataSetChanged();
						}
						else 
						{
							popPayDlg(jo);
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}
				
				break;
			case 8://保存销售单
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							//adapter.notifyDataSetChanged();
						}
						else if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), "单据保存成功！");
							Intent resultIntent = new Intent();
							getActivity().setResult(1, resultIntent);
							getActivity().finish();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}
				
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
					EditText et=(EditText)searchDialog.findViewById(R.id.et_prodid);
					et.setText(result);
					searchProdId(searchDialog.findViewById(R.id.ll_content));
					searchDialog.dismiss();
				}
				
				//显示
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		default:
		    break;
		}
	}
	public SchoolBillDetailFragment() {
		
	}
	public SchoolBillDetailFragment(String title, String iunterfaceName) {
		this.title = title;
		this.interfaceName = iunterfaceName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_listview_fragment_huizong,
				container, false);
		RelativeLayout navibar=(RelativeLayout)view.findViewById(R.id.navibar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			navibar.setBackgroundColor(color);
		myListview = (ListView) view.findViewById(R.id.my_listview);
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		tvRight = (TextView) view.findViewById(R.id.tv_right);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		huizongLayout=(LinearLayout) view.findViewById(R.id.ll_huizong);
		tv_huizong1=(TextView) view.findViewById(R.id.tv_huizong1);
		tv_huizong2=(TextView) view.findViewById(R.id.tv_huizong2);
		bt_gotopay=(Button) view.findViewById(R.id.bt_gotopay);
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
				Intent resultIntent = new Intent();
				getActivity().setResult(1, resultIntent);
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
	private void closeInputMethod(View v) {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
	    	imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	/**
	 * 功能描述:初始化数据
	 * 
	 * @author shengguo 2014-4-17 下午5:18:06
	 * 
	 */
	private void initDate(int ifBottom) {
		tvTitle.setText(billDetail.getTitle());
		details = billDetail.getItems();
		
		if(billDetail.getOpertionType().equals("edit"))
		{
			tvRight.setText("新增");
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
						popSearchDlg();
				}
			});
			huizongLayout.setVisibility(View.VISIBLE);
			tv_huizong1.setText(billDetail.getHuizong1());
			tv_huizong2.setText(billDetail.getHuizong2());
			bt_gotopay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(details.size()==0)
						{
							AppUtility.showToastMsg(getContext(), "请先添加销售明细");
							return;
						}
						else
							getBillPayInfo();	
					}
				});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			huizongLayout.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
		adapter.notifyDataSetChanged();
		if(ifBottom==1)
			myListview.setSelection(myListview.getCount()-1);
		
		
		
	}
	public void getBillPayInfo() {
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		JSONObject queryJson=AppUtility.parseQueryStrToJson(interfaceName);
		
		try {
			jo.put("用户较验码", checkCode);
			Iterator it = queryJson.keys();
			while (it.hasNext()) {
                String key = (String) it.next();
                String value = queryJson.getString(key); 
                jo.put(key, value);
			}
			jo.put("action", "billPayInfo");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		CampusAPI.httpPost(jo, mHandler, 7);
	}
	private OnFocusChangeListener editChangeListener=new OnFocusChangeListener(){
       
        @Override
        public void onFocusChange(View arg0, boolean arg1) {
            EditText et = (EditText) arg0;
            if(arg1) {
                //Log.e("", "获得焦点"+detailItem.getId());
            } else {
            	updateQuling(et);
                //Log.e("", "失去焦点"+detailItem.getId());
                
            }
        }
         
    };
    private void updateQuling(EditText et)
    {
    	if(et.getText().toString().length()>0)
    	{
	    	try
	        {
	        	Double.parseDouble(et.getText().toString());
	        }
	        catch(NumberFormatException e)
	        {
	        	AppUtility.showToastMsg(getActivity(), "请输入数字");
	        	et.setText("");
	        	return;
	        }
    	}
    	JSONObject jo=(JSONObject)et.getTag();
    	double totalmoney=jo.optDouble("totalmoney");
    	String qulingStr=et_quling.getText().toString();
    	try
    	{
    		double quling=0;
	    	if(qulingStr.length()>0)
	    	{
	    		quling=Double.parseDouble(qulingStr);
	    	}
	    	tv_yingshou.setText("(应收:￥"+String.valueOf(totalmoney-quling)+")");
	    	String shoukuanStr=et_shoukuan.getText().toString();
	    	double shoukuan=0;
	    	if(shoukuanStr.length()>0)
	    	{
	    		shoukuan=Double.parseDouble(shoukuanStr);
	    	}
	    	if(rb_ifpay1.isChecked())
	    	{
	    		tv_yingshou.setHint("押金");
	    		tv_zhaoling.setText("(尚欠:￥"+String.valueOf(totalmoney-quling-shoukuan)+")");
	    	}
	    	else
	    	{
	    		tv_yingshou.setHint("收款");
	    		tv_zhaoling.setText("(找零:￥"+String.valueOf(shoukuan-(totalmoney-quling))+")");
	    	}
    	}
    	catch(NumberFormatException e)
        {
        	AppUtility.showToastMsg(getActivity(), "请输入数字");
        	return;
        }
    }
	private void popPayDlg(JSONObject jo)
	{
		String title="销售单结算";
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View textEntryView = inflater.inflate(R.layout.dialog_bill_pay,
			    null);
		textEntryView.setOnTouchListener(touchListener);
		TextView tv_totalmoney=(TextView)textEntryView.findViewById(R.id.tv_totalmoney);
		tv_totalmoney.setText("￥"+jo.optString("应收款"));
		RadioGroup rg_ifpay=(RadioGroup)textEntryView.findViewById(R.id.rg_ifpay);
		rb_ifpay0=(RadioButton)textEntryView.findViewById(R.id.rb_ifpay0);
		rb_ifpay1=(RadioButton)textEntryView.findViewById(R.id.rb_ifpay1);
		
		// 初始化控件
		spinner = (Spinner) textEntryView.findViewById(R.id.sp_accountid);
		// 建立数据源
		JSONArray banklist;
		String[] mItems;
		try {
			banklist = jo.getJSONArray("收款账户");
			if(banklist==null || banklist.length()==0)
			{
				AppUtility.showToastMsg(getActivity(), "收款账号为空，请先添加银行账号");
				return;
			}
			mItems = new String[banklist.length()];
			for(int i=0;i<banklist.length();i++)
			{
				JSONObject item=banklist.getJSONObject(i);
				mItems[i]=item.optString("name");
			}
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, mItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//绑定 Adapter到控件
			spinner.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			AppUtility.showToastMsg(getActivity(), e.getLocalizedMessage());
			return;
		}
		et_quling=(EditText)textEntryView.findViewById(R.id.et_quling);
		et_shoukuan=(EditText)textEntryView.findViewById(R.id.et_shoukuan);
		et_quling.setOnFocusChangeListener(editChangeListener);
		et_shoukuan.setOnFocusChangeListener(editChangeListener);
		et_quling.setTag(jo);
		et_shoukuan.setTag(jo);
		tv_yingshou=(TextView)textEntryView.findViewById(R.id.tv_yingshou);
		tv_zhaoling=(TextView)textEntryView.findViewById(R.id.tv_zhaoling);
		et_shoukuan.setOnEditorActionListener(new EditText.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) { 
					updateQuling(et_shoukuan);
				}
				return false;
			}  
			  
		  
		      
		});
		rg_ifpay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
            	updateQuling(et_quling);
            }
        });

        AlertDialog.Builder builder = new Builder(getActivity());
        
        builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				JSONObject jo1=(JSONObject)et_shoukuan.getTag();
				double totalmoney=jo1.optDouble("totalmoney");
				double shoukuan=Double.parseDouble(et_shoukuan.getText().toString());
				double quling=Double.parseDouble(et_quling.getText().toString());
				if(rb_ifpay0.isChecked())
				{
					if(shoukuan<(totalmoney-quling))
					{
						AppUtility.showToastMsg(getActivity(), "收款金额不能小于"+(totalmoney-quling));
						return;
					}
				}
				else
				{
					if(shoukuan>(totalmoney-quling))
					{
						AppUtility.showToastMsg(getActivity(), "押金不能大于"+(totalmoney-quling));
						return;
					}
				}
				JSONArray banklist = null;
				try {
					banklist = jo1.getJSONArray("收款账户");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
				JSONObject queryObj=AppUtility.parseQueryStrToJson(interfaceName);
				JSONObject jo = new JSONObject();
				try {
					jo.put("用户较验码", checkCode);
					Iterator it = queryObj.keys();
					while (it.hasNext()) {
		                String key = (String) it.next();
		                String value = queryObj.getString(key); 
		                jo.put(key, value);
					}
					jo.put("action", "saveDetail");
					jo.put("totalmoney", totalmoney);
					int index=spinner.getSelectedItemPosition();
					JSONObject accountObj=(JSONObject)banklist.get(index);
					jo.put("accountid", accountObj.optString("id"));
					jo.put("quling", quling);
					jo.put("shoukuan", shoukuan);
					if(rb_ifpay0.isChecked())
						jo.put("ifpay", 1);
					else
						jo.put("ifpay", 0);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				CampusAPI.httpPost(jo, mHandler, 8);
			}
			
		}).setNegativeButton("取消", null);
        searchDialog=builder.create();
        searchDialog.show();
		
	}
	private void popSearchDlg()
	{
		String title="搜索商品";
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View textEntryView = inflater.inflate(R.layout.dialog_text_entry,
			    null);
		
		Button bt=(Button)textEntryView.findViewById(R.id.bt_scancode);
		RadioButton rb1=(RadioButton)textEntryView.findViewById(R.id.radio1);
		RadioButton rb2=(RadioButton)textEntryView.findViewById(R.id.radio2);
		if(opertype==0)
			rb1.setChecked(true);
		else if(opertype==-1)
			rb2.setChecked(true);
        AlertDialog.Builder builder = new Builder(getActivity());
        
        builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				searchProdId(textEntryView);
			}
			
		}).setNegativeButton("取消", null);
        searchDialog=builder.create();
        bt.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
            	Intent intent = new Intent();
				intent.setClass(getActivity(), MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            	
            }
         });
        searchDialog.show();
        EditText et=(EditText)textEntryView.findViewById(R.id.et_prodid);
		TimeUtility.popSoftKeyBoard(getActivity(),et);
		
	}
	private void searchProdId(View textEntryView)
	{
		RadioButton rb1=(RadioButton)textEntryView.findViewById(R.id.radio1);
		RadioButton rb2=(RadioButton)textEntryView.findViewById(R.id.radio2);
		EditText et=(EditText)textEntryView.findViewById(R.id.et_prodid);
		if(rb1.isChecked())
			opertype=0;
		else if(rb2.isChecked())
			opertype=-1;
		else
			opertype=1;
		String searchValue=et.getText().toString();
		if(AppUtility.isNotEmpty(searchValue))
		{
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			JSONObject jo = new JSONObject();
			try {
				jo.put("用户较验码", checkCode);
				jo.put("function", "searchForValue");
				jo.put("searchType", "product");
				jo.put("searchValue", searchValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			CampusAPI.httpPost(jo, mHandler, 5);
		}
	}
	
	private void insertNewDetail(String searchValue) {
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject queryObj=AppUtility.parseQueryStrToJson(billDetail.getNewUrl());
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			Iterator it = queryObj.keys();
			while (it.hasNext()) {
                String key = (String) it.next();
                String value = queryObj.getString(key); 
                jo.put(key, value);
			}
			jo.put("searchValue", searchValue);
			jo.put("opertype", opertype);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 6);
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
					insertNewDetail(ID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			} 
		} 
		).setNegativeButton("取消", null) .show();
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
	private OnTouchListener touchListener= new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.setFocusable(true);
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			closeInputMethod(v);
			return false;
		}
		
	};
	/**
	 * 功能描述:获取通知内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getAchievesItem() {
		showProgress(true);
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		JSONObject queryJson=AppUtility.parseQueryStrToJson(interfaceName);
		
		try {
			jo.put("用户较验码", checkCode);
			Iterator it = queryJson.keys();
			while (it.hasNext()) {
                String key = (String) it.next();
                String value = queryJson.getString(key); 
                jo.put(key, value);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		CampusAPI.httpPost(jo, mHandler, 0);
	}

	@SuppressLint("NewApi")
	class AchieveAdapter extends BaseAdapter {

		
		@Override
		public int getCount() {
			return details.size();
		}

		@Override
		public Object getItem(int position) {
			return details.get(position);
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
						R.layout.bill_detail_item, parent, false);
				holder = new ViewHolder();
				holder.iv_leftImage=(ImageView)convertView.findViewById(R.id.iv_leftImage);
				holder.tv_up = (TextView) convertView.findViewById(R.id.tv_up);
				holder.tv_bottom = (TextView) convertView.findViewById(R.id.tv_bottom);
				holder.tv_opertype = (TextView) convertView.findViewById(R.id.tv_opertype);
				holder.hiddenBtn=(ImageView)convertView.findViewById(R.id.hiddenBtn);
				holder.ly_hidden=(LinearLayout)convertView.findViewById(R.id.ly_hidden);
				holder.et_num=(EditText)convertView.findViewById(R.id.et_num);
				holder.iv_up=(ImageView)convertView.findViewById(R.id.iv_up);
				holder.iv_down=(ImageView)convertView.findViewById(R.id.iv_down);
				holder.ll_editSection=(LinearLayout)convertView.findViewById(R.id.ll_editSection);
				holder.tv_operSign = (TextView) convertView.findViewById(R.id.tv_operSign);
				convertView.setTag(holder);
				
				convertView.setOnTouchListener(touchListener);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AQuery aq = new AQuery(convertView);
			final BillDetailItem detailItem = (BillDetailItem) getItem(position);
			
			if(detailItem.getProdImage()!=null && detailItem.getProdImage().length()>0)
				aq.id(holder.iv_leftImage).image(detailItem.getProdImage(),false,true);
			else
				aq.id(holder.iv_leftImage).image(R.drawable.gift);
			holder.iv_leftImage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(),ShowPersonInfo.class);
					intent.putExtra("studentId", detailItem.getProdid());
					intent.putExtra("userImage", detailItem.getProdImage());
					intent.putExtra("userType", "-1");
					startActivity(intent);
				}
				
			});
			holder.tv_up.setText(detailItem.getTitle());
			if(detailItem.getOpertype()!=null)
			{
				holder.tv_opertype.setVisibility(View.VISIBLE);
				holder.tv_opertype.setText(detailItem.getOpertype());
				if(detailItem.getOpertype().equals("退"))
				{
					holder.tv_opertype.setBackground(getResources().getDrawable(R.drawable.school_achievement_red));
				}
				else if(detailItem.getOpertype().equals("赠"))
				{
					holder.tv_opertype.setBackground(getResources().getDrawable(R.drawable.school_achievement_blue));
				}
				else
				{
					holder.tv_opertype.setBackground(getResources().getDrawable(R.drawable.school_achievement_bg));
				}
			}
			else
				holder.tv_opertype.setVisibility(View.GONE);
			holder.tv_bottom.setText(detailItem.getDetail());
			
			if(billDetail.getOpertionType().equals("edit"))
			{
				holder.ll_editSection.setVisibility(View.VISIBLE);
				holder.et_num.setText(String.valueOf(detailItem.getNum()));
				holder.et_num.setOnFocusChangeListener(new OnFocusChangeListenerImpl(position));
				
				if(detailItem.getHiddenBtnUrl()!=null && detailItem.getHiddenBtnUrl().length()>0)
				{
					
					aq.id(holder.hiddenBtn).image(detailItem.getHiddenBtn(),false,true);
					holder.ly_hidden.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							doRequestUrl(detailItem.getHiddenBtnUrl());
						}
						
					});
				}
				convertView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						if(detailItem.getHiddenBtn()!=null && detailItem.getHiddenBtn().length()>0)
						{
							ViewHolder holder = (ViewHolder) v.getTag();
							if(holder.ly_hidden.getVisibility()==View.GONE)
							{
								holder.ly_hidden.setVisibility(View.VISIBLE);
								Timer timer = new Timer(); 
								timer.schedule(new Task(holder), 3 * 1000);
								holder.ly_hidden.setTag(timer);
							}
							else
							{
								holder.ly_hidden.setVisibility(View.GONE);
								Timer timer=(Timer)holder.ly_hidden.getTag();
								if(timer!=null)
									timer.cancel();
							}
						}
						 
						
					}
					
				});
				holder.iv_up.setTag(holder);
				holder.iv_up.setOnTouchListener(touchListener);
				holder.iv_up.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
					    
						ViewHolder holder=(ViewHolder) v.getTag();
						try
						{
							int num=Integer.parseInt(holder.et_num.getText().toString());
							//holder.et_num.setText(String.valueOf(num+1));
							updateAmount(detailItem.getId(),num+1);
						}
						catch(NumberFormatException e)
						{
							AppUtility.showToastMsg(getActivity(), "数量必须是整数");
						}
						
					}
					
				});
				holder.iv_down.setTag(holder);
				holder.iv_down.setOnTouchListener(touchListener);
				holder.iv_down.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
					    closeInputMethod(v);
						ViewHolder holder=(ViewHolder) v.getTag();
						try
						{
							int num=Integer.parseInt(holder.et_num.getText().toString());
							//holder.et_num.setText(String.valueOf(num-1));
							updateAmount(detailItem.getId(),num-1);
						}
						catch(NumberFormatException e)
						{
							AppUtility.showToastMsg(getActivity(), "数量必须是整数");
						}
						
					}
					
				});
			}
			else
			{
				holder.ll_editSection.setVisibility(View.GONE);
				holder.tv_operSign.setText("X "+detailItem.getNum());
			}
			
			
			
			
			return convertView;
		}
		class Task extends TimerTask {
			private ViewHolder holder;
			public Task(ViewHolder h)
			{
				holder=h;
			}
			public void run()
			{
				Message msg = new Message();
				msg.what = 2;
				msg.obj = holder.ly_hidden;
				mHandler.sendMessage(msg);   
			}
		}
		class ViewHolder {
			ImageView iv_leftImage;
			TextView tv_up;
			TextView tv_opertype;
			TextView tv_bottom;
			ImageView hiddenBtn;
			LinearLayout ly_hidden;
			EditText et_num;
			ImageView iv_up;
			ImageView iv_down;
			LinearLayout ll_editSection;
			TextView tv_operSign;
		}
		private class OnFocusChangeListenerImpl implements OnFocusChangeListener {
	        private int position;
	        public OnFocusChangeListenerImpl(int position) {
	            this.position = position;
	        }
	        @Override
	        public void onFocusChange(View arg0, boolean arg1) {
	            EditText et = (EditText) arg0;
	            BillDetailItem detailItem = (BillDetailItem) getItem(position);
	            if(arg1) {
	                //Log.e("", "获得焦点"+detailItem.getId());
	            } else {
	            	
	                //Log.e("", "失去焦点"+detailItem.getId());
	                try
	                {
	                	int num=Integer.parseInt(et.getText().toString());
	                	if(detailItem.getNum()!=num)
	                	{
	                		updateAmount(detailItem.getId(),num);
	                		//detailItem.setNum(num);
	                	}
	                }
	                catch(NumberFormatException e)
	                {
	                	AppUtility.showToastMsg(getActivity(), "请输入整型数字");
	                	et.setText(String.valueOf(detailItem.getNum()));
	                }
	                
	            }
	        }
	         
	    }
	}
	
	private void updateAmount(int id,int num)
	{
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject queryJson=AppUtility.parseQueryStrToJson(interfaceName);
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			Iterator it = queryJson.keys();
			while (it.hasNext()) {
	            String key = (String) it.next();
	            String value = queryJson.getString(key); 
	            jo.put(key, value);
			}
			jo.put("action", "updateAmount");
			jo.put("detailId", id);
			jo.put("num", num);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 6);
	}
	private void doRequestUrl(String url)
	{
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				"删除此条记录。。。");
		dialog.show();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject queryObj=AppUtility.parseQueryStrToJson(url);
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
		CampusAPI.httpPost(jo, mHandler, 1);
	}
	
}

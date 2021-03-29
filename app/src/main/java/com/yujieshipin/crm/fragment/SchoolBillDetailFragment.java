package com.yujieshipin.crm.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.yujieshipin.crm.BuildConfig;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.CallClassActivity;
import com.yujieshipin.crm.activity.CaptureActivity;
import com.yujieshipin.crm.activity.ImagesActivity;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.adapter.PopColorListAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.BillDetail;
import com.yujieshipin.crm.entity.BillDetailItem;
import com.yujieshipin.crm.entity.Question;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.HttpMultipartPost;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;
import com.yujieshipin.crm.widget.CustomCircleProgressBar;
import com.yujieshipin.crm.widget.SegmentedGroup;
import com.yujieshipin.crm.widget.XListView;

import static com.umeng.analytics.AnalyticsConfig.getLocation;

/**
 * 成绩详情
 */
public class SchoolBillDetailFragment extends Fragment{
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
	private Dialog dialog,searchDialog;
	private AlertDialog popColorDialog;
	private List<BillDetailItem> details = new ArrayList<BillDetailItem>();
	private int opertype=1;
	private final static int SCANNIN_GREQUEST_CODE = 2;
	private EditText et_quling,et_shoukuan;
	private RadioButton rb_ifpay0,rb_ifpay1;
	private Spinner spinner;
	private int billzhekou=0;
    private List<RadioButton> radioBtnList;
    private SegmentedGroup segmented2;
	private static final int REQUEST_CODE_TAKE_PICTURE = 3;// //设置图片操作的标志
	private static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	PopColorListAdapter colorAdapter;
	private EditText lastFocusEt;
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
							initDate(msg.arg1,msg.arg2);
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
								if(billDetail.isAllowAddBatch()) {
									popColorSelect(billDetail.getBillid(), "",jo.optJSONArray("value"),jo.optBoolean("edit"));
								}
								else {
									JSONArray userArray = jo.optJSONArray("value");
									popSelectList(userArray);
								}
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
							//AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							AppUtility.showErrorDialog(getActivity(), "提示",jo.optString("errorMsg"));
							adapter.notifyDataSetChanged();
						}
						else 
						{
							Message msg1 = new Message();
							msg1.what = 0;
							msg1.obj = jo.optJSONObject("detailList");
							msg1.arg1=jo.optInt("toBottom");
							msg1.arg2=jo.optInt("popcolor");
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
			case 9://获取某一条明细记录的颜色库存
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if(res.equals("失败")){
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							//adapter.notifyDataSetChanged();
						}
						else {
							popColorSelect(jo.optInt("detailid"), jo.optString("prodid"),jo.optJSONArray("colorlist"),jo.optBoolean("edit"));
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
					}
				}

				break;
			case 10://颜色图片上传
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {

					try {
						JSONObject jo = new JSONObject(result);
						if ("成功".equals(jo.optString("result"))) {
							int positon=jo.optInt("position");
							if(popColorDialog.isShowing()) {
								JSONObject obj = (JSONObject) colorAdapter.getMlist().get(positon);
								obj.put("colorimage", jo.optString("文件地址"));
								colorAdapter.notifyDataSetChanged();
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 11://颜色图片删除
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) {

					try {
						JSONObject jo = new JSONObject(result);
						if ("成功".equals(jo.optString("result"))) {
							int positon=jo.optInt("position");
							colorAdapter.clearCacheFile(jo.optString("imageName"));
							if(popColorDialog.isShowing()) {
								JSONObject obj = (JSONObject) colorAdapter.getMlist().get(positon);
								obj.put("colorimage", "");
								colorAdapter.notifyDataSetChanged();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 12:
				EditText v1=(EditText)msg.obj;
				if(v1.isFocused())
				{
					Timer timer = new Timer();
					timer.schedule(new Task(v1,12), 3 * 1000);
				}
				else if(v1.getText().length()==0)
					v1.setVisibility(View.GONE);

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
				if(result!=null && result.length()>0)
				{
					insertNewDetail(result);
				}
				//显示
				//mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		case REQUEST_CODE_TAKE_CAMERA: // 拍照返回
			uploadFile(new File(colorAdapter.getPicturePath()));
			break;
		case REQUEST_CODE_TAKE_PICTURE:
			if (data != null) {
				Uri uri = data.getData();
				String[] pojo  = { MediaStore.Images.Media.DATA };
				CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, pojo, null,null, null);
				Cursor cursor = cursorLoader.loadInBackground();
				cursor.moveToFirst();
				String picturePath = cursor.getString(cursor.getColumnIndex(pojo[0]));
				String tempPath =FileUtility.getRandomSDFileName("jpg");
				if(FileUtility.copyFile(picturePath,tempPath))
					uploadFile(new File(tempPath));
				else
					AppUtility.showErrorToast(getActivity(), "向SD卡复制文件出错");
			}
				break;
		default:
		    break;
		}
	}

	public static final Fragment newInstance(String title, String interfaceName){
		Fragment fragment = new SchoolBillDetailFragment();
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
        segmented2 = (SegmentedGroup) view.findViewById(R.id.segmentedGroup2);
        segmented2.setTintColor(Color.DKGRAY);
        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                getAchievesItem();
            }
        });
		radioBtnList=new ArrayList<RadioButton>();
        radioBtnList.add((RadioButton) view.findViewById(R.id.button21));
		RadioButton btn22=(RadioButton) view.findViewById(R.id.button22);
		btn22.setVisibility(View.GONE);
        radioBtnList.add((RadioButton) view.findViewById(R.id.button23));
        segmented2.setVisibility(View.GONE);

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
		myListview.setOnTouchListener(touchListener);
		tvRight.setOnTouchListener(touchListener);
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
	private void popInputDelay(final EditText et)
	{
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				et.requestFocus();
				//et.setSelection(et.getText().length());
				//InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				//imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
			}
		},100);
	}
	/**
	 * 功能描述:初始化数据
	 * 
	 * @author shengguo 2014-4-17 下午5:18:06
	 * 
	 */
	private void initDate(int ifBottom,int hascolor) {
		tvTitle.setText(billDetail.getTitle());
		details = billDetail.getItems();
		for(int i=0;i<radioBtnList.size();i++)
        {
            radioBtnList.get(i).setVisibility(View.GONE);
        }
		if(billDetail.getSegmentedArr()!=null) {
            segmented2.setVisibility(View.VISIBLE);
			tvTitle.setVisibility(View.GONE);
            for(int i=0;i<billDetail.getSegmentedArr().length();i++)
            {
                if(i==2) break;
                JSONObject obj=billDetail.getSegmentedArr().optJSONObject(i);
                if(obj!=null) {
                    radioBtnList.get(i).setText(obj.optString("title"));
                    radioBtnList.get(i).setVisibility(View.VISIBLE);
                    if(i==billDetail.getSegmentindex())
						radioBtnList.get(i).setChecked(true);
                    else
						radioBtnList.get(i).setChecked(false);
                }
            }
        }
		else {
			segmented2.setVisibility(View.GONE);
			tvTitle.setVisibility(View.VISIBLE);
		}
		if(billDetail.getOpertionType().equals("edit"))
		{
		    if(myListview.getFooterViewsCount()==0) {
				View ll_foot = inflater.inflate(R.layout.bill_detail_bottom,null);
		    	Button bottomBtn=ll_foot.findViewById(R.id.btn_startscan);
				bottomBtn.setOnTouchListener(touchListener);
                bottomBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (AppUtility.checkPermission(getActivity(), 6, Manifest.permission.CAMERA))
                                openScanCode();
                        } else
                            openScanCode();

                    }
                });
                EditText et_zhekouall=ll_foot.findViewById(R.id.et_zhekouall);
                if(billDetail.isShouzhekouall())
				{
					et_zhekouall.setVisibility(View.VISIBLE);
					et_zhekouall.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							v.setOnFocusChangeListener(zhekouChangeListener);
							return false;
						}
					});
				}
                else {
					et_zhekouall.setVisibility(View.GONE);
					ll_foot.findViewById(R.id.tx_percent).setVisibility(View.GONE);
				}

                myListview.addFooterView(ll_foot);
            }
			tvRight.setText("新增");
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JSONObject queryJson=AppUtility.parseQueryStrToJson(billDetail.getNewUrl());
					if(queryJson.optString("searchValueDefault").length()>0)
						searchProdId(queryJson.optString("searchValueDefault"));
					else
						popSearchDlg();
				}
			});
			bt_gotopay.setVisibility(View.VISIBLE);
			tv_huizong1.setText(billDetail.getHuizong1());
			tv_huizong2.setText(billDetail.getHuizong2());
			bt_gotopay.setText(billDetail.getRightbottomBtn());
			bt_gotopay.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(details.size()==0)
						{
							AppUtility.showToastMsg(getContext(), "请先添加明细");
							return;
						}
						else {
							if(billDetail.getRightbottomBtn().equals("结算"))
								getBillPayInfo();
							else
								savebill();
						}
					}
				});
		}
		else if(billDetail.getOpertionType().equals("caigouedit"))
		{
			tvRight.setText("新增");
			tvRight.setVisibility(View.VISIBLE);
			lyRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent =new Intent(getActivity(), SchoolDetailActivity.class);
					intent.putExtra("templateName", "调查问卷");
					intent.putExtra("interfaceName", billDetail.getNewUrl());
					intent.putExtra("title", title);
					startActivityForResult(intent,101);
				}
			});
			bt_gotopay.setVisibility(View.VISIBLE);
			tv_huizong1.setText(billDetail.getHuizong1());
			if(AppUtility.isNotEmpty(billDetail.getHuizong2()))
				tv_huizong2.setText(billDetail.getHuizong2());
			else
				tv_huizong2.setVisibility(View.GONE);
			bt_gotopay.setText(billDetail.getRightbottomBtn());
			bt_gotopay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					savebill();
				}
			});
		}
		else if(billDetail.getOpertionType().equals("shoukuan"))
		{
			tv_huizong1.setText(billDetail.getHuizong1());
			tv_huizong2.setText(billDetail.getHuizong2());
			bt_gotopay.setVisibility(View.VISIBLE);
			bt_gotopay.setText(billDetail.getRightbottomBtn());
			bt_gotopay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(details.size()==0)
					{
						AppUtility.showToastMsg(getContext(), "请先添加明细");
						return;
					}
					else {
						getBillPayInfo();
					}
				}
			});
		}
		else if(billDetail.getOpertionType().equals("chuku"))
		{
			tv_huizong1.setText(billDetail.getHuizong1());
			tv_huizong2.setText(billDetail.getHuizong2());
			bt_gotopay.setVisibility(View.VISIBLE);
			bt_gotopay.setText(billDetail.getRightbottomBtn());
			bt_gotopay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(details.size()==0)
					{
						AppUtility.showToastMsg(getContext(), "请先添加明细");
						return;
					}
					else {
						savebill();
					}
				}
			});
		}
		else
		{
			tvRight.setVisibility(View.GONE);
			tv_huizong1.setText(billDetail.getHuizong1());
			tv_huizong2.setText(billDetail.getHuizong2());
			bt_gotopay.setVisibility(View.GONE);
			lyRight.setOnClickListener(null);
		}
		adapter.notifyDataSetChanged();
		if(ifBottom==1)
			myListview.setSelection(myListview.getCount()-1);
		if(hascolor==1)//弹出颜色选择列表
		{
			getDetailColorKucun(adapter.getCount()-1);
		}
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
	public void getDetailColorKucun(int position) {
		BillDetailItem detailItem=billDetail.getItems().get(position);
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
			jo.put("action", "detailColorKucun");
			jo.put("detailId",detailItem.getId());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		CampusAPI.httpPost(jo, mHandler, 9);
	}
	private OnFocusChangeListener editChangeListener=new OnFocusChangeListener(){
       
        @Override
        public void onFocusChange(View arg0, boolean arg1) {
            EditText et = (EditText) arg0;
            if(arg1) {
                //Log.e("", "获得焦点"+detailItem.getId());
            } else {
            	updateQuling(et,false);
                //Log.e("", "失去焦点"+detailItem.getId());
                
            }
        }
         
    };
	private OnFocusChangeListener zhekouChangeListener=new OnFocusChangeListener(){

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			EditText et = (EditText) arg0;
			if(arg1) {
				//Log.e("", "获得焦点"+detailItem.getId());
			} else {
				try {
					if(et.getText().toString().length()>0) {
						int zhekou = Integer.parseInt(et.getText().toString());
						if(zhekou!=billzhekou) {
							updateZhekouAll(zhekou);
							billzhekou=zhekou;
							et.setOnFocusChangeListener(null);
						}
					}
				}
				catch(NumberFormatException e)
				{
					AppUtility.showToastMsg(getActivity(), "请输入整型数字");
				}
				//Log.e("", "失去焦点"+detailItem.getId());
			}
		}

	};
    private boolean updateQuling(EditText et,Boolean forsubmit)
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
	        	return false;
	        }
    	}
    	JSONObject jo=(JSONObject)et.getTag();
		double maxOdd=jo.optDouble("maxOdd");
		int jifenBaseNum=jo.optInt("jifenBaseNum");
		double jifenToMoneyRate=jo.optDouble("jifenToMoneyRate");

    	double totalmoney=jo.optDouble("totalmoney");
    	String qulingStr=et_quling.getText().toString();
    	try
    	{
    		double quling=0;
	    	if(qulingStr.length()>0)
	    	{
	    		quling=Double.parseDouble(qulingStr);
	    	}
	    	if(maxOdd>0 && Math.abs(quling)>maxOdd)
			{
				AppUtility.showToastMsg(getActivity(), "最大去零不能超过"+maxOdd);
				et_quling.setText("");
				return false;
			}
			else if(maxOdd==0 && Math.abs(quling)>Math.abs(totalmoney/10))
			{
				AppUtility.showToastMsg(getActivity(), "最大去零不能超过总金额的十分之一");
				et_quling.setText("");
				return false;
			}
	    	tv_yingshou.setText("(应收:￥"+AppUtility.formatNumber(totalmoney-quling)+")");
	    	String shoukuanStr=et_shoukuan.getText().toString();
	    	double shoukuan=0;
	    	if(shoukuanStr.length()>0)
	    	{
	    		shoukuan=Double.parseDouble(shoukuanStr);
	    	}
	    	if(rb_ifpay1.isChecked())
	    	{
	    		if(et.equals(et_shoukuan) || forsubmit) {
					if (totalmoney - quling >= 0) {
						if (shoukuan > (totalmoney - quling) || shoukuan < 0) {
							AppUtility.showToastMsg(getActivity(), "押金不能小于0,或大于" + (totalmoney - quling));
							return false;
						}
					} else {
						if (shoukuan < (totalmoney - quling) || shoukuan > 0) {
							AppUtility.showToastMsg(getActivity(), "押金不能大于0,或小于" + (totalmoney - quling));
							return false;
						}
					}
				}
	    		tv_yingshou.setHint("押金");
	    		tv_zhaoling.setText("(尚欠:￥"+String.format("%.2f", totalmoney-quling-shoukuan)+")");
	    	}
	    	else
	    	{
				if(et.equals(et_shoukuan) || forsubmit) {
					if (totalmoney - quling >= 0) {
						if (shoukuan < (totalmoney - quling)) {
							AppUtility.showToastMsg(getActivity(), "收款金额不能小于" + (totalmoney - quling));
							return false;
						}
					}
				}
	    		tv_yingshou.setHint("收款");
	    		tv_zhaoling.setText("(找零:￥"+String.format("%.2f",shoukuan-(totalmoney-quling))+")");
	    	}
    	}
    	catch(NumberFormatException e)
        {
        	AppUtility.showToastMsg(getActivity(), "请输入数字");
        	return false;
        }
        return true;
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
					updateQuling(et_shoukuan,false);
				}
				return false;
			}  
			  
		  
		      
		});
		rg_ifpay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
            	updateQuling(et_quling,false);
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
				if(et_shoukuan.getText().toString().length()==0)
					et_shoukuan.setText("0");
				if(et_quling.getText().toString().length()==0)
					et_quling.setText("0");
				double shoukuan=Double.parseDouble(et_shoukuan.getText().toString());
				double quling=Double.parseDouble(et_quling.getText().toString());
				if(!updateQuling(et_quling,true))
					return;
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
		EditText et=(EditText)textEntryView.findViewById(R.id.et_quling);
		TimeUtility.popSoftKeyBoard(getActivity(),et);
	}
	private void popSearchDlg()
	{
		String title="搜索商品";
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View textEntryView = inflater.inflate(R.layout.dialog_text_entry,
			    null);
		
		Button bt=(Button)textEntryView.findViewById(R.id.bt_scancode);
		RadioGroup rg=(RadioGroup)textEntryView.findViewById(R.id.rg_opertype);
		RadioButton rb0=(RadioButton)textEntryView.findViewById(R.id.radio0);
		RadioButton rb1=(RadioButton)textEntryView.findViewById(R.id.radio1);
		RadioButton rb2=(RadioButton)textEntryView.findViewById(R.id.radio2);
		List<RadioButton> radioList = new ArrayList<RadioButton>();
		radioList.add(rb0);
		radioList.add(rb1);
		radioList.add(rb2);
		if(billDetail.getOpertype().length()>0) {
			rg.setVisibility(View.VISIBLE);
			String opertypeStr[] = billDetail.getOpertype().split(",");
			if(opertypeStr.length==3)
			{
				for(int i=0;i<3;i++)
				{
					if(opertypeStr[i].length()>0) {
						radioList.get(i).setText(opertypeStr[i]);
						radioList.get(i).setVisibility(View.VISIBLE);
					}
					else
						radioList.get(i).setVisibility(View.GONE);
				}
			}
		}
		else
			rg.setVisibility(View.GONE);

		if(opertype==0)
			rb1.setChecked(true);
		else if(opertype==-1)
			rb2.setChecked(true);
		RadioGroup radioGroup = (RadioGroup) textEntryView.findViewById(R.id.rg_opertype);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.radio1:
						opertype=0;
						break;
					case R.id.radio2:
						opertype=-1;
						break;
					default:
						opertype=1;
						break;
				}
			}
		});
        AlertDialog.Builder builder = new Builder(getActivity());
        
        builder.setTitle(title).setView(textEntryView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText et=(EditText)textEntryView.findViewById(R.id.et_prodid);
				String searchValue=et.getText().toString();
				searchProdId(searchValue);
			}
			
		}).setNegativeButton("取消", null);
        searchDialog=builder.create();
        bt.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {

				if (Build.VERSION.SDK_INT >= 23) {
					if (AppUtility.checkPermission(getActivity(), 6, Manifest.permission.CAMERA))
						openScanCode();
				} else
					openScanCode();
				searchDialog.dismiss();
            }
         });
        searchDialog.show();
        //EditText et=(EditText)textEntryView.findViewById(R.id.et_prodid);
		//TimeUtility.popSoftKeyBoard(getActivity(),et);
		
	}
	private void popColorSelect(final int detailid,String prodid,JSONArray list,Boolean bedit)
	{
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View layout = inflater.inflate(R.layout.dialog_listview_single, null);
		final ListView myListView = (ListView) layout.findViewById(R.id.colorselectlist);
		colorAdapter = new PopColorListAdapter(this, list,prodid,bedit);
		myListView.setAdapter(colorAdapter);
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setView(layout).setPositiveButton("确定",null).setNegativeButton("取消", null);
		popColorDialog=builder.create();
		//searchDialog = builder.create();
		popColorDialog.show();
		popColorDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = myListView.findFocus();
				if (view != null && view instanceof EditText) {
					view.clearFocus();
					//return;
				}
				new Handler().postDelayed(new Runnable(){
					public void run() {
						String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
						JSONObject queryJson = AppUtility.parseQueryStrToJson(interfaceName);
						JSONObject jsonObj = new JSONObject();
						try {
							jsonObj.put("用户较验码", checkCode);
							Iterator it = queryJson.keys();
							while (it.hasNext()) {
								String key = (String) it.next();
								String value = queryJson.getString(key);
								jsonObj.put(key, value);
							}
							jsonObj.put("颜色数组", colorAdapter.getMlist().toString());
							jsonObj.put("action", "updateAmountColor");
							jsonObj.put("detailid", detailid);
							jsonObj.put("segmentindex",billDetail.getSegmentindex());
							jsonObj.put("opertype", opertype);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						CampusAPI.httpPost(jsonObj, mHandler, 6);
						popColorDialog.dismiss();
					}
				}, 500);
				;
			}
		});
	}


	private void openScanCode()
	{
		Intent intent = new Intent();
		intent.setClass(getActivity(), CaptureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	}
	private void searchProdId(String searchValue)
	{
		if(AppUtility.isNotEmpty(searchValue))
		{
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			JSONObject jo = new JSONObject();
			try {
				jo.put("用户较验码", checkCode);
				jo.put("function", "searchForValue");
				jo.put("searchType", "product");
				jo.put("searchValue", searchValue);
				jo.put("supplyid",billDetail.getLimitSupplyId());
				jo.put("allowAddBatch",billDetail.isAllowAddBatch()?1:0);
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
			jo.put("segmentindex",billDetail.getSegmentindex());
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
			lastFocusEt=null;
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
		if(billDetail!=null && billDetail.getSegmentedArr()!=null) {
            if (segmented2.getCheckedRadioButtonId() == R.id.button21 && billDetail.getSegmentedArr().optJSONObject(0) != null)
				interfaceName = billDetail.getSegmentedArr().optJSONObject(0).optString("url");
            if (segmented2.getCheckedRadioButtonId() == R.id.button23 && billDetail.getSegmentedArr().optJSONObject(1) != null)
				interfaceName = billDetail.getSegmentedArr().optJSONObject(1).optString("url");
        }
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
	private void savebill()
	{
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
			jo.put("action","saveDetail");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 8);
	}
	@SuppressLint("NewApi")
	class AchieveAdapter extends BaseAdapter {

		private HashMap<Integer, AchieveAdapter.OnFocusChangeListenerImpl> listenerhm=new HashMap();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
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
				holder.et_zhekou=(EditText)convertView.findViewById(R.id.et_zhekou);
				holder.ll_editSection=(LinearLayout)convertView.findViewById(R.id.ll_editSection);
				holder.tv_operSign = (TextView) convertView.findViewById(R.id.tv_operSign);
				holder.pb_right=(CustomCircleProgressBar) convertView.findViewById(R.id.pb_right);
				holder.tv_right = (TextView) convertView.findViewById(R.id.tv_right);
				holder.et_memo=(EditText)convertView.findViewById(R.id.et_memo);
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
					if(detailItem.getProdid()!=null && detailItem.getProdid().length()>0) {
						Intent intent = new Intent(getActivity(), ShowPersonInfo.class);
						intent.putExtra("studentId", detailItem.getProdid());
						intent.putExtra("userImage", detailItem.getProdImage());
						intent.putExtra("userType", "-1");
						startActivity(intent);
					}
					else
					{
						Intent intent = new Intent(getActivity(),
								ImagesActivity.class);
						ArrayList<String> picturePaths=new ArrayList<String>();
						picturePaths.add(detailItem.getProdImage());
						intent.putStringArrayListExtra("pics",
								(ArrayList<String>) picturePaths);
						if(detailItem.getPicDesc().length()>0)
						{
							ArrayList<String> pictureNames=new ArrayList<String>();
							pictureNames.add(detailItem.getPicDesc());
							intent.putStringArrayListExtra("txts",
									(ArrayList<String>) pictureNames);

						}
						startActivity(intent);
					}
				}
				
			});
			holder.tv_up.setText(detailItem.getTitle());
			if(detailItem.getOpertype()!=null && detailItem.getOpertype().length()>0)
			{
				holder.tv_opertype.setVisibility(View.VISIBLE);
				holder.tv_opertype.setText(detailItem.getOpertype());
				if(detailItem.getOpertype().equals("退") || detailItem.getOpertype().equals("亏") || detailItem.getOpertype().equals("返"))
				{
					holder.tv_opertype.setBackground(getResources().getDrawable(R.drawable.school_achievement_red));
				}
				else if(detailItem.getOpertype().equals("赠") || detailItem.getOpertype().equals("平") || detailItem.getOpertype().equals("配件"))
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
			
			if(billDetail.getOpertionType().equals("edit") || billDetail.getOpertionType().equals("caigouedit") || billDetail.getOpertionType().equals("chuku")) {
				holder.ll_editSection.setVisibility(View.VISIBLE);
				if (detailItem.isShowzhekou()) {
					holder.et_zhekou.setVisibility(View.VISIBLE);
					holder.tv_operSign.setText("%");
				} else {
					holder.tv_operSign.setText("");
					holder.et_zhekou.setVisibility(View.GONE);
				}
				holder.et_zhekou.setEnabled(detailItem.isEditzhekou());
				holder.et_zhekou.setText(String.valueOf(detailItem.getZhekou()));
				holder.et_num.setText(String.valueOf(detailItem.getNum()));
				holder.et_memo.setText(detailItem.getBeizhu());
				if(holder.et_memo.getText().length()>0)
					holder.et_memo.setVisibility(View.VISIBLE);
				else
					holder.et_memo.setVisibility(View.GONE);
				OnFocusChangeListenerImpl listener = listenerhm.get(position);
				if (listener == null) {
					listener = new OnFocusChangeListenerImpl(position);
					listenerhm.put(position, listener);
				}
				holder.et_num.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						v.setOnFocusChangeListener(listenerhm.get(position));
						return false;
					}
				});
				holder.et_zhekou.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						v.setOnFocusChangeListener(listenerhm.get(position));
						return false;
					}
				});
				holder.et_memo.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						v.setOnFocusChangeListener(listenerhm.get(position));
						return false;
					}
				});
				holder.et_num.setCursorVisible(detailItem.isEditNum());      //设置输入框中的光标不可见
				holder.et_num.setFocusable(detailItem.isEditNum());           //无焦点
				holder.et_num.setFocusableInTouchMode(detailItem.isEditNum());
				if(!detailItem.isEditNum()) {
					holder.et_num.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							getDetailColorKucun(position);
						}
					});
					holder.et_num.setTextColor(Color.RED);
				}
				else {
					holder.et_num.setOnClickListener(null);
					holder.et_num.setTextColor(Color.BLACK);
				}
				holder.et_num.setGravity(Gravity.CENTER);
				holder.et_num.setTag(position);
				holder.et_zhekou.setTag(position);
				holder.et_memo.setTag(position);
				if (lastFocusEt!=null && lastFocusEt.getTag().equals(position))
				{
					EditText et=(EditText) convertView.findViewById(lastFocusEt.getId());
					if(et!=null) {
						et.setOnFocusChangeListener(listenerhm.get(position));
						if(detailItem.isEditNum()) {
							popInputDelay(et);
						}

					}
				}
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
								timer.schedule(new Task(holder.ly_hidden,2), 3 * 1000);
								holder.ly_hidden.setTag(timer);
							}
							/*
							else
							{
								holder.ly_hidden.setVisibility(View.GONE);
								Timer timer=(Timer)holder.ly_hidden.getTag();
								if(timer!=null)
									timer.cancel();
							}

							*/
						}
						if(detailItem.isShowmemo())
						{
							ViewHolder holder = (ViewHolder) v.getTag();
							if(holder.et_memo.getVisibility()==View.GONE)
							{
								holder.et_memo.setVisibility(View.VISIBLE);
								Timer timer = new Timer();
								timer.schedule(new Task(holder.et_memo,12), 3 * 1000);
							}

						}
						 
						
					}
					
				});

			}
			else
			{
				holder.ll_editSection.setVisibility(View.GONE);
				holder.tv_operSign.setText("X "+detailItem.getNum());
			}
			if(detailItem.getProgress()>=0) {
				holder.pb_right.setVisibility(View.VISIBLE);

				if(detailItem.getProgress()>=100)
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.subject_single));
				else if(detailItem.getProgress()>=80)
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.edit_color));
				else if(detailItem.getProgress()>=60)
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.blue_color_shade));
				else if(detailItem.getProgress()>=40)
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.blue_color_2));
				else if(detailItem.getProgress()>=20)
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.moban_color_brown));
				else
					holder.pb_right.setOutsideColor(getActivity().getResources().getColor(R.color.red_color));
				holder.pb_right.setProgressText(detailItem.getProgressText());
				holder.pb_right.setProgress(detailItem.getProgress());

			}
			else
				holder.pb_right.setVisibility(View.GONE);
			if(detailItem.getRightText().length()>0)
			{
				holder.tv_right.setVisibility(View.VISIBLE);
				holder.tv_right.setText(detailItem.getRightText());
			}
			else
				holder.tv_right.setVisibility(View.GONE);
			return convertView;
		}

		class ViewHolder {
			ImageView iv_leftImage;
			TextView tv_up;
			TextView tv_opertype;
			TextView tv_bottom;
			ImageView hiddenBtn;
			LinearLayout ly_hidden;
			EditText et_num;
			EditText et_zhekou;
			ImageView iv_up;
			ImageView iv_down;
			LinearLayout ll_editSection;
			TextView tv_operSign;
			CustomCircleProgressBar pb_right;
			TextView tv_right;
			EditText et_memo;
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
	                Log.d("", "获得焦点"+position);
					lastFocusEt=et;
	            } else {
	                Log.d("", "失去焦点"+position);
	                try
	                {
	                	if(et.getId()==R.id.et_num) {
							int num = Integer.parseInt(et.getText().toString());
							if (detailItem.getNum() != num) {
								updateAmount(detailItem.getId(), num);
								//et.setOnFocusChangeListener(null);
							}
						}
						else if(et.getId()==R.id.et_zhekou)
						{
							float zhekou = Float.parseFloat(et.getText().toString());
							if (detailItem.getZhekou() != zhekou) {
								updateZhekou(detailItem.getId(), zhekou);
								//et.setOnFocusChangeListener(null);
							}
						}
						else if(et.getId()==R.id.et_memo)
						{
							String beizhu = et.getText().toString();
							if (!detailItem.getBeizhu().equals(beizhu)) {
								updateBeizhu(detailItem.getId(), beizhu);
								//et.setOnFocusChangeListener(null);
							}
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
	class Task extends TimerTask {
		private View v;
		private int what;
		public Task(View h,int what)
		{
			this.v=h;
			this.what=what;
		}
		public void run()
		{
			Message msg = new Message();
			msg.what = what;
			msg.obj = v;
			mHandler.sendMessage(msg);
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
	private void updateZhekou(int id,float zhekou)
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
			jo.put("action", "updateZhekou");
			jo.put("detailId", id);
			jo.put("zhekou", zhekou);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 6);
	}
	private void updateZhekouAll(int zhekou)
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
			jo.put("action", "updateZhekouAll");
			jo.put("zhekou", zhekou);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 6);
	}
	private void updateBeizhu(int id,String beizhu)
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
			jo.put("action", "updateBeizhu");
			jo.put("detailId", id);
			jo.put("beizhu", beizhu);
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
	public void uploadFile(File file)  {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(getActivity(), "对不起，您上传的文件太大了，请选择小于10M的文件！");
		}else{
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			CampusParameters params = new CampusParameters();
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
			params.add("token", checkCode);
			params.add("pic", file.getAbsolutePath());
			params.add("function","uploadAvatar");
			params.add("action","productcolortmp");
			params.add("position",colorAdapter.getCurPosition());
			params.add("prodid",colorAdapter.getProdid());
			JSONObject jo= (JSONObject) colorAdapter.getItem(colorAdapter.getCurPosition());
			params.add("colorid",jo.optInt("id"));
			HttpMultipartPost post = new HttpMultipartPost(getActivity(), params){
				@Override
				protected void onPostExecute(String result) {
					Message msg = new Message();
					msg.what = 10;
					msg.obj = result;
					mHandler.sendMessage(msg);
					this.pd.dismiss();
				}
			};
			post.execute();

		}
	}
	public void SubmitDeleteinfo(String imageName,int position) {

		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject queryJson=AppUtility.parseQueryStrToJson(interfaceName);
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("用户较验码", checkCode);
			Iterator it = queryJson.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = queryJson.getString(key);
				jsonObj.put(key, value);
			}
			jsonObj.put("action", "deleteImage");
			jsonObj.put("picFilename", imageName);
			jsonObj.put("position", position);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 11);
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
			if(popColorDialog.isShowing() && colorAdapter!=null)
				colorAdapter.getPictureByCamera();
			else
				openScanCode();
			if(searchDialog.isShowing())
				searchDialog.dismiss();
		}

		@Override
		public void getPictureFromLocation1() {
			colorAdapter.getPictureFromLocation();
		}


	};
}

package com.yujieshipin.crm.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.yujieshipin.crm.BuildConfig;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.ImagesActivity;
import com.yujieshipin.crm.adapter.MyPictureAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.DownloadSubject;
import com.yujieshipin.crm.entity.ImageItem;
import com.yujieshipin.crm.entity.QuestionnaireList;
import com.yujieshipin.crm.entity.QuestionnaireList.Question;
import com.yujieshipin.crm.lib.DateTimePickDialogUtil;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DateHelper;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.HttpMultipartPost;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.NonScrollableGridView;
import com.yujieshipin.crm.widget.NonScrollableListView;


@SuppressLint("ValidFragment")
public class SchoolQuestionnaireDetailFragment extends Fragment {
	private final String TAG = "SchoolQuestionnaireDetailFragment";
	private ListView myListview;
	private Button btnLeft;
	private TextView tvTitle, tvRight;
	private LinearLayout lyLeft, lyRight,loadingLayout,contentLayout,failedLayout,emptyLayout;
	private QuestionnaireList questionnaireList;
	private String title,status, interfaceName,picturePath,delImagePath,autoClose;
	private LayoutInflater inflater;
	private QuestionAdapter adapter;
	private boolean isEnable = true;
	private Dialog dialog, getPictureDiaLog;
	private MyPictureAdapter myPictureAdapter;
	private List<String> picturePaths = new ArrayList<String>();// 选中的图片路径
	private ArrayList<Question> questions = new ArrayList<Question>();
	private List<ImageItem> images = new ArrayList<ImageItem>();
	private static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	private static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	
	private int size = 5;//已提交图片数量;size:图片最大数量
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = "";
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				if(myPictureAdapter!=null)
				{
					myPictureAdapter.setPicPaths(picturePaths);
					//myPictureAdapter.notifyDataSetChanged();
				}
				showProgress(false);
				if(dialog!=null)
					dialog.dismiss();
				break;
			case 0://获取数据
				showProgress(false);
				result = msg.obj.toString();

				if (AppUtility.isNotEmpty(result)) {
					try {
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if (res.equals("失败")) {
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						} else {
							questionnaireList = new QuestionnaireList(jo);
							tvTitle.setText(questionnaireList.getTitle());
							status=questionnaireList.getStatus();
							if(status.equals("进行中"))
							{
								isEnable = true;
								tvRight.setVisibility(View.VISIBLE);
							}
							else
							{
								isEnable=false;
								lyRight.setVisibility(View.INVISIBLE);
							}
							autoClose=questionnaireList.getAutoClose();
							questions = questionnaireList.getQuestions();
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						showFetchFailedView();
						e.printStackTrace();
					}
				}else{
					showFetchFailedView();
				}
				break;
			case 1://保存成功
				
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) 
				{
					try {
						
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						
						if(res.equals("成功"))
						{
							AppUtility.showToastMsg(getActivity(), "保存成功！");
							if(autoClose!=null && autoClose.equals("是"))
							{
								
								Intent aintent = new Intent();
								getActivity().setResult(1,aintent); 
								getActivity().finish();
							}
						}
						else
							AppUtility.showErrorToast(getActivity(), jo.optString("errorMsg"));
					}
					 catch (JSONException e) {
						e.printStackTrace();
						AppUtility.showErrorToast(getActivity(), "失败:"+e.getMessage());
						
					}
					
				}
				if(dialog!=null)
					dialog.dismiss();
				break;
			case 2://删除图片
				result = msg.obj.toString();
				
				if (AppUtility.isNotEmpty(result)) 
				{
					
					try {
						JSONObject	jo = new JSONObject(result);
						if("成功".equals(jo.optString("result"))){
							images=questions.get(myPictureAdapter.getPosition()).getImages();
							for (int i = 0; i < images.size(); i++) {
								if(images.get(i).getDownAddress().equals(delImagePath)){
									images.remove(i);
								}
							}
							File cacheFile=FileUtility.getCacheFile(delImagePath);
							if(cacheFile.exists())
								cacheFile.delete();
							picturePaths.remove(delImagePath);
							myPictureAdapter.setPicPaths(picturePaths);

							//myPictureAdapter.notifyDataSetChanged();
						}else{
							AppUtility.showToastMsg(getActivity(), jo.optString("STATUS"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3://图片上传
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) 
				{
					
					try {
						JSONObject jo = new JSONObject(result);
						if("成功".equals(jo.optString("result"))){
							
							ImageItem ds = new ImageItem(jo);
							images.add(ds);
							picturePaths.add(ds.getDownAddress());
							myPictureAdapter.setPicPaths(picturePaths);
							//myPictureAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	};
	public SchoolQuestionnaireDetailFragment() {

	}
	public SchoolQuestionnaireDetailFragment(String title,String status,
			String iunterfaceName,String autoClose) {
		
		this.title = title;
		this.status = status;
		this.interfaceName = iunterfaceName;
		//this.autoClose=autoClose;
	}

	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerBroastcastReceiver();
		
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
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);

		myListview.setEmptyView(emptyLayout);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		tvRight.setText("保存");
		tvTitle.setText(title);
		
		adapter = new QuestionAdapter();
		myListview.setAdapter(adapter);
		getPictureDiaLog = new Dialog(getActivity(), R.style.dialog);
		//退出
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		//保存数据
		lyRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					saveQuestionAnswer();
			}
		});
		// 重新加载
		failedLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestionsItem();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//AppUtility.showToastMsg(getActivity(), "正在获取数据");
		getQuestionsItem();
	}

	private void registerBroastcastReceiver() {
		IntentFilter mFilter = new IntentFilter(Constants.GET_PICTURE);
		mFilter.addAction(Constants.DEL_OR_LOOK_PICTURE);
		getActivity().registerReceiver(mBroadcastReceiver, mFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String fromTag=intent.getStringExtra("TAG");
			if (action.equals(Constants.GET_PICTURE)&&fromTag.equals(TAG)) {
				showGetPictureDiaLog();
			}else if(action.equals(Constants.DEL_OR_LOOK_PICTURE)&&fromTag.equals(TAG)){
				//查看详图或删除图片
				delImagePath = intent.getStringExtra("imagePath");
				showDelOrShowPictureDiaLog(delImagePath);
			}
		}
	};
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	};
	/**
	 * 功能描述:获取图片
	 * 
	 * @author shengguo 2014-5-5 下午3:45:04
	 * 
	 */
	private void showGetPictureDiaLog() {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);
		getPictureDiaLog.setContentView(view);
		getPictureDiaLog.show();
		Window window = getPictureDiaLog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPictureDiaLog.dismiss();
			}
		});
		//调用系统相机拍照
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) 
				{
					if(AppUtility.checkPermission(getActivity(), 6,Manifest.permission.CAMERA))
						getPictureByCamera();
				}
				else
					getPictureByCamera();
				getPictureDiaLog.dismiss();
			}
		});
		//选择本地图片
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) 
				{
					if(AppUtility.checkPermission(getActivity(),7,Manifest.permission.READ_EXTERNAL_STORAGE))
						getPictureFromLocation();
				}
				else
					getPictureFromLocation();
				getPictureDiaLog.dismiss();
			}
		});
	}
	/**
	 * 功能描述:删除或查看图片
	 *
	 * @author shengguo  2014-5-8 下午6:32:49
	 *
	 */
	private void showDelOrShowPictureDiaLog(final String imageName) {
		
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.view_show_or_del_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView delPicture = (TextView) view.findViewById(R.id.tv_delete);
		TextView showPicture = (TextView) view.findViewById(R.id.tv_show);
		View v = view.findViewById(R.id.view_dividing_line);
		final AlertDialog ad=new AlertDialog.Builder(getActivity()).setView(view).create();
		if(isEnable){
			delPicture.setVisibility(View.VISIBLE);
			v.setVisibility(View.VISIBLE);
		}else{
			delPicture.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
		}
		
		
		Window window = ad.getWindow();
		
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		ad.show();
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		//删除图片
		delPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SubmitDeleteinfo();
				ad.dismiss();
			}
		});
		//显示大图
		showPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				picturePaths.remove("");
				Intent intent = new Intent(getActivity(), ImagesActivity.class);
				intent.putStringArrayListExtra("pics", (ArrayList<String>) picturePaths);
				
				for (int i = 0; i < picturePaths.size(); i++) {
					if(picturePaths.get(i).equals(imageName)){
						intent.putExtra("position", i);
					}
				}
				startActivity(intent);
				ad.dismiss();
			}
		});
	}


	/**
	 * 调用系统相机拍照获取图片
	 * 
	 * @param
	 */
	private void getPictureByCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			AppUtility.showToastMsg(getActivity(), getString(R.string.Commons_SDCardErrorTitle));
			return;
		}
		picturePath =FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(picturePath);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
			intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", mCurrentPhotoFile)); //Uri.fromFile(tempFile)
		else {
			Uri uri = Uri.fromFile(mCurrentPhotoFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
	}

	/**
	 * 功能描述:从本地获取图片
	 * 
	 * @author shengguo 2014-5-8 上午10:58:45
	 * 
	 */
	private void getPictureFromLocation() {
		/*
		picturePaths.remove("");
		Intent intent = new Intent(getActivity(),AlbumActivity.class);
		intent.putStringArrayListExtra("picturePaths",
				(ArrayList<String>) picturePaths);
		intent.putExtra("size", 5);
		startActivityForResult(intent,
				SchoolDetailActivity.REQUEST_CODE_TAKE_PICTURE);
		*/
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			/*
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			*/
			Intent intent; 
			intent = new Intent(Intent.ACTION_PICK, 
			                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			
		} else {
			AppUtility.showToastMsg(getActivity(), "SD卡不可用");
		}
		
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
	 * 功能描述:获取问卷内容
	 * 
	 * @author shengguo 2014-4-16 上午11:12:43
	 * 
	 */
	public void getQuestionsItem() {
		showProgress(true);
		
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
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 0);
		
	}

	/**
	 * 功能描述:保存问卷答案
	 * 
	 * @author shengguo 2014-5-5 下午5:29:30
	 * 
	 */
	private void saveQuestionAnswer() {
		
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject joarr = getAnswers();
		if(joarr==null){
			return ;
		}
		JSONObject queryJson=AppUtility.parseQueryStrToJson(questionnaireList.getSubmitTo());
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("用户较验码", checkCode);
			Iterator it = queryJson.keys();
			while (it.hasNext()) {
                String key = (String) it.next();
                String value = queryJson.getString(key); 
                jsonObj.put(key, value);
			}
			jsonObj.put("选项记录集", joarr);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		dialog = DialogUtility.createLoadingDialog(getActivity(),
				"保存中...");
		dialog.show();
		CampusAPI.httpPost(jsonObj, mHandler, 1);
		
	}
	
	/**
	 * 功能描述:处理文件上传
	 * 
	 * @author shengguo 2013-12-26 下午4:36:51
	 * 
	 * @param
	 *
	 */
	public void uploadFile(File file)  {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(getActivity(), "对不起，您上传的文件太大了，请选择小于10M的文件！");
		}else{
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());       	
			SubmitUploadFile(file.getAbsolutePath());
	       
		}
	}
	/**
	 * 功能描述:上传文件
	 *
	 * @author shengguo  2013-12-18 上午11:48:59
	 * 
	 * @param
	 * @param
	 */
	public void SubmitUploadFile(String filePath){
		CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		
		params.add("token", checkCode);
		params.add("pic", filePath);
		params.add("function","uploadAvatar");
		params.add("action","product");
		JSONObject queryJson=AppUtility.parseQueryStrToJson(questionnaireList.getSubmitTo());
		params.add("productid",queryJson.optString("productid"));
		picturePaths.remove("");
		picturePaths.add("loading");
		myPictureAdapter.setPicPaths(picturePaths);
		HttpMultipartPost post = new HttpMultipartPost(getActivity(), params){
			@Override  
		    protected void onPostExecute(String result) {  
				
				picturePaths.remove("loading");
				Message msg = new Message();
				msg.what = 3;
				msg.obj = result;
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
	}
	
	/**
	 * 功能描述:获取答案
	 * 
	 * @author shengguo 2014-5-5 下午5:37:56
	 * 
	 * @return
	 */
	private JSONObject getAnswers() {
		JSONObject joarr = new JSONObject();
		try {
			for (int i = 0; i < questions.size(); i++) {
				String mStatus = questions.get(i).getStatus();
				String title=questions.get(i).getTitle();
				if(!mStatus.equals("图片")){
					
					String usersAnswer = questions.get(i).getUsersAnswer();
					String isRequired = questions.get(i).getIsRequired();//是否必填
					String validate=questions.get(i).getValidate();
					if(AppUtility.isNotEmpty(isRequired)){
						if(isRequired.equals("是")){
							if(AppUtility.isNotEmpty(usersAnswer)){
								
								joarr.put(title,usersAnswer);
								
							}else{
								AppUtility.showToastMsg(getActivity(),"请填写所有必填项");
								myListview.setSelection(i);
								
								return null;
							}
						}else{
							joarr.put(title,usersAnswer);
						}
					}
					if(AppUtility.isNotEmpty(validate) && AppUtility.isNotEmpty(usersAnswer)){
						if(validate.equals("手机号") && !AppUtility.checkPhone(usersAnswer))
						{
							AppUtility.showToastMsg(getActivity(),title+",格式不正确");
							myListview.setSelection(i);
							return null;
						}
						else if(validate.equals("浮点型") && !AppUtility.isDecimal(usersAnswer))
						{
							AppUtility.showToastMsg(getActivity(),title+",必须是浮点型数字,如:99.9");
							myListview.setSelection(i);
							return null;
						}
						else if(validate.equals("整型") && !AppUtility.isInteger(usersAnswer))
						{
							AppUtility.showToastMsg(getActivity(),title+",必须整形数字,如:99");
							myListview.setSelection(i);
							return null;
						}
						else if(validate.equals("邮箱") && !AppUtility.checkEmail(usersAnswer))
						{
							AppUtility.showToastMsg(getActivity(),title+",邮箱格式不正确");
							myListview.setSelection(i);
							return null;
						}
					}
				}else{
					JSONArray joimages = new JSONArray();
					for (ImageItem imageItem :images) {
						JSONObject joimgs = new JSONObject();
						try {
							joimgs.put("文件名", imageItem.getFileName());
							joimgs.put("文件地址", imageItem.getDownAddress());
							joimgs.put("课程名称", imageItem.getCurriculumName());
							joimgs.put("下载次数", imageItem.getLoadCount());
							joimgs.put("上课记录编号", imageItem.getSubjectId());
							joimgs.put("最后一次下载", imageItem.getLastDown());
							joimgs.put("名称", imageItem.getName());
							joimgs.put("STATUS", "OK");
							joimages.put(joimgs);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					joarr.put(title,joimages);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return joarr;
	}
	/**
	 * 功能描述:删除图片
	 *
	 * @author shengguo  2014-5-9 下午12:05:03
	 * 
	 * @param
	 */
	
	public void SubmitDeleteinfo() {
		
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
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 2);
		
	}
	public List<String> getPicturePaths() {
		return picturePaths;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_TAKE_CAMERA: // 拍照返回
				//Bundle bundle = data.getExtras();
				//Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				//ImageUtility.writeTofiles(bitmap, picturePath);
				uploadFile(new File(picturePath));
				
				break;
			case REQUEST_CODE_TAKE_PICTURE:
				
				if (data != null) {
					Uri uri = data.getData();
					String[] pojo  = { MediaStore.Images.Media.DATA };
					CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, pojo, null,null, null); 
					Cursor cursor = cursorLoader.loadInBackground();
					cursor.moveToFirst(); 
				    picturePath = cursor.getString(cursor.getColumnIndex(pojo[0])); 
					
					String tempPath =FileUtility.getRandomSDFileName("jpg");
					if(FileUtility.copyFile(picturePath,tempPath))
						uploadFile(new File(tempPath));
					else
						AppUtility.showErrorToast(getActivity(), "向SD卡复制文件出错");
				}
				break;
		}
	};

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
	private void closeInputMethod(View v) {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
	    	imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	class QuestionAdapter extends BaseAdapter {
	
		int mFocusPosition = -1;
		OnFocusChangeListener mListener = new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            int position = (Integer) v.getTag();
	            if (hasFocus) {
	                mFocusPosition = position;
	            }
	            Log.e("test", "onFocusChange:" + position + " " + hasFocus);
	        }
	    };
		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	
		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
						
			final Question question = (Question) getItem(position);
			convertView = inflater.inflate(R.layout.school_questionnaire_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_questionnaire_name);
			holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.rg_choose);
			holder.multipleChoice = (NonScrollableListView) convertView.findViewById(R.id.lv_choose);
			holder.etAnswer = (EditText) convertView.findViewById(R.id.et_answer);
			holder.tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
			holder.imageGridView = (NonScrollableGridView) convertView.findViewById(R.id.grid_picture);
			holder.tvRemark = (TextView) convertView.findViewById(R.id.tv_remark);
			holder.bt_date=(Button)convertView.findViewById(R.id.bt_date);
			holder.bt_datetime=(Button)convertView.findViewById(R.id.bt_datetime);
			holder.sp_select=(Spinner)convertView.findViewById(R.id.sp_select);
			holder.etAnswer.setOnFocusChangeListener(mListener);
			holder.etAnswer.setTag(position);
			
			convertView.setOnTouchListener(touchListener);
			
			String mStatus = question.getStatus();
			String addstr="";
			if(question.getIsRequired().equals("是") && !question.getTitle().endsWith("*"))
				addstr="*";
			holder.title.setText(position+1+"."+question.getTitle()+addstr);

			String remark = question.getRemark();
			if(AppUtility.isNotEmpty(remark) && remark.length()>0){
				holder.tvRemark.setText(remark);
				holder.tvRemark.setVisibility(View.VISIBLE);
				if(remark.length()>=7 && (remark.substring(0, 7).equals("答题状态:错误") || remark.indexOf("error")>0)){
					holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.red_color));
				}else if(remark.length()>=7 && (remark.substring(0, 7).equals("答题状态:正确") || remark.indexOf("right")>0)){
					holder.tvRemark.setTextColor(getActivity().getResources().getColor(R.color.title_nor));
				}
				else
					holder.tvRemark.setTextColor(Color.BLUE);
			}
			if (mStatus.equals("单选")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.VISIBLE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				final List<JSONObject> answers = question.getOptions();
				holder.radioGroup.removeAllViews();

				int checkIndex = -1;
				holder.radioGroup.setOnCheckedChangeListener(null);
				for (int i = 0; i < answers.size(); i++) {
					JSONObject objItem=answers.get(i);
					String key=objItem.optString("key");
					String value=objItem.optString("value");
					View v= inflater.inflate(R.layout.my_radiobutton, parent, false);
					RadioButton radioButton = (RadioButton) v.findViewById(R.id.rb_chenck);
					radioButton.setText(value);
					radioButton.setTextSize(12.0f);
					radioButton.setId(i);
					boolean bflag=false;
					if(isEnable && !question.isIfRead())
						bflag=true;
					radioButton.setEnabled(bflag);
					if (key.equals(question.getUsersAnswer())) {
						checkIndex = i;
					}
					radioButton.setTag(key);
					holder.radioGroup.addView(radioButton);
				}
				if (checkIndex != -1) {
					holder.radioGroup.clearCheck();
					holder.radioGroup.check(checkIndex);
				} else {
					holder.radioGroup.clearCheck();
				}
				holder.radioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								JSONObject objItem=answers.get(checkedId);
								String key=objItem.optString("key");
								question.setUsersAnswer(key);
								questions.set(position, question);
								questionnaireList.setQuestions(questions);
							}
						});
			} else if (mStatus.equals("多选")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.VISIBLE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(
						getActivity(), position, question);
				holder.multipleChoice.setAdapter(checkBoxAdapter);
				
			} else if (mStatus.equals("单行文本输入框")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				
				if (status.equals("已结束") || status.equals("未开始")) {
					holder.etAnswer.setVisibility(View.GONE);
					holder.tvAnswer.setVisibility(View.VISIBLE);
					holder.tvAnswer.setText(question.getUsersAnswer());
				} else {
					holder.etAnswer.setVisibility(View.VISIBLE);
					holder.etAnswer.setEnabled(!question.isIfRead());
					holder.tvAnswer.setVisibility(View.GONE);
					holder.etAnswer.setText(question.getUsersAnswer());
					if(question.getLines()>0)
						holder.etAnswer.setLines(question.getLines());
					if(question.getLines()==1)
						holder.etAnswer.setSingleLine();
					if (mFocusPosition == position) {
						holder.etAnswer.requestFocus();
			        } else {
			        	holder.etAnswer.clearFocus();
			        }
					holder.etAnswer.addTextChangedListener(new TextWatcher() {
						
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						Log.d(TAG, "-------------" + s);
						
					}
	
					@Override
					public void beforeTextChanged(CharSequence s,
							int start, int count, int after) {
						// TODO Auto-generated method stub
	
					}
	
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						question.setUsersAnswer(s.toString());
						questions.set(position, question);
						questionnaireList.setQuestions(questions);
					}
				});
					
				}
			} else if (mStatus.equals("图片")) {
				holder.imageGridView.setVisibility(View.VISIBLE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				Log.d(TAG, "-----------------" + picturePaths.size());
				List<ImageItem> images = question.getImages();
				if(images != null){
					for (int i = 0; i < images.size(); i++) {
						String imagePath = images.get(i).getDownAddress();
						if(!picturePaths.contains(imagePath)){
							picturePaths.add(images.get(i).getDownAddress());
						}
					}
				}
				boolean bflag=false;
				if(!question.isIfRead() && isEnable)
					bflag=true;
				myPictureAdapter = new MyPictureAdapter(getActivity(),bflag,picturePaths,question.getLines(),"调查问卷",position);
				myPictureAdapter.setFrom(TAG);
				holder.imageGridView.setAdapter(myPictureAdapter);
			}
			else if (mStatus.equals("日期")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.VISIBLE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.GONE);
				holder.bt_date.setEnabled(!question.isIfRead());
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_date.setText(question.getUsersAnswer());

				holder.bt_date.setOnClickListener(new OnClickListener(){
					
					private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){  //
						@Override
						public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
						
							question.setUsersAnswer(DateHelper.getDateString(new Date(arg1-1900,arg2,arg3), "yyyy-MM-dd"));
							Button bt=(Button)arg0.getTag();
							bt.setText(question.getUsersAnswer());
						}
					};
						
					@Override
					public void onClick(View v) {
						Date dt=DateHelper.getStringDate(question.getUsersAnswer(), "yyyy-MM-dd");
						Calendar cal=Calendar.getInstance();
						cal.setTime(dt);
						DatePickerDialog dialog = new DatePickerDialog(getActivity(),listener,cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
						dialog.getDatePicker().setTag(v);
						if(question.getOptions().size()==2)
						{
							JSONObject obj0=question.getOptions().get(0);
							JSONObject obj1=question.getOptions().get(1);
							Date minDt=DateHelper.getStringDate(obj0.optString("value"), "yyyy-MM-dd");
							Date maxDt=DateHelper.getStringDate(obj1.optString("value"), "yyyy-MM-dd");
							dialog.getDatePicker().setMinDate(minDt.getTime());
							dialog.getDatePicker().setMaxDate(maxDt.getTime());
						}
						dialog.setButton2("取消", new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						dialog.show();
					}
					
				});
	
			}
			else if (mStatus.equals("日期时间")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.VISIBLE);
				holder.sp_select.setVisibility(View.GONE);
				holder.bt_datetime.setEnabled(!question.isIfRead());
				if(!AppUtility.isNotEmpty(question.getUsersAnswer()))
				{
					question.setUsersAnswer(DateHelper.getToday());
				}
				holder.bt_datetime.setText(question.getUsersAnswer());

				holder.bt_datetime.setOnClickListener(new OnClickListener(){
				
					@Override
					public void onClick(View v) {
						
						DateTimePickDialogUtil dialog = new DateTimePickDialogUtil(getActivity(),question.getUsersAnswer(),"yyyy-MM-dd HH:mm");
						Button bt=(Button)v;
						bt.setTag(question);
						dialog.dateTimePicKDialog(bt);
						
					}
					
				});
	
			}
			else if (mStatus.equals("下拉")) {
				holder.imageGridView.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.multipleChoice.setVisibility(View.GONE);
				holder.etAnswer.setVisibility(View.GONE);
				holder.tvAnswer.setVisibility(View.GONE);
				holder.bt_date.setVisibility(View.GONE);
				holder.bt_datetime.setVisibility(View.GONE);
				holder.sp_select.setVisibility(View.VISIBLE);
				holder.sp_select.setEnabled(!question.isIfRead());
				String [] listStr=new String[question.getOptions().size()];
				int pos=0;
				for(int i=0;i<question.getOptions().size();i++)
				{
					JSONObject obj=question.getOptions().get(i);
					if(i==0 && !AppUtility.isNotEmpty(question.getUsersAnswer()))
						question.setUsersAnswer(obj.optString("key"));
					listStr[i]=obj.optString("value");
					if(obj.optString("key").equals(question.getUsersAnswer()))
						pos=i;
				}
				ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,listStr);
				holder.sp_select.setAdapter(aa);
				holder.sp_select.setSelection(pos);
				holder.sp_select.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						JSONObject obj=question.getOptions().get(position);
						question.setUsersAnswer(obj.optString("key"));
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			return convertView;
		}

		class ViewHolder {
			TextView title;
			EditText etAnswer;
			TextView remark;
			TextView tvAnswer;
			TextView tvRemark;
			RadioGroup radioGroup;
			NonScrollableListView multipleChoice;
			NonScrollableGridView imageGridView;
			Spinner sp_select;
			Button bt_date;
			Button bt_datetime;
		}
		
	}
	
	/**
	 * 
	 *  #(c) ruanyun PocketCampus <br/>
	 *
	 *  版本说明: $id:$ <br/>
	 *
	 *  功能说明: 加载多选列表
	 * 
	 *  <br/>创建说明: 2014-5-17 上午9:44:52 shengguo  创建文件<br/>
	 * 
	 *  修改历史:<br/>
	 *
	 */
	@SuppressWarnings("unused")
	private class CheckBoxAdapter extends BaseAdapter {

		private Context context;
		private List<JSONObject> anwsers;
		private String anwser;
		private int questionIndex;// question 在list中的下标
		private Question question;
		public Map<String, Boolean> isChecked = new HashMap<String, Boolean>();

		public CheckBoxAdapter(Context context, int questionIndex,
				Question question) {
			super();
			this.context = context;
			this.questionIndex = questionIndex;
			this.question = question;
			anwsers = question.getOptions();
			anwser = question.getUsersAnswer();
			initDate();
		}

		private void initDate() {

			String[] arr = anwser.split("@");
			List<String> list = Arrays.asList(arr);
			for (int i = 0; i < anwsers.size(); i++) {
				JSONObject obj=anwsers.get(i);
				String key=obj.optString("key");
				if (list.contains(key)) {
					isChecked.put(key, true);
				} else {
					isChecked.put(key, false);
				}
			}
		}

		public String getAnwser() {
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < anwsers.size(); i++) {
				JSONObject obj=anwsers.get(i);
				String key=obj.optString("key");
				
				if (isChecked.get(key)) {
					str.append(key).append("@");
				}
			}
			if (str.indexOf(",") > -1) {
				str.deleteCharAt(str.lastIndexOf("@"));
			}
			return str.toString();
		}

		public void setAnwser(String anwser) {
			this.anwser = anwser;
		}

		@Override
		public int getCount() {
			return anwsers.size();
		}

		@Override
		public Object getItem(int position) {
			return anwsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			view = inflater.inflate(R.layout.checkbox_item, parent, false);
			final CheckBox cb = (CheckBox) view.findViewById(R.id.cb_chenck);
			JSONObject obj=anwsers.get(position);
			final String key=obj.optString("key");
			String value=obj.optString("value");
			cb.setText(value);
			if (isChecked.get(key)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}
			cb.setEnabled(isEnable);
			cb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Boolean flag = cb.isChecked();
					isChecked.put(key, flag);
					String answer = getAnwser();
					Log.d(TAG, "---------" + answer +question.getStatus()+"ss" +question.getTitle());
					question.setUsersAnswer(answer);
					questions.set(questionIndex, question);
					questionnaireList.setQuestions(questions);
				}
			});
			return view;
		}
	}
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		AppUtility.permissionResult(requestCode,grantResults,getActivity(),callBack);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	public CallBackInterface callBack=new CallBackInterface()
	{

		@Override
		public void getLocation1() {
			// TODO Auto-generated method stub
		
		}

		@Override
		public void getPictureByCamera1() {
			// TODO Auto-generated method stub
			getPictureByCamera();
		}

		@Override
		public void getPictureFromLocation1() {
			// TODO Auto-generated method stub
			getPictureFromLocation();
		}

		
	};

}

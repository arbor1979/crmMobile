package com.yujieshipin.crm.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.adapter.ExpressionGvAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AlbumImageInfo;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.fragment.AlbumImageFragment;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.ExpressionUtil;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.ZLibUtils;



public class AlbumShowImagePage extends FragmentActivity {

	private ViewPager mViewpager;
	private StuInfoPagerAdapter mStuInfoPagerAdapter;
	private boolean misScrolled;
	public ArrayList<AlbumImageInfo> browsedList,praisedList,deletedList,commentedList;
	Button bn_back, menu,btnRight,btnShare;
	String hostid;
	LinearLayout bottomLayout;
	EditText edit;
	ImageView faceImage;
	View viewpager_layout;
	private int imageIds[] = ExpressionUtil.getExpressRcIds(); // 保存所有表情资源的id
	private RelativeLayout express_spot_layout;
	private ViewPager viewPager; // 实现表情的滑动翻页
	private LinearLayout replyToLayout;
	private TextView replyToText;
	private ImageView replyToImage;
	private User user;
	private ProgressBar proBar;
	private static final int MY_PERMISSIONS_REQUEST_Album = 7;
	//private Intent intent;
	//private Student studentInfo;
	
	
	private ArrayList<AlbumImageInfo> imageList,allList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_image_page);
		proBar=(ProgressBar)findViewById(R.id.progressBar2);
		hostid = PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
		imageList=(ArrayList<AlbumImageInfo>)getIntent().getSerializableExtra("imageList");
		allList=(ArrayList<AlbumImageInfo>)getIntent().getSerializableExtra("allList");
		browsedList=new ArrayList<AlbumImageInfo>();
		praisedList=new ArrayList<AlbumImageInfo>();
		deletedList=new ArrayList<AlbumImageInfo>();
		commentedList=new ArrayList<AlbumImageInfo>();
		initTitle();
		initViewPager();
		if(!browsedList.contains(imageList.get(0)))
			browsedList.add(imageList.get(0));
		
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		getImageDetailInfo();
	}
	private void getImageDetailInfo()
	{
		//proBar.setVisibility(View.VISIBLE);
		String imageIds="";
		for(int i=0;i<imageList.size();i++)
		{
			if(imageIds.length()!=0)
				imageIds=imageIds+";";
			imageIds=imageIds+imageList.get(i).getName();
		}
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("imageIds",imageIds);
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getAlbumDetailList(params,new RequestListener() {

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
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
		
		
	}
	private void initTitle() {
		
		bn_back = (Button) findViewById(R.id.back);
		bn_back.setVisibility(View.VISIBLE);
		
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(browsedList.size()>0)
				{
					sendBrowsed();
				}
				
				Intent aintent = new Intent(AlbumShowImagePage.this,AlbumFlowActivity.class);
				aintent.putExtra("praisedList", praisedList);
				aintent.putExtra("deletedList", deletedList);
				setResult(200,aintent); 
				Intent bintent = new Intent(AlbumShowImagePage.this,AlbumPersonalActivity.class);
				bintent.putExtra("praisedList", praisedList);
				bintent.putExtra("deletedList", deletedList);
				bintent.putExtra("commentedList", commentedList);
				setResult(200,bintent); 
				finish();
			}
		});
		LinearLayout layout=(LinearLayout)findViewById(R.id.setting_layout_goto);
		LinearLayout layout1=(LinearLayout)findViewById(R.id.setting_layout_goto1);
		layout.setVisibility(View.VISIBLE);
		layout1.setVisibility(View.VISIBLE);
		btnRight = (Button) findViewById(R.id.setting_btn_goto);
		btnShare= (Button) findViewById(R.id.setting_btn_goto1);
		btnShare.setBackgroundResource(R.drawable.share);
		layout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendPraise();
				
			}
			
		});
		layout1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clickMoreBtn();
				
			}
			
		});
		bottomLayout=(LinearLayout)findViewById(R.id.bottom);
		edit=(EditText)findViewById(R.id.edit);
		TextView send=(TextView)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendComment();
				
			}
			
		});
		faceImage = (ImageView) findViewById(R.id.face);
		viewpager_layout = findViewById(R.id.viewpager_layout);
		express_spot_layout = (RelativeLayout) findViewById(R.id.express_spot_layout);
		viewPager = (ViewPager) findViewById(R.id.tabpager);
		// 显示表情
		faceImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showExpressionWindow(v);
			}
		});
		// 隐藏表情框
		edit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				viewpager_layout.setVisibility(View.GONE);
				return false;
			}
		});
		replyToLayout = (LinearLayout) findViewById(R.id.replyToLayout);
		replyToText = (TextView) findViewById(R.id.replyToText);
		replyToImage = (ImageView) findViewById(R.id.replyToImage);
		replyToImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				replyToLayout.setVisibility(View.GONE);
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
		});
	}
	private void clickMoreBtn()
	{
		final String[] moreAction;
		final AlbumImageInfo image=imageList.get(mViewpager.getCurrentItem());
		if(hostid.equals(image.getHostId()) || user.getAlbumAdmin().equals("是"))
			moreAction=new String[]{"分享","保存到本地","举报","删除","取消"};
		else
			moreAction=new String[]{"分享","保存到本地","举报","取消"};
		Builder builder = new AlertDialog.Builder(this);  
		builder.setCancelable(true);
        builder.setTitle("");  
        //builder.setIcon(R.drawable.dialog);  
        DialogInterface.OnClickListener listener =   
            new DialogInterface.OnClickListener() {  
                  
                @Override  
                public void onClick(DialogInterface dialogInterface,   
                        int which) {  
                	if(moreAction[which].equals("分享"))
                		sendShare();
                	else if (moreAction[which].equals("保存到本地"))
                	{
                		String imagePath=ImageLoader.getInstance().getDiscCache().get(image.getUrl()).getAbsolutePath();
                		if (Build.VERSION.SDK_INT >= 23) 
        				{
        					if(AppUtility.checkPermission(AlbumShowImagePage.this, MY_PERMISSIONS_REQUEST_Album,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        						saveToDisk(imagePath,image.getName());
        				}
        				else
        					saveToDisk(imagePath,image.getName());
                	}
                	else if (moreAction[which].equals("举报"))
                	{
                		sendJuBao();
                	}
                	else if (moreAction[which].equals("删除"))
                	{
                		sendDelete();
                	}
                	dialogInterface.dismiss();
                		
                }  
            };  
        builder.setItems(moreAction, listener);  
        AlertDialog dialog = builder.create();  
        dialog.show();
	}
	private void saveToDisk(String imagePath,String filename)
	{
		String result;
		if(ImageUtility.addImageToMediaStore(AlbumShowImagePage.this, imagePath, filename))
			result="保存成功";
		else
			result="保存图片到本地相册失败";
		AppUtility.showToastMsg(AlbumShowImagePage.this,result);
	}
	private void sendShare()
	{
		AlbumImageInfo image=imageList.get(mViewpager.getCurrentItem());
		Intent intent=new Intent(Intent.ACTION_SEND);   
        intent.setType("image/*");   
        String newFileName=FileUtility.creatSDDir("相册")+image.getName();
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" +newFileName));
        intent.putExtra(Intent.EXTRA_SUBJECT, "掌上校园相册");   
        intent.putExtra(Intent.EXTRA_TEXT, image.getDescription());   
        intent.putExtra("sms_body", image.getDescription());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        startActivity(Intent.createChooser(intent,"分享")); 
	}
	/**
	 * 隐藏软键盘
	 * 
	 * @param view
	 */
	public void hideOrShowSoftinput(View view) {
		InputMethodManager manager = (InputMethodManager) this
				.getSystemService(Service.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(edit.getWindowToken(),
					0);
			viewpager_layout.setVisibility(View.GONE);
		} else {
			manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	/**
	 * 功能描述:加载表情图片
	 * 
	 * @author shengguo 2013-12-26 上午11:55:01
	 * 
	 * @param index
	 * @param colums
	 * @param pageItemCount
	 * @return
	 */
	private View getViewPagerItem(final int index, int colums,
			final int pageItemCount) {
		LayoutInflater inflater = this.getLayoutInflater();
		View express_view = inflater.inflate(R.layout.express_gv, null);
		GridView gridView = (GridView) express_view
				.findViewById(R.id.gv_express);
		gridView.setNumColumns(colums);
		gridView.setAdapter(new ExpressionGvAdapter(index, pageItemCount,
				imageIds, inflater));
		// 注册监听事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int positon, long id) {
				
				
				Bitmap bitmap = null;
				int start = index * pageItemCount; // 起始位置
				positon = positon + start;
				bitmap = BitmapFactory.decodeResource(getResources(),
						imageIds[positon]);
				ImageSpan imageSpan = new ImageSpan(AlbumShowImagePage.this,
						bitmap, ImageSpan.ALIGN_BOTTOM);
				
				
				String str = "";
				if (positon < 10) {
					str = "[f00" + positon + "]";
				} else if (positon < 100) {
					str = "[f0" + positon + "]";
				} else {
					str = "[f" + positon + "]";
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, str.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				edit.append(spannableString);
			}

		});
		return express_view;
	}
	public void showExpressionWindow(View v) {
		Log.d("showExpressionWindow", "选择表情");
		// 判断软键盘是否打开
		if (viewpager_layout.getVisibility() == View.VISIBLE) {
			viewpager_layout.setVisibility(View.GONE);
		} else {
			this.hideOrShowSoftinput(v);
			// 显示表情对话框
			viewpager_layout.setVisibility(View.VISIBLE);
		}
		// 获取屏幕当前分辨率
		Display currDisplay = getWindowManager().getDefaultDisplay();
		int displayWidth = currDisplay.getWidth();
		// 获得表情图片的宽度/高度
		Bitmap express = BitmapFactory.decodeResource(getResources(),
				R.drawable.f000);
		int headWidth = express.getWidth();
		int headHeight = express.getHeight();
		

		final int colmns = displayWidth / headWidth > 7 ? 7 : displayWidth
				/ headWidth; // 每页显示的列数
		final int rows = 230 / headHeight > 4 ? 4 : 230 / headHeight; // 每页显示的行数
		final int pageItemCount = colmns * rows; // 每页显示的条目数
		// 计算总页数
		int totalPage = Constants.express_counts % pageItemCount == 0 ? Constants.express_counts
				/ pageItemCount
				: Constants.express_counts / pageItemCount + 1;

		final List<View> listView = new ArrayList<View>();
		for (int index = 0; index < totalPage; index++) {
			listView.add(getViewPagerItem(index, colmns, pageItemCount));
		}
		express_spot_layout.removeAllViews();
		for (int i = 0; i < totalPage; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setId(i + 1);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.d2);
			} else {
				imageView.setBackgroundResource(R.drawable.d1);
			}
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.bottomMargin = 20;
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			if (i != 0) {
				layoutParams.addRule(RelativeLayout.ALIGN_TOP, i);
				layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
			}
			express_spot_layout.addView(imageView, layoutParams);
		}
		
		// 填充viewPager的适配器
		viewPager.setAdapter(new PagerAdapter() {
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			public int getCount() {
				return listView.size();
			}

			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(listView.get(position));
			}

			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(listView.get(position));
				return listView.get(position);
			}
		});
		// 注册监听器
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
	}
	private final class MyPageChangeListener implements OnPageChangeListener {
		private int curIndex = 0;

		public void onPageSelected(int index) {
			express_spot_layout.getChildAt(curIndex).setBackgroundResource(
					R.drawable.d1);
			express_spot_layout.getChildAt(index).setBackgroundResource(
					R.drawable.d2);
			curIndex = index;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}
	//点赞请求
	private void sendPraise()
	{
		String action="点赞";
		AlbumImageInfo image=imageList.get(mViewpager.getCurrentItem());
		for(int i=0;i<image.getPraiseList().size();i++)
		{
			if(image.getPraiseList().get(i).getFromId().equals(hostid))
			{
				//AppUtility.showToastMsg(this, "您已赞过！");
				//return;
				action="取消赞";
			}
		}
		if(action.equals("点赞"))
			btnRight.setBackgroundResource(R.drawable.fill_heart_white);
		else
			btnRight.setBackgroundResource(R.drawable.empty_heart_white);
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action",action);
			jo.put("imageId",imageList.get(mViewpager.getCurrentItem()).getName());
			jo.put("hostId",imageList.get(mViewpager.getCurrentItem()).getHostId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php",new RequestListener() {

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
	private void sendComment()
	{
		if(edit.getText().toString().length()==0)
		{
			AppUtility.showToastMsg(this, "评论内容不能为空！");
			return;
		}
		if(edit.getText().toString().length()>100)
		{
			AppUtility.showToastMsg(this, "评论内容不能超过100个字！");
			return;
		}
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		viewpager_layout.setVisibility(View.GONE);
		
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action","评论");
			jo.put("imageId",imageList.get(mViewpager.getCurrentItem()).getName());
			jo.put("hostId",imageList.get(mViewpager.getCurrentItem()).getHostId());
			if(replyToLayout.getVisibility()==View.VISIBLE)
			{
				String replyId=(String) replyToText.getTag();
				jo.put("replyId",replyId);
			}
			jo.put("comment",edit.getText().toString());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php",new RequestListener() {

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
		edit.setText("");
	}
	//举报请求
	private void sendJuBao()
	{
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action","举报");
			jo.put("imageId",imageList.get(mViewpager.getCurrentItem()).getName());
			jo.put("hostId",imageList.get(mViewpager.getCurrentItem()).getHostId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php",new RequestListener() {

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
	//举报请求
	private void sendDelete()
	{
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action","删除");
			jo.put("imageId",imageList.get(mViewpager.getCurrentItem()).getName());
			jo.put("hostId",imageList.get(mViewpager.getCurrentItem()).getHostId());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php",new RequestListener() {

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
	private void sendBrowsed()
	{
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		String imageIds="";
		String hostIds="";
		for(int i=0;i<browsedList.size();i++)
		{
			if(hostIds.length()>0)
				hostIds=hostIds+";";
			hostIds=hostIds+browsedList.get(i).getHostId();
			if(imageIds.length()>0)
				imageIds=imageIds+";";
			imageIds=imageIds+browsedList.get(i).getName();
			
		}
		try {
			jo.put("action","浏览");
			jo.put("imageIds",imageIds);
			jo.put("hostIds",hostIds);
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, "AlbumPraise.php",new RequestListener() {

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
	private void initViewPager() {
		mViewpager = (ViewPager) findViewById(R.id.stuinfo_pager);
		mStuInfoPagerAdapter = new StuInfoPagerAdapter(
				getSupportFragmentManager(), imageList);
		mViewpager.setAdapter(mStuInfoPagerAdapter);
		mViewpager.setPageMargin(30);
		mViewpager.setOnPageChangeListener(new OnPageChangeListener(){
		
			@Override
			public void onPageScrollStateChanged(int state) 
			{		
				switch (state) {		
					case ViewPager.SCROLL_STATE_DRAGGING:			
						misScrolled = false;			
						break;		
					case ViewPager.SCROLL_STATE_SETTLING:			
						misScrolled = true;			
						break;		
					case ViewPager.SCROLL_STATE_IDLE:			
						if (mViewpager.getCurrentItem() == mViewpager.getAdapter().getCount() - 1 && !misScrolled) 
						{				
							
							AlbumImageInfo image=imageList.get(mViewpager.getCurrentItem());
							if(allList!=null && imageList.size()>=20)
							{
								AppUtility.showToastMsg(AlbumShowImagePage.this, "正在获取数据");
								imageList.clear();
								boolean flag=false;
								for(AlbumImageInfo item:allList)
								{
									if(item.getName().equals(image.getName()))
									{
										flag=true;
									}
									if(flag)
									{
										imageList.add(item);
										if(imageList.size()>=20)
										{
											break;
										}
									}
								}
								initViewPager();
								getImageDetailInfo();
							}
							else
								AppUtility.showToastMsg(AlbumShowImagePage.this, "已是最后一张");
							
						}
						else if (mViewpager.getCurrentItem() == 0 && !misScrolled) 
						{
							AppUtility.showToastMsg(AlbumShowImagePage.this, "已是第一张");
						}
						misScrolled = true;			
						break;		
					}
			}
			 

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
				updateRightButton(arg0);
				AlbumImageInfo image=imageList.get(arg0);
				if(!browsedList.contains(image))
					browsedList.add(image);
				
			}
			
			
		});
	}

	private void updateRightButton(int curPos)
	{
		boolean flag=false;
		AlbumImageInfo image=imageList.get(curPos);
		for(int i=0;i<image.getPraiseList().size();i++)
		{
			if(image.getPraiseList().get(i).getFromId().equals(hostid))
			{
				flag=true;
				break;
			}
		}
		if(flag)
			btnRight.setBackgroundResource(R.drawable.fill_heart_white);
		else
			btnRight.setBackgroundResource(R.drawable.empty_heart_white);
	}

	public class StuInfoPagerAdapter extends FragmentStatePagerAdapter {
		List<AlbumImageInfo> list = new ArrayList<AlbumImageInfo>();
		FragmentManager fm;
		public StuInfoPagerAdapter(FragmentManager fm, List<AlbumImageInfo> list) {
			super(fm);
			this.list = list;
			this.fm=fm;
		}
		
		@Override
		public Fragment getItem(int position) {
			Bundle bundle = null;
			AlbumImageInfo image=list.get(position);
			
			Log.d("ImageFragment",position+"getItem:"+image.getName());
			AlbumImageFragment mStuInfoFragment = new AlbumImageFragment();
			bundle = new Bundle();
			bundle.putSerializable("imageInfo",  image);
			mStuInfoFragment.setArguments(bundle);
			return mStuInfoFragment;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) 
		{
			AlbumImageFragment f = (AlbumImageFragment) super.instantiateItem(container, position);
			AlbumImageInfo image=list.get(position);
			Log.d("ImageFragment",position+"instantiateItem:"+image.getName());
		    f.setImage(image);
		    return f;
		}
		
		@Override
		public int getItemPosition(Object object) {
		     return PagerAdapter.POSITION_NONE;
		}
		
		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}
		@Override  
	    public void destroyItem(ViewGroup container, int position, Object object) 
		{
			super.destroyItem(container, position, object);
			/*
			AlbumImageFragment fragment = (AlbumImageFragment)object; 
			//fragment.main_image.setImageResource(R.drawable.empty_photo);
			Log.d("ImageFragment",position+"destroyItem:"+fragment.getImage().getName());
			if(fm.getFragments().contains(fragment))
				fm.getFragments().remove(fragment);
			*/
		}
		
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			
			String result = "";
			String resultStr = "";
			switch (msg.what) {
			case -1:// 请求失败
				proBar.setVisibility(View.INVISIBLE);
				AppUtility.showErrorToast(AlbumShowImagePage.this,
						msg.obj.toString());
				
				break;
			case 1:// 获取赞和评论
				
				final String result1 = msg.obj.toString();
				Thread thread=new Thread(new Runnable()  
		        {  
		            @Override  
		            public void run()  
		            {  
		            	byte[] resultByte=null;
						String	resultStr="";
						if (AppUtility.isNotEmpty(result1)) 
						{
							try {
								resultByte =Base64.decode(result1
										.getBytes("GBK"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							resultStr = ZLibUtils.decompress(resultByte);
						}
						try {
							JSONObject jo=new JSONObject(resultStr);
							JSONObject praiseOb=jo.getJSONObject("点赞");
							JSONObject commentOb=jo.getJSONObject("评论");
							for(int i=0;i<imageList.size();i++)
							{
								AlbumImageInfo image=imageList.get(i);
								if(praiseOb!=null)
								{
									JSONArray ja=praiseOb.getJSONArray(image.getName());
									if(ja!=null)
									{
										
										for(int j=0;j<ja.length();j++)
										{
											JSONObject item=ja.getJSONObject(j);
											AlbumMsgInfo u=new AlbumMsgInfo(item);
											image.getPraiseList().add(u);
										}
										
									}
										
										
								}
								if(commentOb!=null)
								{
									JSONArray ja=commentOb.getJSONArray(image.getName());
									if(ja!=null)
									{
										
										for(int j=0;j<ja.length();j++)
										{
											JSONObject item=ja.getJSONObject(j);
											AlbumMsgInfo u=new AlbumMsgInfo(item);
											image.getCommentsList().add(u);
										}
										
									}
								}
								
							}
							
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Message msg = new Message();
						msg.what = 3;
						mHandler.sendMessage(msg);
		            }  
		        });  
		        thread.start();  
				
				
				break;
			case 2: //赞或评论
				result = msg.obj.toString();
				resultStr = "";
				try {
					resultStr = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				try {
					JSONObject jo = new JSONObject(resultStr);
					
					if("成功".equals(jo.optString("结果"))){
						
						JSONObject value=jo.optJSONObject("返回");
						if(jo.optString("action").equals("点赞"))
						{
							AlbumMsgInfo u=new AlbumMsgInfo(value);
							
							String imageId=value.optString("imageId");
							for(int i=0;i<imageList.size();i++)
							{
								if(imageList.get(i).getName().equals(imageId))
								{
									imageList.get(i).getPraiseList().add(0,u);
									if(!praisedList.contains(imageList.get(i)))
										praisedList.add(imageList.get(i));
									break;
								}
							}
							AppUtility.showToastMsg(AlbumShowImagePage.this, "已赞！");
							updateRightButton(mViewpager.getCurrentItem());
							mStuInfoPagerAdapter.notifyDataSetChanged();
							
						}
						else if(jo.optString("action").equals("取消赞"))
						{
							AlbumMsgInfo u=new AlbumMsgInfo(value);
							
							String imageId=value.optString("imageId");
							for(int i=0;i<imageList.size();i++)
							{
								if(imageList.get(i).getName().equals(imageId))
								{
									for(int j=0;j<imageList.get(i).getPraiseList().size();j++)
									{
										if(imageList.get(i).getPraiseList().get(j).getFromId().equals(u.getFromId()))
										{
											imageList.get(i).getPraiseList().remove(j);
											break;
										}
									}
									if(!praisedList.contains(imageList.get(i)))
										praisedList.add(imageList.get(i));
									break;
								}
							}
							AppUtility.showToastMsg(AlbumShowImagePage.this, "已取消赞！");
							updateRightButton(mViewpager.getCurrentItem());
							mStuInfoPagerAdapter.notifyDataSetChanged();
							
						}
						else if(jo.optString("action").equals("评论"))
						{
							AlbumMsgInfo u=new AlbumMsgInfo(value);
							
							String imageId=value.optString("imageId");
							for(int i=0;i<imageList.size();i++)
							{
								if(imageList.get(i).getName().equals(imageId))
								{
									imageList.get(i).getCommentsList().add(0,u);
									if(!commentedList.contains(imageList.get(i)))
										commentedList.add(imageList.get(i));
									break;
								}
							}
							AppUtility.showToastMsg(AlbumShowImagePage.this, "评论成功！");
							replyToLayout.setVisibility(View.GONE);
							mStuInfoPagerAdapter.notifyDataSetChanged();
						}
						else if(jo.optString("action").equals("浏览"))
						{
							
						}
						else if(jo.optString("action").equals("举报"))
						{
							AppUtility.showToastMsg(AlbumShowImagePage.this, "举报成功！");
						}
						else if(jo.optString("action").equals("删除"))
						{
							String imageId=value.optString("文件名");
							for(int i=0;i<imageList.size();i++)
							{
								if(imageList.get(i).getName().equals(imageId))
								{
									deletedList.add(imageList.get(i));
									break;
								}
							}
							
							AppUtility.showToastMsg(AlbumShowImagePage.this, "图片已从服务器删除！");
						}
						
					}
					else
						AppUtility.showToastMsg(AlbumShowImagePage.this, jo.optString("结果"));
				}catch (Exception e) {
					AppUtility.showToastMsg(AlbumShowImagePage.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			case 3:
				proBar.setVisibility(View.INVISIBLE);
				mStuInfoPagerAdapter.notifyDataSetChanged();
				updateRightButton(mViewpager.getCurrentItem());
				break;
			}
		}
	};
	public CallBackInterface callBack=new CallBackInterface()
	{

		@Override
		public void getLocation1() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getPictureByCamera1() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getPictureFromLocation1() {
			// TODO Auto-generated method stub
			AlbumImageInfo image=imageList.get(mViewpager.getCurrentItem());
			String imagePath=ImageLoader.getInstance().getDiscCache().get(image.getUrl()).getAbsolutePath();
			saveToDisk(imagePath,image.getName());
		}

		@Override
		public void sendCall1() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendMsg1() {
			// TODO Auto-generated method stub
			
		}
		
	};

}

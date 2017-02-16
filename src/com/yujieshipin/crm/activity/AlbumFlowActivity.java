package com.yujieshipin.crm.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.adapter.WaterfallAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.AlbumImageInfo;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.service.Alarmreceiver;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.HttpMultipartPost;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.ZLibUtils;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.widget.MultiColumnListView;
import com.yujieshipin.crm.widget.SegmentedGroup;

public class AlbumFlowActivity extends FragmentActivity  implements RadioGroup.OnCheckedChangeListener,SwipeRefreshLayout.OnRefreshListener{

	public static LinearLayout layout_menu;
	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	private static final int MY_PERMISSIONS_REQUEST_LOCATION=5;
	private static final int MY_PERMISSIONS_REQUEST_Camera= 6;
	private static final int MY_PERMISSIONS_REQUEST_Album = 7;
	private String picturePath;
	private User user;
	private static final String SD_PATH = "相册";
	private AlbumImageInfo imageInfo;
	
	private LinearLayout loadingLayout;
	private SwipeRefreshLayout swipeLayout; 
	private RadioButton btn21,btn22,btn23;
	private String ACTION_NAME="hasUnreadAlbumMsg";
	private TextView unreadMsgCount;
	public ArrayList<AlbumMsgInfo> unreadList=new ArrayList<AlbumMsgInfo>();
	private ArrayList<AlbumImageInfo> imageList=new ArrayList<AlbumImageInfo>();
	private MultiColumnListView mclv = null;
	private WaterfallAdapter mAdapter = null;
	
	DatabaseHelper database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_album_flow);
		
		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getApplicationContext());
		
		layout_menu = (LinearLayout) findViewById(R.id.layout_btn_left);
		unreadMsgCount=(TextView)findViewById(R.id.unreadMsgCount);
		Button btnLeft = (Button) findViewById(R.id.btn_left);
		btnLeft.setBackgroundResource(R.drawable.bg_title_homepage_back);
		btnLeft.setVisibility(View.VISIBLE);
		Button btnRight = (Button) findViewById(R.id.btn_right);
		btnRight.setBackgroundResource(R.drawable.photograph);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showGetPictureDiaLog();
			}
			
		});
		unreadMsgCount.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AlbumFlowActivity.this,AlbumShowMessage.class);
				intent.putExtra("ifRead", "0");
				startActivity(intent);
			}
			
		});
		layout_menu.setOnClickListener(TabHostActivity.menuListener);
		SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.segmentedGroup2);
		segmented2.setTintColor(Color.DKGRAY);
		
		segmented2.setOnCheckedChangeListener(this);
		btn21 = (RadioButton) findViewById(R.id.button21);
		btn22 = (RadioButton) findViewById(R.id.button22);
		btn23 = (RadioButton) findViewById(R.id.button23);
		
		mclv=(MultiColumnListView) findViewById(R.id.list);
		
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		
		swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swip);  
        swipeLayout.setOnRefreshListener(this); 
       
        // 顶部刷新的样式  
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,  
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);  
		
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();

		if(user.getUserType().equals("老师"))
			btn22.setText("本部门");
		
		if(user.getLatestAddress().isEmpty())
		{
			if (Build.VERSION.SDK_INT >= 23)
		    {
		          if (ContextCompat.checkSelfPermission(getParent(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		          {  
		        	  ActivityCompat.requestPermissions(getParent(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
		          }     
		          else
		        	  getLocation();
		    }
			else
				getLocation();
		}
		if(!btn21.isChecked() && !btn22.isChecked() && !btn23.isChecked())
			btn21.setChecked(true);
		registerBoradcastReceiver();
		
		((TabHostActivity)getParent()).callBack=callBack;
		
	}

	private void getLocation()
	{
		Intent intent = new Intent(this, Alarmreceiver.class);
		intent.setAction("reportLocation");
		sendBroadcast(intent);
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		updateMsgTip();
		
	}
	public void onRefresh() {  
        new Handler().postDelayed(new Runnable() {  
            public void run() {  
            	
                getDownloadSubject(false); 
            }  
        }, 50);  
    }
	
	private void getDownloadSubject(boolean showProg) {
		showProgress(showProg);

		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			if(btn21.isChecked())
				jo.put("范围","全校");
			else if(btn22.isChecked())
			{
				
			}
			else if(btn23.isChecked())
				jo.put("范围","人气");
			
			if(!showProg && imageList.size()>0)
				jo.put("curImageName", imageList.get(0).getName());
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getAlbumList(params, new RequestListener() {

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
				msg.what = 3;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
	}
	
	private void showProgress(boolean progress) {
		if (progress) {
			
			swipeLayout.setVisibility(View.GONE);
			loadingLayout.setVisibility(View.VISIBLE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			swipeLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private void showGetPictureDiaLog() {
		View view = getLayoutInflater()
				.inflate(R.layout.view_get_picture, null);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
		TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);

		final AlertDialog ad = new AlertDialog.Builder(this).setView(view)
				.create();

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
		byCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) 
				{
					if(AppUtility.checkPermission(getParent(), MY_PERMISSIONS_REQUEST_Camera,Manifest.permission.CAMERA))
						getPictureByCamera();
				}
				else
					getPictureByCamera();
				ad.dismiss();
			}
		});
		byLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= 23) 
				{
					if(AppUtility.checkPermission(getParent(),MY_PERMISSIONS_REQUEST_Album,Manifest.permission.READ_EXTERNAL_STORAGE))
						getPictureFromLocation();
				}
				else
					getPictureFromLocation();
				ad.dismiss();
			}
		});
	}
	
	
	private synchronized void getPictureByCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			AppUtility.showToastMsg(this, "没有安装SD卡，无法使用相机功能");
			return;
		}
		picturePath = FileUtility.getRandomSDFileName("jpg");
		
		File mCurrentPhotoFile = new File(picturePath);

		Uri uri = Uri.fromFile(mCurrentPhotoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
	}
	
	public void getPictureFromLocation() {
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
			AppUtility.showToastMsg(this, "SD卡不可用");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case REQUEST_CODE_TAKE_CAMERA: // 拍照返回
			if (resultCode == RESULT_OK) {
				fileUploadWay(new File(picturePath));
			}
			break;
		case REQUEST_CODE_TAKE_PICTURE:
			if (data != null) {
				
				//picturePath = data.getStringExtra("filepath");
				//String myImageUrl = data.getDataString();
				//Uri uri = Uri.parse(myImageUrl);
				Uri uri = data.getData();
				String[] pojo  = { MediaStore.Images.Media.DATA };
				CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null); 
				Cursor cursor = cursorLoader.loadInBackground();
				if(cursor!=null)
				{
					cursor.moveToFirst(); 
					picturePath = cursor.getString(cursor.getColumnIndex(pojo[0]));
				}
				else
				{
					if(uri.toString().startsWith("file://"))
					{
						picturePath=uri.toString().replace("file://", "");
					}
					else
					{
						AppUtility.showErrorToast(this, "获取相册图片失败");
						return;
					}
				}
				
				String tempPath =FileUtility.getRandomSDFileName("jpg");
				if(FileUtility.copyFile(picturePath,tempPath))
					fileUploadWay(new File(tempPath));
				else
					AppUtility.showErrorToast(this, "向SD卡复制文件出错");
			}
			break;
		case 3:
			if(resultCode==200)
			{
				boolean flag=false;
				ArrayList<AlbumImageInfo> praisedList=(ArrayList<AlbumImageInfo>) data.getSerializableExtra("praisedList");  //data为B中回传的Intent
				
				for(int i=0;i<praisedList.size();i++)
				{
					AlbumImageInfo image=praisedList.get(i);
					for(int j=0;j<imageList.size();j++)
					{
						
						AlbumImageInfo image1=(AlbumImageInfo) imageList.get(j);
						if(image1.getName().equals(image.getName()))
						{
							image1.setPraiseCount(image.getPraiseList().size());
							flag=true;
							break;
						}
					}
				}
				ArrayList<AlbumImageInfo> deletedList=(ArrayList<AlbumImageInfo>) data.getSerializableExtra("deletedList");  //data为B中回传的Intent
				
				for(int i=0;i<deletedList.size();i++)
				{
					AlbumImageInfo image=deletedList.get(i);
					for(int j=0;j<imageList.size();j++)
					{
						if(imageList.get(j).getName().equals(image.getName()))
						{
							imageList.remove(j);
							flag=true;
							break;
						}
					}
				}
				if(flag)
				{
					mAdapter=new WaterfallAdapter(imageList,this);
					mclv.setAdapter(mAdapter);
				}
			}
			
			break;
		default:
			break;
		}
	}
	private void fileUploadWay(final File file) {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*2){
			AppUtility.showToastMsg(this, "对不起，您上传的文件太大了，请选择小于10M的文件！");
		}else{
			
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			imageInfo = new AlbumImageInfo();
			String filename = file.getName();
			imageInfo.setName(filename);
			imageInfo.setLocalPath(file.getAbsolutePath());
			
			LayoutInflater inflater = LayoutInflater.from(this);
			View layout=inflater.inflate(R.layout.dialog_album_descrption,null);
			ImageView iv=(ImageView)layout.findViewById(R.id.theImage);
			iv.setImageBitmap(ImageUtility.getDiskBitmapByPath(file.getAbsolutePath()));
			final TextView tv=(TextView)layout.findViewById(R.id.theDescription);
			//user=((CampusApplication)getApplicationContext()).getLoginUserObj();
			if(user.getLatestAddress().isEmpty())
				tv.setVisibility(ViewGroup.GONE);
			else
			{
				String[] addressArray=user.getLatestAddress().split("\n");
				tv.setText(addressArray[0]);
				imageInfo.setAddress(addressArray[0]);
			}
			
			TextView theDevice=(TextView)layout.findViewById(R.id.theDevice);
			String model=android.os.Build.MODEL;
			if(model.isEmpty())
				theDevice.setVisibility(ViewGroup.GONE);
			else
			{
				theDevice.setText("来自："+model);
				imageInfo.setDevice(model);
			}
			
			final EditText et=(EditText)layout.findViewById(R.id.editText1);
			final RadioButton radio0=(RadioButton)layout.findViewById(R.id.radio0);
			RadioButton radio1=(RadioButton)layout.findViewById(R.id.radio1);
			if(user.getUserType().equals("老师"))
				radio1.setText("仅本部门可见");
			new AlertDialog.Builder(this).setTitle("请输入图片的描述")
			.setCancelable(false)
			.setView(layout)
			.setPositiveButton(R.string.go, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try 
					{  
			            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
			            field.setAccessible(true);  
			            field.set(dialog, false);  
			        
						if(et.getText().length()>0)
						{
							if(et.getText().length()>100)
							{
								
								AppUtility.showToastMsg(AlbumFlowActivity.this, "描述文字不能超过100字");
								return;
							}
							imageInfo.setDescription(et.getText().toString());
						}
						if(radio0.isChecked())
							imageInfo.setShowLimit("全校");
						else
							imageInfo.setShowLimit("本班");
						SubmitUploadFile(imageInfo);
						((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
						field.set(dialog, true);  
						dialog.dismiss();
					} catch (Exception e) {  
			            e.printStackTrace();  
			        }
				}})
			.setNegativeButton("取消",new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {  
			            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
			            field.setAccessible(true);  
			            field.set(dialog, true);  
			        } catch (Exception e) {  
			            e.printStackTrace();  
			        }
					file.delete();
					dialog.dismiss();
				}
			
				})
			.show();
			
			
			
			
		}
	}
	
	public void SubmitUploadFile(final AlbumImageInfo image){
		CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		/*
		params.add("用户较验码", checkCode);
		params.add("课程名称", downloadSubject.getCourseName());
		params.add("老师姓名", downloadSubject.getUserName());
		params.add("文件名", downloadSubject.getFileName());
		*/
		params.add("JiaoYanMa", checkCode);
		
		
		params.add("pic", image.getLocalPath());
		params.add("TuPianLeiBie", "相册");
		params.add("Description", image.getDescription());
		params.add("Address", image.getAddress());
		params.add("ShowLimit", image.getShowLimit());
		params.add("device", image.getDevice());
		HttpMultipartPost post = new HttpMultipartPost(this, params){
			@Override  
		    protected void onPostExecute(String result) {  
				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				bundle.putSerializable("image", image);
				Message msg = new Message();
				msg.what = 5;
				msg.obj = bundle; 
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
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
				
				AppUtility.showErrorToast(AlbumFlowActivity.this,
						msg.obj.toString());
				
				break;
			case 3:// 获取相册
				
				swipeLayout.setRefreshing(false);  
				final String result1 = msg.obj.toString();
				
				Thread thread=new Thread(new Runnable()  
		        {  
		            @Override  
		            public void run()  
		            {  
		            	byte [] resultByte = null;
						String unZlibStr=null;
						if (AppUtility.isNotEmpty(result1)) 
						{
							try {
								resultByte = Base64.decode(result1
										.getBytes("GBK"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							unZlibStr = ZLibUtils.decompress(resultByte);
						}
						try {
							JSONObject job=new JSONObject(unZlibStr);
							String curImageName=job.optString("curImageName");
							JSONArray jo = job.getJSONArray("相册");
							List<AlbumImageInfo> newlist = AlbumImageInfo.toList(jo);
							
								if(curImageName!=null && !curImageName.equals("null") && curImageName.length()>0)
								{
									for(int i=newlist.size()-1;i>=0;i--)
									{
										imageList.add(0,newlist.get(i));
									}
								}
								else
									imageList=(ArrayList<AlbumImageInfo>) newlist;
								
						} catch (JSONException e) {
							e.printStackTrace();
						}  
						Message msg = new Message();
						msg.what = 6;
						mHandler.sendMessage(msg);	
		            }  
		        });  
		        thread.start();  
				
				break;
			case 5:
				
				Bundle	upLoadbundle = (Bundle) msg.obj;
				result = upLoadbundle.getString("result");
				AlbumImageInfo image = (AlbumImageInfo) upLoadbundle.getSerializable("image");
				
				
				try {
					resultStr = new String(Base64.decode(result.getBytes("GBK")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				try {
					JSONObject jo = new JSONObject(resultStr);
					
					if("OK".equals(jo.optString("STATUS"))){
						DialogUtility.showMsg(AlbumFlowActivity.this, "上传成功！");
						
						AlbumImageInfo ds = new AlbumImageInfo(jo);
						String newFileName=FileUtility.creatSDDir(SD_PATH)+ds.getName();
						FileUtility.copyFile(image.getLocalPath(),newFileName);
						FileUtility.deleteFile(image.getLocalPath());
						ds.setLocalPath(newFileName);
						boolean flag=false;
						if(ds.getShowLimit().equals("全校"))
						{
							imageList.add(0, ds);
							flag=true;
						}
						else
						{
							if(btn22.isChecked())
							{
								imageList.add(0, ds);
								flag=true;
							}
						}
						if(flag)
						{
							mAdapter=new WaterfallAdapter(imageList,AlbumFlowActivity.this);
							mclv.setAdapter(mAdapter);
						}
						//downloadSubjectDao.create(ds);
						//将标识更新为isModify=0
						//PreparedUpdate<DownloadSubject> preparedUpdateDownloadSubject = (PreparedUpdate<DownloadSubject>) downloadSubjectDao.updateBuilder().updateColumnValue("isModify", 0).where().eq("isModify", 1).prepare();
						//downloadSubjectDao.update(preparedUpdateDownloadSubject);
					
						
						//getDownloadSubject();
						
					}else{
						DialogUtility.showMsg(AlbumFlowActivity.this, "上传失败！");
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(AlbumFlowActivity.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			case 6:
				showProgress(false); 
				mAdapter=new WaterfallAdapter(imageList,AlbumFlowActivity.this);
				mclv.setAdapter(mAdapter);
			
			
				if (imageList.size()==0) {
					String tipmsg="目前还没有照片，点击右上按钮开始上传第一张照片吧！";
					AppUtility.showToastMsg(AlbumFlowActivity.this, tipmsg);
				}
				
				break;
			}
		}
	};
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		 switch (checkedId) {
         case R.id.button21:
        	 getDownloadSubject(true);
             return;
         case R.id.button22:
        	 getDownloadSubject(true);
             return;
         case R.id.button23:
        	 getDownloadSubject(true);
             return;
		 }
	}
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NAME)) {
				updateMsgTip();
			}
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}
	private ArrayList<AlbumMsgInfo> getUnreadList()
	{
		unreadList.clear();
		try {
			String hostId=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
			unreadList=(ArrayList<AlbumMsgInfo>) getHelper().getAlbumMsgDao().queryBuilder().orderBy("id", false).where().eq("ifRead",0).and().eq("toId", hostId).query();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unreadList;
	}
	private void updateMsgTip()
	{
		ArrayList<AlbumMsgInfo> al=getUnreadList();
		if(al!=null && al.size()>0)
		{
			unreadMsgCount.setVisibility(View.VISIBLE);
			unreadMsgCount.setText("你有 "+al.size()+" 条未读消息，点击查看");
		}
		else
			unreadMsgCount.setVisibility(View.GONE);
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("imageList",imageList);
		savedInstanceState.putString("picturePath", picturePath);
		
		
		
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        imageList = (ArrayList<AlbumImageInfo>) savedInstanceState.getSerializable("imageList");
        picturePath=savedInstanceState.getString("picturePath");
    }
	public CallBackInterface callBack=new CallBackInterface()
	{

		@Override
		public void getLocation1() {
			// TODO Auto-generated method stub
			getLocation();
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

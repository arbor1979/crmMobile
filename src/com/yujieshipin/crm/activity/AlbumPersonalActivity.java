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
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
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
import com.yujieshipin.crm.entity.AlbumImageInfo;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.service.Alarmreceiver;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.HttpMultipartPost;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;

public class AlbumPersonalActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener{

	public static LinearLayout layout_menu;
	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	public static final int MY_PERMISSIONS_REQUEST_Camera=6;
	private String picturePath,hostId,userId;
	private User user;
	private static final String SD_PATH = "相册";
	private AlbumImageInfo imageInfo;
	private ListView msv;
	private String mInterface = "AlbumPraise.php";
	private ArrayList<AlbumImageInfo> list=new ArrayList<AlbumImageInfo>();
	private LinearLayout loadingLayout;
	private SwipeRefreshLayout swipeLayout; 
	private PersonalAdapter pAdpter;
	private Dao<User, Integer> userDao;
	DatabaseHelper database;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_album_personal);
		TextView title=(TextView)findViewById(R.id.setting_tv_title);
		
		msv=(ListView)findViewById(R.id.listView1);
		Button bn_back = (Button) findViewById(R.id.back);
		bn_back.setVisibility(View.VISIBLE);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
	
		String hostName=getIntent().getStringExtra("hostName");
		title.setText(hostName+"的相册");
		if(user.getLatestAddress().isEmpty())
		{
			if (Build.VERSION.SDK_INT >= 23)
		    {
		          if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		          {  
		        	  ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);
		          }     
		          else
		        	  getLocation();
		    }
			else
				getLocation();
			
		}
		hostId=getIntent().getStringExtra("hostId");
		userId = PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
		if(hostId==null || hostId.length()==0)
			hostId=userId;
		if(userId.equals(hostId))
		{
			title.setText("我的相册");
			LinearLayout layout=(LinearLayout)findViewById(R.id.setting_layout_goto);
			layout.setVisibility(View.VISIBLE);
			Button btnRight = (Button) findViewById(R.id.setting_btn_goto);
			btnRight.setBackgroundResource(R.drawable.photograph);
			layout.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					showGetPictureDiaLog();
					
				}
				
			});
			LinearLayout layout1=(LinearLayout)findViewById(R.id.setting_layout_goto1);
			layout1.setVisibility(View.VISIBLE);
			Button btnRight1 = (Button) findViewById(R.id.setting_btn_goto1);
			btnRight1.setBackgroundResource(R.drawable.album_message_history);
			layout1.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(AlbumPersonalActivity.this,AlbumShowMessage.class);
					intent.putExtra("ifRead", 1);
					startActivity(intent);
					
				}
				
			});
		}
		loadingLayout = (LinearLayout) findViewById(R.id.data_load);
		
		swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.swip);  
        swipeLayout.setOnRefreshListener(this); 
       
        // 顶部刷新的样式  
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,  
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);  
        getDownloadSubject(true);
        pAdpter=new PersonalAdapter(this);
        msv.setAdapter(pAdpter);
	}
	private void getLocation()
	{
		Intent intent = new Intent(this, Alarmreceiver.class);
		intent.setAction("reportLocation");
		sendBroadcast(intent);
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
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
			
			jo.put("action","个人相册");
			jo.put("hostId",hostId);
			jo.put("用户较验码", checkCode);
			jo.put("DATETIME", datatime);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String base64Str = Base64.encode(jo.toString().getBytes());
		CampusParameters params = new CampusParameters();
		params.add(Constants.PARAMS_DATA, base64Str);
		CampusAPI.getDownloadSubject(params, mInterface, new RequestListener() {

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
					if(AppUtility.checkPermission(AlbumPersonalActivity.this, 6,Manifest.permission.CAMERA))
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
					if(AppUtility.checkPermission(AlbumPersonalActivity.this,7,Manifest.permission.READ_EXTERNAL_STORAGE))
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
				ArrayList<AlbumImageInfo> praisedList=(ArrayList<AlbumImageInfo>) data.getSerializableExtra("praisedList");  //data为B中回传的Intent
				for(int i=0;i<praisedList.size();i++)
				{
					AlbumImageInfo image=praisedList.get(i);
					for(int j=0;j<list.size();j++)
					{
						AlbumImageInfo image1=list.get(j);
						if(image1.getName().equals(image.getName()))
						{
							image1.setPraiseCount(image.getPraiseList().size());
							break;
						}
					}
				}
				ArrayList<AlbumImageInfo> commentedList=(ArrayList<AlbumImageInfo>) data.getSerializableExtra("commentedList");  //data为B中回传的Intent
				for(int i=0;i<commentedList.size();i++)
				{
					AlbumImageInfo image=commentedList.get(i);
					for(int j=0;j<list.size();j++)
					{
						AlbumImageInfo image1=list.get(j);
						if(image1.getName().equals(image.getName()))
						{
							image1.setCommentCount(image.getCommentsList().size());
							break;
						}
					}
				}
				ArrayList<AlbumImageInfo> deletedList=(ArrayList<AlbumImageInfo>) data.getSerializableExtra("deletedList");  //data为B中回传的Intent
				for(int i=0;i<deletedList.size();i++)
				{
					AlbumImageInfo image=deletedList.get(i);
					for(int j=0;j<list.size();j++)
					{
						if(list.get(j).getName().equals(image.getName()))
						{
							list.remove(j);
							break;
						}
					}
				}
				pAdpter.notifyDataSetChanged();
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
								
								AppUtility.showToastMsg(AlbumPersonalActivity.this, "描述文字不能超过100字");
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
		params.add("device", image.getDevice());
		params.add("ShowLimit", image.getShowLimit());
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
				
				AppUtility.showErrorToast(AlbumPersonalActivity.this,
						msg.obj.toString());
				
				break;
			case 3:// 获取相册
				showProgress(false); 
				swipeLayout.setRefreshing(false);  
				result = msg.obj.toString();
				resultStr = "";
				if (AppUtility.isNotEmpty(result)) {
					try {
						resultStr = new String(Base64.decode(result
								.getBytes("GBK")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					JSONObject jo=new JSONObject(resultStr);
					JSONArray ja = jo.getJSONArray("相册");
					list.clear();
					if(ja!=null)
						list=AlbumImageInfo.toList(ja);
					
					if (list.size()==0) {
						String tipmsg="目前还没有照片";
						AppUtility.showToastMsg(AlbumPersonalActivity.this, tipmsg);
					}
					pAdpter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
						DialogUtility.showMsg(AlbumPersonalActivity.this, "上传成功！");
						
						AlbumImageInfo ds = new AlbumImageInfo(jo);
						String newFileName=FileUtility.creatSDDir(SD_PATH)+ds.getName();
						FileUtility.copyFile(image.getLocalPath(),newFileName);
						FileUtility.deleteFile(image.getLocalPath());
						ds.setLocalPath(newFileName);
						list.add(0,ds);
						
						pAdpter.notifyDataSetChanged();
						
					}else{
						DialogUtility.showMsg(AlbumPersonalActivity.this, "上传失败！");
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(AlbumPersonalActivity.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			}
		}
	};
	public class PersonalAdapter extends BaseAdapter{

		private Context context;
		public PersonalAdapter(Context ct)
		{
			context=ct;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh;
			AQuery aq = new AQuery(convertView);
			if (convertView == null) 
			{		
				convertView = LayoutInflater.from(AlbumPersonalActivity.this).inflate(R.layout.activity_album_personallist_item, null);
				
				vh=new ViewHolder();
				vh.theDay=(TextView)convertView.findViewById(R.id.theDay);
				vh.theMonth=(TextView)convertView.findViewById(R.id.theMonth);
				vh.theAddress=(TextView)convertView.findViewById(R.id.theAddress);
				vh.theImage=(ImageView)convertView.findViewById(R.id.theImage);
				vh.theDescription=(TextView)convertView.findViewById(R.id.theDescription);
				vh.thePraise=(TextView)convertView.findViewById(R.id.thePraise);
				vh.theComment=(TextView)convertView.findViewById(R.id.theComment);
				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			
			AlbumImageInfo aii=list.get(position);
			vh.theDay.setText("");
			vh.theMonth.setText("");
			vh.thePraise.setText("0");
			vh.theComment.setText("0");
			boolean flag=false;
			if(position==0)
			{
				flag=true;
			}
			else
			{
				AlbumImageInfo last=list.get(position-1);
				if(!last.getTime().substring(0, 10).equals(aii.getTime().substring(0, 10)))
				{
					flag=true;
				}
			}
			if(flag)
			{
				vh.theDay.setText(aii.getTime().substring(8,10));
				vh.theMonth.setText(aii.getTime().substring(5,7)+"月");
			}
			
			BitmapAjaxCallback cb = new BitmapAjaxCallback(){
		        @Override
		        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		            if(status.getCode()==200) {
		                super.callback(url, iv, bm,status);
		                ImageUtility.writeTofiles(bm, getImagePath(url));
		            } 
		        }            
		    };
			aq.id(vh.theImage).image(aii.getUrl(),false,true,200,R.drawable.empty_photo,cb);
			vh.theAddress.setText(aii.getAddress());
			vh.theDescription.setText(aii.getDescription());
			vh.thePraise.setText(String.valueOf(aii.getPraiseCount()));
			vh.theComment.setText(String.valueOf(aii.getCommentCount()));
			vh.theImage.setTag(position);
			convertView.setOnClickListener(new OnClickListener(){
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index=(Integer) v.findViewById(R.id.theImage).getTag();
					ArrayList<AlbumImageInfo> detailList=new ArrayList<AlbumImageInfo>();
					int PAGE_SIZE=20;
					for(int i=index;i<list.size();i++)
					{
						detailList.add(list.get(i));
						if(detailList.size()>PAGE_SIZE)
							break;
					}
					Intent intent=new Intent(context,AlbumShowImagePage.class);
					intent.putExtra("imageList", detailList);
					intent.putExtra("allList", list);
					((FragmentActivity) context).startActivityForResult(intent,3);
				}
				
			});
			
			return convertView;
		}
		public class ViewHolder {
			public TextView theDay;
			public TextView theMonth;
			public TextView theAddress;
			public ImageView theImage;
			public TextView theDescription;
			public TextView thePraise;
			public TextView theComment;
			
		}
		
	}
	private String getImagePath(String imageUrl) {
		String imageName=FileUtility.getFileRealName(imageUrl);
		String imageDir = FileUtility.creatSDDir("相册");
		String imagePath = imageDir + imageName;
		return imagePath;
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("user",user);
		savedInstanceState.putString("picturePath", picturePath);
		
		
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        user = (User) savedInstanceState.getSerializable("user");
        picturePath=savedInstanceState.getString("picturePath");
    }
	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		CallBackInterface callBack=new CallBackInterface()
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
		AppUtility.permissionResult(requestCode,grantResults,this,callBack);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}

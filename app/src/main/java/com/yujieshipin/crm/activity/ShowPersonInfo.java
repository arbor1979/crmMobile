package com.yujieshipin.crm.activity;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.BuildConfig;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.adapter.MyPictureAdapter;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.HttpMultipartPost;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;
import com.yujieshipin.crm.widget.NonScrollableGridView;

public class ShowPersonInfo extends Activity {

	public static final int REQUEST_CODE_TAKE_PICTURE = 2;// //设置图片操作的标志
	public static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
	private static final int GETUSERINFO_CODE=1,CHANGEUSERINFO_CODE=2;
	private String picturePath;
	private String studentId;
	private String userId;
	private String userImage;
	private int userType;
	AQuery aq;
	DatabaseHelper database;
	JSONObject userObj;
	String[] keyList;
	User user;
	MyAdapter adapter;
	Button changeheader;
	private Dao<User, Integer> userDao;
	ImageView headImgView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
        headImgView=(ImageView)findViewById(R.id.iv_pic);
        RelativeLayout headerlayout=(RelativeLayout)findViewById(R.id.headerlayout);
        int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			headerlayout.setBackgroundColor(color);
		studentId = getIntent().getStringExtra("studentId");
		if(studentId==null)
			studentId="";
		userId=getIntent().getStringExtra("userId");
		if(userId==null)
			userId="";
		userImage = getIntent().getStringExtra("userImage");
		userType = Integer.parseInt(getIntent().getStringExtra("userType"));
		try {
			userDao = getHelper().getUserDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		
		if(userType>0 && studentId.equals(user.getId()))//用户
		{
			changeheader= (Button) findViewById(R.id.bt_changeHeader);
			changeheader.setVisibility(View.VISIBLE);
			changeheader.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showGetPictureDiaLog();
				}
				
			});
		}
		else if(userType==-1)//产品
		{
			changeheader= (Button) findViewById(R.id.bt_changeHeader);
			changeheader.setVisibility(View.VISIBLE);
			changeheader.setText("更换图片");
			changeheader.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					showGetPictureDiaLog();
				}

			});
		}
		aq = new AQuery(this);
		userObj=new JSONObject();
		getPrivateAlbum();
		initContent();
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}
	
	private void initContent() {
		
        ImageOptions options = new ImageOptions();
        options.memCache=false;
        options.fileCache=true;
        options.fallback=R.drawable.ic_launcher1;
		options.targetWidth=200;
		options.round = 100;
		aq.id(R.id.iv_pic).image(userImage,options);
        
        //ImageLoader.getInstance().displayImage(userImage,headImgView,TabHostActivity.headOptions);

		//aq.id(R.id.iv_pic).image(userImage,flag,flag,800,R.drawable.ic_launcher1);
		aq.id(R.id.tv_name).text(userObj.optString("姓名"));
		aq.id(R.id.user_type).text(userObj.optString("角色名称"));
		if(userType>0)
			aq.id(R.id.setting_tv_title).text("用户信息");
		else if(userType==0)
		{
			aq.id(R.id.setting_tv_title).text("客户资料");
			
		}
		else if(userType==-1)
			aq.id(R.id.setting_tv_title).text("产品资料");
		else if(userType==-2)
			aq.id(R.id.setting_tv_title).text("供应商资料");
		aq.id(R.id.back).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		aq.id(R.id.iv_pic).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//DialogUtility.showImageDialog(ShowPersonInfo.this,userImage);
				Intent intent = new Intent(ShowPersonInfo.this,
						ImagesActivity.class);
				ArrayList<String> picturePaths=new ArrayList<String>();
				picturePaths.add(userImage);
				intent.putStringArrayListExtra("pics",
						(ArrayList<String>) picturePaths);
				if(userObj.optString("图片描述").length()>0)
				{
					ArrayList<String> pictureNames=new ArrayList<String>();
					pictureNames.add(userObj.optString("图片描述"));
					intent.putStringArrayListExtra("txts",
							(ArrayList<String>) pictureNames);
				}
				//intent.putExtra("position", i);

				startActivity(intent);
				
			}
			
		});
		
		/*
		SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.list_left_right,
				new String[]{"title","info"},
				new int[]{R.id.left_title,R.id.right_detail});
		*/
		keyList=userObj.optString("字段顺序").split(",");
		if(keyList==null)
			keyList=new String[0];
		adapter=new MyAdapter(this);
		aq.id(R.id.listView1).adapter(adapter);
		
	}
	
	public class MyAdapter extends BaseAdapter{
		 
        private LayoutInflater mInflater;
        
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
           
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return keyList.length;
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
 
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
             
            ViewHolder holder = null;
            
            if (convertView == null) {
                 
                holder=new ViewHolder();  
                 
                convertView = mInflater.inflate(R.layout.list_left_right, null);
                holder.title = (TextView)convertView.findViewById(R.id.left_title);
                holder.info = (TextView)convertView.findViewById(R.id.right_detail);
                holder.grid_picture= (NonScrollableGridView)convertView.findViewById(R.id.grid_picture);
                holder.bt_changeNumber= (Button)convertView.findViewById(R.id.bt_changeNumber);
                convertView.setTag(holder);
                 
            }else {
                 
                holder = (ViewHolder)convertView.getTag();
            }
            String key=keyList[position];
            holder.title.setText(key);
			if(userObj.optJSONArray(key)!=null)
			{
				holder.info.setVisibility(View.GONE);
				holder.grid_picture.setVisibility(View.VISIBLE);
				ArrayList<String> picturePaths=new ArrayList<String>();
				ArrayList<String> pictureNames=new ArrayList<String>();
				for(int j=0;j<userObj.optJSONArray(key).length();j++)
				{
					JSONObject imageobj=userObj.optJSONArray(key).optJSONObject(j);
					picturePaths.add(imageobj.optString("url"));
					pictureNames.add(imageobj.optString("name"));
				}
				if(holder.grid_picture.getAdapter()==null)
				{
					MyPictureAdapter myPictureAdapter = new MyPictureAdapter(ShowPersonInfo.this,
							false,picturePaths,10,"课堂笔记",position);
					myPictureAdapter.setPicNames(pictureNames);
					holder.grid_picture.setAdapter(myPictureAdapter);
				}
				else
				{
					MyPictureAdapter myPictureAdapter=(MyPictureAdapter) holder.grid_picture.getAdapter();
					myPictureAdapter.setPicPaths(picturePaths);
				}


			}
			else {
				holder.info.setText(userObj.optString(key));
				holder.grid_picture.setVisibility(View.GONE);
				holder.info.setVisibility(View.VISIBLE);
			}

            if(key.equals("手机") && studentId.equals(user.getId()))
            {
            	holder.bt_changeNumber.setVisibility(View.VISIBLE);
            	holder.bt_changeNumber.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				final EditText et=new EditText(ShowPersonInfo.this);
        				et.setInputType(InputType.TYPE_CLASS_PHONE);
        				new AlertDialog.Builder(ShowPersonInfo.this).setTitle("请输入新的联系方式").setView(et)
        				.setPositiveButton("确定", new DialogInterface.OnClickListener()
        				{

        					@Override
        					public void onClick(DialogInterface dialog, int which) {
        						// TODO Auto-generated method stub
        						String newphone=et.getText().toString().trim();
        						if(et.length()!=11)
        						{
        							AppUtility.showToastMsg(ShowPersonInfo.this, "要求11位手机号码！");
        						}
        						else
        							updateUserPhone(newphone);
        					}
        					
        				}).setNegativeButton("取消", null).show();
        				TimeUtility.popSoftKeyBoard(ShowPersonInfo.this,et);
        			}
        			
        		});
            }
            else {
				holder.bt_changeNumber.setVisibility(View.GONE);
				Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
				Linkify.addLinks(holder.info, pattern, "tel:", new Linkify.MatchFilter() {
					public final boolean acceptMatch(CharSequence s, int start, int end) {
						int digitCount = 0;

						for (int i = start; i < end; i++) {
							if (Character.isDigit(s.charAt(i))) {
								digitCount++;
								if (digitCount == 11) {
									return true;
								}
							}
						}
						return false;
					}
				}, Linkify.sPhoneNumberTransformFilter);
			}


            return convertView;
        }
        public final class ViewHolder{
          
            public TextView title;
            public TextView info;
            public NonScrollableGridView grid_picture;
            public Button bt_changeNumber;

        }
         
    }
	private void updateUserPhone(String newphone)
	{
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("function", "changeUserInfo");
			jsonObj.put("fieldName", "MOBIL_NO");
			jsonObj.put("newValue", newphone);
			jsonObj.put("用户较验码", checkCode);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, CHANGEUSERINFO_CODE);
		
		
	}
	private void getPrivateAlbum() 
	{
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("function", "getUserInfo");
			jsonObj.put("用户较验码", checkCode);
			jsonObj.put("uid", studentId);
			jsonObj.put("userId", userId);
			jsonObj.put("userType", userType);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, GETUSERINFO_CODE);
		
		
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			
			String result = "";
			switch (msg.what) {
			case -1:// 请求失败
				
				AppUtility.showErrorToast(ShowPersonInfo.this,
						msg.obj.toString());
				break;
			case GETUSERINFO_CODE:
				
				result = msg.obj.toString();
				
				try {
					JSONObject obj=new JSONObject(result);
					
					if (obj.optString("result").equals("失败")) {
						AppUtility.showToastMsg(ShowPersonInfo.this, obj.optString("errorMsg"),1);
					} 
					else 
					{
						userObj=obj;
						userImage=userObj.optString("用户头像");
						initContent();
						
					}

						
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case CHANGEUSERINFO_CODE:
				result = msg.obj.toString();
				
				try {
					JSONObject jo = new JSONObject(result);
					
					if(jo.optString("result").equals("成功"))
					{
						AppUtility.showToastMsg(ShowPersonInfo.this, "更新成功！");
						userObj.put("手机", jo.optString("newValue"));
						user.setPhone(jo.optString("newValue"));
						try {
							userDao.update(user);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						initContent();
					}
					else
						AppUtility.showToastMsg(ShowPersonInfo.this, "更新失败:"+jo.optString("errorMsg"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case 5:
				result = msg.obj.toString();
				try {
					JSONObject jo = new JSONObject(result);
					
					if("成功".equals(jo.optString("result"))){
						DialogUtility.showMsg(ShowPersonInfo.this, "上传成功！");
						if(userType==-1)
							userImage=jo.optString("文件地址");
						else
							userImage=jo.optString("avatar");
						user.setUserImage(userImage);
						userDao.update(user);
						initContent();
						Intent intent = new Intent("ChangeHead");
						intent.putExtra("newhead", userImage);
						sendBroadcast(intent);
						
					}else{
						DialogUtility.showMsg(ShowPersonInfo.this, "上传失败:"+jo.optString("errorMsg"));
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(ShowPersonInfo.this, e.getMessage());
					e.printStackTrace();
				}	
				break;
			}
		}
	};
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
					if(AppUtility.checkPermission(ShowPersonInfo.this, 6,Manifest.permission.CAMERA))
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
					if(AppUtility.checkPermission(ShowPersonInfo.this,7,Manifest.permission.READ_EXTERNAL_STORAGE))
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", mCurrentPhotoFile)); //Uri.fromFile(tempFile)
        else {
            Uri uri = Uri.fromFile(mCurrentPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
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
				rotateAndCutImage(new File(picturePath));
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
					rotateAndCutImage(new File(tempPath));
				else
					AppUtility.showErrorToast(this, "向SD卡复制文件出错");
			}
			break;
		case 3:
			if (resultCode == 200 && data != null) {
				
				String picPath = data.getStringExtra("picPath");
				SubmitUploadFile(picPath);
			}
		default:
			break;
		}
	}
	private void rotateAndCutImage(final File file) {
		if(!file.exists()) return;
		if(AppUtility.formetFileSize(file.length()) > 5242880*4){
			AppUtility.showToastMsg(this, "对不起，您上传的文件太大了，请选择小于20M的文件！");
		}else{
			ImageUtility.rotatingImageIfNeed(file.getAbsolutePath());
			if(userType==-1)//如果是产品则不必裁切
				SubmitUploadFile(file.getAbsolutePath());
			else {
				Intent intent = new Intent(this, CutImageActivity.class);
				intent.putExtra("picPath", file.getAbsolutePath());
				startActivityForResult(intent, 3);
			}
		}
	}
	
	public void SubmitUploadFile(String picPath){
		CampusParameters params = new CampusParameters();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");// 获取用户校验码
		/*
		params.add("用户较验码", checkCode);
		params.add("课程名称", downloadSubject.getCourseName());
		params.add("老师姓名", downloadSubject.getUserName());
		params.add("文件名", downloadSubject.getFileName());
		*/
		params.add("token", checkCode);
		params.add("pic", picPath);
		params.add("function","uploadAvatar");
		if(userType==-1) {
			params.add("action", "product");
			params.add("productid", studentId);
		}
		else
			params.add("action","user");
		HttpMultipartPost post = new HttpMultipartPost(this, params){
			@Override  
		    protected void onPostExecute(String result) {  
				
				Message msg = new Message();
				msg.what = 5;
				msg.obj = result; 
				mHandler.sendMessage(msg);	
				this.pd.dismiss();
		    }
		};  
        post.execute();
	}
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("userObj",userObj.toString());
		savedInstanceState.putString("picturePath", picturePath);
		
		
	}
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        try {
			userObj = new JSONObject((String) savedInstanceState.getSerializable("userObj"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        picturePath=savedInstanceState.getString("picturePath");
    }
	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		AppUtility.permissionResult(requestCode,grantResults,this,callBack);
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

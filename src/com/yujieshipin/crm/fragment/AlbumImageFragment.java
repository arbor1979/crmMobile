package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.example.androidgifdemo.MyTextViewEx;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.AlbumShowImageDetail;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.activity.TabHostActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.AlbumImageInfo;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;

public class AlbumImageFragment extends Fragment{

	ScrollView scrollView1;
	
	LinearLayout praiseLayout,praiseLayoutDetail,commentLayout;
	ListView mList;
	GridView mGrid;
	AlbumImageInfo image;
	private ImageView main_image,iv_right;
	TextView description,viewMore,device;
	PraiseAdapter praiseAdapter;
	CommentAdapter commentAdapter;
	ArrayList<AlbumMsgInfo> praiseList,commentList;
	ProgressBar pbLoad;
	int screenWidth;
	//AttendAdapter mAttend;
	private Drawable drawable;
	TextView publisher_name,brower_count,address,time1,praise_count,comments_count;
	View localView;
    ArrayList<MyTextViewEx> MyTextList;
    ArrayList<ImageView> imgViewList;
    ImageView headimgView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(image==null)
			image=(AlbumImageInfo) getArguments().getSerializable("imageInfo");

		if(!ImageLoader.getInstance().isInited())
			AppUtility.iniImageLoader(getActivity().getApplicationContext());
        MyTextList=new ArrayList<MyTextViewEx>();
        imgViewList=new ArrayList<ImageView>();
		localView = inflater.inflate(R.layout.fragment_album_image, container, false);
		
		AQuery aq = new AQuery(localView);
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		
		
		scrollView1=(ScrollView)localView.findViewById(R.id.scrollView1);
		praiseLayout=(LinearLayout)localView.findViewById(R.id.praiseLayout);
		praiseLayoutDetail=(LinearLayout)localView.findViewById(R.id.praiseLayoutDetail);
		commentLayout=(LinearLayout)localView.findViewById(R.id.commentLayout);
		iv_right=(ImageView)localView.findViewById(R.id.iv_right);
		mList = (ListView)localView.findViewById(R.id.listView1);
		mGrid=(GridView)localView.findViewById(R.id.gridView1);
		publisher_name=(TextView)localView.findViewById(R.id.publisher_name);
		
		publisher_name.setText(image.getHostName()+" "+image.getHostBanji());
		brower_count=(TextView)localView.findViewById(R.id.brower_count);
		brower_count.setText(String.valueOf(image.getBrowerCount()+1)+" 次浏览");
		address=(TextView)localView.findViewById(R.id.address);
		if(image.getAddress().isEmpty())
			address.setVisibility(View.GONE);
		else
			address.setText(image.getAddress());
		time1=(TextView)localView.findViewById(R.id.time1);
		time1.setText(image.getTime());
		praise_count=(TextView)localView.findViewById(R.id.praise_count);
		comments_count=(TextView)localView.findViewById(R.id.comments_count);
		main_image=(ImageView)localView.findViewById(R.id.main_image);
		pbLoad=(ProgressBar)localView.findViewById(R.id.pb_load);
		viewMore=(TextView)localView.findViewById(R.id.viewMore);
	

		ImageLoader.getInstance().displayImage(image.getUrl(), main_image,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
						main_image.setImageDrawable(drawable);
						pbLoad.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "can not be decoding";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "内存不足";
							if (null == AlbumImageFragment.this||!AlbumImageFragment.this.isAdded()) {
				                return;
				            }
							Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
									.show();
							break;
						case UNKNOWN:
							message = "Unknown error";
							Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
									.show();
							break;
						}
						pbLoad.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if(loadedImage!=null)
						{
							pbLoad.setVisibility(View.GONE);
							int height=screenWidth*loadedImage.getHeight()/loadedImage.getWidth();
							main_image.getLayoutParams().height=height;
						}
					}

					@Override
					public void onLoadingCancelled(String paramString,
							View paramView) {
					}
				});
		
		
		if(image.getHeadUrl()!=null && image.getHeadUrl().length()>0)
		{
			/*
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(R.id.publisher_head).image(image.getHeadUrl(), options);
			*/
			headimgView=(ImageView)localView.findViewById(R.id.publisher_head);
			ImageLoader.getInstance().displayImage(image.getHeadUrl(),headimgView, AppUtility.headOptions);
			
		}
		aq.id(R.id.publisher_head).clicked(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						ShowPersonInfo.class);
				
				intent.putExtra("studentId", image.getHostId());
				intent.putExtra("userImage", image.getHeadUrl());
				startActivity(intent);
			}
			
		});
		description=(TextView)localView.findViewById(R.id.theDescription);
		if(image.getDescription().isEmpty())
			description.setVisibility(View.GONE);
		else
			description.setText(image.getDescription());
		
		device=(TextView)localView.findViewById(R.id.device);
		if(image.getDevice().isEmpty())
			device.setVisibility(View.GONE);
		else
			device.setText("来自："+image.getDevice());
		
		if(image.getPraiseList().size()>0)
		{
			praiseLayout.setVisibility(View.VISIBLE);
			praiseLayoutDetail.setVisibility(View.VISIBLE);
			praise_count.setText(String.valueOf(image.getPraiseList().size()));
			/*
			LayoutParams linearParams = (LinearLayout.LayoutParams)mGrid.getLayoutParams(); // 取控件mGrid当前的布局参数
			linearParams.width = image.getPraiseList().size() * (114);
			mGrid.setLayoutParams(linearParams);
			mGrid.setNumColumns(image.getPraiseList().size());
			*/
			
			praiseList=new ArrayList<AlbumMsgInfo>();
			int colWidth=AppUtility.getPixByDip(getActivity(), 60);
			
			
            
			int maxShowPraise=(int) Math.floor(screenWidth/colWidth);
			if(maxShowPraise>=image.getPraiseList().size())
			{
				maxShowPraise=image.getPraiseList().size();
				iv_right.setVisibility(View.GONE);
			}
			else
			{
				iv_right.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(getActivity(),AlbumShowImageDetail.class);
						intent.putExtra("userList", image.getPraiseList());
						intent.putExtra("type", "喜欢");
						getActivity().startActivity(intent);
					}
					
				});
			}
			mGrid.setNumColumns(maxShowPraise);
			mGrid.setColumnWidth(colWidth);
			mGrid.setStretchMode(GridView.NO_STRETCH); 
			for(int i=0;i<maxShowPraise;i++)
			{
				praiseList.add(image.getPraiseList().get(i));
			}
			praiseAdapter=new PraiseAdapter();
			mGrid.setAdapter(praiseAdapter);
			
		}
		else
		{
			praiseLayout.setVisibility(View.GONE);
			praiseLayoutDetail.setVisibility(View.GONE);
		}
		if(image.getCommentsList().size()>0)
		{
			commentLayout.setVisibility(View.VISIBLE);
			mList.setVisibility(View.VISIBLE);
			comments_count.setText(String.valueOf(image.getCommentsList().size()));
			
			commentList=new ArrayList<AlbumMsgInfo>();
			int maxShowComment=30;
			if(maxShowComment>=image.getCommentsList().size())
			{
				maxShowComment=image.getCommentsList().size();
				viewMore.setVisibility(View.GONE);
			}
			else
			{
				viewMore.setVisibility(View.VISIBLE);
				viewMore.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(getActivity(),AlbumShowImageDetail.class);
						intent.putExtra("userList", image.getCommentsList());
						intent.putExtra("type", "评论");
						getActivity().startActivity(intent);
					}
					
				});
			}
			for(int i=0;i<maxShowComment;i++)
			{
				commentList.add(image.getCommentsList().get(i));
			}
			commentAdapter=new CommentAdapter();
			mList.setAdapter(commentAdapter);

			setListViewHeightBasedOnChildren(mList);
		}
		else
		{
			commentLayout.setVisibility(View.GONE);
			mList.setVisibility(View.GONE);
			viewMore.setVisibility(View.GONE);
		}
		return localView;
	}
	
	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    main_image.setImageDrawable(null);
        headimgView.setImageDrawable(null);
        for (MyTextViewEx item:MyTextList)
        {
            if(item!=null)
                item.destroy();
        }
        MyTextList.clear();
        MyTextList=null;
        for (ImageView item:imgViewList)
        {
            item.setImageDrawable(null);
        }
        imgViewList.clear();
        imgViewList=null;

	    //unbindDrawables(localView);
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {  
            // pre-condition  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
    }
	
	public AlbumImageInfo getImage() {
		return image;
	}

	public void setImage(AlbumImageInfo image) {
		this.image = image;
	}

	public class PraiseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return praiseList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return praiseList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView iv;
			if (convertView == null) 
			{		
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.imageview_griditem, null);
				
				iv = (ImageView)convertView.findViewById(R.id.image);
                imgViewList.add(iv);
				convertView.setTag(iv);
			}
			else
			{
				iv = (ImageView) convertView.getTag();
			}
			/*
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(iv).image(praiseList.get(position).getFromHeadUrl(), options);
			*/
			ImageLoader.getInstance().displayImage(praiseList.get(position).getFromHeadUrl(),iv,AppUtility.headOptions);
			iv.setTag(praiseList.get(position));
			iv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							ShowPersonInfo.class);
					AlbumMsgInfo u=(AlbumMsgInfo)v.getTag();
					intent.putExtra("studentId", u.getFromId());
					intent.putExtra("userImage", u.getFromHeadUrl());
					startActivity(intent);
				}
				
			});
			return convertView;
		}
		
	}
	public class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commentList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return commentList.get(position);
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
			if (convertView == null) 
			{		
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_album_comment, null);
				
				vh=new ViewHolder();
				vh.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
				vh.tv_title=(MyTextViewEx)convertView.findViewById(R.id.tv_title);
				vh.tv_left=(TextView)convertView.findViewById(R.id.thieDescription);
                MyTextList.add(vh.tv_title);
                imgViewList.add(vh.iv_icon);
				convertView.setTag(vh);

			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			/*
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(vh.iv_icon).image(commentList.get(position).getFromHeadUrl(), options);
			*/
			ImageLoader.getInstance().displayImage(commentList.get(position).getFromHeadUrl(),vh.iv_icon,AppUtility.headOptions);
			
			String timeStr=TimeUtility.getDayTime(commentList.get(position).getTime());
			if(commentList.get(position).getToId().length()>0 && !commentList.get(position).getToId().equals("null") && !commentList.get(position).getToId().equals(image.getHostId()))
				timeStr=timeStr+" 回复"+commentList.get(position).getToName();
			vh.tv_left.setText(timeStr);
			vh.iv_icon.setTag(commentList.get(position));
			vh.iv_icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							ShowPersonInfo.class);
					AlbumMsgInfo u=(AlbumMsgInfo)v.getTag();
					intent.putExtra("studentId", u.getFromId());
					intent.putExtra("userImage", u.getFromHeadUrl());
					startActivity(intent);
				}
				
			});
			
			vh.tv_title.setText("");
			vh.tv_title.insertGif(commentList.get(position).getFromName()+":"+commentList.get(position).getMsg());
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ImageView iv=(ImageView) v.findViewById(R.id.iv_icon);
					final AlbumMsgInfo ami=(AlbumMsgInfo) iv.getTag();
					String hostId=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
					final String[] moreAction;
					if(hostId.equals(ami.getFromId()))
						moreAction=new String[]{"删除","取消"};
					else
						moreAction=new String[]{"回复","举报","取消"};
					Builder builder = new AlertDialog.Builder(getActivity());  
					builder.setCancelable(true);
			        builder.setTitle("");  
			        //builder.setIcon(R.drawable.dialog);  
			        DialogInterface.OnClickListener listener =   
			            new DialogInterface.OnClickListener() {  
			                  
			                @Override  
			                public void onClick(DialogInterface dialogInterface,   
			                        int which) {  
			                	if(moreAction[which].equals("删除"))
			                		sendCommentAction(ami,"删除评论");
			                	else if (moreAction[which].equals("回复"))
			                	{
			                		LinearLayout replyToLayout = (LinearLayout) getActivity().findViewById(R.id.replyToLayout);
			                		TextView replyToText = (TextView) getActivity().findViewById(R.id.replyToText);
			                		replyToLayout.setVisibility(View.VISIBLE);
			                		replyToText.setText("回复"+ami.getFromName()+":");
			                		replyToText.setTag(ami.getFromId());
			                		EditText edit=(EditText)getActivity().findViewById(R.id.edit);
			                		edit.requestFocus();
			                		
			                		((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(edit, 0);
			                	}
			                	else if (moreAction[which].equals("举报"))
			                	{
			                		sendCommentAction(ami,"举报评论");
			                	}
			                	dialogInterface.dismiss();
			                		
			                }  
			            };  
			        builder.setItems(moreAction, listener);  
			        AlertDialog dialog = builder.create();  
			        dialog.show();
					
				}
				
			});
			return convertView;
		}
		public class ViewHolder {
			public MyTextViewEx tv_title;
			public ImageView iv_icon;
			public TextView tv_left;
			
			
		}
		
	}
	//举报,删除评论
	private void sendCommentAction(AlbumMsgInfo ami,String action)
	{
		
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("action",action);
			jo.put("imageId",image.getName());
			jo.put("评论人",ami.getFromId());
			jo.put("评论时间",ami.getTime());
			jo.put("评论内容",ami.getMsg());
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
				msg.what = 1;
				msg.obj = response;
				mHandler.sendMessage(msg);
				
			}
		});
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
				
				AppUtility.showErrorToast(getActivity(),
						msg.obj.toString());
				
				break;
			
			case 1: //删除或举报评论
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
						String action=jo.optString("action");
						AppUtility.showToastMsg(getActivity(), action+"成功！");
						if(action.equals("删除评论"))
						{
							AlbumMsgInfo ami=new AlbumMsgInfo(jo.optJSONObject("返回"));
							for(int i=0;i<commentList.size();i++)
							{
								if(commentList.get(i).getFromId().equals(ami.getFromId()) && commentList.get(i).getTime().equals(ami.getTime()) && commentList.get(i).getMsg().equals(ami.getMsg()))
								{
									commentList.remove(i);
									break;
								}
							}
							for(int i=0;i<image.getCommentsList().size();i++)
							{
								if(image.getCommentsList().get(i).getFromId().equals(ami.getFromId()) && image.getCommentsList().get(i).getTime().equals(ami.getTime()) && image.getCommentsList().get(i).getMsg().equals(ami.getMsg()))
								{
									image.getCommentsList().remove(i);
									break;
								}
							}
							commentAdapter.notifyDataSetChanged();
							comments_count.setText(String.valueOf(image.getCommentsList().size()));
						}
							
						
					}
				}catch (Exception e) {
					AppUtility.showToastMsg(getActivity(), e.getMessage());
					e.printStackTrace();
				}	
				break;
			}
		}
	};
	
}

package com.yujieshipin.crm.adapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.ImagesActivity;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.FileUtility;

public class MyPictureAdapter extends BaseAdapter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3534044376315830431L;
	private String TAG = "MyPictureAdapter";
	private Context mContext;
	private List<String> picPaths;
	private LayoutInflater inflater;
	private boolean isCanAdd = true;
	private int size = 9;//最大图片数量
	private String from;
	private String imagetype;
	
	public MyPictureAdapter(Context context,boolean flag, List<String> picPaths,int size,String imagetype) {
		this.mContext = context;
		this.picPaths = picPaths;
		this.isCanAdd= flag;
		this.size = size;
		this.imagetype=imagetype;
		inflater = LayoutInflater.from(context);
		Log.d(TAG, "isCanAdd"+isCanAdd);
		if(isCanAdd){
			initData();
		}
	}

	private void initData() {
		if(picPaths != null && picPaths.size() < size && !picPaths.contains("loading")){
			picPaths.remove("");
			picPaths.add("");
			
		}
	}

	public void setPicPaths(List<String> picPaths) {
		//picPaths.add("http://qd.baidupcs.com/file/43fd14d79e77ef636980d7792d5e3b00?fid=253833689-250528-1069475648697006&time=1400308734&sign=FDTAXER-DCb740ccc5511e5e8fedcff06b081203-0y%2BFbiDw0mcYK1qMntKx0%2BAz7P8%3D&to=qb&fm=Q,B,T,t&newver=1&expires=1400309334&rt=sh&r=505173609&logid=3777274181&sh=1&vuk=253833689&fn=20140517_141008-736098600.jpg");
		this.picPaths = picPaths;
		if(isCanAdd){
			initData();
		}
		notifyDataSetChanged();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public int getCount() {
		return picPaths.size();
	}

	public void removePicPaths(int position) {
		if (picPaths.size() == 2) {
			if (picPaths.get(1).equals("")) {
				picPaths.clear();
			}
		} else {
			picPaths.remove(position);
		}
		notifyDataSetChanged();
	}

	public List<String> getPicPaths() {
		picPaths.remove("");
		return picPaths;
	}

	@Override
	public Object getItem(int position) {
		return picPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		convertView = inflater.inflate(R.layout.view_image_item, null);
		AQuery aq = new AQuery(convertView);
		imageView = (ImageView) convertView.findViewById(R.id.iv_image);
		ProgressBar pb1=(ProgressBar)convertView.findViewById(R.id.progressBar1);
		
		pb1.setVisibility(View.INVISIBLE);
		imageView.setEnabled(true);
		String imgPath = picPaths.get(position);
		Log.d(TAG, "----imgPath:"+imgPath);
		imageView.setTag(imgPath);

		if (AppUtility.isNotEmpty(imgPath)) {
			//aq.id(imageView).image(imgPath);
			if(imgPath.equals("loading"))
			{
				pb1.setVisibility(View.VISIBLE);
				imageView.setEnabled(false);
			}
			else
			{
				aq.id(imageView).progress(R.id.progressBar1).image(imgPath,false,true);
			}
		} else {
			aq.id(imageView).image(R.drawable.pic_add_more);
		}
		
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String imgPath = v.getTag().toString();
				Log.d(TAG, "----imgPath:"+imgPath);
				if (imgPath.equals("")) {
					Intent intent=new Intent(Constants.GET_PICTURE);
					intent.putExtra("TAG", from);
					intent.putExtra("imagetype", imagetype);
					mContext.sendBroadcast(intent);
				} else {
					Log.d(TAG, "---------------------------------");
					if(isCanAdd)
					{
						Intent intent=new Intent(Constants.DEL_OR_LOOK_PICTURE);
						intent.putExtra("imagePath", imgPath);
						intent.putExtra("TAG", from);
						intent.putExtra("imagetype", imagetype);
						mContext.sendBroadcast(intent);
					}
					else
					{
						
						Intent intent = new Intent(mContext,ImagesActivity.class);
						intent.putStringArrayListExtra("pics",
								(ArrayList<String>) picPaths);
						for (int i = 0; i < picPaths.size(); i++) {
							if(picPaths.get(i).equals(imgPath)){
								intent.putExtra("position", i);
							}
						}
						mContext.startActivity(intent);
					}
				}
			}
		});
		return convertView;
	}
}

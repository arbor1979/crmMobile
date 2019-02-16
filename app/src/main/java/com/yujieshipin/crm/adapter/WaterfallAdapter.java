package com.yujieshipin.crm.adapter;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.AlbumShowImagePage;
import com.yujieshipin.crm.entity.AlbumImageInfo;


public class WaterfallAdapter extends BaseAdapter {

	
	ArrayList<AlbumImageInfo> list;
	Context context;
	private Drawable drawable;

	public WaterfallAdapter(ArrayList<AlbumImageInfo> list , Context context) {
		this.list = list;
		this.context = context;
		drawable = context.getResources().getDrawable(R.drawable.empty_photo);
		
	}


	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup group) {
		final Holder holder;
		// 得到View
		if (view == null) {
			holder = new Holder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.image_item, null);
			holder.ivIcon = (ImageView) view.findViewById(R.id.row_icon);
			holder.pbLoad = (ProgressBar) view.findViewById(R.id.pb_load);
			holder.tvIntro=(TextView) view.findViewById(R.id.tv_Intro);
			holder.layout_heart=(RelativeLayout) view.findViewById(R.id.layout_heart);
			holder.tv_heart=(TextView) view.findViewById(R.id.tv_heart);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		
		AlbumImageInfo image = list.get(position);
		holder.ivIcon.setTag(position);
		holder.tvIntro.setText(image.getHostName()+" "+image.getHostBanji());
		if(image.getPraiseCount()>0)
		{
			holder.layout_heart.setVisibility(View.VISIBLE);
			holder.tv_heart.setText(String.valueOf(image.getPraiseCount()));
		}
		else
			holder.layout_heart.setVisibility(View.GONE);
		ImageLoader.getInstance().displayImage(image.getUrl(), holder.ivIcon,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
						//这儿初先初始化出来image�?占的位置的大小，先把瀑布流固定住，这样�?�布流就不会因为图片加载出来后大小变化了
						//LayoutParams lp = (LayoutParams) holder.ivIcon.getLayoutParams();
						//多屏幕�?�配
						//int dWidth = 480;
						//int dHeight = 800;
						//float wscale = dWidth / 480.0f;
						//float hscale = dHeight / 800.0f;
						//lp.height = (int) (yourImageHeight * hscale);
						//lp.width = (int) (yourImageWidth * wscale);
						//holder.ivIcon.setLayoutParams(lp);
						
						holder.ivIcon.setImageDrawable(drawable);
						holder.pbLoad.setVisibility(View.VISIBLE);
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
							Toast.makeText(context, message, Toast.LENGTH_SHORT)
									.show();
							break;
						case UNKNOWN:
							message = "Unknown error";
							Toast.makeText(context, message, Toast.LENGTH_SHORT)
									.show();
							break;
						}
						holder.pbLoad.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						holder.pbLoad.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String paramString,
							View paramView) {
					}
				});

		
		holder.ivIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ArrayList<AlbumImageInfo> sublist=new ArrayList<AlbumImageInfo>();
				int m=(Integer)view.getTag();
				for(int i=m;i<list.size();i++)
				{
					AlbumImageInfo item=list.get(i);
					sublist.add(item);
					if(sublist.size()>=20)
						break;
					
				}
				Intent intent=new Intent(context,AlbumShowImagePage.class);
				intent.putExtra("imageList", sublist);
				intent.putExtra("allList", list);
				((Activity) context).startActivityForResult(intent,3);
			}
		});
		//view.setBackgroundColor(Color.RED);
		return view;
	}



}

class Holder {
	public ImageView ivIcon;
	public ProgressBar pbLoad;
	public TextView tvIntro;
	public RelativeLayout layout_heart;
	public TextView tv_heart;
}
package com.yujieshipin.crm.adapter;

import java.util.List;

import com.yujieshipin.crm.R;


import com.yujieshipin.crm.entity.ImageInfo;
import com.yujieshipin.crm.util.ImageManager2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 向picturechooseFragment listView添加数据
 * 
 * @author shengguo
 * 
 */
public class ListViewAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ImageInfo> imageInfo;

	 private Context mContext;
	public ListViewAdapter(Context context, List<ImageInfo> imageInfos) {
		mInflater = LayoutInflater.from(context);
		imageInfo = imageInfos;
		mContext=context;
	}

	public int getCount() {
		return imageInfo.size();
	}

	public Object getItem(int position) {
		return imageInfo.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.picture_path_item, null);
			holder = new ViewHolder();

			holder.icon = (ImageView) convertView.findViewById(R.id.picture);

			holder.path = (TextView) convertView.findViewById(R.id.path_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (imageInfo == null) {
			Log.i("ListViewAdapter", "imageInfo is null!");
			return convertView;
		}
		ImageInfo imgInfo=imageInfo.get(position);
		if (imgInfo == null) {
			Log.i("ListViewAdapter", "imageInfo.get(position) is null!");
			return convertView;
		}
//		holder.icon.setImageBitmap(imageInfo.get(position).icon);

		ImageManager2.from(mContext).displayImage(holder.icon,
				imgInfo.tag.get(0), R.drawable.default_photo, 100, 100);
		holder.path.setText( imgInfo.displayName+ "("
				+ imgInfo.picturecount + ")");
		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder {
		TextView path;
		ImageView icon;
	}
}

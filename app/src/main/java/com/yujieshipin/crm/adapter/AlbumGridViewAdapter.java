package com.yujieshipin.crm.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.util.ImageManager2;

@SuppressLint("ResourceAsColor")
public class AlbumGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> dataList;
	private DisplayMetrics dm;
	private LayoutInflater inflater;

	public AlbumGridViewAdapter(Context c, ArrayList<String> dataList) {

		mContext = c;
		this.dataList = dataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_select_imageview,parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position);
		else
			path = "camera_default";
		if (path.contains("default")) {
			viewHolder.imageView.setImageResource(R.drawable.default_photo);
		} else {
			ImageManager2.from(mContext).displayImage(viewHolder.imageView,
					path, R.drawable.default_photo, 100, 100);
		}
		viewHolder.imageView.setTag(path);
		return convertView;
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}
}

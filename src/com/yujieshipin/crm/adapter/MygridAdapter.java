package com.yujieshipin.crm.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yujieshipin.crm.R;

public class MygridAdapter extends BaseAdapter {
	List<Bitmap> bitmaplist;
	List<File> fileList;
	private Context context;
	private LayoutInflater mInflater;
	private class GridHolder {  
        ImageView appImage;  
        TextView appText;
    }  
  
	public MygridAdapter(Context context,List<Bitmap> bitmaplist,List<File> list){
		this.bitmaplist = bitmaplist;
		this.fileList=list;
		this.context = context;
		mInflater = LayoutInflater.from(this.context);
	}
	@Override
	public int getCount() {
		return bitmaplist.size();
	}

	@Override
	public Object getItem(int position) {
		return bitmaplist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		  GridHolder holder;  
	        if (convertView == null) {     
	            convertView = mInflater.inflate(R.layout.list_item, null);     
	            holder = new GridHolder();  
	            holder.appImage = (ImageView)convertView.findViewById(R.id.item_imgeView);  
	            if((Bitmap) getItem(position)!=null)
	            	holder.appImage.setBackgroundDrawable(new BitmapDrawable((Bitmap) getItem(position)));
	            else
	            	holder.appImage.setBackgroundColor(Color.BLACK);
	            holder.appText = (TextView)convertView.findViewById(R.id.item_textView);
	            holder.appText.setText(fileList.get(position).getName());
	            convertView.setTag(holder);     
	  
	        }else{  
	             holder = (GridHolder) convertView.getTag();     
	        }  
	        return convertView;  
	}
}

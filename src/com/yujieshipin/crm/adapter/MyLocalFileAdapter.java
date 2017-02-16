package com.yujieshipin.crm.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.util.FileUtility;

public class MyLocalFileAdapter extends BaseAdapter {

	private List<File> fileList;
	private LayoutInflater inflater;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	private Context context;

	public MyLocalFileAdapter(Context context,List<File> files) {
		this.context = context;
		this.fileList=files;
		inflater = LayoutInflater.from(this.context);
	}
	
	public Map<Integer, Boolean> getMap() {
		return map;
	}
	public void setFileList(List<File> fileList) {

		map.clear();

		if (fileList == null) {
			fileList = new ArrayList<File>();
		}

		for (int i = 0; i < fileList.size(); i++) {
			map.put(i, false);
		}

		this.fileList = fileList;
	}

	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("DefaultLocale") @Override
	public View getView(int position, View convertView, ViewGroup root) {

		ViewHolder holder = null;

		if (null == convertView) {

			convertView = inflater.inflate(R.layout.list_item, null);

			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.item_imgeView);
			holder.textView = (TextView) convertView
					.findViewById(R.id.item_textView);

			convertView.setTag(holder);

		} else {
		holder = (ViewHolder) convertView.getTag();
		}
		File file = fileList.get(position);
		if (file.isDirectory()) {
			holder.imageView.setImageResource(R.drawable.icon_folder);
		} else {
			String extName=FileUtility.getFileExtName(file.getAbsolutePath()).toLowerCase(Locale.getDefault());
			if(extName.equals("ppt"))
				holder.imageView.setImageResource(R.drawable.ic_file_ppt);
			else if(extName.equals("doc") || extName.equals("docx"))
				holder.imageView.setImageResource(R.drawable.ic_file_doc);
			else if(extName.equals("xls") || extName.equals("xlsx"))
				holder.imageView.setImageResource(R.drawable.ic_file_xls);
			else if(extName.equals("zip"))
				holder.imageView.setImageResource(R.drawable.ic_file_zip);
			else if(extName.equals("rar"))
				holder.imageView.setImageResource(R.drawable.ic_file_rar);
			else if(extName.equals("txt"))
				holder.imageView.setImageResource(R.drawable.ic_file_txt);
			else if(extName.equals("pdf"))
				holder.imageView.setImageResource(R.drawable.ic_file_pdf);
			else
				holder.imageView.setImageResource(R.drawable.ic_file_default);
		}

		holder.textView.setText(file.getName());

		return convertView;
	}

	class ViewHolder {

		ImageView imageView;
		TextView textView;
	}
}

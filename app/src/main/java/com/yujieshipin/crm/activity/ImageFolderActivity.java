package com.yujieshipin.crm.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.adapter.ListViewAdapter;
import com.yujieshipin.crm.entity.ImageInfo;

/**
 * 
 * #(c) ruanyun YeyPro <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 选择路径下的图片
 * 
 * <br/>
 * 创建说明: 2014-4-8 下午4:40:05 shengguo 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class ImageFolderActivity extends Activity {
	private String TAG = "ImageFolderActivity";
	private AQuery aq;
	private ArrayList<String> dataList = new ArrayList<String>();// 所有图片的路径
	private ArrayList<ImageInfo> bitImages = new ArrayList<ImageInfo>();// 某些路径图片信息集合
	private ProgressBar progressBar;
	private ListView myListView;
	private ListViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_folder);
		aq = new AQuery(this);
		init();
		initListener();
	}

	private void init() {
		aq.id(R.id.tv_title).text("选择相册");
		aq.id(R.id.thieDescription).text("取消");
		aq.id(R.id.thieDescription).visibility(View.VISIBLE);
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putStringArrayListExtra("imageList", dataList);
				setResult(1, intent);
				finish();
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		myListView = (ListView) findViewById(R.id.myListView);
		mAdapter = new ListViewAdapter(this, bitImages);
		myListView.setAdapter(mAdapter);
		refreshData();
	}

	private void initListener() {
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				dataList.clear();
				dataList.addAll(bitImages.get(position).tag);
				ArrayList<String> imageList = (ArrayList<String>) bitImages
						.get(position).tag;
				Intent intent = getIntent();
				intent.putStringArrayListExtra("imageList", imageList);
				setResult(1, intent);
				finish();
			}
		});
	}

	private void refreshData() {

		new AsyncTask<Void, Void, ArrayList<String>>() {

			@Override
			protected void onPreExecute() {
				progressBar.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				dataList = listAlldir();
				ImageInfo info = new ImageInfo();
				info.path = "";
				info.displayName = "ALL";
				info.tag.addAll(dataList);
				info.picturecount = dataList.size();
				bitImages.add(info);
				for (String str : dataList) {
					String imgPath = str.substring(0, str.lastIndexOf("/"));
					boolean flag = false;// list中没有某路径下图片信息
					for (ImageInfo imageInfo : bitImages) {
						if (imageInfo.path.equals(imgPath)) {
							imageInfo.tag.add(str);
							imageInfo.picturecount++;
							flag = true;
						}
					}
					if (!flag) {
						String displayName = imgPath.substring(
								imgPath.lastIndexOf("/") + 1, imgPath.length());
						ImageInfo imageInfo = new ImageInfo();
						imageInfo.path = imgPath;
						imageInfo.displayName = displayName;
						imageInfo.tag.add(str);
						imageInfo.picturecount = 1;
						bitImages.add(imageInfo);
					}
				}
				Log.d(TAG, "--------" + bitImages.size());
				return null;
			}

			protected void onPostExecute(ArrayList<String> tmpList) {

				if (ImageFolderActivity.this == null
						|| ImageFolderActivity.this.isFinishing()) {
					return;
				}
				progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				return;
			};

		}.execute();
	}

	/**
	 * 获取图库图片所有路径
	 * 
	 * @return
	 */
	private ArrayList<String> listAlldir() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = intent.getData();
		ArrayList<String> list = new ArrayList<String>();
		String[] proj = { MediaStore.Images.Media.DATA };
		String orderBy = MediaStore.Images.Media.DATE_ADDED;
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, proj, null, null, orderBy + " DESC");
		while (cursor.moveToNext()) {
			String path = cursor.getString(0);
			list.add(new File(path).getAbsolutePath());
		}
		return list;
	}

	@Override
	public void onBackPressed() {
		finish();
		// super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		// ImageManager2.from(AlbumActivity.this).recycle(dataList);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

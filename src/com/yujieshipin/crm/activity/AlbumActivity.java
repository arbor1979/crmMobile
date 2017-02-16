package com.yujieshipin.crm.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.adapter.AlbumGridViewAdapter;

public class AlbumActivity extends Activity {
	private String TAG = "AlbumActivity";
	private AQuery aq;

	private GridView myGridView;
	private ArrayList<String> dataList = new ArrayList<String>();// 所有图片的路径
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
	
		// 刷新媒体库
		// Intent refrashMediaIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED,
		// Uri.parse("file://" + Environment.getExternalStorageDirectory()));
		// sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
		// Uri.parse("file://" +
		// Environment.getExternalStorageDirectory().getAbsolutePath())));
		// sendBroadcast(refrashMediaIntent);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Log.d(TAG, "-----currentapiVersion:" + currentapiVersion);
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// Do something for froyo and above versions
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath())));
		} else {
			// do something for phones running an SDK before froyo
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath())));
		}
		aq = new AQuery(this);

		init();
		initListener();
	}

	private void init() {
		aq.id(R.id.tv_title).text("照片");
		aq.id(R.id.thieDescription).text("取消");
		aq.id(R.id.tv_right).text("相册");
		aq.id(R.id.thieDescription).visibility(View.VISIBLE);
		aq.id(R.id.tv_right).visibility(View.VISIBLE);
		aq.id(R.id.layout_btn_left).clicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		myGridView = (GridView) findViewById(R.id.myGridView);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList);
		myGridView.setAdapter(gridImageAdapter);
		refreshData();
	}

	private void initListener() {
		aq.id(R.id.layout_btn_right).clicked(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "选择相册");
				Intent intent = new Intent(AlbumActivity.this,ImageFolderActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		
		myGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
				String path = imageView.getTag().toString();
				Log.d(TAG, "-----------"+path);
				Intent intent = getIntent();
				intent.putExtra("filepath", path);
				setResult(2,intent);
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
				ArrayList<String> listDirlocal = listAlldir();
				return listDirlocal;
			}

			protected void onPostExecute(ArrayList<String> tmpList) {

				if (AlbumActivity.this == null
						|| AlbumActivity.this.isFinishing()) {
					return;
				}
				progressBar.setVisibility(View.GONE);
				dataList.clear();
				dataList.addAll(tmpList);
				gridImageAdapter.notifyDataSetChanged();
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
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			ArrayList<String> tmpList = data
					.getStringArrayListExtra("imageList");
			dataList.clear();
			dataList.addAll(tmpList);
			gridImageAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}

package com.yujieshipin.crm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.adapter.MyLocalFileAdapter;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.util.AppUtility;

/**
 * 获取图片和视频的缩略图 这两个方法必须在2.2及以上版本使用，因为其中使用了ThumbnailUtils这个类
 */
public class LocalDocument extends Activity {
	private MyLocalFileAdapter adapter;
	
	private String filepath;
	private LinearLayout initLayout;
	private List<File> listFiles = new ArrayList<File>();
	private String videoPath;
	private File root;
	private TextView fileText;
	private List<String> videopaths = new ArrayList<String>();
	private ListView grid;
	private static ArrayList<String> VIDEO_SUFFIX = new ArrayList<String>();
	static {
		VIDEO_SUFFIX.add(".ppt");
		VIDEO_SUFFIX.add(".doc");
		VIDEO_SUFFIX.add(".docx");
		VIDEO_SUFFIX.add(".xls");
		VIDEO_SUFFIX.add(".xlsx");
		VIDEO_SUFFIX.add(".zip");
		VIDEO_SUFFIX.add(".rar");
		VIDEO_SUFFIX.add(".pdf");
		//VIDEO_SUFFIX.add(".txt");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localdocuments);
		grid = (ListView) findViewById(R.id.grid);
		initLayout = (LinearLayout)findViewById(R.id.initlayout);
		fileText = (TextView) findViewById(R.id.fileText);
		initLayout.setVisibility(View.VISIBLE);
		grid.setVisibility(View.GONE);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			filepath = "/mnt";
			root = new File(filepath);
		} else {
			Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
			finish();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				listFiles = getAllFiles(root);
				Message msg = mHandler.obtainMessage();
				msg.what = 0;
				mHandler.sendMessage(msg);

			}
		}).start();

	}

	/**
	 * 功能描述:判断是否是视频文件
	 * 
	 * @author linrr 2013-12-26 下午4:05:45
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isVideoFile(String fileName) {

		if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
			return false;
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		if (VIDEO_SUFFIX.contains(fileSuffix)) {
			System.out.println(fileName + ".........................."
					+ fileSuffix);
			return true;
		} else {
			return false;
		}

	}
	

	/**
	 * 功能描述:遍历文件
	 * 
	 * @author linrr 2013-12-26 下午4:04:10
	 * 
	 * @param root
	 * @return
	 */
	private List<File> getAllFiles(File root) {
		List<File> list = new ArrayList<File>();
		File files[] = root.listFiles();
		if (files != null&&files.length>0) {
			for (File f : files) {
				if(f.getName().equals("PocKetCampus")) continue;
				if(f.getPath().equals("/mnt/shell")) continue;
				if (f.isDirectory()&&f.canRead()&&!f.isHidden()&&!f.getName().contains(".")&&f.list().length>0) {
					list.addAll(getAllFiles(f));
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.obj = f.getName();
					mHandler.sendMessage(msg);
				} else if(isVideoFile(f.getName())){
					list.add(f);
					Message msg = mHandler.obtainMessage();
					msg.what = 2;
					msg.obj = f.getName();
					mHandler.sendMessage(msg);
					videoPath = f.getAbsolutePath();
					videopaths.add(videoPath);
					
				}
			}
		}
		return list;
	}
	/**
	 * 
	 * #(c) ruanyun PocketCampus <br/>
	 * 
	 * 版本说明: $id:$ <br/>
	 * 
	 * 功能说明:
	 * 
	 * <br/>
	 * 创建说明: 2013-12-26 下午4:04:56 linrr 创建文件<br/>
	 * 
	 * 修改历史:<br/>
	 * 
	 * @param parent
	 *            代表当前的gridview
	 * @param 代表点击的item
	 * @param 当前点击的item在适配中的位置
	 * @param id
	 *            The row id of the item that was clicked.
	 */
	private class MainItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent;
			intent = getIntent();
			File file=new File(videopaths.get(position));
			if(file.length()>Constants.kejianMaxSize)
			{
				AppUtility.showToastMsg(LocalDocument.this, "选择的文件大小不能超过"+Constants.kejianMaxSize/1024/1024+"M");
				return;
			}
			
			intent.putExtra("paths", videopaths.get(position));
			System.out.println(videopaths.get(position) + ".....");
			setResult(5, intent);
			finish();
		}
	}
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(listFiles.size() == 0){
					Toast.makeText(LocalDocument.this, "您还没有任何文档哦", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					initLayout.setVisibility(View.GONE);
					grid.setVisibility(View.VISIBLE);
					adapter = new MyLocalFileAdapter(LocalDocument.this, listFiles);
					grid.setAdapter(adapter);
					grid.setOnItemClickListener(new MainItemClickListener());
				}
				break;
			case 1:
				fileText.setText("正在扫描："+msg.obj.toString()+"文件夹...");
				break;
			case 2:
				AppUtility.showToast(LocalDocument.this, "已扫描到"+msg.obj.toString()+"文档文件", 10);
				break;
			}
		}

	};
}

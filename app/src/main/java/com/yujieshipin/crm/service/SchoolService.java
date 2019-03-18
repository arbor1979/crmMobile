package com.yujieshipin.crm.service;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.DownloadTask;
import com.yujieshipin.crm.entity.NotificationBean;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DownloadUtil;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.IntentUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.DownloadUtil.IOnDownloadListener;


public class SchoolService extends Service {
	final String TAG = "SchoolService";
	private final String ACTION_NAME = "refreshSubject";
	private Context mContext = SchoolService.this;
	/** 正在下载 */
	private final int DOWN_LOADING = 0;
	/** 下载完成 */
	private final int DOWN_COMPLETE = 1;
	/** 下载失败 */
	private final int DOWN_ERR = 2;
	/** Timer 执行时间间隔 */
	private final int TIMER_PERIOD = 1500;

	protected Timer mTimer;
	protected NotificationManager mNotificationManager;
	/** 下载任务管理 */
	protected Map<String, DownloadTask> map_downloadtask;
	private final MyIBinder binder = new MyIBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "-------------->onBind is running!");
		return binder;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "--------------->onCreate is running!");
		super.onCreate();
		mTimer = new Timer();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		map_downloadtask = new HashMap<String, DownloadTask>();
	}

	/**
	 * 功能描述:初始化数据
	 * 
	 * @author shengguo 2014-5-28 下午3:18:12
	 * 
	 */
	public void initData() {
		Log.i(TAG, "--------------->initData!");
		if (!AppUtility.isInitBaseData()) {
			DatabaseHelper database = OpenHelperManager.getHelper(SchoolService.this, DatabaseHelper.class);
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			InitData initData = new InitData(SchoolService.this,database, null,ACTION_NAME,checkCode);
			initData.initAllInfo();
		}
	}
	public void initContracts() {
		Log.i(TAG, "--------------->initContracts!");
		if (!AppUtility.isInitContactData()) {
			DatabaseHelper database = OpenHelperManager.getHelper(SchoolService.this, DatabaseHelper.class);
			String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
			InitData initData = new InitData(SchoolService.this,database, null,"refreshContact",checkCode);
			initData.initContactInfo();
		}
	}

	public void downLoadUpdate(String mUrl,int mNotifyId){
		String fileName=FileUtility.getUrlRealName(mUrl);
		
		String downloadPath = FileUtility.creatSDDir("download")+fileName;
		fileName=URLDecoder.decode(fileName);
		Notification mNotification = new NotificationBean(this,R.drawable.ic_logo1, "开始下载",fileName, System.currentTimeMillis(),downloadPath);
		Log.i(TAG, "NotifyId = " + mNotifyId);

		if (map_downloadtask.containsKey(mUrl)) {
			AppUtility.showToastMsg(mContext, "已存在此下载任务");
		} else {
			DownloadTask mDownloadTask = new DownloadTask();
			mDownloadTask.setUrl(mUrl);
			mDownloadTask.setNotifyID(mNotifyId);
			mDownloadTask.setNotification(mNotification);
			map_downloadtask.put(mUrl, mDownloadTask);

			Runnable mRunnable = new MyRunnable(mDownloadTask);
			new Thread(mRunnable).start();
			AppUtility.showToastMsg(mContext, "已开始后台下载..");
		}
	}
	@Override
	public void onDestroy() {
		Log.i(TAG, "--------------->onDestroy is running!");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "-------------->onStartCommand is running!");
		return super.onStartCommand(intent, flags, startId);
	}

	public class MyIBinder extends Binder {
		public SchoolService getService() {
			return SchoolService.this;
		}
	}
	class MyRunnable implements Runnable {
		private DownloadUtil mDownUtil = new DownloadUtil();
		private DownloadTask mDownTask;
		private Handler mHandler;
		private TimerTask mTimerTask;

		public MyRunnable(DownloadTask downTask) {
			super();
			this.mDownTask = downTask;
			this.mHandler = new MyHandler(mDownUtil);
			this.mTimerTask = new MyTimerTask(mDownUtil, mHandler, mDownTask);
		}

		@Override
		public void run() {
			mTimer.schedule(mTimerTask, 0, TIMER_PERIOD);
			String downloadPath = FileUtility.creatSDDir("download");
			mDownUtil.downloadFile(mDownTask.getUrl(),downloadPath);
		}

	}

	class MyTimerTask extends TimerTask {
		private Handler mHandler;
		private DownloadUtil mDownUtil;
		private DownloadTask mDownTask;
		private IOnDownloadListener mListener;

		public MyTimerTask(DownloadUtil downUtil, Handler handler,
				DownloadTask downTask) {
			super();
			this.mHandler = handler;
			this.mDownUtil = downUtil;
			this.mDownTask = downTask;
			this.mListener = new IOnDownloadListener() {
				@Override
				public void updateNotification(int progress, int totalSize,
						File downFile) {
					float rate = 0;
					// 计算百分比
					if (totalSize > 0) {
						if(progress == totalSize){
							mHandler.obtainMessage(DOWN_LOADING, 100,
									mDownTask.getNotifyID(),
									mDownTask.getNotification()).sendToTarget();
						}else{
							rate = progress * 100 / totalSize;
							mHandler.obtainMessage(DOWN_LOADING, (int)rate,
									mDownTask.getNotifyID(),
									mDownTask.getNotification()).sendToTarget();
						}
					} else if (totalSize == 0) {
						mHandler.obtainMessage(DOWN_LOADING, 0,
								mDownTask.getNotifyID(),
								mDownTask.getNotification()).sendToTarget();
					} else {
						cancel();
						mHandler.obtainMessage(DOWN_ERR, mDownTask)
								.sendToTarget();
					}
					// 是否下载结束
					if (totalSize > 0 && null != downFile
							&& totalSize == (int) downFile.length()) {
						cancel();
						mHandler.obtainMessage(DOWN_COMPLETE, downFile)
								.sendToTarget();
						String fileName=downFile.getName();
						String str=fileName.substring(fileName.lastIndexOf("."),fileName.length());
						if(str.equals(".apk")){
							mDownUtil.installApk(mContext, downFile);
						}
						else
						{
							if (!downFile.exists()) {
								return;
							}
							Intent intent=IntentUtility.openUrl(CampusApplication.getContext(),downFile.toString());
							IntentUtility.openIntent(mContext,intent,true);
						}
						map_downloadtask.remove(mDownTask.getUrl());// 移除已完成任务
						Log.i(TAG, "DOWN_COMPLETE ==> totalSize ==> "
								+ totalSize);
					}
				}

			};
		}
		
		@Override
		public void run() {
			mDownUtil.setOnDownloadListener(mListener);
		}
	}
	@SuppressWarnings("unused")
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		private DownloadUtil mDownUtil;

		public MyHandler(DownloadUtil downUtil) {
			super();
			this.mDownUtil = downUtil;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_LOADING:
				if(msg.arg1==100){
					((Notification) msg.obj).contentView.setViewVisibility(R.id.pb,View.GONE);
					((Notification) msg.obj).contentView.setTextViewText(R.id.tv,"下载完成");}
				else{
					((Notification) msg.obj).contentView.setViewVisibility(R.id.pb,View.VISIBLE);
					((Notification) msg.obj).contentView.setProgressBar(R.id.pb,
							100, msg.arg1, false);
					((Notification) msg.obj).contentView.setTextViewText(R.id.tv,
							"下载" + msg.arg1 + "%");
				}
				mNotificationManager.notify(msg.arg2, ((Notification) msg.obj));
				Log.d(TAG, "DOWN_LOADING --> mNotifyId --> " + msg.arg2
						+ " --> " + msg.arg1 + "%");
				break;
			case DOWN_COMPLETE:
				removeMessages(DOWN_LOADING);
				AppUtility.showToastMsg(mContext, "下载完成");
				Log.i("下载完成", "--->  " + msg.obj.toString());
				Log.i(TAG, "======================DOWN_COMPLETE================================");
				stopService();
				break;
			case DOWN_ERR:
				removeMessages(DOWN_LOADING);
				map_downloadtask.remove(((DownloadTask) msg.obj).getUrl());
				AppUtility.showToastMsg(mContext, "下载失败");
				stopService();
				break;
			default:
				break;
			}
		}

		/**
		 * 如果无下载任务，关闭服务
		 */
		private void stopService() {
			if (map_downloadtask.isEmpty()) {
				stopSelf(-1);
			}
		}
	}
}

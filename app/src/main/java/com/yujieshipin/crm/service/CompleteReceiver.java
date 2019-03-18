package com.yujieshipin.crm.service;

import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.IntentUtility;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.DownloadManager.Query;
import android.os.Build;

import java.io.File;

public class CompleteReceiver extends BroadcastReceiver {

	private DownloadManager downloadManager; 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();  
        if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {  
        	AppUtility.showToastMsg(context, "下载完毕！");
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);                                                                                      //TODO 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件  
            Query query = new Query();  
            query.setFilterById(id);
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = downloadManager.query(query);
            String local_filename = null;
            if (cursor.moveToFirst()) {
                int fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String local_uri = cursor.getString(fileUriIdx);

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (local_uri != null) {
                        local_filename = Uri.parse(local_uri).getPath();
                    }
                } else {
                    int fileNameIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    local_filename = cursor.getString(fileNameIdx);
                }
            }
            cursor.close();
            if(local_filename!=null) {
                File file = new File(Uri.decode(local_filename));
                if(file.exists())
                    local_filename=Uri.decode(local_filename);
                Intent aintent = IntentUtility.openUrl(context,local_filename.replace("file://", ""));
                if (aintent != null)
                    IntentUtility.openIntent(context, aintent, true);
            }
              
        }else if(action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {  
              
        }  
	}

}

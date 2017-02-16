package com.yujieshipin.crm.util;

import java.io.File;
import java.util.List;

import com.yujieshipin.crm.activity.CourseClassActivity;
import com.yujieshipin.crm.activity.TabHostActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

@SuppressLint("DefaultLocale")
public class IntentUtility {
	public static Intent openFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* 取得扩展名 */
		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
		System.out.println("-------------------end----------" + end);
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("3gp") || end.equals("mp4") || end.equals("mov") || end.equals("avi") || end.equals("flv") || end.equals("f4v") || end.equals("wmv")) {
			return getVideoFileIntent(filePath);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return getImageFileIntent(filePath);
		} else if (end.equals("apk")) {
			return getApkFileIntent(filePath);
		} else if (end.equals("ppt")) {
			return getPptFileIntent(filePath);
		} else if (end.equals("xls") || end.equals("xlsx")) {
			return getExcelFileIntent(filePath);
		} else if (end.equals("doc") || (end.equals("docx"))) {
			return getWordFileIntent(filePath);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(filePath);
		} else if (end.equals("chm")) {
			return getChmFileIntent(filePath);
		} else if (end.equals("txt")) {
			return getTextFileIntent(filePath, false);
		} else if(end.equals("rar")){
			return getRarFileIntent(filePath);
		}else if(end.equals("zip")){
			return getZipFileIntent(filePath);
		}else{
			return getAllIntent(filePath);
		}
	}
	
	public static Intent openUrl(String url) {
		/* 取得扩展名 */
		String end = url.substring(url.lastIndexOf(".") + 1,url.length()).toLowerCase();
		if (end==null || end.length()==0)
			return null;
		
		System.out.println("-------------------end----------" + end);
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			return getAudioFileIntent(url);
		} else if (end.equals("3gp") || end.equals("mp4") || end.equals("mov") || end.equals("avi") 
				|| end.equals("flv") || end.equals("f4v") || end.equals("wmv") || end.equals("rm") 
				|| end.equals("asf") || end.equals("mpg") || end.equals("mpeg") || end.equals("rmvb") 
				|| end.equals("ogm")) {
			return getVideoFileIntent(url);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return getImageFileIntent(url);
		} else if (end.equals("apk")) {
			return getApkFileIntent(url);
		} else if (end.equals("ppt")) {
			return getPptFileIntent(url);
		} else if (end.equals("xls") || end.equals("xlsx")) {
			return getExcelFileIntent(url);
		} else if (end.equals("doc") || (end.equals("docx"))) {
			return getWordFileIntent(url);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(url);
		} else if (end.equals("chm")) {
			return getChmFileIntent(url);
		} else if (end.equals("txt")) {
			return getTextFileIntent(url, false);
		} else if(end.equals("rar")){
			return getRarFileIntent(url);
		}else if(end.equals("zip")){
			return getZipFileIntent(url);
		}else if(end.equals("swf")){
			return getFlashFileIntent(url);
		}
		else{
			return null;
		}
	}
	public static Intent getFlashFileIntent(String param) {

		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-shockwave-flash");
		return intent;
	}
	// Android获取一个用于打开APK文件的intent
	public static Intent getAllIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "*/*");
		return intent;
	}

	// Android获取一个用于打开APK文件的intent
	public static Intent getApkFileIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	// Android获取一个用于打开VIDEO文件的intent
	public static Intent getVideoFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// Android获取一个用于打开AUDIO文件的intent
	public static Intent getAudioFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// Android获取一个用于打开Html文件的intent
	public static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// Android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}
	
	// Android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// Android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}
	// Android获取一个用于打开rar文件的intent
	public static Intent getRarFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-rar-compressed");
		return intent;
	}
	// Android获取一个用于打开rar文件的intent
	public static Intent getZipFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if(param.startsWith("http:"))
			uri=Uri.parse(param);
		else
			uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/zip");
		return intent;
	}
	
	public static boolean openIntent(Context context,Intent intent,boolean iftip)
	{
		if(hasApplication(context,intent))
		{
			context.startActivity(intent);
			return true;
		}
		else
		{
			if(iftip)
				AppUtility.showToastMsg(context, "没有可以打开此文件的程序");
			return false;
		}
	}
	private static boolean hasApplication(Context context,Intent intent){    
        android.content.pm.PackageManager packageManager = context.getPackageManager();    
        //查询是否有该Intent的Activity    
        List<android.content.pm.ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);    
        //activities里面不为空就有，否则就没有    
        return activities.size() > 0 ? true : false;
	}
	public static void checkAppByPackName(final Context context,final Intent intent,String[] packName,String tip,final String url){    
	
        android.content.pm.PackageManager packageManager = context.getPackageManager();    
            
        ApplicationInfo appinfo = null;
        for(int i=0;i<packName.length;i++)
        {
			try {
				appinfo = packageManager.getApplicationInfo(packName[i], 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(appinfo!=null) break;
        }
        if(appinfo==null)
        {
        	AlertDialog.Builder builder = new AlertDialog.Builder(context);
        	builder.setMessage(tip)
        	       .setCancelable(false)
        	       .setPositiveButton("安装", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   
        	        	   AppUtility.downloadUrl(url, null, context);
        	        	   dialog.dismiss();
        	           }
        	       })
        	       .setNegativeButton("取消", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	        	   openIntent(context,intent,false);
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }
        else
        	openIntent(context,intent,true);
    
	}
}

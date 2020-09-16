package com.yujieshipin.crm.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PrinterShareUtil {

    /**
     * 判断PrinterShare是否安装
     *
     * @param context
     * @param
     * @return
     */
    public static boolean isAppInstalled(Context context) {
        String packageName = "com.dynamixsoftware.printershare";
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /**
     * 安装apk
     *
     * @param context
     */
    public static void startInstallApp(Context context) {
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = getAssetFileToCacheDir(context, "PrinterShare_11.29.5.apk");
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        */
        String siteUrl = PrefUtility.get(Constants.PREF_LOGIN_URL, "");
        siteUrl="http://119.29.6.239:"+siteUrl;
        String url=siteUrl+"/general/ERP/Interface/mobile/version/PrinterShare_11.29.5.apk";
        AppUtility.downloadUrl(url, null, context);
    }

    /**
     * 启动图片打印
     *
     * @param context
     * @param url 网页地址
     */
    public static void startUrlPrinterShare(Context context, String url) {
        Log.d("PrinterShare", "-->   url: " + url);
        ComponentName comp = new ComponentName("com.dynamixsoftware.printershare", "com.dynamixsoftware.printershare.ActivityWeb");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(comp);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);

    }
}

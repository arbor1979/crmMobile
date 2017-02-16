package com.yujieshipin.crm.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

public class ExitApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitApplication instance;
	private static Boolean isExit = false;
	private ExitApplication() {

	}

	// 单例模式中获取唯一的ExitApplication 实例
	public static ExitApplication getInstance() {
		if (null == instance) {
			instance = new ExitApplication();
		}
		return instance;

	}
	// 添加Activity 到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity 并finish

	public void exit() {
		for (Activity activity :activityList) {
			activity.finish();
		}
		System.exit(0);
	}
	
	public void exitByTwoClick(){
		Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	    } else {  
	       exit();
	    }  
	}
}

package com.yujieshipin.crm.entity;

import android.app.Notification;
/**
 * 
 *  #(c) ruanyun YeyPro <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 下载任务
 * 
 *  <br/>创建说明: 2014-2-15 下午2:49:56 linrr 创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class DownloadTask
{
	private String url;
	private int notifyID;
	private Notification notification;

	public DownloadTask()
	{
		// TODO Auto-generated constructor stub
	}

	public Notification getNotification()
    {
	    return notification;
    }

	public void setNotification(Notification notification)
    {
	    this.notification = notification;
    }

	public int getNotifyID()
    {
	    return notifyID;
    }

	public void setNotifyID(int notifyID)
    {
	    this.notifyID = notifyID;
    }

	public String getUrl()
    {
	    return url;
    }

	public void setUrl(String url)
    {
	    this.url = url;
    }

}

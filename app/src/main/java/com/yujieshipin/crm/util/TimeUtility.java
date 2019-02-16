package com.yujieshipin.crm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yujieshipin.crm.activity.SysSettingActivity;

public class TimeUtility {

	public static String getDayTime(String daytime) { // daytime 格式为yyyy-MM-dd
														// HH:mm:ss
		int mimutes = 0;
		int days = 0;
		int hours = 0;
		String timeStr = "";
		Date nowdate = new Date();
		Date createdate = null;
		if (daytime != null && !"".equals(daytime) && daytime.length() == 19) {
			try {
				String pattern = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				createdate = format.parse(daytime);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (nowdate.getDate() == createdate.getDate()
					&& nowdate.getMonth() == createdate.getMonth()
					&& nowdate.getYear() == createdate.getYear()) {
				mimutes = TimeUtility.getMinutesDiff(nowdate, createdate);
				if (mimutes == 0) {
					timeStr = "刚刚";
				} else if (mimutes > 0) {
					if (mimutes > 0 && mimutes < 60) {
						timeStr = String.valueOf(mimutes) + "分钟前";
					} else {
						hours = mimutes / 60;
						if (hours > 0 && hours <= 24) {
							timeStr = String.valueOf(hours) + "小时前";
						}
					}
				}
			} else {
				// int daysDiff = getDaysDiff(nowdate,createdate);
				if (nowdate.getMonth() == createdate.getMonth() && nowdate.getYear() == createdate.getYear()) {
					days = nowdate.getDate() - createdate.getDate();
				} else {
					days = getDaysDiff(nowdate,createdate);
				}

				if (days == 1) {
					timeStr = "昨天" + daytime.substring(10, 16);
				} else if (days == 2) {
					timeStr = "前天" + daytime.substring(10, 16);
				} else {
					timeStr = daytime.substring(0, 16);
				}
			}
		}

		return timeStr;

	}

	/**
	 * 获取指定日期的月份总共有多少天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDaysOfMonth(Date date) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(date);
		aCalendar.add(Calendar.MONTH, 1);
		aCalendar.add(Calendar.DAY_OF_MONTH, -1);
		date = aCalendar.getTime();
		return date.getDate();
	}

	/**
	 * 得到两个日期的相差的分钟数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMinutesDiff(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		return (int) (c1.getTimeInMillis() - c2.getTimeInMillis())
				/ (60 * 1000);
	}
	
	/**
	 * 得到两个日期的相差的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDaysDiff(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		return (int) Math.abs((c1.getTimeInMillis() - c2.getTimeInMillis())
				/ (24 * 3600 * 1000));

	}

	public static String getWeekOfDate(String daytime) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = format.parse(daytime);
			String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
					"星期六" };
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
				w = 0;
			return weekDays[w];
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 功能描述: 获得当前时间
	 * 
	 * @author wanggz 2013-12-7 下午3:02:09
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
				.format(new Date());
	}
	
	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	public static Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date = null;
	   try {
	    date = format.parse(str);
	   } catch (ParseException e) {
	    e.printStackTrace();
	   }
	   return date;
	}
	/**
	* 日期转换成字符串
	* @param date 
	* @return str
	*/
	public static String DateToStr(Date date) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String str = format.format(date);
	   return str;
	} 
	public static String DateToStr(Date date,String model) {
		  
		   SimpleDateFormat format = new SimpleDateFormat(model);
		   String str = format.format(date);
		   return str;
		}
	public static String cleverTimeString(String fullTimeStr)
	{
		
		Date dt=TimeUtility.StrToDate(fullTimeStr);
		int minutes=TimeUtility.getMinutesDiff(new Date(),dt);
		String timeStr;
		if(minutes==0)
			timeStr="刚刚";
		else if(minutes<60)
			timeStr=""+minutes+"分钟前";
		else if(minutes<12*60)
			timeStr=""+(int)Math.floor(minutes/60)+"小时前";
		else if(minutes<24*60*2)
		{
			 long intervalMilli = dt.getTime() -StrToDate(DateToStr(new Date(),"yyyy-MM-dd")+" 0:00:00").getTime(); 
			 int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
			 if(xcts==0)
				 timeStr="今天 "+TimeUtility.DateToStr(dt, "HH:mm");
			 else if(xcts==-1)
				 timeStr="昨天"+TimeUtility.DateToStr(dt, "HH:mm");
			 else if(xcts==-2)
				 timeStr="前天 "+TimeUtility.DateToStr(dt, "HH:mm");
			 else
				 timeStr=fullTimeStr;
		}
		else
			timeStr=fullTimeStr;
		return timeStr;
	}
	public static void popSoftKeyBoard(final Context context,final View v)
	{
		Timer timer = new Timer();
	     timer.schedule(new TimerTask()
	     {
	         public void run() 
	         {
				InputMethodManager inputManager =
	                 (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	             inputManager.showSoftInput(v, 0);
	         }

	     },  

	         500);
	}

}

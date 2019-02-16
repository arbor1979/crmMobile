package com.yujieshipin.crm.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 设置viewpager是否可以滑动
 * 
 *  <br/>创建说明: 2014-4-25 下午3:17:47 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class CustomViewPager extends ViewPager {

	private boolean isPagingEnabled;
	public CustomViewPager(Context context) {
		super(context);
		this.isPagingEnabled = true;
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.isPagingEnabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.isPagingEnabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.isPagingEnabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setPagingEnabled(boolean b) {
		this.isPagingEnabled = b;
	}
}
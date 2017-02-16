package com.yujieshipin.crm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
 *  #(c) ruanyun YeyPro <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 自身不滑动的Listview
 * 
 *  <br/>创建说明: 2014-1-13 下午4:25:05 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class NonScrollableListView extends ListView {

	public NonScrollableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NonScrollableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NonScrollableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private int maxHeight;

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
		getLayoutParams().height = getMeasuredHeight();
	}
}

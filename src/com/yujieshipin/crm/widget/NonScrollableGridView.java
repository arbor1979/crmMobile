package com.yujieshipin.crm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 解决Add a GridView to a ListView
 * 
 * @Title NonScrollableGridView.java
 * @Description: TODO
 * 
 * @author Zecker
 * @date 2013-10-30 下午3:33:56
 * @version V1.0
 * 
 */
public class NonScrollableGridView extends GridView {
//	public NonScrollableGridView(Context context) {
//		super(context);
//	}
//
//	public NonScrollableGridView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// Do not use the highest two bits of Integer.MAX_VALUE because they are
//		// reserved for the MeasureSpec mode
//		int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, heightSpec);
//		getLayoutParams().height = getMeasuredHeight();
//	}
	public NonScrollableGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		}
		public NonScrollableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		}

		public NonScrollableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		}
		     
		//通过重新dispatchTouchEvent方法来禁止滑动
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
		   return true;//禁止Gridview进行滑动
		}
		return super.dispatchTouchEvent(ev);
		}
		@Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int heightSpec;
	 
	        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
	            // The great Android "hackatlon", the love, the magic.
	            // The two leftmost bits in the height measure spec have
	            // a special meaning, hence we can't use them to describe height.
	            heightSpec = MeasureSpec.makeMeasureSpec(
	                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
	        }
	        else {
	            // Any other height should be respected as is.
	            heightSpec = heightMeasureSpec;
	        }
	 
	        super.onMeasure(widthMeasureSpec, heightSpec);
	    }


}


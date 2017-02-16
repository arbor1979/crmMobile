package com.yujieshipin.crm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class BottomTabLayout extends LinearLayout {
	private View mSlecetView = null;
	private OnCheckedChangeListener mOnCheckedChangeListener;

	public interface OnCheckedChangeListener {
		public void OnCheckedChange(View checkview);

		public void OnCheckedClick(View checkview);
	}

	public BottomTabLayout(Context context) {
		super(context);
	}

	public BottomTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onFinishInflate() {
		setListners();
	}

	public void setOnCheckedChangeListener(
			OnCheckedChangeListener changeListener) {
		mOnCheckedChangeListener = changeListener;
	}

	public View getSelectedView() {
		return mSlecetView;
	}

	private void setListners() {
		View child = null;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setCheckedView(v);
				}
			});
		}
	}

	private void setCheckedView(View selectview) {
		if (selectview != null) {
			View child = null;
			for (int i = 0; i < getChildCount(); i++) {
				child = getChildAt(i);
				if (child != selectview) {
					child.setSelected(false);
				}
			}
			boolean checkchange = mSlecetView != selectview;
			selectview.setSelected(true);
			mSlecetView = selectview;
			if (mOnCheckedChangeListener != null) {
				if (checkchange) {
					mOnCheckedChangeListener.OnCheckedChange(selectview);
				} else {
					mOnCheckedChangeListener.OnCheckedClick(selectview);
				}
			}

		}
	}
}

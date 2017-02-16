package com.yujieshipin.crm.widget.pulltorefresh;


import com.yujieshipin.crm.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 
 * #(c) ruanyun YeyPro <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 可下拉刷新ListView底部
 * 
 * <br/>
 * 创建说明: 2013-12-7 下午2:27:52 wanggz 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class RYFooterView extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private View mLayout;
	private View mProgressBar;
	private TextView mHintView;
	// private ImageView mHintImage;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

	private final int ROTATE_ANIM_DURATION = 180;
	private int mState = STATE_NORMAL;

	public RYFooterView(Context context) {
		super(context);
		initView(context);
	}

	public RYFooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_LOADING) {
			// mHintImage.clearAnimation();
			// mHintImage.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setVisibility(View.INVISIBLE);
		} else {
			mHintView.setVisibility(View.VISIBLE);
			// mHintImage.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state) {
		case STATE_NORMAL:
			// if (mState == STATE_READY) {
			// mHintImage.startAnimation(mRotateDownAnim);
			// }
			// if (mState == STATE_LOADING) {
			// mHintImage.clearAnimation();
			// }
			mHintView.setText(R.string.footer_hint_load_normal);
			break;

		case STATE_READY:
			if (mState != STATE_READY) {
				// mHintImage.clearAnimation();
				// mHintImage.startAnimation(mRotateUpAnim);
				mHintView.setText(R.string.footer_hint_load_ready);
			}
			break;

		case STATE_LOADING:
			break;
		}
		mState = state;
	}

	public void setBottomMargin(int height) {
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
				.getLayoutParams();
		lp.bottomMargin = height;
		mLayout.setLayoutParams(lp);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
				.getLayoutParams();
		return lp.bottomMargin;
	}

	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	/**
	 * loading status
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
				.getLayoutParams();
		lp.height = 0;
		mLayout.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLayout
				.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mLayout.setLayoutParams(lp);
	}

	private void initView(Context context) {
		mLayout = LayoutInflater.from(context)
				.inflate(R.layout.vw_footer, null);
		mLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		addView(mLayout);

		mProgressBar = mLayout.findViewById(R.id.footer_progressbar);
		mHintView = (TextView) mLayout.findViewById(R.id.footer_hint_text);
		// mHintImage = (ImageView) mLayout.findViewById(R.id.footer_arrow);

		mRotateUpAnim = new RotateAnimation(0.0f, 180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

}

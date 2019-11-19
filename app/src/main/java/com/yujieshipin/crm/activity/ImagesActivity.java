package com.yujieshipin.crm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.util.FileUtility;

public class ImagesActivity extends Activity {
	private static String TAG = "InteractImagesActivity";
	private static List<String> imagePaths = new ArrayList<String>();
	private List<String> imageComments = new ArrayList<String>();
	private int index = 0;// 当前图片的下标
	private ViewPager mViewPager;
	private TextView textView;
	private TextView textComment;
	private SamplePagerAdapter samplePagerAdapter;

	public void setImageComments(List<String> imageComments) {
		this.imageComments = imageComments;
	}

	private ImageButton ibtnLeft;
	private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();

	@SuppressLint("NewApi")
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_interact_images);

		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		ibtnLeft = (ImageButton) findViewById(R.id.ib_left);
		mViewPager = ((ViewPager) findViewById(R.id.zoom_imags));
		textView = ((TextView) findViewById(R.id.imags_index));
		textComment= ((TextView) findViewById(R.id.imags_comment));
		Intent intent = getIntent();
		imagePaths = intent.getStringArrayListExtra("pics");
		imageComments = intent.getStringArrayListExtra("txts");
		index = intent.getIntExtra("position", 0);
		Log.d(TAG, "------index:" + index);
		initListener();
		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
	}

	private void initListener() {
		// 退出
		ibtnLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageScrollStateChanged(int position) {
						Log.d(TAG, "-----position:" + position);
					}

					public void onPageScrolled(int position, float paramFloat,
							int paramInt2) {
						index = position;
						String comment="";
						if(imageComments!=null && imageComments.size()>position)
							comment=imageComments.get(position);
						if(comment!=null && comment.length()>0)
							textComment.setText(comment);
						else
							textComment.setText("");
						textView.setText((position + 1) + "/" + imagePaths.size());
					}

					public void onPageSelected(int paramInt) {
						Log.d(TAG, "------paramInt:" + paramInt);
					}
				});
	}

	@Override
	protected void onDestroy() {
		imagePaths.clear();
		images.clear();
		super.onDestroy();
	}

	@SuppressLint("ResourceAsColor")
	static class SamplePagerAdapter extends PagerAdapter {
		private LayoutInflater inflater;
		private Context context;
		private AQuery aq;

		public SamplePagerAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			aq = new AQuery(context);
		}

		public int getCount() {
			return imagePaths.size();
		}

		public int getItemPosition() {
			return POSITION_NONE;
		}

		public View instantiateItem(ViewGroup container, final int position) {
			View view = inflater.inflate(R.layout.zoom_images, null);
			final PhotoView zoomImageView = (PhotoView) view
					.findViewById(R.id.zoomview);
			final ProgressBar taProgressBar = (ProgressBar) view
					.findViewById(R.id.tb_progress);
			
			final String imageUrl = imagePaths.get(position);
			File imgCache=FileUtility.getCacheFile(imageUrl);
			if(imgCache.exists())
				zoomImageView.setImageURI(Uri.fromFile(imgCache));
			else
			{
				taProgressBar.setVisibility(View.VISIBLE);
				aq.id(zoomImageView).progress(taProgressBar)
				.image(imageUrl, false, true, 0, R.drawable.default_photo);
			}
				

			zoomImageView.setOnPhotoTapListener(new OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					((ImagesActivity) context).finish();
				}
			});

			container.addView(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return view;
		}

		public void destroyItem(ViewGroup paramViewGroup, int position,
				Object object) {
			Log.d(TAG, "destroyItem-------position" + position);
			paramViewGroup.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				samplePagerAdapter = new SamplePagerAdapter(ImagesActivity.this);
				mViewPager.setAdapter(samplePagerAdapter);
				mViewPager.setCurrentItem(index);
				break;
			default:
				break;
			}
		};
	};
}

package com.yujieshipin.crm.fragment.dialog;

import java.util.Date;

import com.androidquery.AQuery;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.callback.OnImageTouchedListener;
import com.yujieshipin.crm.util.ImageUtility;
import com.yujieshipin.crm.widget.ZoomableImageView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

@SuppressLint("NewApi")
public class ShowImageDialogFragment extends DialogFragment {
	private final String TAG = "ShowImageDialogFragment";
	private ZoomableImageView imageView;
	private ProgressBar progressBar;
	Bitmap bitmap = null;
	AQuery aq;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0 :
				progressBar.setVisibility(View.GONE);
				Bitmap bitmap = (Bitmap) msg.obj;
				imageView.setImageBitmap(bitmap);
				break;
			}
		}
		
	};
	public static ShowImageDialogFragment newInstance(String imageName) {

		ShowImageDialogFragment f = new ShowImageDialogFragment();
		Bundle args = new Bundle();
		args.putString("imageName", imageName);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int style = DialogFragment.STYLE_NO_TITLE;
		
		setStyle(style,R.style.zoomdialog);
		aq = new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.zoom_image, container, false);
		final String imageName = getArguments().getString("imageName");

		Log.d(TAG, "--->  " + imageName);

		imageView = (ZoomableImageView) v.findViewById(R.id.zoomview);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBarImage);
		progressBar.setVisibility(View.VISIBLE);
		Log.d(TAG, "--------------->startTime:" + String.valueOf(new Date().getTime()));
		imageView.setOnImageTouchedListener(new OnImageTouchedListener() {
			@Override
			public void onImageTouched() {
				dismiss();
			}
		});
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				showImage(imageName);
			}
		});
		thread.start();
		Log.d(TAG, "---------------->endTime:" + String.valueOf(new Date().getTime()));
		return v;
	}

	private void showImage(String imageUrl) {
		Bitmap bitmap = null;
		bitmap = ImageUtility.getDiskBitmapByPath(imageUrl);
		if(bitmap==null)
			bitmap = aq.getCachedImage(imageUrl);
		if(bitmap == null){
			bitmap = ImageUtility.getbitmap(imageUrl);
		}
		Message msg = new Message();
		msg.what = 0;
		msg.obj = bitmap;
		mHandler.sendMessage(msg);
	}
}

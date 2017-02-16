package com.example.androidgifdemo;

import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditTextEx extends EditText implements Runnable {
	private Vector<GifDrawalbe> drawables = new Vector<GifDrawalbe>();
	private Hashtable<Integer, GifDrawalbe> cache = new Hashtable<Integer, GifDrawalbe>();;
	
	private boolean mRunning = true;
	private Context context = null;
	private static final int SPEED = 100;
	
	public MyEditTextEx(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		new Thread(this).start();
	}

	public MyEditTextEx(Context context) {
		super(context);
		this.context = context;
		new Thread(this).start();
	}
	
	public void insertGif(String str) {
		if (drawables.size() > 0)
			drawables.clear();
		//鍦ㄥ厜鏍囧鎻掑叆鍔ㄦ�佽〃鎯�
		int index = getSelectionStart();  
		Editable editable = getText();
		editable.insert(index, str);
		SpannableString spannableString = ExpressionUtil.getExpressionString(context, getText().toString(), cache, drawables);
		setText(spannableString);
	}

	public void run() {
		while (mRunning) {
			if (super.hasWindowFocus()) {
				for (int i = 0; i < drawables.size(); i++) {
					drawables.get(i).run();
				}
				postInvalidate();
			}
			sleep();
		}
	}
	
	private void sleep() {
		try {
			Thread.sleep(SPEED);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		mRunning = false;
		drawables.clear();
		drawables = null;
	}

}

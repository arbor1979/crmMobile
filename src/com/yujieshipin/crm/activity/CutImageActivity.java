package com.yujieshipin.crm.activity;

import java.io.ByteArrayOutputStream;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.util.ImageUtility;
import com.zhy.view.ClipImageLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * @author zhy
 *
 */
public class CutImageActivity extends Activity
{
	private ClipImageLayout mClipImageLayout;
	String picPath;
	Button save;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cut_image);
		picPath=getIntent().getStringExtra("picPath");
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		mClipImageLayout.setPicPath(picPath);
		LinearLayout layout=(LinearLayout)findViewById(R.id.setting_layout_goto);
		layout.setVisibility(View.VISIBLE);
		
		save= (Button) findViewById(R.id.uploading);
		save.setCompoundDrawables(null, null, null, null); 
		save.setVisibility(View.VISIBLE);
		save.setText("保存");
		save.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Bitmap bitmap = mClipImageLayout.clip();
				if(bitmap!=null)
				{
					bitmap=ImageUtility.zoomBitmap(bitmap, 151);
					bitmap=ImageUtility.getRoundedCornerBitmap(bitmap,5,800);
					ImageUtility.writeTofiles(bitmap, picPath,100);
					Intent aintent = new Intent(CutImageActivity.this,ShowPersonInfo.class);
					aintent.putExtra("picPath", picPath);
					setResult(200,aintent); 
					finish();
				}
				
			}
			
		});
		Button bn_back = (Button) findViewById(R.id.back);
		bn_back.setVisibility(View.VISIBLE);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}

	
}

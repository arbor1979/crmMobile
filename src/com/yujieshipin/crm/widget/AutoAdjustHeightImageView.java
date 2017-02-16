package com.yujieshipin.crm.widget;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.drawable.BitmapDrawable;  
import android.graphics.drawable.Drawable;  
import android.util.AttributeSet;  
import android.widget.ImageView;  
  

public class AutoAdjustHeightImageView extends ImageView {
	 private int imageWidth;  
	    private int imageHeight;  
	    public AutoAdjustHeightImageView(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	     
	    }  
	      
	    private void getImageSize(Drawable drawable) {  
	        if (drawable == null) return;  
	        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();  
	        imageWidth = bitmap.getWidth();  
	        imageHeight = bitmap.getHeight();  
	    }  
	    @Override
		public void setImageDrawable(Drawable drawable)
	    {
	    	super.setImageDrawable(drawable);
	    	getImageSize(drawable);
	    }
	    @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	          
	        if(imageWidth>0)
	        {
	        	int width = MeasureSpec.getSize(widthMeasureSpec);
	        	int height = width  * imageHeight / imageWidth;  
	        	this.setMeasuredDimension(width, height);  
	        }
	        else
	        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    } 
}

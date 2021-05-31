package com.yujieshipin.crm.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ImageUtility {
	private static final String TAG = "ImageUtility";
	private static String SDPATH = Environment.getExternalStorageDirectory().getPath();

	/**
	 * 功能描述:获取本地图片sdcard
	 * 
	 * @author yanzy 2013-12-9 下午5:49:44
	 * 
	 * @param pathString
	 * @return
	 */
	public static Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(SDPATH + "/" + pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(SDPATH + "/" + pathString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	
	public static Bitmap getDiskBitmapByPath(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 根据一个网络连接(URL)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getfriendicon(URL imageUri) {

		BitmapDrawable icon = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imageUri
					.openConnection();
			icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * 根据一个网络连接(String)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getcontentPic(String imageUri) {
		URL imgUrl = null;
		try {
			imgUrl = new URL(imageUri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		BitmapDrawable icon = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imgUrl.openConnection();
			icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * 根据一个网络连接(URL)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static Bitmap getusericon(URL imageUri) {
		// 显示网络上的图片
		URL myFileUrl = imageUri;
		Bitmap bitmap = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.i(TAG, "image download finished." + imageUri);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 下载图片 同时写道本地缓存文件中
	 * 
	 * @param context
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmapAndwrite(String imageUri) {
		Bitmap bitmap = null;
		try {
			// 显示网络上的图片
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();

			InputStream is = conn.getInputStream();
			File cacheFile = FileUtility.getCacheFile(imageUri);
			BufferedOutputStream bos = null;
			bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
			Log.i(TAG, "write file to " + cacheFile.getCanonicalPath());

			byte[] buf = new byte[1024];
			int len = 0;
			// 将网络上的图片存储到本地
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}

			is.close();
			bos.close();

			// 从本地加载图片
			bitmap = BitmapFactory.decodeFile(cacheFile.getCanonicalPath());
			// String name = MD5Util.encoderByMd5(imageUri);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static boolean downpic(String picName, Bitmap bitmap) {
		boolean nowbol = false;
		try {
			File saveFile = new File(SDPATH+ "/PocketCampus/weibopic/" + picName+ ".png");
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			FileOutputStream saveFileOutputStream;
			saveFileOutputStream = new FileOutputStream(saveFile);
			nowbol = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					saveFileOutputStream);
			saveFileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowbol;
	}

	/**
	 * 功能描述:
	 *
	 * @author shengguo  2014-5-27 下午2:07:17
	 * 
	 * @param bitmap 
	 * @param filename 图片全名
	 */
	public static void writeTofiles(Bitmap bitmap,
			String filename) {
		Log.d(TAG, "writeTofiles-->filename" + filename);
		FileOutputStream outputStream = null;
		try {
			String filePath = filename.substring(0, filename.lastIndexOf("/"));
			Log.d(TAG, "-->filePath" + filePath);
			File filesPath = new File(filePath);
			// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果（此处以后可以扩展为已经存在图片不再重新下载功能）
			Log.d(TAG, "-->!filesPath.exists()" + !filesPath.exists());
			FileUtility.createFilePath(filePath);

			outputStream = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 65, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void writeTofiles(Bitmap bitmap,
			String filename,int pressRate) {
		Log.d(TAG, "writeTofiles-->filename" + filename);
		FileOutputStream outputStream = null;
		try {
			String filePath = filename.substring(0, filename.lastIndexOf("/"));
			Log.d(TAG, "-->filePath" + filePath);
			File filesPath = new File(filePath);
			// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果（此处以后可以扩展为已经存在图片不再重新下载功能）
			Log.d(TAG, "-->!filesPath.exists()" + !filesPath.exists());
			FileUtility.createFilePath(filePath);

			outputStream = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.JPEG, pressRate, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void writeTofilesPNG(Bitmap bitmap,
			String filename,int pressRate) {
		Log.d(TAG, "writeTofiles-->filename" + filename);
		FileOutputStream outputStream = null;
		try {
			String filePath = filename.substring(0, filename.lastIndexOf("/"));
			Log.d(TAG, "-->filePath" + filePath);
			File filesPath = new File(filePath);
			// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果（此处以后可以扩展为已经存在图片不再重新下载功能）
			Log.d(TAG, "-->!filesPath.exists()" + !filesPath.exists());
			FileUtility.createFilePath(filePath);

			outputStream = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, pressRate, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将文件写入缓存系统中
	 * 
	 * @param filename
	 * @param is
	 * @return
	 */
	public static String writefile(Context context, String filename,
			InputStream is) {
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(is);
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		} catch (Exception e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return context.getFilesDir() + "/" + filename + ".jpg";
	}

	// 根据指定宽度高度，放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}
	
	// 根据图片的宽度，等比例放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int h = height*w/width;
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if (bitmap == null) {
			return null;
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx,int zoomPx) {
		if (bitmap == null) {
			return null;
		}

		bitmap = zoomBitmap(bitmap, zoomPx, zoomPx); //压缩图片
		
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	 //质量压缩方法
    public static Bitmap compressImage(Bitmap image) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100*1024*100/baos.toByteArray().length;
        if(options==0){
        	options=1;
        }else if(options>100){
        	options=100;
        }
        Log.d(TAG, "这里压缩"+options+"%，把压缩后的数据存放到baos中");
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Log.d(TAG, "------------------bos"+baos.size());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }  
    
	// 获得带倒影的图片方法
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
	
	/**
	 * 功能描述:将Bitmap转成Bytes
	 *
	 * @author yanzy  2013-12-27 下午1:24:49
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
         return baos.toByteArray();
     }
	
	/**
	 * 功能描述:将Bytes转成Bitmap
	 *
	 * @author yanzy  2013-12-27 下午1:25:22
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap BytesToBimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
	public static int readPictureDegree(String path) {
	    int degree  = 0;
	    try {
	        ExifInterface exifInterface = new ExifInterface(path);
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	        switch (orientation) {
	            case ExifInterface.ORIENTATION_ROTATE_90:
	                degree = 90;
	                break;
	            case ExifInterface.ORIENTATION_ROTATE_180:
	                degree = 180;
	                break;
	            case ExifInterface.ORIENTATION_ROTATE_270:
	                degree = 270;
	                break;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return degree;
	}
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
	    //旋转图片 动作
	    Matrix matrix = new Matrix();;
	    matrix.postRotate(angle);
	    System.out.println("angle2=" + angle);
	    // 创建新的图片
	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
	            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	    return resizedBitmap;
	}
	public static void rotatingImageIfNeed(String filePath)
	{
		int orientation = ImageUtility.readPictureDegree(filePath);
    	if(Math.abs(orientation) > 0){
    	    Bitmap bitmap =  rotaingImageView(orientation, getBitmapFromPath(filePath,1280));//旋转图片
    	    writeTofiles(bitmap,filePath);
    	}
    	else
    	{
    		Bitmap bitmap=getBitmapFromPath(filePath,1280);
    		writeTofiles(bitmap,filePath);
    	}
	}
	

	public static Bitmap getBitmapFromPath(String srcPath,int maxSize)
	{
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了
	    newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, newOpts);
		

		int rate = 0;
		for (int i = 0;; i++) {
			if ((newOpts.outWidth >> i <= maxSize)
					&& (newOpts.outHeight >> i <= maxSize)) {
				rate = i;
				break;
			}
		}

		newOpts.inSampleSize = (int) Math.pow(2, rate);
		newOpts.inJustDecodeBounds = false;
	    Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);
	    return bitmap;
	}
	public static boolean hasSDCard()
	{
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			return true;
		} else {
			return false;
		}
	}
	public static boolean addImageToMediaStore(Context mContext,String imagePath,String fileName)
	{
		if(hasSDCard())
		{

			Bitmap bmp = getDiskBitmapByPath(imagePath);

			try {

				android.content.ContentResolver cr = mContext.getContentResolver();
				MediaStore.Images.Media.insertImage(cr, bmp, fileName, "");
				
				return true;
			}catch(Exception e){

				e.printStackTrace();
				return false;
			}

		}else
		{

			return false;

		}
			
	}
	public static Drawable decodeFile(Context context,File f,int width) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = width;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, o2);
            BitmapDrawable db=new BitmapDrawable(context.getResources(),bitmap);
            return db;
            /*
            ImageView iv = new ImageView(context);
            iv.setImageBitmap(bitmap);
            return iv.getDrawable();
            */
        } catch (FileNotFoundException e) {

        }
        return null;
    }
}

package com.yujieshipin.crm.widget;

import java.util.ArrayList;

import com.yujieshipin.crm.entity.StuInfoBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

@SuppressLint("DrawAllocation")
public class StuInfoBarGraph extends View {

	private ArrayList<StuInfoBar> bars = new ArrayList<StuInfoBar>();
	private boolean showClear = true;
	private Paint paint = new Paint();
	private Bitmap fullImage;
	private boolean shouldUpdate = false;
	private RectF r;

	public ArrayList<StuInfoBar> getBars() {
		return bars;
	}

	public void setBars(ArrayList<StuInfoBar> bars) {
		this.bars = bars;
		postInvalidate();
	}

	public StuInfoBarGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StuInfoBarGraph(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onDraw(Canvas ca) {
		// TODO Auto-generated method stub
		if (fullImage == null || shouldUpdate) {
			fullImage = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(fullImage);
			canvas.drawColor(Color.TRANSPARENT);

			float maxVaules = 100;
			float leftPadding = 100;
			float rightPadding = 70;
			float barDistance = 10;
			float barHeight = (getHeight() - (barDistance * 2) * bars.size())
					/ bars.size();
			
			int count = 0;

			r = new RectF();
			for (StuInfoBar p : bars) {
				// 绘制柱状背景
				int left = (int) (leftPadding);
				int top = (int) ((barDistance * 2) * count + barHeight * count);
				int right = (int) (getWidth() - rightPadding);
				int bottom = (int) ((barDistance * 2) * count + barHeight
						* (count + 1));
				r.set(left, top, right, bottom);
				this.paint.setColor(Color.parseColor("#cfe6d9"));
				canvas.drawRoundRect(r, 6, 6, this.paint);
				// 绘制侧栏文字
				this.paint.setColor(Color.BLACK);
				this.paint.setAlpha(200);
				this.paint.setTextSize(22);
				this.paint.setAntiAlias(true);
				int x = 0;
				int y = (int) ((r.top + r.bottom) / 2 + 4);
				if (paint.measureText(p.getName()) <= leftPadding) {
					canvas.drawText(p.getName(), x, y, this.paint);
				} else {
					StringBuffer str = new StringBuffer();
					char[] ch = p.getName().toCharArray();
					float eachWidth = paint.measureText(p.getName())
							/ ch.length;
					int num = (int) Math.floor(leftPadding / eachWidth);
					for (int i = 0; i < num; i++) {
						str = str.append(ch[i]);
						Log.i("str", str.toString());
						canvas.drawText(str.toString(), x, y, paint);
					}
					StringBuffer str1 = new StringBuffer();
					for (int i = num; i < ch.length; i++) {
						str1 = str1.append(ch[i]);
						canvas.drawText(str1.toString(), x, y + 20, paint);
					}
				}
				// 绘制侧栏分数
				this.paint.setColor(Color.RED);
				this.paint.setAlpha(255);
				int textX = (int) (getWidth() - rightPadding + 10);
				int textY = y;
				canvas.drawText(String.valueOf(p.getGrade()), textX, textY,
						this.paint);
				// 绘制分数柱形
				int leftinner = (int) (leftPadding);
				int topinner = (int) ((barDistance * 2) * count + barHeight
						* count);
				int rightinner = (int) (leftPadding + (p.getGrade() / maxVaules)
						* (getWidth() - leftPadding - rightPadding));
				int bottominner = (int) ((barDistance * 2) * count + barHeight
						* (count + 1));
				r.set(leftinner, topinner, rightinner, bottominner);
				if (p.getGrade() < 60) {
					this.paint.setColor(Color.parseColor("#f43737"));
					canvas.drawRoundRect(r, 6, 6, this.paint);
				} else {
					this.paint.setColor(Color.parseColor("#27ae62"));
					canvas.drawRoundRect(r, 6, 6, paint);
				}

				count++;
			}
		}
		ca.drawBitmap(fullImage, 0, 0, null);
	}

}

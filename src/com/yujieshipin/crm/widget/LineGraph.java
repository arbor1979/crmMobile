/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.yujieshipin.crm.widget;

import java.util.ArrayList;

import com.yujieshipin.crm.entity.Line;
import com.yujieshipin.crm.entity.LinePoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

@SuppressLint({ "DrawAllocation", "DrawAllocation" })
public class LineGraph extends View {

	private ArrayList<Line> lines = new ArrayList<Line>();
	Paint paint = new Paint();
	private int lineToFill = -1;
	private int indexSelected = -1;
	private OnPointClickedListener listener;
	private Bitmap fullImage;
	private boolean shouldUpdate = false;
	private String[] values = { "0", "20", "40", "60", "80", "100" };

	public LineGraph(Context context) {
		super(context);
	}

	public LineGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMinY(float minY) {

	}

	public void removeAllLines() {
		while (lines.size() > 0) {
			lines.remove(0);
		}
		shouldUpdate = true;
		postInvalidate();
	}

	public void addLine(Line line) {
		lines.add(line);
		shouldUpdate = true;
		postInvalidate();
	}

	public ArrayList<Line> getLines() {
		return lines;
	}

	public void setLineToFill(int indexOfLine) {
		this.lineToFill = indexOfLine;
		shouldUpdate = true;
		postInvalidate();
	}

	public int getLineToFill() {
		return lineToFill;
	}

	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}

	public Line getLine(int index) {
		return lines.get(index);
	}

	public int getSize() {
		return lines.size();
	}

	public void onDraw(Canvas ca) {
		if (fullImage == null || shouldUpdate) {
			fullImage = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(fullImage);

			paint.reset();

			float maxValue = 100;
			float bottomPadding = 40, topPadding = 40;
			float sidePadding = 10;
			float leftPadding = 60;
			float textRight = 7;
			float usableHeight = getHeight() - bottomPadding - topPadding;
			float usableWidth = getWidth() - 2 * sidePadding - leftPadding;
			float lineDistance = usableHeight / 5;
			// 绘制背景线条和文字；
			for (int i = 0; i <= 5; i++) {
				paint.setColor(Color.BLACK);
				paint.setAlpha(50);
				paint.setStrokeWidth(1);
				paint.setAntiAlias(true);
				canvas.drawLine(leftPadding, getHeight() - bottomPadding
						- lineDistance * i, getWidth() - sidePadding,
						getHeight() - bottomPadding - lineDistance * i, paint);
				// Rect r = new Rect();
				// paint.getTextBounds(values[count], 0, 1, r);
				paint.setAlpha(200);
				paint.setTextSize(20);
				canvas.drawText(values[i],
						leftPadding - paint.measureText(values[i]) - textRight,
						getHeight() - bottomPadding - lineDistance * i, paint);

			}

			// 绘制底部文字
			if (!lines.isEmpty()) {
				int pointCount = 0;
				for (LinePoint p : lines.get(0).getPoints()) {
					paint.setTextSize(20);
					canvas.drawText(String.valueOf(pointCount + 1), leftPadding
							+ 2*sidePadding
							+ (usableWidth /lines.get(0).getSize())
							* pointCount, getHeight() - 5,
							paint);
					pointCount++;
				}
			}
			// 绘制线条
			int index = 0;
			float lastTextX = 0,newTextX = 0;
			float lastTextY = 0,newTextY = 0;
			float lastLineX = 0,newLineX = 0;
			float lastLineY = 0,newLineY = 0;
			for (Line line : lines) {
				int count = 0;
				float lastXPixels = 0, newYPixels = 0;
				float lastYPixels = 0, newXPixels = 0;
				paint.reset();
				paint.setAntiAlias(true);
				paint.setColor(line.getColor());
				paint.setTextSize(20);
				if(index == 0){
					lastTextX = getWidth() - sidePadding - paint.measureText(line.getTitle());
					lastTextY = topPadding;
					lastLineX = getWidth() - sidePadding - paint.measureText(line.getTitle());
					lastLineY = topPadding/2;
				canvas.drawText(line.getTitle(), lastTextX, lastTextY, paint);
				canvas.drawLine(lastLineX, lastLineY, getWidth()-sidePadding, lastLineY, paint);
				}else{
					newTextX = lastTextX - paint.measureText(line.getTitle());
					newTextY = topPadding;
					newLineX = lastLineX - paint.measureText(line.getTitle());
					newLineY = topPadding/2;
					canvas.drawText(line.getTitle(), newTextX, newTextY, paint);
					canvas.drawLine(newLineX, newLineY, lastLineX, newLineY, paint);
					lastTextX = newTextX;
					lastTextY = newTextY;
					lastLineX = newLineX;
					lastLineY = newLineY;
				};
				paint.setStrokeWidth(3);
				for (LinePoint p : line.getPoints()) {
					float yPercent = p.getGrade() / maxValue;
					if (count == 0) {
						lastXPixels = leftPadding + 2*sidePadding;
						lastYPixels = getHeight() - bottomPadding
								- (usableHeight * yPercent);
					} else {
						newXPixels = leftPadding + 2*sidePadding
								+ (usableWidth / line.getSize()) * count;
						newYPixels = getHeight() - bottomPadding
								- (usableHeight * yPercent);
						canvas.drawLine(lastXPixels, lastYPixels, newXPixels,
								newYPixels, paint);
						lastXPixels = newXPixels;
						lastYPixels = newYPixels;
					}
					count++;
				}
				index++;
			}
			// 绘制点
			int pointCount = 0;
			for (Line line : lines) {
				int count = 0;
				paint.reset();
				paint.setColor(line.getColor());
				paint.setStrokeWidth(3);
				paint.setStrokeCap(Paint.Cap.ROUND);
				paint.setTextSize(20);
				paint.setAntiAlias(true);
				if (line.isShowingPoints()) {
					for (LinePoint p : line.getPoints()) {
						float yPercent = p.getGrade() / maxValue;
						float xPixels = leftPadding + 2*sidePadding
								+ (usableWidth / line.getSize()) * count;
						float yPixels = getHeight() - bottomPadding
								- (usableHeight * yPercent);

						canvas.drawCircle(xPixels, yPixels, 5, paint);
						canvas.drawText(String.valueOf(p.getGrade()), xPixels - paint.measureText(String.valueOf(p.getGrade()))/2, yPixels - 5, paint);
						Path path2 = new Path();
						path2.addCircle(xPixels, yPixels, 30, Direction.CW);
						p.setPath(path2);
						p.setRegion(new Region((int) (xPixels - 30),
								(int) (yPixels - 30), (int) (xPixels + 30),
								(int) (yPixels + 30)));

						if (indexSelected == pointCount && listener != null) {
							paint.setColor(Color.parseColor("#33B5E5"));
							paint.setAlpha(100);
							canvas.drawPath(p.getPath(), paint);
							paint.setAlpha(255);
						}
						count++;
						pointCount++;
					}
				}
			}

			shouldUpdate = false;
		}

		ca.drawBitmap(fullImage, 0, 0, null);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		Point point = new Point();
		point.x = (int) event.getX();
		point.y = (int) event.getY();

		int count = 0;
		int lineCount = 0;
		int pointCount = 0;

		Region r = new Region();
		for (Line line : lines) {
			pointCount = 0;
			for (LinePoint p : line.getPoints()) {

				if (p.getPath() != null && p.getRegion() != null) {
					r.setPath(p.getPath(), p.getRegion());
					if (r.contains((int) point.x, (int) point.y)
							&& event.getAction() == MotionEvent.ACTION_DOWN) {
						indexSelected = count;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						if (r.contains((int) point.x, (int) point.y)
								&& listener != null) {
							listener.onClick(lineCount, pointCount);
						}
						indexSelected = -1;
					}
				}

				pointCount++;
				count++;
			}
			lineCount++;

		}

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_UP) {
			shouldUpdate = true;
			postInvalidate();
		}

		return true;
	}

	public void setOnPointClickedListener(OnPointClickedListener listener) {
		this.listener = listener;
	}

	public interface OnPointClickedListener {
		abstract void onClick(int lineIndex, int pointIndex);
	}
}

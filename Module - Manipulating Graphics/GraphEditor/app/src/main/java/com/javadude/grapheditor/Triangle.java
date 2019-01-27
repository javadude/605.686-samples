package com.javadude.grapheditor;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by scott on 5/8/2016.
 */
public class Triangle extends Shape {
	private int strokeWidth;
	private final int fillColor;
	private ColorStateList strokeColor;
	private Path path;
	private Paint strokePaint;
	private Paint fillPaint;

	public Triangle(int strokeWidth, int fillColor, ColorStateList strokeColor) {
		this.strokeWidth = strokeWidth;
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;

		this.strokePaint = new Paint();
		this.strokePaint.setStyle(Paint.Style.STROKE);
		this.strokePaint.setColor(strokeColor.getColorForState(new int[0], 0));
		this.strokePaint.setStrokeJoin(Paint.Join.ROUND);
		this.strokePaint.setStrokeWidth(strokeWidth);

		this.fillPaint = new Paint();
		this.fillPaint.setStyle(Paint.Style.FILL);
		this.fillPaint.setColor(fillColor);
	}

	public void setState(int[] stateList) {
		this.strokePaint.setColor(strokeColor.getColorForState(stateList, 0));
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(path, fillPaint);
		canvas.drawPath(path, strokePaint);
	}

	@Override
	protected void onResize(float width, float height) {
		super.onResize(width, height);
		path = new Path();
		path.moveTo(width/2, 0);
		path.lineTo(width, height);
		path.lineTo(0, height);
		path.close();
	}
}

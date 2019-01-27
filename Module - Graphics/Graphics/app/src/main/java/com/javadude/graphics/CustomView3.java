package com.javadude.graphics;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CustomView3 extends View {
	private int cx = 0;
	private int cy = 0;

	private int dx = 10;
	private int dy = 20;

	private Paint paint;
	private Paint triangleStrokePaint;
	private Paint triangleFillPaint;
	private int color;
	private int radius;

	private Thread mover;
	private Path path;

	public CustomView3(Context context) {
		super(context);
//		init();
	}

	public CustomView3(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
		processAttributes(context, attrs);
	}

	public CustomView3(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init();
		processAttributes(context, attrs);
	}

	private void move() {
		cx += dx;
		cy += dy;

		if (cx < radius) {
			dx = -dx;
			cx = radius;
		}
		if (cy < radius) {
			dy = -dy;
			cy = radius;
		}
		if (cx > getWidth() - radius) {
			dx = -dx;
			cx = getWidth() - radius;
		}
		if (cy > getHeight() - radius) {
			dy = -dy;
			cy = getHeight() - radius;
		}
	}


	private void processAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView2);
		color = a.getColor(R.styleable.CustomView2_color, 0xffcccccc);
		a.recycle();
	}

	private void init() {
		paint = new Paint();
//		Resources.Theme theme = getContext().getTheme();
//		int color = ResourcesCompat.getColor(getResources(), R.color.circleColor, theme);
		paint.setColor(color);
		radius = Math.min(getWidth(), getHeight()) / 3;

		triangleStrokePaint = new Paint();
		triangleStrokePaint.setStrokeWidth(getResources().getDimension(R.dimen.strokeWidth));
		triangleStrokePaint.setStyle(Paint.Style.STROKE);
		triangleStrokePaint.setStrokeJoin(Paint.Join.ROUND);

		triangleFillPaint = new Paint();
		triangleFillPaint.setColor(Color.BLUE);
		triangleFillPaint.setStyle(Paint.Style.FILL);
		triangleFillPaint.setStrokeJoin(Paint.Join.ROUND);


		path = new Path();
		path.moveTo(40,40);
		path.lineTo(getWidth()-40, 40);
		path.lineTo(getWidth()/2, getHeight() - 40);
		path.close();

		mover = new Thread() {
			@Override
			public void run() {
				while(!isInterrupted()) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						interrupt();
					}

					move();

					postInvalidate();
				}
			}
		};

		mover.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mover.interrupt();
		mover = null;
		paint = null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (paint == null)
			init();

		canvas.drawColor(Color.WHITE);

		canvas.drawPath(path, triangleFillPaint);
		canvas.drawPath(path, triangleStrokePaint);
		canvas.drawCircle(cx, cy, radius, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = determineMeasure(widthMeasureSpec, R.dimen.preferredWidth);
		int height = determineMeasure(heightMeasureSpec, R.dimen.preferredHeight);

		setMeasuredDimension(width, height);
	}

	private int determineMeasure(int measureSpec, int preferredSizeId) {
		float preferredSize = getResources().getDimension(preferredSizeId);
		int size = MeasureSpec.getSize(measureSpec);
		int mode = MeasureSpec.getMode(measureSpec);

		switch(mode) {
			case MeasureSpec.AT_MOST:
				return (int) Math.min(preferredSize, size);

			case MeasureSpec.EXACTLY:
				return size;

			case MeasureSpec.UNSPECIFIED:
				return (int) preferredSize;

			default:
				throw new IllegalArgumentException("Unhandled measure mode: " + mode);
		}
	}
}

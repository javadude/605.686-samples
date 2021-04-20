package com.javadude.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PuckView extends View {
	private int cx = 0;
	private int cy = 0;

	private int dx = 10;
	private int dy = 20;

	private int ax = 0;
	private int ay = 0;

	private Paint paint;
	private int radius;

	private Thread mover;

	public PuckView(Context context) {
		super(context);
	}

	public void changeAccel(int ax, int ay) {
		this.ax = ax;
		this.ay = ay;
	}
	private void move() {
		dx += ax;
		dy += ay;

		cx += dx;
		cy += dy;

		if (cx < radius) {
			dx = -dx/2;
			cx = radius;
		}
		if (cy < radius) {
			dy = -dy/2;
			cy = radius;
		}
		if (cx > getWidth() - radius) {
			dx = -dx/2;
			cx = getWidth() - radius;
		}
		if (cy > getHeight() - radius) {
			dy = -dy/2;
			cy = getHeight() - radius;
		}
	}


	private void init() {
		paint = new Paint();
		paint.setColor(Color.RED);
		radius = Math.min(getWidth(), getHeight()) / 10;

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
		canvas.drawCircle(cx, cy, radius, paint);
	}
}

package com.javadude.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

public class CustomView2 extends View {

	private Paint paint;
	private int color;

	public CustomView2(Context context) {
		super(context);
//		init();
	}

	public CustomView2(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
		processAttributes(context, attrs);
	}

	public CustomView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init();
		processAttributes(context, attrs);
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
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (paint == null)
			init();

		canvas.drawColor(Color.WHITE);

		int cx = getWidth()/2;
		int cy = getHeight()/2;
		int radius = Math.min(getWidth(), getHeight()) / 3;

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

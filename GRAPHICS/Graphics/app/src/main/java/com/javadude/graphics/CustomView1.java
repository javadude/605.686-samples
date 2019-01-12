package com.javadude.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class CustomView1 extends View {
	public CustomView1(Context context) {
		super(context);
	}

	public CustomView1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomView1(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		Drawable drawable = getResources().getDrawable(R.mipmap.bb);

		assert drawable != null;

		drawable.setBounds(getWidth()/2, getHeight()/2, getWidth(), getHeight());

		drawable.draw(canvas);

		Paint paint = new Paint();
		paint.setColor(Color.BLUE);

		for(int i = 0; i <= 100; i++) {
			canvas.drawLine(getWidth()*i/100, 0,
							0, getHeight() - (getHeight()*i/100), paint);
		}

	}
}

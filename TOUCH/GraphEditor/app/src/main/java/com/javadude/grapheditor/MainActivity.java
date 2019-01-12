package com.javadude.grapheditor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private ShapeDrawable triangleDrawable;
	private Drawable squareDrawable;
	private Drawable circleDrawable;
	private float shapeSize;
	private float lineWidth;
	private int lineColor;
	private DrawingArea drawingArea;
	private Thing tappedThing = null;


	private enum Mode {
		AddSquare, AddCircle, AddTriangle, Select, SelectFirstEndpoint, SelectSecondEndpoint;
	}
	private Mode mode = null;
	private volatile boolean blink = false;

	private Runnable blinker = new Runnable() {
		@Override
		public void run() {
			try {
				blink = true;
				drawingArea.postInvalidate();
				Thread.sleep(250);
				blink = false;
				drawingArea.postInvalidate();
				Thread.sleep(250);
				blink = true;
				drawingArea.postInvalidate();
				Thread.sleep(250);
				blink = false;
				drawingArea.postInvalidate();
				tappedThing = null;
			} catch (InterruptedException e) {
				blink = false;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView triangleImageView = (ImageView) findViewById(R.id.triangleButton);
		float strokeWidth = getResources().getDimension(R.dimen.strokeWidth);
		shapeSize = getResources().getDimension(R.dimen.shapeSize);
		int triangleFillColor = getResources().getColor(R.color.triangleColor);
		ColorStateList strokeColor = getResources().getColorStateList(R.color.stroke);
		squareDrawable = getResources().getDrawable(R.drawable.square);
		circleDrawable = getResources().getDrawable(R.drawable.circle);
		triangleDrawable = createTriangle((int) strokeWidth, triangleFillColor, strokeColor);

		lineColor = getResources().getColor(R.color.lineColor);
		lineWidth = getResources().getDimension(R.dimen.lineWidth);

		assert triangleImageView != null;
		triangleImageView.setImageDrawable(createTriangle((int) strokeWidth, triangleFillColor, strokeColor));

		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		drawingArea = new DrawingArea(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		drawingArea.setLayoutParams(layoutParams);
		assert mainLayout != null;
		mainLayout.addView(drawingArea);
	}

	private ShapeDrawable createTriangle(int strokeWidth, int triangleFillColor, ColorStateList strokeColor) {
		final Triangle triangle = new Triangle(strokeWidth, triangleFillColor, strokeColor);
		ShapeDrawable shapeDrawable = new ShapeDrawable(triangle) {
			@Override
			protected boolean onStateChange(int[] stateSet) {
				triangle.setState(stateSet);
				return super.onStateChange(stateSet);
			}

			@Override
			public boolean isStateful() {
				return true;
			}
		};
		shapeDrawable.setIntrinsicHeight((int) shapeSize);
		shapeDrawable.setIntrinsicWidth((int) shapeSize);
		shapeDrawable.setBounds(0, 0, (int) shapeSize, (int) shapeSize);
		return shapeDrawable;
	}

	public void buttonPressed(View view) {
		switch(view.getId()) {
			case R.id.circleButton:
				mode = Mode.AddCircle;
				break;
			case R.id.squareButton:
				mode = Mode.AddSquare;
				break;
			case R.id.lineButton:
				mode = Mode.SelectFirstEndpoint;
				break;
			case R.id.triangleButton:
				mode = Mode.AddTriangle;
				break;
			case R.id.selectionButton:
				mode = Mode.Select;
				break;
		}
		ViewGroup group = (ViewGroup) view.getParent();
		for(int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			if (child != view) {
				child.setSelected(false);
			}
		}
		view.setSelected(true);
	}

	private class DrawingArea extends View {
		private List<Thing> things = new ArrayList<>();
		private List<Line> lines = new ArrayList<>();
		private Thing selectedThing = null;
		private Thing thing1 = null;
		private Paint linePaint = new Paint();

		public DrawingArea(Context context) {
			super(context);
			linePaint.setColor(lineColor);
			linePaint.setStrokeWidth(lineWidth);
			linePaint.setStyle(Paint.Style.STROKE);
		}

		private Thing findThingAt(int x, int y) {
			for(int i = things.size()-1; i>=0; i--) {
				Thing thing = things.get(i);
				if (thing.getBounds().contains(x, y)) {
					return thing;
				}
			}
			return null;
		}
		private Rect thingBounds(int x, int y, int size) {
			int halfSize = size / 2;
			return new Rect(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					switch(mode) {
						case AddSquare:
							things.add(new Thing(Thing.Type.Square,
									thingBounds((int)event.getX(), (int)event.getY(), (int)shapeSize)));
							break;
						case AddCircle:
							things.add(new Thing(Thing.Type.Circle,
									thingBounds((int)event.getX(), (int)event.getY(), (int)shapeSize)));
							break;
						case AddTriangle:
							things.add(new Thing(Thing.Type.Triangle,
									thingBounds((int)event.getX(), (int)event.getY(), (int)shapeSize)));
							break;
						case Select:
							selectedThing = findThingAt((int) event.getX(), (int) event.getY());
							if(selectedThing != null) {
								tappedThing = selectedThing;
								things.remove(selectedThing);
								things.add(selectedThing);
								new Thread(blinker).start();
							}
							break;
						case SelectFirstEndpoint:
							thing1 = findThingAt((int) event.getX(), (int) event.getY());
							if (thing1 != null) {
								mode = Mode.SelectSecondEndpoint;
							}
							return true;
						case SelectSecondEndpoint:
							Thing thing2 = findThingAt((int) event.getX(), (int) event.getY());
							if (thing2 != null) {
								if (thing1 == thing2) {
									thing1 = null;
								} else {
									Line line = new Line(thing1, thing2);
									lines.add(line);
									thing1 = null;
									invalidate();
								}
								mode = Mode.SelectFirstEndpoint;
							}
							return true;
					}
					invalidate();
					return true;
				case MotionEvent.ACTION_MOVE:
					if (selectedThing != null) {
						selectedThing.setBounds(thingBounds((int)event.getX(), (int)event.getY(), (int)shapeSize));
					}
					invalidate();
					return true;
				case MotionEvent.ACTION_UP:
					selectedThing = null;
					invalidate();
					return true;
			}
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			for(Line line : lines) {
				int x1 = line.getEnd1().getBounds().centerX();
				int y1 = line.getEnd1().getBounds().centerY();
				int x2 = line.getEnd2().getBounds().centerX();
				int y2 = line.getEnd2().getBounds().centerY();
				canvas.drawLine(x1, y1, x2, y2, linePaint);
			}
			Drawable drawableToUse = null;
			for(Thing thing : things) {
				switch(thing.getType()) {
					case Square:
						drawableToUse = squareDrawable;
						break;
					case Circle:
						drawableToUse = circleDrawable;
						break;
					case Triangle:
						drawableToUse = triangleDrawable;
						break;
				}

				if (thing == selectedThing || (blink && tappedThing != null && thing.getType() == tappedThing.getType())) {
					drawableToUse.setState(selectedState);
				} else {
					drawableToUse.setState(unselectedState);
				}

				drawableToUse.setBounds(thing.getBounds());
				drawableToUse.draw(canvas);
			}
		}
	}
	private static final int[] selectedState = {android.R.attr.state_selected};
	private static final int[] unselectedState = {};
}

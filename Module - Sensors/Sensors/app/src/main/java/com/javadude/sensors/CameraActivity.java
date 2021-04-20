package com.javadude.sensors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {
	private static final int permissionsRequestCode = 42;
	private static final String CAMERA_PERMISSION = "android.permission.CAMERA";
	private static final double smoothFactor = 0.15;

	private Camera camera;
	private FrameLayout preview;
	private DrawOnTop drawOnTop;

	private Sensor accelerometer;
	private Sensor magnetometer;
	private final float[] gravity = new float[3];
	private final float[] geomagnetic = new float[3];
	private final float[] matrixR = new float[9];
	private final float[] orientation = new float[3];
	private float horizontalViewAngle;
	private float verticalViewAngle;
	private int halfHorizontalViewAngle;
	private int halfVerticalViewAngle;
	private SensorManager sensorManager;
	private float textSize;

	private double compass;
	private double elevation;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		// Create an instance of Camera
		// Create our Preview view and set it as the content of our activity.
		preview = (FrameLayout) findViewById(R.id.camera_preview);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		textSize = getResources().getDimension(R.dimen.text_size);

		setupCamera();
	}


	SensorEventListener accelListener = new SensorEventListener() {
		@Override public void onSensorChanged(final SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				System.arraycopy(event.values, 0, geomagnetic, 0, 3);
			else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				System.arraycopy(event.values, 0, gravity, 0, 3);
			updateOrientation();
		}
		@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};


	private double lowpass(double newValue, double oldValue) {
		if (Math.abs(newValue - oldValue) < 180) {
			return oldValue + smoothFactor * (newValue - oldValue);
		}
		else {
			if (oldValue > newValue) {
				return (oldValue + smoothFactor * ((360 + newValue - oldValue) % 360) + 360) % 360;
			}
			else {
				return (oldValue - smoothFactor * ((360 - newValue + oldValue) % 360) + 360) % 360;
			}
		}
	}

	private void updateOrientation() {
		if (SensorManager.getRotationMatrix(matrixR, null, gravity, geomagnetic)) {
			//noinspection SuspiciousNameCombination
			SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, matrixR);
			SensorManager.getOrientation(matrixR, orientation);
			compass = lowpass((Math.toDegrees(orientation[0]) + 360) % 360, compass);
			elevation = lowpass(Math.toDegrees(orientation[1])%360, elevation);
			drawOnTop.postInvalidate();
		}
	}





	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Throwable e) {
			Log.e("CAMERA", "Camera not available", e);
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(accelListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(accelListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(accelListener);
		releaseCamera();              // release the camera immediately on pause event
		super.onPause();
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();        // release the camera for other applications
			camera = null;
		}
	}

	private boolean needsRuntimePermissions() {
		return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
	}
	private void setupCamera() {
		if (needsRuntimePermissions()) {
			if (checkSelfPermission(CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
				String[] permissions = {CAMERA_PERMISSION};
				requestPermissions(permissions, permissionsRequestCode);
			} else {
				createCamera();
			}
		} else {
			createCamera();
		}
	}
	@Override
	public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
		switch(permsRequestCode){
			case permissionsRequestCode:
				if (grantResults[0]== PackageManager.PERMISSION_GRANTED) {
					createCamera();
				} else {
					TextView textView = new TextView(this);
					textView.setText("You denied me permission!");
					preview.addView(textView);
				}
				break;
		}
	}
	private void createCamera() {
		camera = getCameraInstance();
		preview.addView(new CameraPreview(this, camera));
		drawOnTop = new DrawOnTop(this);
		addContentView(drawOnTop, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		Camera.Parameters parameters = camera.getParameters();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		camera.setParameters(parameters);
		horizontalViewAngle = parameters.getHorizontalViewAngle();
		verticalViewAngle = parameters.getVerticalViewAngle();
		halfHorizontalViewAngle = (int)(horizontalViewAngle/2);
		halfVerticalViewAngle = (int)(verticalViewAngle/2);
	}

	private class DrawOnTop extends View {
		private Paint paint1;
		private Paint paint2;
		private final Rect bounds = new Rect();
		public DrawOnTop(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (paint1 == null) {
				paint1 = new Paint();
				paint1.setColor(Color.YELLOW);
				paint1.setStyle(Paint.Style.FILL);
				paint2 = new Paint();
				paint2.setColor(Color.GREEN);
				paint2.setStyle(Paint.Style.STROKE);
				paint2.setTextSize(textSize);
			}
			int left = (int)compass - halfHorizontalViewAngle;
			int right = (int)compass + halfHorizontalViewAngle;
			int bottom = (int)elevation - halfVerticalViewAngle;
			int top = (int)elevation + halfVerticalViewAngle;

			int chunkSize = 5;

			int xpixelsPerDegree = getWidth()/(int)horizontalViewAngle;
			int xpixelsPerChunk = xpixelsPerDegree * chunkSize;
			int xoffset = left % chunkSize;
			int xstart = left + chunkSize - xoffset;
			int x = xpixelsPerDegree * xoffset;

			for (int i = xstart; i < right; i+=chunkSize) {
				String text = ""+(i%360);
				if (i % 20 == 0)
					canvas.drawText(text, x - paint2.measureText(text)/2, getHeight()-40, paint2);
				canvas.drawLine(x, getHeight(), x, getHeight()-30, paint2);
				x += xpixelsPerChunk;
			}

			int ypixelsPerDegree = getHeight()/(int)verticalViewAngle;
			int ypixelsPerChunk = ypixelsPerDegree * chunkSize;
			int yoffset = bottom % chunkSize;
			int ystart = bottom + chunkSize - yoffset;
			int y = ypixelsPerDegree * yoffset;

			for (int i = ystart; i < top; i+=chunkSize) {
				String text = ""+(-(i%360));
				paint2.getTextBounds(text, 0, text.length(), bounds);
				if (i % 20 == 0)
					canvas.drawText(text, 40, y + (bounds.bottom-bounds.top)/2f, paint2);
				canvas.drawLine(0, y, 30, y, paint2);
				y += ypixelsPerChunk;
			}
		}
	}
}

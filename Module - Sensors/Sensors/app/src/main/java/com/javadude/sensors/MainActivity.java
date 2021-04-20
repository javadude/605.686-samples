package com.javadude.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView accelX;
    private TextView accelY;
    private TextView accelZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Display defaultDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        defaultDisplay = windowManager.getDefaultDisplay();
//        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        for(Sensor sensor : sensorList) {
//            Log.d("SENSOR", "name: " + sensor.getName());
//            Log.d("SENSOR", "type: " + sensor.getStringType());
//            Log.d("SENSOR", "power: " + sensor.getPower() + " mA");
//            Log.d("SENSOR", "resolution: " + sensor.getResolution());
//            Log.d("SENSOR", "max range: " + sensor.getMaximumRange());
//            Log.d("SENSOR", " ");
//        }

        accelX = (TextView) findViewById(R.id.accelX);
        accelY = (TextView) findViewById(R.id.accelY);
        accelZ = (TextView) findViewById(R.id.accelZ);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];
    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // alpha is calculated as t / (t + dT)
            // with t, the low-pass filter's time-constant
            // and dT, the event delivery rate

            float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];


            float x = 0;
            float y = 0;
            switch(defaultDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    x = linear_acceleration[0];
                    y = linear_acceleration[1];
                    break;
                case Surface.ROTATION_90:
                    x = -linear_acceleration[1];
                    y = linear_acceleration[0];
                case Surface.ROTATION_180:
                    x = -linear_acceleration[0];
                    y = -linear_acceleration[1];
                    break;
                case Surface.ROTATION_270:
                    x = linear_acceleration[1];
                    y = -linear_acceleration[0];
            }
            accelX.setText(x + "");
            accelY.setText(y + "");
            accelZ.setText(linear_acceleration[2] + "");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}

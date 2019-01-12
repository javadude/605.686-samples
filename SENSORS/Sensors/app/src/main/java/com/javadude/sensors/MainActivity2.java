package com.javadude.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private WindowManager windowManager;
    private Display defaultDisplay;
    private PuckView puckView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puckView = new PuckView(this);
        setContentView(puckView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        defaultDisplay = windowManager.getDefaultDisplay();

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

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = 0;
            float y = 0;
            switch(defaultDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    x = event.values[0];
                    y = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    x = -event.values[1];
                    y = event.values[0];
                case Surface.ROTATION_180:
                    x = -event.values[0];
                    y = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    x = event.values[1];
                    y = -event.values[0];
            }
            puckView.changeAccel(-(int)x * 5, (int)y * 5);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}

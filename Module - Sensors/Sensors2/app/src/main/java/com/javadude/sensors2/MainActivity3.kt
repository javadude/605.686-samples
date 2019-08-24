package com.javadude.sensors2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Display
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_2.*

class MainActivity3 : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var defaultDisplay: Display
    private lateinit var gravitySensor: Sensor
    private lateinit var puckView : PuckView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        puckView = PuckView(this)
        setContentView(puckView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        defaultDisplay = windowManager.defaultDisplay

        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            var xValue = 0f
            var yValue = 0f
            when (defaultDisplay.rotation) {
                Surface.ROTATION_0 -> {
                    xValue = event.values[0]
                    yValue = event.values[1]
                }
                Surface.ROTATION_90 -> {
                    xValue = -event.values[1]
                    yValue = event.values[0]
                }
                Surface.ROTATION_180 -> {
                    xValue = -event.values[0]
                    yValue = -event.values[1]
                }
                Surface.ROTATION_270 -> {
                    xValue = event.values[1]
                    yValue = -event.values[0]
                }
            }
            puckView.ax = -xValue.toInt() * 5
            puckView.ay = yValue.toInt() * 5
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }
}

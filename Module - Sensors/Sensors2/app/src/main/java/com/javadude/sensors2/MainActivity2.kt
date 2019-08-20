package com.javadude.sensors2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_2.*

class MainActivity2 : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private lateinit var defaultDisplay: Display
    private lateinit var sensors: List<Sensor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        defaultDisplay = windowManager.defaultDisplay

        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        sensors = listOf(
            gravitySensor, linearAccelerationSensor, magneticFieldSensor, rotationVectorSensor, proximitySensor
        )
        sensor_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            arrayOf("Gravity", "Linear Acceleration", "Magnetic Field", "Rotation", "Proximity"))

        sensor_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                sensorManager.unregisterListener(listener)
                clearFields()
            }

            override fun onItemSelected(spinner: AdapterView<*>?, itemView: View?, position: Int, id: Long) {
                sensorManager.unregisterListener(listener)
                clearFields()
                sensorManager.registerListener(listener, sensors[position], SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun clearFields() {
        x.text = ""
        y.text = ""
        z.text = ""
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(listener, sensors[0], SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(listener)
    }

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.values.size == 1) {
                x.text = event.values[0].toString()
                y.text = ""
                z.text = ""

            } else {
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
                x.text = xValue.toString()
                y.text = yValue.toString()
                z.text = event.values[2].toString()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }
}

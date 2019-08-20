package com.javadude.sensors2

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main_1.*
import kotlinx.android.synthetic.main.sensor_item.view.*

class MainActivity1 : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_1)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        recycler_view.adapter = SensorAdapter(sensorManager.getSensorList(Sensor.TYPE_ALL))
    }

    class SensorAdapter(private val sensors : List<Sensor>) : RecyclerView.Adapter<SensorViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SensorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sensor_item, parent, false))

        override fun getItemCount() = sensors.size

        override fun onBindViewHolder(holder: SensorViewHolder, position: Int) =
            holder.bind(sensors[position])
    }
    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(sensor: Sensor) {
            itemView.name.text = sensor.name
            itemView.type.text = sensor.stringType
            itemView.power.text = "${sensor.power}mA"
            itemView.resolution.text = sensor.resolution.toString()
            itemView.max_range.text = sensor.maximumRange.toString()
        }
    }
}

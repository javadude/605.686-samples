package com.javadude.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContentView(CustomView1(this))
//        setContentView(R.layout.activity_main2)
//        setContentView(R.layout.activity_main3)
        setContentView(R.layout.activity_main4)

        val picto = findViewById<PictographView>(R.id.picto)
        findViewById<Button>(R.id.button).setOnClickListener {
            picto.count = picto.count + 0.6f
        }
    }
}

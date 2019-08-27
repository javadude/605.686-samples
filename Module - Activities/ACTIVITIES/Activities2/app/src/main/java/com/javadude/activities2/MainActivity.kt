package com.javadude.activities2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.genres_value).text = "Comedy-Drama"
        findViewById<EditText>(R.id.title_field).setText("Field of Dreams")
        findViewById<EditText>(R.id.year_field).setText("1989")
    }
}

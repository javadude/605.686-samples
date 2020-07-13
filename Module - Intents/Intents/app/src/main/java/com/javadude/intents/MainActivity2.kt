package com.javadude.intents

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.util.*

class MainActivity2 : MyActivityBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val data = intent.getStringExtra("data") ?: "no data"
        findViewById<TextView>(R.id.data).text = data

        R.id.button_ok.onClick {
            setResult(RESULT_OK, Intent().apply {
                putExtra("resultInfo", data.toUpperCase(Locale.getDefault()))
            })
            finish()
        }
        R.id.button_cancel.onClick {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}

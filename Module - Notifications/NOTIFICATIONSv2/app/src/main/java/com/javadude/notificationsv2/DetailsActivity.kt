package com.javadude.notificationsv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.content_main.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val id = intent.extras?.getInt("id", 0) ?: 0
        val message = intent.extras?.getString("message", "") ?: ""

        text.text = "From notification $id\n$message"
    }
}

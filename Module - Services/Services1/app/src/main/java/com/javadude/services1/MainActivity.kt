package com.javadude.services1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Activity that starts a "started" service
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        startService(Intent(this, StartedService::class.java))
    }

    override fun onStop() {
        stopService(Intent(this, StartedService::class.java))
        super.onStop()
    }
}


package com.javadude.services1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

// Activity that starts a bound service and sends it "reset" requests
// This one is used to show binding overlap
class MainActivity2a : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reset_button.setOnClickListener {
            binder?.reset()
        }
    }

    override fun onStart() {
        Log.d("MainActivity2a", "onStart")
        super.onStart()
        bindService(Intent(this, BoundService1::class.java),
            serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.d("MainActivity2a", "onStop")
        unbindService(serviceConnection)
        super.onStop()
    }


    private var binder : BoundService1.BoundServiceBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            this@MainActivity2a.binder = null
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            this@MainActivity2a.binder = binder as BoundService1.BoundServiceBinder
        }
    }
}


package com.javadude.services1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*

// Activity that starts a bound service and sends it "reset" requests
// This one is used to show binding overlap
class MainActivity3a : AppCompatActivity() {

    private val reporter = object : BoundService2.Reporter {
        override fun report(i: Int) {
            runOnUiThread {
                progressBar.progress = i
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reset_button.setOnClickListener {
            binder?.reset()
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, BoundService2::class.java),
            serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        binder?.removeReporter(reporter)
        unbindService(serviceConnection)
        super.onStop()
    }


    private var binder : BoundService2.BoundServiceBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            this@MainActivity3a.binder = null
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            this@MainActivity3a.binder = binder as BoundService2.BoundServiceBinder
            binder.addReporter(reporter)
        }
    }
}


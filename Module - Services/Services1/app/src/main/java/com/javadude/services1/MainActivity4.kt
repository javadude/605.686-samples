package com.javadude.services1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

// Activity that connects to a remove service (defined in Services2) using a Messenger and Handler
// This demonstrates a callback as well
class MainActivity4 : AppCompatActivity() {
    private val messenger = Messenger(MyHandler(this))
    private var remoteService : Messenger? = null

    class MyHandler(activity : MainActivity4) : Handler() {
        private val activityRef = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = activityRef.get()
            activity?.let {
                when (msg.what) {
                    REPORT -> it.progressBar.progress = msg.arg1
                    else -> super.handleMessage(msg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reset_button.setOnClickListener {
            val message = Message.obtain(null, RESET).apply {
                replyTo = messenger
            }
            remoteService?.send(message)
        }

        other_activity_button.setOnClickListener {
            startActivity(Intent(this, MainActivity3a::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent().apply {
            setClassName("com.javadude.services2", "com.javadude.services2.RemoteService1")
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        val message = Message.obtain(null, REMOVE_REPORTER).apply {
            replyTo = messenger
        }
        remoteService?.send(message)
        unbindService(serviceConnection)
        super.onStop()
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            remoteService = null
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            remoteService = Messenger(binder)
            val message = Message.obtain(null, ADD_REPORTER).apply {
                replyTo = messenger
            }
            remoteService?.send(message)
        }
    }
    companion object {
        const val RESET = 1
        const val ADD_REPORTER = 2
        const val REMOVE_REPORTER = 3
        const val REPORT = 100
    }
}


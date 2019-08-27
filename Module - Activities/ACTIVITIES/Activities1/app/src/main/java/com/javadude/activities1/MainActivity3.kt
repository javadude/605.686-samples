package com.javadude.activities1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class MainActivity3 : AppCompatActivity() {
    var n = 1
    var textView : TextView? = null

    inner class Updater : LifecycleObserver {
        var counter : Counter? = null
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun startThread() {
            counter = Counter()
            counter?.start()
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun stopThread() {
            counter?.interrupt()
            counter = null
        }
    }

    inner class Counter : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    sleep(1000)
                } catch (e : InterruptedException) {
                    interrupt()
                }
                n++
                runOnUiThread {
                    textView?.text = n.toString()
                }
                Log.d("!!", "count=$n")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("!!", "onCreate3")
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(Updater())

        textView = findViewById(R.id.textView)
        val button = findViewById<Button>(R.id.button)

        textView?.text = n.toString()
        button.setOnClickListener {
            n++
            textView?.text = n.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("!!", "onResume3")
    }

    override fun onStart() {
        super.onStart()
        Log.d("!!", "onStart3")
    }

    override fun onPause() {
        Log.d("!!", "onPause3")
        super.onPause()
    }
    override fun onStop() {
        Log.d("!!", "onStop3")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("!!", "onDestroy3")
        super.onDestroy()
    }
}

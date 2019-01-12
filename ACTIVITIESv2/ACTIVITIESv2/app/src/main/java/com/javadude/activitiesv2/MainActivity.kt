package com.javadude.activitiesv2

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var n : Int = 0

    inner class Updater : LifecycleObserver {
        var updateThread : UpdateThread? = null

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun start() {
            updateThread = UpdateThread()
            updateThread!!.start()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun stop() {
            updateThread?.interrupt()
            updateThread = null
        }

        inner class UpdateThread : Thread() {
            override fun run() {
                while(!isInterrupted) {
                    updateText()
                    Log.d("THREAD", n.toString())
                    try {
                        sleep(1000)
                    } catch (e: InterruptedException) {
                        interrupt()
                    }
                }
            }
        }
    }

    fun updateText() {
        val newText = edit_text.text.toString() + n
        runOnUiThread { text.text = newText }
        n++
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MY ACTIVITY", "onCreate")
        setContentView(R.layout.activity_main)

        if (savedInstanceState !== null) {
            n = savedInstanceState.getInt("n")
        }

        button.setOnClickListener({ _ -> updateText() })
        lifecycle.addObserver(Updater())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("n", n)
    }

    override fun onStart() {
        Log.d("MY ACTIVITY", "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("MY ACTIVITY", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("MY ACTIVITY", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("MY ACTIVITY", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("MY ACTIVITY", "onDestroy")
        super.onDestroy()
    }
}

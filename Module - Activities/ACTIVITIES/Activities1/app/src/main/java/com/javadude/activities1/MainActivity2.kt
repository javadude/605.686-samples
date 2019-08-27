package com.javadude.activities1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    var n = 1
    var textView : TextView? = null
    var counter : Counter? = null

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
        Log.d("!!", "onCreate")
        setContentView(R.layout.activity_main)

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
        Log.d("!!", "onResume")
        counter = Counter()
        counter?.start()
    }

    override fun onStart() {
        super.onStart()
        Log.d("!!", "onStart")
    }

    override fun onPause() {
        Log.d("!!", "onPause")
        counter?.interrupt()
        counter = null
        super.onPause()
    }
    override fun onStop() {
        Log.d("!!", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("!!", "onDestroy")
        super.onDestroy()
    }
}

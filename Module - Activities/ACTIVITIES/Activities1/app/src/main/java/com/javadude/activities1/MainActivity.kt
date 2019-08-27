package com.javadude.activities1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var n = 1

//    inner class ButtonListener(val textView : TextView) : View.OnClickListener {
//        override fun onClick(p0: View?) {
//            n++
//            textView.text = n.toString()
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        n = PreferenceManager.getDefaultSharedPreferences(this)
            .getInt("n", 1)

//        if (savedInstanceState != null) {
//            n = savedInstanceState.getInt("n", 1)
//        }
        val textView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)


        textView.text = n.toString()
//        button.setOnClickListener(ButtonListener(textView))
//        button.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                n++
//                textView.text = n.toString()
//            }
//        })
        button.setOnClickListener {
            n++
            textView.text = n.toString()
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt("n", n)
//    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putInt("n", n)
            .apply()
    }
}

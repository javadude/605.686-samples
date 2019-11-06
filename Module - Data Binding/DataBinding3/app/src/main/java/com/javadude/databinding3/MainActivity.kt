package com.javadude.databinding3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Wow... this is tiny. We just set up the layout (which is a NavHost). There might be a little
 * more here if we had a toolbar or nav drawer...
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

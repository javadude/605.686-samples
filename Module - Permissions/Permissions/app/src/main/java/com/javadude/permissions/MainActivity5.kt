package com.javadude.permissions

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Permissions using QuickPermissions-Kotlin library
 *     https://github.com/QuickPermissions/QuickPermissions-Kotlin
 * This version does not permanently disable the button;
 *   it tries again each time the user chooses the action
 * We use a helper function runWithPermissions2 to reduce code further for calls and
 *   give more details about which permissions are needed.
 */
class MainActivity5 : AppCompatActivity() {
    private lateinit var recordButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.record_button)

        recordButton.setOnClickListener {
            runWithPermissions2("record audio", Manifest.permission.RECORD_AUDIO) {
                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

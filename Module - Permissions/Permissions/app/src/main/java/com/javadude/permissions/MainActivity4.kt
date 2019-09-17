package com.javadude.permissions

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions

/**
 * Permissions using QuickPermissions-Kotlin library
 *     https://github.com/QuickPermissions/QuickPermissions-Kotlin
 * This version does not permanently disable the button;
 *   it tries again each time the user chooses the action
 */
class MainActivity4 : AppCompatActivity() {
    private lateinit var recordButton : Button

    private val recordPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        handlePermanentlyDenied = true,
        rationaleMessage = "This permission is required to record audio. Please press allow if you want to record audio.",
        permanentlyDeniedMessage = "You can no longer record audio because you denied permission. If you want to perform this action, please open settings and enable the 'record audio' permission"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.record_button)

        recordButton.setOnClickListener {
            runWithPermissions(Manifest.permission.RECORD_AUDIO, options = recordPermissionsOption) {
                Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.javadude.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/**
 * Permissions using QuickPermissions-Kotlin library
 *     https://github.com/QuickPermissions/QuickPermissions-Kotlin
 * This version respects "don't ask again" with a helper class PermissionAction
 *   to reduce code further for calls and give more details about which permissions are needed.
 */
class MainActivity6 : AppCompatActivity() {
    private lateinit var recordButton : Button

    private val recordAction = PermissionAction(this,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            description = "record and write file",
            onDisabled = {recordButton.isEnabled = false}) {
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
    }

    // NOTE: PermissionAction sets up a lifecycle observer to check if any permissions
    //          have been permanently denied when we're resuming the activity
    //       This allows the PermissionAction to be defined before onCreate() is called

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.record_button)

        if (!recordAction.somePermissionPermanentlyDenied()) {
            recordButton.setOnClickListener {
                recordAction.runWithPermissions()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}

package com.javadude.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Basic permissions use in Android without third-party libraries
 */
class MainActivity1 : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_RECORD_AUDIO = 42
    }

    private lateinit var recordButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.record_button)

        recordButton.setOnClickListener {
            tryToRecordAudio()
        }
    }

    private fun recordAudio() {
        // pretend to record...
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
    }

    private fun tryToRecordAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // explain to the user why we need the permission
            AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("We need you to grant permission to record audio.\n\nOn the next screen, please press 'Allow'")
                .setPositiveButton("Ok") {_,_ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_RECORD_AUDIO)
                }
                .show()

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            // Permission has already been granted
            recordAudio()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                    recordAudio()
                } else {
                    recordButton.isEnabled = false
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}

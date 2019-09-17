package com.javadude.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions

/**
 * Permissions using QuickPermissions-Kotlin library
 *     https://github.com/QuickPermissions/QuickPermissions-Kotlin
 * This version handles "don't ask again" requests
 */
class MainActivity3 : AppCompatActivity() {
    companion object {
        const val DISABLE_RECORD_AUDIO = "disable.record.audio"
    }

    private lateinit var recordButton : Button

    private val recordPermissionsOption = QuickPermissionsOptions(
        handleRationale = true,
        handlePermanentlyDenied = true,
        rationaleMessage = "This permission is required to record audio. Please press allow if you want to record audio.",
        permanentlyDeniedMessage = "You can no longer record audio because you denied permission. If you want to perform this action, please open settings and enable the 'record audio' permission",
        permissionsDeniedMethod = {
            recordButton.isEnabled = false
        },
        permanentDeniedMethod = {
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(DISABLE_RECORD_AUDIO, true)
                .apply()

            recordButton.isEnabled = false
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.record_button)

        val disableRecordAudio =
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
                PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .remove(DISABLE_RECORD_AUDIO)
                    .apply()
                false
            } else {
                PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(DISABLE_RECORD_AUDIO, false)
            }


        if (disableRecordAudio) {
            recordButton.isEnabled = false
        } else {
            recordButton.setOnClickListener {
                runWithPermissions(Manifest.permission.RECORD_AUDIO, options = recordPermissionsOption) {
                    Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

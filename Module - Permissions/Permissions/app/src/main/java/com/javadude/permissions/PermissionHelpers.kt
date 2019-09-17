package com.javadude.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.PreferenceManager
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest

/**
 * Helper functions for common behaviors we might want in a specific app with quickpermissions
 */
fun Context.runWithPermissions2(
    description: String? = null,
    vararg permissions: String,
    callback: () -> Unit
) {
    // nested function so we only lookup the names if we need them
    fun names() =
        permissions
            .map { getPermissionGroup(it) }
            .distinct()
            .sortedBy { it }
            .joinToString(prefix = "\n\t", separator = "\n\t") { it }

    val options =
        description?.let {
            QuickPermissionsOptions(
                handleRationale = true,
                handlePermanentlyDenied = true,
                rationaleMessage = "These permissions are required to $it.\n\nPlease press 'Try Again' and then 'Allow' if you want to perform this action.",
                permanentlyDeniedMessage = "You can no longer $it because you denied permission.\n\nIf you want to perform this action, please press 'Open Settings' and enable the following permissions:${names()}"
            )
        } ?:
        QuickPermissionsOptions(
            handleRationale = true,
            handlePermanentlyDenied = true
        )

    runWithPermissions(*permissions, callback = callback, options = options)
}

/**
 * A helper class that encapsulates an action that needs to be run with permissions
 */
class PermissionAction(
    private val context: AppCompatActivity,
    private vararg val permissions: String,
    private val description: String? = null,
    private val onDisabled : () -> Unit = {},
    private val action: () -> Unit
) : LifecycleObserver {

    // set up a lifecycle observer so we can check to see if we should run onDisabled()
    //   when we're starting the activity
    init {
        context.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun checkDisabled() {
        if (somePermissionPermanentlyDenied()) {
            onDisabled()
        }
    }

    fun runWithPermissions() {
        // nested function so we only lookup the names if we need them
        fun names() =
            permissions
                .map { context.getPermissionGroup(it) }
                .distinct()
                .sortedBy { it }
                .joinToString(prefix = "\n\t", separator = "\n\t") { it }

        val disabler = { request: QuickPermissionsRequest ->
            request.deniedPermissions.forEach { permission ->
                PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(permissionDeniedKey(permission), true)
                    .apply()
            }
            onDisabled()
        }

        val options =
            description?.let {
                QuickPermissionsOptions(
                    handleRationale = true,
                    handlePermanentlyDenied = true,
                    rationaleMessage = "These permissions are required to $it.\n\nPlease press 'Try Again' and then 'Allow' if you want to perform this action.",
                    permanentlyDeniedMessage = "You can no longer $it because you denied permission.\n\nIf you want to perform this action, please press 'Open Settings' and enable the following permissions:${names()}",
                    permissionsDeniedMethod = { onDisabled() },
                    permanentDeniedMethod = disabler
                )
            } ?: QuickPermissionsOptions(
                handleRationale = true,
                handlePermanentlyDenied = true,
                permissionsDeniedMethod = { onDisabled() },
                permanentDeniedMethod = disabler
            )

        context.runWithPermissions(*permissions, callback = action, options = options)
    }

    fun somePermissionPermanentlyDenied() =
        permissions.asSequence().map {permission ->
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .remove(permissionDeniedKey(permission))
                    .apply()
                false
            } else {
                PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(permissionDeniedKey(permission), false)
            }
        }.any { it }

    private fun permissionDeniedKey(permission : String) = "$permission.permanently.denied"
}

/**
 * Get the name of the permission group that a permission belongs to.
 * When changing permissions in settings, the toggles are listed for the groups,
 * not the individual permissions.
 */
fun Context.getPermissionGroup(permission: String) =
    packageManager.let {
        val permissionInfo = it.getPermissionInfo(permission, 0)
        val permissionGroupInfo = it.getPermissionGroupInfo(permissionInfo.group!!, 0)
        permissionGroupInfo.loadLabel(packageManager).toString()
    }

package com.javadude.files;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionAction {
	private Activity activity;
	private String[] requiredPermissions;
	private CharSequence description;
	private boolean permissionsDenied;

	protected abstract void onPermissionGranted();
	protected abstract void onPermissionDenied();

	public PermissionAction(Activity activity, int descriptionId, String... requiredPermissions) {
		this(activity, activity.getResources().getString(descriptionId), requiredPermissions);
	}
	public PermissionAction(Activity activity, CharSequence description, String... requiredPermissions) {
		this.activity = activity;
		this.requiredPermissions = requiredPermissions;
		this.description = description;
	}

	public String[] getRequiredPermissions() {
		return requiredPermissions;
	}
	public CharSequence getDescription() {
		return description;
	}

	void runAction(PermissionManager permissionManager, final int requestId) {
		if (permissionsDenied) {
			return;
		}

		// if we don't need runtime permissions on this device, just execute the request
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			onPermissionGranted();

		} else {
			// check which permissions have not been granted and need to be requested
			SharedPreferences preferences = activity.getSharedPreferences("requestedPermissions", Context.MODE_PRIVATE);

			PackageManager packageManager = activity.getPackageManager();
			List<String> ungrantedPermissions = null;
			String previouslyDeniedPermissions = null;
			for (String requiredPermission : requiredPermissions) {
				int currentPermissionResult = ContextCompat.checkSelfPermission(activity, requiredPermission);
				if (currentPermissionResult == PackageManager.PERMISSION_GRANTED) {
					// save the last result we've seen for this permission
					Log.d("PermissionAction", "PREVIOUSLY GRANTED: " + requiredPermission);
					preferences.edit().putInt(requiredPermission, currentPermissionResult).apply();
					continue;
				}
				int previousGrantResult = preferences.getInt(requiredPermission, -9999);
				if (previousGrantResult == -9999) {
					Log.d("PermissionAction", "NOT YET GRANTED: " + requiredPermission);
					if (ungrantedPermissions == null)
						ungrantedPermissions = new ArrayList<>();
					ungrantedPermissions.add(requiredPermission);
				}
				else if (previousGrantResult == PackageManager.PERMISSION_DENIED) {
					Log.d("PermissionAction", "PREVIOUSLY DENIED: " + requiredPermission);
					try {
						PermissionInfo permissionInfo = packageManager.getPermissionInfo(requiredPermission, 0);
						CharSequence label = permissionInfo.loadLabel(packageManager);
						// for some reason Android Studio thinks previouslyDeniedPermissions will always be null here... weird
						//noinspection ConstantConditions
						if (previouslyDeniedPermissions == null) {
							previouslyDeniedPermissions = "'" + label + "'";
						} else {
							previouslyDeniedPermissions += ", '" + label + "'";
						}
					} catch (PackageManager.NameNotFoundException e) {
						// TODO better handling here!!! Dialog? Throw?
						Log.d("PermissionAction", "Could not load requested permission", e);
					}
					break;
				}
			}

			// if the user had previously denied permissions, display message to prompt them to go to app settings
			//   (they may have checked "don't bother me again")
			// TODO - can we check for "don't bother me again" and not do this if they said that?
			if (previouslyDeniedPermissions != null) {
				new AlertDialog.Builder(activity)
						.setTitle(R.string.permission_needed_title)
						.setMessage(activity.getResources().getString(R.string.permissions_denied_message, previouslyDeniedPermissions, description != null ? description : activity.getResources().getString(R.string.perform_this_action)))
						.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								openApplicationSettings();
							}})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								onPermissionDenied();
								dialog.dismiss();
							}})
						.show();
				return;
			}

			// continue with normal permission request

			// if we've already been granted all permissions we need, perform the action
			if (ungrantedPermissions == null) {
				onPermissionGranted();
				return;
			}

			// otherwise we need to request permissions

			// if this action has a description, display it before requesting permission
			if (description != null) {
				final String[] ungrantedPermissionsArray = ungrantedPermissions.toArray(new String[ungrantedPermissions.size()]);
				new AlertDialog.Builder(activity)
						.setTitle(R.string.permission_needed_title)
						.setMessage(activity.getResources().getString(R.string.permission_needed_message, description))
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							@TargetApi(Build.VERSION_CODES.M) // we know this is true because of where the dialog is called
							@Override public void onClick(DialogInterface dialog, int which) {
								// request permissions. note that the activity using the PermissionsManager must override onRequestPermissionsResult and delegate to the PermissionManager
								activity.requestPermissions(ungrantedPermissionsArray, requestId);
								dialog.dismiss();
							}})
						.show();
			} else {
				activity.requestPermissions(ungrantedPermissions.toArray(new String[ungrantedPermissions.size()]), requestId);
			}
		}
	}

	void handlePermissionResponse(String[] permissions, int[] grantResults) {
		SharedPreferences.Editor editor = activity.getSharedPreferences("requestedPermissions", Context.MODE_PRIVATE).edit();
		for(int i = 0; i < permissions.length; i++) {
			int result = grantResults[i];
			editor.putInt(permissions[i], result);
			if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
				permissionsDenied = true;
			}
		}
		editor.apply();
		if (permissionsDenied) {
			onPermissionDenied();

		} else {
			// all requested permissions were granted; proceed
			onPermissionGranted();
		}
	}

	private void openApplicationSettings() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
		intent.setData(uri);
		activity.startActivity(intent);
	}
}


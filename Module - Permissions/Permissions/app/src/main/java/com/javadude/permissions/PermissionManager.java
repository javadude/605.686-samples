package com.javadude.permissions;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
	private Map<Integer, PermissionAction> requestIds = new HashMap<>();

	public void run(PermissionAction permissionAction) {
		// find a free requestId
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			if (!requestIds.containsKey(i)) {
				requestIds.put(i, permissionAction);
				permissionAction.runAction(this, i);
				return;
			}
		}
	}
	public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		PermissionAction permissionAction = requestIds.get(requestCode);
		if (permissionAction == null) {
			throw new IllegalStateException("Could not find action for requestCode " + requestCode);
		}

		requestIds.remove(requestCode);
		permissionAction.handlePermissionResponse(permissions, grantResults);
	}
}

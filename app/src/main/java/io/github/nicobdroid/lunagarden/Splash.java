package io.github.nicobdroid.lunagarden;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.github.nicobdroid.lunagarden.settings.FragmentBackStackActivity;
import io.github.nicobdroid.lunagarden.settings.FruitVegPrefs;

public class Splash extends AppCompatActivity {
	public static final String TAG = "Splash";

	/** The code used when requesting permissions */
	private static final int    PERMISSIONS_REQUEST = 1234;
	private boolean permissionsRequestLaunched;
	private boolean recheckPermissionsOnResume;

	/*
	 * ---------------------------------------------
	 *
	 * Getters
	 *
	 * ---------------------------------------------
	 */


	@SuppressWarnings("rawtypes")
	public Class getNextActivityClass() {
		// Save settings are done
		FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(getApplicationContext());
		if (fruitVegPrefs.areSettingsDone()) {
			return ActivityCalendar.class;
		} else {
			return FragmentBackStackActivity.class;
		}
	}

	/**
	 * Get the list of required permissions by searching the manifest. If you
	 * don't think the default behavior is working, then you could try
	 * overriding this function to return something like:
	 *
	 * <pre>
	 * <code>
	 * return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
	 * </code>
	 * </pre>
	 */
	public String[] getRequiredPermissions() {
		String[] permissions = null;
		try {
			permissions = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_PERMISSIONS).requestedPermissions;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "[getRequiredPermissions] Failed to get permissions from manifest", e);
		}
		if (permissions == null) {
			return new String[0];
		} else {
			return permissions.clone();
		}
	}

	/*
	 * ---------------------------------------------
	 *
	 * Activity Methods
	 *
	 * ---------------------------------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/* Default creation code. */
		AppearanceModeManager.applySavedMode(this);
		super.onCreate(savedInstanceState);

		/*
		 * On a post-Android 6.0 devices, check if the required permissions have
		 * been granted.
		 */
		checkPermissions();
	}

	/**
	 * See if we now have all of the required dangerous permissions. Otherwise,
	 * tell the user that they cannot continue without granting the permissions,
	 * and then request the permissions again.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
			if (allPermissionsGranted(grantResults)) {
				startNextActivity();
			} else {
				showPermissionDeniedDialog();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (recheckPermissionsOnResume) {
			recheckPermissionsOnResume = false;
			checkPermissions();
		}
	}

	/*
	 * ---------------------------------------------
	 *
	 * Other Methods
	 *
	 * ---------------------------------------------
	 */
	private void startNextActivity() {
		startActivity(new Intent(this, getNextActivityClass()));
		finish();
	}

	/**
	 * Check if the required permissions have been granted, and
	 * {@link #startNextActivity()} if they have. Otherwise
	 * {@link #requestPermissions(String[], int)}.
	 */
	private void checkPermissions() {

        String[] ungrantedPermissions = requiredPermissionsStillNeeded();
		if (ungrantedPermissions.length == 0) {
			startNextActivity();
		} else {
			if (!permissionsRequestLaunched) {
				permissionsRequestLaunched = true;
				requestPermissions(ungrantedPermissions, PERMISSIONS_REQUEST);
			} else {
				showPermissionDeniedDialog();
			}
		}
	}

	private boolean allPermissionsGranted(@NonNull int[] grantResults) {
		if (grantResults.length == 0) {
			return false;
		}
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private void showPermissionDeniedDialog() {
		if (isFinishing()) {
			return;
		}

		new AlertDialog.Builder(this)
				.setTitle("Autorisation recommandee")
				.setMessage("Certaines fonctionnalites peuvent etre limitees sans cette autorisation.")
				.setCancelable(false)
				.setPositiveButton("Continuer", (dialog, which) -> startNextActivity())
				.setNegativeButton("Ouvrir parametres", (dialog, which) -> openAppSettings())
				.show();
	}

	private void openAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.fromParts("package", getPackageName(), null));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		recheckPermissionsOnResume = true;
		startActivity(intent);
	}

	/**
	 * Convert the array of required permissions to a {@link Set} to remove
	 * redundant elements. Then remove already granted permissions, and return
	 * an array of ungranted permissions.
	 */
	private String[] requiredPermissionsStillNeeded() {

		Set<String> permissions = new HashSet<>();
		Collections.addAll(permissions, getRequiredPermissions());
		for (Iterator<String> i = permissions.iterator(); i.hasNext();) {
			String permission = i.next();
			if (permission == null || permission.trim().isEmpty()) {
				i.remove();
				continue;
			}
			if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
				i.remove();
			}
		}
		return permissions.toArray(new String[0]);
	}
}

package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public final class AppearanceModeManager {
    public static final String MODE_SYSTEM = "system";
    public static final String MODE_LIGHT = "light";
    public static final String MODE_DARK = "dark";
    public static final String PREF_APPEARANCE_MODE = "settings_appearance_mode";

    private AppearanceModeManager() {
    }

    public static void applySavedMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String mode = prefs.getString(PREF_APPEARANCE_MODE, MODE_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(toNightMode(mode));
    }

    public static boolean applyModeValue(String modeValue) {
        int targetMode = toNightMode(modeValue);
        if (AppCompatDelegate.getDefaultNightMode() == targetMode) {
            return false;
        }
        AppCompatDelegate.setDefaultNightMode(targetMode);
        return true;
    }

    private static int toNightMode(String modeValue) {
        if (MODE_LIGHT.equals(modeValue)) {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        if (MODE_DARK.equals(modeValue)) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}


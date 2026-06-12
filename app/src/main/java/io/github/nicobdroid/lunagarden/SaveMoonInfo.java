package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Objects;

public class SaveMoonInfo {
    public static final String TAG = "SaveMoonInfo";
    private final SharedPreferences mPrefs;

    // constructor
    public SaveMoonInfo(Context context) {
        Context mContext = Objects.requireNonNull(context, "Context must not be null").getApplicationContext();
        this.mPrefs = mContext.getSharedPreferences(
                AppTechnicalKeys.SHARED_PREF_MOON_INFO,
                Context.MODE_PRIVATE
        );
    }

    public void saveAscendingMoonNodeDate(String strValue) {
        Log.i(TAG, "Save Ascending Moon Node Date: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(AppTechnicalKeys.SAVE_MOON_ASCENDING_NODE, strValue);
        editor.apply();
    }

    public void saveDescendingMoonNodeDate(String strValue) {
        Log.i(TAG, "Save Descending Moon Node Date: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(AppTechnicalKeys.SAVE_MOON_DESCENDING_NODE, strValue);
        editor.apply();
    }

    public void saveApogeeMoonDate(String strValue) {
        Log.i(TAG, "Save Apogee Moon Date: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(AppTechnicalKeys.SAVE_MOON_APOGEE, strValue);
        editor.apply();
    }

    public void savePerigeeMoonDate(String strValue) {
        Log.i(TAG, "Save Perigee Moon Date: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(AppTechnicalKeys.SAVE_MOON_PERIGEE, strValue);
        editor.apply();
    }

    public void saveDayRacine(int dayRacine, String strValue) {
        Log.i(TAG, "Save Day Racine " + dayRacine + ": " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(getKeyForIndex(AppTechnicalKeys.SAVE_DAY_RACINE, dayRacine), strValue);
        editor.apply();
    }

    public void saveDayFeuille(int dayFeuille, String strValue) {
        Log.i(TAG, "Save Day Feuille " + dayFeuille + ": " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(getKeyForIndex(AppTechnicalKeys.SAVE_DAY_FEUILLE, dayFeuille), strValue);
        editor.apply();
    }

    public void saveDayFruit(int dayFruit, String strValue) {
        Log.i(TAG, "Save Day Fruit " + dayFruit + ": " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(getKeyForIndex(AppTechnicalKeys.SAVE_DAY_FRUIT, dayFruit), strValue);
        editor.apply();
    }

    public String readDayFruit(int dayFruit) {
        return getSharedPreferenceString(
                getKeyForIndex(AppTechnicalKeys.SAVE_DAY_FRUIT, dayFruit),
                getKeyForIndex(AppTechnicalKeys.DEFAULT_DAY_FRUIT, dayFruit)
        );
    }

    public String readDayRacine(int dayRacine) {
        return getSharedPreferenceString(
                getKeyForIndex(AppTechnicalKeys.SAVE_DAY_RACINE, dayRacine),
                getKeyForIndex(AppTechnicalKeys.DEFAULT_DAY_RACINE, dayRacine)
        );
    }

    public String readDayFeuille(int dayFeuille) {
        return getSharedPreferenceString(
                getKeyForIndex(AppTechnicalKeys.SAVE_DAY_FEUILLE, dayFeuille),
                getKeyForIndex(AppTechnicalKeys.DEFAULT_DAY_FEUILLE, dayFeuille)
        );
    }

    public String readAscendingMoonNodeDate() {
        return getSharedPreferenceString(
                AppTechnicalKeys.SAVE_MOON_ASCENDING_NODE,
                AppTechnicalKeys.DEFAULT_MOON_ASCENDING_NODE
        );
    }

    public String readDescendingMoonNodeDate() {
        return getSharedPreferenceString(
                AppTechnicalKeys.SAVE_MOON_DESCENDING_NODE,
                AppTechnicalKeys.DEFAULT_MOON_DESCENDING_NODE
        );
    }

    public String readApogeeMoonDate() {
        return getSharedPreferenceString(
                AppTechnicalKeys.SAVE_MOON_APOGEE,
                AppTechnicalKeys.DEFAULT_MOON_APOGEE
        );
    }

    public String readPerigeeMoonDate() {
        return getSharedPreferenceString(
                AppTechnicalKeys.SAVE_MOON_PERIGEE,
                AppTechnicalKeys.DEFAULT_MOON_PERIGEE
        );
    }

    private String getKeyForIndex(String[] values, int index) {
        int safeIndex = index;
        if (safeIndex < 0) {
            safeIndex = 0;
        }
        if (safeIndex >= values.length) {
            safeIndex = values.length - 1;
        }
        return values[safeIndex];
    }

    private String getSharedPreferenceString(String strTest, String defValue) {
        String iValue = defValue;

        if (isSharedPreferenceAvailable(strTest)) {
            iValue = mPrefs.getString(strTest, defValue);
        }

        return iValue;
    }

    private boolean isSharedPreferenceAvailable(String sharedPref) {
        boolean bResult = false;
        String strPreference;

        strPreference = sharedPref;

        if (mPrefs.contains(strPreference)) {
            bResult = true;
        }
        return bResult;
    }
}

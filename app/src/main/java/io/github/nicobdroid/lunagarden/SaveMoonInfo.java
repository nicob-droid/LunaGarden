package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Objects;

/**
 * Accès aux données lunaires persistées dans les SharedPreferences.
 * <p>
 * Encapsule toutes les opérations de lecture/écriture des dates de référence
 * (nœuds lunaires, apogée, périgée, jours racine/feuille/fruit).
 * </p>
 */
public class SaveMoonInfo {

    private static final String TAG = "SaveMoonInfo";

    private final SharedPreferences mPrefs;

    public SaveMoonInfo(Context context) {
        Context appContext = Objects.requireNonNull(context, "Context must not be null")
                .getApplicationContext();
        this.mPrefs = appContext.getSharedPreferences(
                AppTechnicalKeys.SHARED_PREF_MOON_INFO,
                Context.MODE_PRIVATE
        );
    }

    // -------------------------------------------------------------------------
    // Sauvegarde
    // -------------------------------------------------------------------------

    public void saveAscendingMoonNodeDate(String value) {
        Log.i(TAG, "Save Ascending Moon Node Date: " + value);
        putString(AppTechnicalKeys.SAVE_MOON_ASCENDING_NODE, value);
    }

    public void saveDescendingMoonNodeDate(String value) {
        Log.i(TAG, "Save Descending Moon Node Date: " + value);
        putString(AppTechnicalKeys.SAVE_MOON_DESCENDING_NODE, value);
    }

    public void saveApogeeMoonDate(String value) {
        Log.i(TAG, "Save Apogee Moon Date: " + value);
        putString(AppTechnicalKeys.SAVE_MOON_APOGEE, value);
    }

    public void savePerigeeMoonDate(String value) {
        Log.i(TAG, "Save Perigee Moon Date: " + value);
        putString(AppTechnicalKeys.SAVE_MOON_PERIGEE, value);
    }

    public void saveDayRacine(int index, String value) {
        Log.i(TAG, "Save Day Racine " + index + ": " + value);
        putString(keyAt(AppTechnicalKeys.SAVE_DAY_RACINE, index), value);
    }

    public void saveDayFeuille(int index, String value) {
        Log.i(TAG, "Save Day Feuille " + index + ": " + value);
        putString(keyAt(AppTechnicalKeys.SAVE_DAY_FEUILLE, index), value);
    }

    public void saveDayFruit(int index, String value) {
        Log.i(TAG, "Save Day Fruit " + index + ": " + value);
        putString(keyAt(AppTechnicalKeys.SAVE_DAY_FRUIT, index), value);
    }

    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    public String readAscendingMoonNodeDate() {
        return mPrefs.getString(AppTechnicalKeys.SAVE_MOON_ASCENDING_NODE,
                AppTechnicalKeys.DEFAULT_MOON_ASCENDING_NODE);
    }

    public String readDescendingMoonNodeDate() {
        return mPrefs.getString(AppTechnicalKeys.SAVE_MOON_DESCENDING_NODE,
                AppTechnicalKeys.DEFAULT_MOON_DESCENDING_NODE);
    }

    public String readApogeeMoonDate() {
        return mPrefs.getString(AppTechnicalKeys.SAVE_MOON_APOGEE,
                AppTechnicalKeys.DEFAULT_MOON_APOGEE);
    }

    public String readPerigeeMoonDate() {
        return mPrefs.getString(AppTechnicalKeys.SAVE_MOON_PERIGEE,
                AppTechnicalKeys.DEFAULT_MOON_PERIGEE);
    }

    public String readDayRacine(int index) {
        return mPrefs.getString(keyAt(AppTechnicalKeys.SAVE_DAY_RACINE, index),
                keyAt(AppTechnicalKeys.DEFAULT_DAY_RACINE, index));
    }

    public String readDayFeuille(int index) {
        return mPrefs.getString(keyAt(AppTechnicalKeys.SAVE_DAY_FEUILLE, index),
                keyAt(AppTechnicalKeys.DEFAULT_DAY_FEUILLE, index));
    }

    public String readDayFruit(int index) {
        return mPrefs.getString(keyAt(AppTechnicalKeys.SAVE_DAY_FRUIT, index),
                keyAt(AppTechnicalKeys.DEFAULT_DAY_FRUIT, index));
    }

    // -------------------------------------------------------------------------
    // Méthodes privées
    // -------------------------------------------------------------------------

    /** Écrit une valeur string dans les SharedPreferences. */
    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    /**
     * Retourne la clé/valeur à l'index donné dans un tableau, en limitant l'index
     * aux bornes du tableau (clamp).
     */
    private static String keyAt(String[] array, int index) {
        int safeIndex = Math.max(0, Math.min(index, array.length - 1));
        return array[safeIndex];
    }
}

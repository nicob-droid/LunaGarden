package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Logique de calcul des jours lunaires (Racine, Feuille, Fruit, Nœuds).
 * <p>
 * Cette classe est indépendante de tout composant Android UI (Activity, Fragment).
 * Elle peut être utilisée depuis n'importe quel contexte : Service, Fragment, Activity.
 * </p>
 */
public class MoonDayCalculator {

    private static final String TAG = "MoonDayCalculator";

    public static final int MOON_NODE_ASCENDING_NODE  = 0;
    public static final int MOON_NODE_DESCENDING_NODE = 1;
    public static final int MOON_NODE_APOGEE          = 2;
    public static final int MOON_NODE_PERIGEE         = 3;

    private static final double MOON_PHASE_LENGTH = 29.530588853;

    private final Context mContext;

    // Caches pour éviter de recalculer plusieurs fois le même jour
    private final Map<Integer, Boolean> dayRootCache  = new HashMap<>();
    private final Map<Integer, Boolean> dayLeafCache  = new HashMap<>();
    private final Map<Integer, Boolean> dayFruitCache = new HashMap<>();
    private final Map<Long, Boolean>    moonNodeCache = new HashMap<>();

    /**
     * @param context Le contexte Android (utilise getApplicationContext() en interne).
     */
    public MoonDayCalculator(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.mContext = context.getApplicationContext();
    }

    /** Vide tous les caches (utile lors d'un changement de mois). */
    public void clearCaches() {
        dayRootCache.clear();
        dayLeafCache.clear();
        dayFruitCache.clear();
        moonNodeCache.clear();
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    public boolean isDayRacine(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cached = dayRootCache.get(cacheKey);
        if (cached != null) return cached;

        boolean result = false;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(mContext);
        DateFormat fmt = new SimpleDateFormat("d_M_yyyy");

        for (int i = 0; i < 10 && !result; i++) {
            String refStr = saveMoonInfo.readDayRacine(i);
            try {
                Date current = fmt.parse(refStr);
                for (int k = 0; k < 26; k++) {
                    current = addTime(current, MOON_NODE_ASCENDING_NODE);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(current);

                    Calendar threeMonthsAgo = Calendar.getInstance();
                    threeMonthsAgo.add(Calendar.MONTH, -3);
                    if (threeMonthsAgo.after(cal)) {
                        saveMoonInfo.saveDayRacine(i, fmt.format(current));
                    }

                    if (cal.get(Calendar.YEAR) > year) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 > month) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.DAY_OF_MONTH) > day) break;
                    if (cal.get(Calendar.DAY_OF_MONTH) == day
                            && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.YEAR) == year) {
                        result = true;
                        break;
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "isDayRacine parse error", e);
            }
        }

        dayRootCache.put(cacheKey, result);
        return result;
    }

    public boolean isDayFeuille(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cached = dayLeafCache.get(cacheKey);
        if (cached != null) return cached;

        boolean result = false;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(mContext);
        DateFormat fmt = new SimpleDateFormat("d_M_yyyy");

        for (int i = 0; i < 10 && !result; i++) {
            String refStr = saveMoonInfo.readDayFeuille(i);
            try {
                Date current = fmt.parse(refStr);
                for (int k = 0; k < 26; k++) {
                    current = addTime(current, MOON_NODE_ASCENDING_NODE);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(current);

                    Calendar threeMonthsAgo = Calendar.getInstance();
                    threeMonthsAgo.add(Calendar.MONTH, -3);
                    if (threeMonthsAgo.after(cal)) {
                        saveMoonInfo.saveDayFeuille(i, fmt.format(current));
                    }

                    if (cal.get(Calendar.YEAR) > year) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 > month) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.DAY_OF_MONTH) > day) break;
                    if (cal.get(Calendar.DAY_OF_MONTH) == day
                            && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.YEAR) == year) {
                        result = true;
                        break;
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "isDayFeuille parse error", e);
            }
        }

        dayLeafCache.put(cacheKey, result);
        return result;
    }

    public boolean isDayFruit(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cached = dayFruitCache.get(cacheKey);
        if (cached != null) return cached;

        boolean result = false;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(mContext);
        DateFormat fmt = new SimpleDateFormat("d_M_yyyy");

        for (int i = 0; i < 10 && !result; i++) {
            String refStr = saveMoonInfo.readDayFruit(i);
            try {
                Date current = fmt.parse(refStr);
                for (int k = 0; k < 26; k++) {
                    current = addTime(current, MOON_NODE_ASCENDING_NODE);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(current);

                    Calendar threeMonthsAgo = Calendar.getInstance();
                    threeMonthsAgo.add(Calendar.MONTH, -3);
                    if (threeMonthsAgo.after(cal)) {
                        saveMoonInfo.saveDayFruit(i, fmt.format(current));
                    }

                    if (cal.get(Calendar.YEAR) > year) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 > month) break;
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.DAY_OF_MONTH) > day) break;
                    if (cal.get(Calendar.DAY_OF_MONTH) == day
                            && cal.get(Calendar.MONTH) + 1 == month
                            && cal.get(Calendar.YEAR) == year) {
                        result = true;
                        break;
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "isDayFruit parse error", e);
            }
        }

        dayFruitCache.put(cacheKey, result);
        return result;
    }

    public boolean isMoonNode(int nodeType, int year, int month, int day) {
        long cacheKey = moonNodeKey(nodeType, year, month, day);
        Boolean cached = moonNodeCache.get(cacheKey);
        if (cached != null) return cached;

        boolean result = false;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(mContext);
        DateFormat fmt = new SimpleDateFormat("d_M_yyyy_HH:mm:ss");
        String refStr = readMoonNodeDate(saveMoonInfo, nodeType);

        try {
            Date current = fmt.parse(refStr);
            for (int i = 0; i < 26; i++) {
                current = addTime(current, nodeType);
                Calendar cal = Calendar.getInstance();
                cal.setTime(current);

                Calendar threeMonthsAgo = Calendar.getInstance();
                threeMonthsAgo.add(Calendar.MONTH, -3);
                if (threeMonthsAgo.after(cal)) {
                    saveMoonNodeDate(saveMoonInfo, nodeType, fmt.format(current));
                }

                if (cal.get(Calendar.YEAR) > year) break;
                if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 > month) break;
                if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month
                        && cal.get(Calendar.DAY_OF_MONTH) > day) break;
                if (cal.get(Calendar.DAY_OF_MONTH) == day
                        && cal.get(Calendar.MONTH) + 1 == month
                        && cal.get(Calendar.YEAR) == year) {
                    result = true;
                    break;
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "isMoonNode parse error", e);
        }

        moonNodeCache.put(cacheKey, result);
        return result;
    }

    /**
     * Calcule la phase lunaire d'après l'algorithme de Bradley E. Schaefer.
     *
     * @return Valeur entre 0 et {@code MOON_PHASE_LENGTH}.
     */
    public double computeMoonPhase(int year, int month, int day) {
        double transformedYear = year - Math.floor((double) (12 - month) / 10);
        int transformedMonth = month + 9;
        if (transformedMonth >= 12) transformedMonth -= 12;

        double term1 = Math.floor(365.25 * (transformedYear + 4712));
        double term2 = Math.floor(30.6 * transformedMonth + 0.5);
        double term3 = Math.floor(Math.floor((transformedYear / 100) + 49) * 0.75) - 38;

        double intermediate = term1 + term2 + day + 59;
        if (intermediate > 2299160) intermediate -= term3;

        double normalizedPhase = (intermediate - 2451550.1) / MOON_PHASE_LENGTH;
        normalizedPhase -= Math.floor(normalizedPhase);
        if (normalizedPhase < 0) normalizedPhase += 1;

        return normalizedPhase * MOON_PHASE_LENGTH;
    }

    // -------------------------------------------------------------------------
    // Méthodes privées
    // -------------------------------------------------------------------------

    private String readMoonNodeDate(SaveMoonInfo saveMoonInfo, int nodeType) {
        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:  return saveMoonInfo.readAscendingMoonNodeDate();
            case MOON_NODE_DESCENDING_NODE: return saveMoonInfo.readDescendingMoonNodeDate();
            case MOON_NODE_APOGEE:          return saveMoonInfo.readApogeeMoonDate();
            case MOON_NODE_PERIGEE:         return saveMoonInfo.readPerigeeMoonDate();
            default:                        return saveMoonInfo.readAscendingMoonNodeDate();
        }
    }

    private void saveMoonNodeDate(SaveMoonInfo saveMoonInfo, int nodeType, String value) {
        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:  saveMoonInfo.saveAscendingMoonNodeDate(value); break;
            case MOON_NODE_DESCENDING_NODE: saveMoonInfo.saveDescendingMoonNodeDate(value); break;
            case MOON_NODE_APOGEE:          saveMoonInfo.saveApogeeMoonDate(value); break;
            case MOON_NODE_PERIGEE:         saveMoonInfo.savePerigeeMoonDate(value); break;
            default: break;
        }
    }

    // -------------------------------------------------------------------------
    // Utilitaires statiques
    // -------------------------------------------------------------------------

    public static Date addTime(Date date, int nodeType) {
        int days, hours, minutes, seconds;
        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:
            case MOON_NODE_DESCENDING_NODE:
                days = 27; hours = 5; minutes = 5; seconds = 36;
                break;
            case MOON_NODE_APOGEE:
            case MOON_NODE_PERIGEE:
            default:
                days = 27; hours = 13; minutes = 18; seconds = 33;
                break;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    private static int dayKey(int year, int month, int day) {
        return year * 10_000 + month * 100 + day;
    }

    private static long moonNodeKey(int nodeType, int year, int month, int day) {
        return (((long) nodeType) << 32) | (dayKey(year, month, day) & 0xFFFFFFFFL);
    }
}


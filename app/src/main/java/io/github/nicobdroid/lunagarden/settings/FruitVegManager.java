package io.github.nicobdroid.lunagarden.settings;
import android.content.Context;
import java.util.ArrayList;

import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.ResultVegItem;

public class FruitVegManager {

    private static final String[] ITEM_KEYS = new String[] {
            "fruit_veg_aubergine",
            "fruit_veg_concombre",
            "fruit_veg_courge",
            "fruit_veg_courgette",
            "fruit_veg_feve",
            "fruit_veg_haricot_vert",
            "fruit_veg_piment",
            "fruit_veg_poivron",
            "fruit_veg_petit_pois_printemps",
            "fruit_veg_petit_pois_automne",
            "fruit_veg_tomate",
            "fruit_veg_tournesol"
    };

    private static final boolean[][] semis = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, true, false, false, false, false, false, false, false, false, false }, // aubergine
            {false, false, false, false, true, true, false, false, false, false, false, false }, // concombre
            {false, false, false, false, true, true, false, false, false, false, false, false }, // courge
            {false, false, false, false, true, true, true, false, false, false, false, false }, // courgette
            {false, false, false, true, true, true, false, false, false, true, false, false }, // feve
            {false, false, false, false, true, true, true, false, false, false, false, false }, // haricot vert
            {false, false, false, false, true, true, false, false, false, false, false, false }, // piment
            {false, false, false, false, true, true, false, false, false, false, false, false }, // poivron
            {false, false, true, true, false, false, false, false, false, false, false, false }, // petits pois printemps
            {false, false, false, false, false, false, false, false, false, true, true, false }, // petits pois automne
            {false, false, false, false, true, true, false, false, false, false, false, false }, // tomate
            {false, false, false, false, true, true, false, false, false, false, false, false } // tournesol
    };

    private static final boolean[][] recoltes = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, false, false, false, false, true, true, true, false, false, false }, // aubergine
            {false, false, false, false, false, false, true, true, true, false, false, false }, // concombre
            {false, false, false, false, false, false, false, false, true, true, false, false }, // courge
            {false, false, false, false, false, false, true, true, true, false, false, false }, // courgette
            {false, false, false, false, true, true, true, false, false, false, false, false }, // feve
            {false, false, false, false, false, false, true, true, true, false, false, false }, // haricot vert
            {false, false, false, false, false, false, false, true, true, true, false, false }, // piment
            {false, false, false, false, false, false, false, true, true, true, false, false }, // poivron
            {false, false, false, false, false, true, true, false, false, false, false, false }, // petits pois printemps
            {false, false, false, true, false, false, false, false, false, false, false, false }, // petits pois automne
            {false, false, false, false, false, false, true, true, true, true, false, false }, // tomate
            {false, false, false, false, false, false, false, false, true, true, false, false } // tournesol
    };

    /**
     * The array to hold pictures names.
     */
    private static final int[] pictures = new int[] {
            R.drawable.aubergine,
            R.drawable.concombre,
            R.drawable.courge,
            R.drawable.courgette,
            R.drawable.feve,
            R.drawable.haricot_vert,
            R.drawable.piment,
            R.drawable.poivron,
            R.drawable.petit_pois_printemps,
            R.drawable.petit_pois_automne,
            R.drawable.tomate,
            R.drawable.tournesol

    };

    /**
     * Method to get fruit list using titles and messages arrays values.
     *
     * @return list
     */
    public static ArrayList<VegItem> getFruitItemList(Context context) {
        String[] titles = getTitles(context);
        String[] messages = getMessages(context);
        int itemCount = getItemCount(titles, messages);

        ArrayList<VegItem> list = new ArrayList<>();
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            VegItem item = new VegItem();
            item.setPreferenceKey(ITEM_KEYS[i]);
            item.setFruitname(titles[i]);
            item.setMessage(messages[i]);
            item.setPictureResId(pictures[i]);
            item.setCheckboxChecked(Boolean.parseBoolean(fruitVegPrefs.readItemEnable(ITEM_KEYS[i], titles[i])));

            list.add(item);

        }
        return list;
    }

    public static int getFirstItemEnabledForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        int result = R.drawable.ic_nothing;
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    result = pictures[i];
                    break;
                }
            }
        }
        return result;
    }

    public static int getFirstItemEnabledForCollect(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        int result = R.drawable.ic_nothing;
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    result = pictures[i];
                    break;
                }
            }
        }
        return result;
    }

    public static ArrayList<ResultVegItem> getResultVegForSow(Context context, int month) {
        String[] titles = getTitles(context);
        String[] sowMessages = getSowMessages(context);
        int itemCount = getItemCount(titles, sowMessages);

        ArrayList<ResultVegItem> result = new ArrayList<>();
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    ResultVegItem item = new ResultVegItem();
                    String strMainMessage = context.getString(R.string.action_sow_format, titles[i].toUpperCase());
                    item.setMainMessage(strMainMessage);
                    item.setSubMessage(sowMessages[i]);
                    item.setPictureResId(pictures[i]);
                    item.setAction(ResultVegItem.RESULT_VEG_ITEM_SOW);
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static ArrayList<ResultVegItem> getResultVegForCollect(Context context, int month) {
        String[] titles = getTitles(context);
        String[] collectMessages = getCollectMessages(context);
        int itemCount = getItemCount(titles, collectMessages);

        ArrayList<ResultVegItem> result = new ArrayList<>();
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    ResultVegItem item = new ResultVegItem();
                    String strMainMessage = context.getString(R.string.action_collect_format, titles[i].toUpperCase());
                    item.setMainMessage(strMainMessage);
                    item.setSubMessage(collectMessages[i]);
                    item.setPictureResId(pictures[i]);
                    item.setAction(ResultVegItem.RESULT_VEG_ITEM_COLLECT);
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static String getListForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        StringBuilder strList = new StringBuilder();
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    if (strList.length() > 0) {
                        strList.append(", ");
                    }
                    strList.append(titles[i]);
                }
            }
        }

        return strList.toString();
    }

    public static String getListForCollect(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        StringBuilder strList = new StringBuilder();
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (fruitVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    if (strList.length() > 0) {
                        strList.append(", ");
                    }
                    strList.append(titles[i]);
                }
            }
        }

        return strList.toString();
    }

    private static boolean isTimeToSow(int i, int month) {
        return semis[i][month];
    }

    private static boolean isTimeToCollect(int i, int month) {
        return recoltes[i][month];
    }

    private static String[] getTitles(Context context) {
        return context.getResources().getStringArray(R.array.fruit_veg_titles);
    }

    private static String[] getMessages(Context context) {
        return context.getResources().getStringArray(R.array.fruit_veg_messages);
    }

    private static String[] getSowMessages(Context context) {
        return context.getResources().getStringArray(R.array.fruit_veg_sow_messages);
    }

    private static String[] getCollectMessages(Context context) {
        return context.getResources().getStringArray(R.array.fruit_veg_collect_messages);
    }

    private static int getItemCount(String[]... arrays) {
        int expected = ITEM_KEYS.length;

        for (String[] array : arrays) {
            if (array.length != expected) {
                throw new IllegalStateException("FruitVegManager string arrays size mismatch.");
            }
        }

        return expected;
    }
}


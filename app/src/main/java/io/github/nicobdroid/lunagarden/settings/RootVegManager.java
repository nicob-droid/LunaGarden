package io.github.nicobdroid.lunagarden.settings;

import android.content.Context;
import java.util.ArrayList;
import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.ResultVegItem;

public class RootVegManager {

    private static final String[] ITEM_KEYS = new String[] {
            "root_veg_ail_blanc",
            "root_veg_ail_rose",
            "root_veg_betterave",
            "root_veg_carotte",
            "root_veg_celeri",
            "root_veg_cerfeuil",
            "root_veg_echalote_ronde_longue",
            "root_veg_echalote_grise",
            "root_veg_navet_printemps",
            "root_veg_navet_hiver",
            "root_veg_oignon_doux",
            "root_veg_oignon_conserver",
            "root_veg_panais",
            "root_veg_pomme_de_terre",
            "root_veg_radis",
            "root_veg_radis_hiver",
            "root_veg_salsifi",
            "root_veg_scorsonere"
    };

    private static final boolean[][] semis = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, false, false, false, false, false, false, false, true, true, true }, // ail blanc
            {false, true,  true, false, false, false, false, false, false, false, false, false }, // ail rose
            {false, false, false, true, true, true, false, false, false, false, false, false }, // betterave
            {false, false, false, true, true, true, true, false, false, false, false, false }, // carotte
            {false, false, false, true, true, false, false, false, false, false, false, false }, // celeri
            {false, false, false, false, false, false, false, false, false, false, true, true }, // cerfeuil
            {false, true, true, true, false, false, false, false, false, false, false, false }, // echalote longue
            {true, true, false, false, false, false, false, false, false, false, true, true }, // echalote grise
            {false, false, false, true, true, true, false, false, false, false, false, false }, // navet printemps
            {false, false, false, false, false, true, true, true, true, false, false, false }, // navet hiver
            {false, false, false, false, false, false, false, true, true, true, true, false }, // oignon doux
            {false, true, true, true, false, false, false, false, false, false, false, false }, // oignon conserver
            {false, false, true, true, true, true, false, false, false, false, false, false }, // panais
            {false, false, true, true, true, false, false, false, false, false, false, false }, // patate
            {false, false, false, true, true, true, true, false, true, false, false, false }, // radis
            {false, false, false, false, false, true, true, false, false, false, false, false }, // radis hiver
            {false, false, true, true, true, true, false, false, false, false, false, false }, // salsifi
            {false, false, true, true, true, true, false, true, true, false, false, false } // scrosonere
    };

    private static final boolean[][] recoltes = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, false, false, false, false, true, true, false, false, false, false }, // ail blanc
            {false, false,  false, false, false, false, true, true, false, false, false, false }, // ail rose
            {false, false, false, false, false, false, false, true, true, true, false, false }, // betterave
            {false, false, false, false, false, false, true, true, true, true, true, false }, // carotte
            {false, false, false, false, false, false, false, false, false, true, true, false }, // celeri
            {false, false, false, false, true, true, true, true, true, false, false, false }, // cerfeuil
            {false, false, false, false, false, false, true, true, false, false, false, false }, // echalote longue
            {false, false, false, false, false, false, true, true, false, false, false, false }, // echalote grise
            {false, false, false, false, false, false, true, true, true, false, false, false }, // navet printemps
            {false, false, false, false, false, false, false, false, false, false, true, false }, // navet hiver
            {false, false, false, true, true, true, true, false, false, false, false, false }, // oignon doux
            {false, false, false, false, false, false, false, true, false, false, false, false }, // oignon conserver
            {true, true, false, false, false, false, false, false, false, true, true, true }, // panais
            {false, false, false, false, true, true, true, true, true, false, false, false }, // patate
            {false, false, false, false, true, true, true, true, true, false, false, false }, // radis
            {false, false, false, false, false, false, false, false, false, true, true, false }, // radis hiver
            {true, true, false, false, false, false, false, false, false, false, true, true }, // salsifi
            {true, true, true, false, false, false, false, false, false, false, true, true } // scrosonere
    };

    /**
     * The array to hold pictures names.
     */
    private static final int[] pictures = new int[] {
            R.drawable.ail_blanc,
            R.drawable.ail_rose,
            R.drawable.betterave,
            R.drawable.carotte,
            R.drawable.celeri,
            R.drawable.cerfeuil,
            R.drawable.echalote_longue,
            R.drawable.echalote_grise,
            R.drawable.navet_printemps,
            R.drawable.navet_hiver,
            R.drawable.oignon_doux,
            R.drawable.oignon_a_conserver,
            R.drawable.panais,
            R.drawable.pomme_de_terre,
            R.drawable.radis,
            R.drawable.radis_hiver,
            R.drawable.salsifi,
            R.drawable.scorsonere
    };

    /**
     * Method to get fruit list using titles and messages arrays values.
     *
     * @return list
     */
    public static ArrayList<VegItem> getFruitItemList(Context context) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        ArrayList<VegItem> list = new ArrayList<>();
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            VegItem item = new VegItem();
            item.setPreferenceKey(ITEM_KEYS[i]);
            item.setFruitname(titles[i]);
            item.setMessage(titles[i]);
            item.setPictureResId(pictures[i]);
            item.setCheckboxChecked(Boolean.parseBoolean(rootVegPrefs.readItemEnable(ITEM_KEYS[i], titles[i])));
            list.add(item);

        }
        return list;
    }

    public static String getListForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        StringBuilder strList = new StringBuilder();
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    if (strList.length() > 0) {
                        strList.append(", ");
                    }
                    strList.append(titles[i]);
                }
            }
        }

        return strList.toString();
    }

    public static ArrayList<ResultVegItem> getResultVegForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);
        String sowPeriodLabel = "Periode de semis";

        ArrayList<ResultVegItem> result = new ArrayList<>();
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    ResultVegItem item = new ResultVegItem();
                    String strMainMessage = context.getString(R.string.action_sow_format, titles[i].toUpperCase());
                    item.setMainMessage(strMainMessage);
                    item.setSubMessage(sowPeriodLabel);
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
        int itemCount = getItemCount(titles);
        String collectPeriodLabel = "Periode de recolte";

        ArrayList<ResultVegItem> result = new ArrayList<>();
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    ResultVegItem item = new ResultVegItem();
                    String strMainMessage = context.getString(R.string.action_collect_format, titles[i].toUpperCase());
                    item.setMainMessage(strMainMessage);
                    item.setSubMessage(collectPeriodLabel);
                    item.setPictureResId(pictures[i]);
                    item.setAction(ResultVegItem.RESULT_VEG_ITEM_COLLECT);
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static String getListForCollect(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        StringBuilder strList = new StringBuilder();
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    if (strList.length() > 0) {
                        strList.append(", ");
                    }
                    strList.append(titles[i]);
                }
            }
        }

        return strList.toString();
    }


    public static int getFirstItemEnabledForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        int result = R.drawable.ic_nothing;
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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
        RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (rootVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    result = pictures[i];
                    break;
                }
            }
        }
        return result;
    }

    private static boolean isTimeToSow(int i, int month) {
        return semis[i][month];
    }

    private static boolean isTimeToCollect(int i, int month) {
        return recoltes[i][month];
    }


    private static String[] getTitles(Context context) {
        return context.getResources().getStringArray(R.array.root_veg_titles);
    }



    private static int getItemCount(String[]... arrays) {
        int expected = ITEM_KEYS.length;

        for (String[] array : arrays) {
            if (array.length != expected) {
                throw new IllegalStateException("RootVegManager string arrays size mismatch.");
            }
        }

        return expected;
    }

}

package io.github.nicobdroid.lunagarden.settings;
import android.content.Context;
import java.util.ArrayList;

import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.ResultVegItem;

public class LeafVegManager {

    private static final String[] ITEM_KEYS = new String[] {
            "leaf_veg_blette",
            "leaf_veg_chou_bxl_automne",
            "leaf_veg_chou_bxl_hiver",
            "leaf_veg_chou_pomme_printemps",
            "leaf_veg_chou_pomme_ete",
            "leaf_veg_chou_pomme_automne",
            "leaf_veg_chou_pomme_hiver",
            "leaf_veg_chou_fleur_printemps",
            "leaf_veg_chou_fleur_ete",
            "leaf_veg_chou_fleur_hiver",
            "leaf_veg_epinard_printemps",
            "leaf_veg_epinard_jours_longs",
            "leaf_veg_epinard_jours_courts",
            "leaf_veg_laitue",
            "leaf_veg_mache_grosses_graines",
            "leaf_veg_mache_petites_graines",
            "leaf_veg_poireau_ete",
            "leaf_veg_poireau_hiver",
            "leaf_veg_roquette"
    };

    private static final boolean[][] semis = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, false, true, true, true, true, false, false, false, false, false }, // blette
            {false, false, true, true, true, true, false, false, false, false, false, false }, // choux brux autom
            {false, false, true, true, true, true, false, false, false, false, false, false }, // choux brux hiver
            {false, false, false, false, false, false, false, false, true, false, false, false }, // chou printemps
            {false, true, true, false, false, false, false, false, false, false, false, false }, // chou ete
            {false, false, false, true, true, false, false, false, false, false, false, false }, // chou automne
            {false, false, false, false, false, true, false, false, false, false, false, false }, // chou hiver
            {false, false, false, false, false, false, false, false, true, false, false, false }, // chou fleur printemps
            {false, true, true, true, false, false, false, false, false, false, false, false }, // chou fleur ete
            {false, false, false, false, true, true, false, false, false, false, false, false }, // chou fleur hiver
            {false, false, true, true, true, false, false, false, false, false, false, false }, // epinard printemps
            {false, false, false, false, false, true, true, true, false, false, false, false }, // epinard ete
            {false, false, false, false, false, false, false, false, true, true, true, false }, // epinard hiver
            {false, false, true, true, true, true, true, true, true, false, false, false }, // laitue
            {false, false, false, false, false, false, false, true, true, false, false, false }, // mache grosse
            {false, false, false, false, false, false, false, false, true, true, false, false }, // mache petite
            {false, true, true, false, false, false, false, false, false, false, false, false }, // poireau ete
            {false, false, false, true, false, false, false, false, false, false, false, false }, // poireau hiver
            {false, false, true, true, true, true, true, true, true, false, false, false } // roquette
    };

    private static final boolean[][] recoltes = new boolean[][] {
            //Jan   Fev   Mars   Avril  Mai   Juin    Juil   Aout  Sept   Octo  Nov   Dec
            {false, false, false, false, true, true, true, true, true, true, false, false }, // blette
            {false, false, false, false, false, false, false, false, true, true, true, false }, // choux brux autom
            {true, true, true, false, false, false, false, false, false, false, true, true }, // choux brux hiver
            {false, true, true, false, false, false, false, false, false, false, false, false }, // chou printemps
            {false, false, false, false, false, true, true, true, false, false, false, false }, // chou ete
            {false, false, false, false, false, false, false, false, false, true, true, false }, // chou automne
            {true, false, false, false, false, false, false, false, false, false, false, true }, // chou hiver
            {false, false, false, false, true, true, false, false, false, false, false, false }, // chou fleur printemps
            {false, false, false, false, false, false, true, true, false, false, false, false }, // chou fleur ete
            {true, false, false, false, false, false, false, false, false, true, true, true }, // chou fleur hiver
            {false, false, false, true, true, true, false, false, false, false, false, false }, // epinard printemps
            {false, false, false, false, false, false, true, true, true, false, false, false }, // epinard ete
            {false, false, false, false, false, false, false, false, false, true, true, true }, // epinard hiver
            {false, true, true, true, true, true, true, true, true, true, true, false }, // laitue
            {false, false, false, false, false, false, false, false, false, true, true, false }, // mache grosse
            {true, true, true, false, false, false, false, false, false, false, false, true }, // mache petite
            {false, false, false, false, false, false, false, true, true, true, false, false }, // poireau ete
            {true, true, false, false, false, false, false, false, false, true, true, true }, // poireau hiver
            {false, false, false, false, true, true, true, true, true, true, false, false } // roquette
    };

    /**
     * The array to hold pictures names.
     */
    public static final int[] pictures = new int[] {
            R.drawable.blette,
            R.drawable.choux_bruxelles_automne,
            R.drawable.choux_bruxelles_hiver,
            R.drawable.chou_pomme_printemps,
            R.drawable.chou_pomme_ete,
            R.drawable.chou_pomme_automne,
            R.drawable.chou_pomme_hiver,
            R.drawable.chou_fleur_printemps,
            R.drawable.chou_fleur_ete,
            R.drawable.chou_fleur_hiver,
            R.drawable.epinards_printemps,
            R.drawable.epinards_ete,
            R.drawable.epinards_hiver,
            R.drawable.laitue,
            R.drawable.mache_grosse_graine,
            R.drawable.mache_petite_graine,
            R.drawable.poireau_ete,
            R.drawable.poireau_hiver,
            R.drawable.roquette
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
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            VegItem item = new VegItem();
            item.setPreferenceKey(ITEM_KEYS[i]);
            item.setFruitname(titles[i]);
            item.setMessage(titles[i]);
            item.setPictureResId(pictures[i]);
            item.setCheckboxChecked(Boolean.parseBoolean(leafVegPrefs.readItemEnable(ITEM_KEYS[i], titles[i])));

            list.add(item);

        }
        return list;
    }

    public static int getFirstItemEnabledForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        int result = R.drawable.ic_nothing;
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
                    result = pictures[i];
                    break;
                }
            }
        }
        return result;
    }

    public static ArrayList<ResultVegItem> getResultVegForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);
        String sowPeriodLabel = "Periode de semis";

        ArrayList<ResultVegItem> result = new ArrayList<>();

        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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

    public static String getListForSow(Context context, int month) {
        String[] titles = getTitles(context);
        int itemCount = getItemCount(titles);

        StringBuilder strList = new StringBuilder();
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToSow(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
        for (int i = 0; i < itemCount; i++) {
            if (isTimeToCollect(i, month)) {
                if (leafVegPrefs.isItemEnabled(ITEM_KEYS[i], titles[i])) {
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
        return context.getResources().getStringArray(R.array.leaf_veg_titles);
    }



    private static int getItemCount(String[]... arrays) {
        int expected = ITEM_KEYS.length;

        for (String[] array : arrays) {
            if (array.length != expected) {
                throw new IllegalStateException("LeafVegManager string arrays size mismatch.");
            }
        }

        return expected;
    }
}


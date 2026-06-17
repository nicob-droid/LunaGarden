package io.github.nicobdroid.lunagarden.settings;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import io.github.nicobdroid.lunagarden.AppTechnicalKeys;
import io.github.nicobdroid.lunagarden.R;

public class FruitVegPrefs {
    private static final String TAG = "FruitVegPrefs";
    private final Context mContext;
    private final SharedPreferences mPrefs;

    public FruitVegPrefs(Context context) {
        this.mContext = context;
        this.mPrefs = context.getSharedPreferences(context.getString(R.string.sharedpref_fruit_veg), MODE_PRIVATE);
    }

    public void saveSettingsDone() {
        Log.i(TAG, "Save Settings Done");
        putString(mContext.getString(R.string.settings_done), mContext.getString(R.string.settings_done));
    }

    public boolean areSettingsDone() {
        String value = getSharedPreferenceString(
                mContext.getString(R.string.settings_done),
                mContext.getString(R.string.default_leaf_veg_item)
        );
        return value.contains(mContext.getString(R.string.settings_done));
    }

    public void saveFruitVegEnable(String strValue) {
        putString(mContext.getString(R.string.settings_fruit_veg_enable), strValue);
    }

    public void saveItem(String itemName, String itemValue) {
        putString(itemName, itemValue);
    }

    public String readFruitVegEnable() {
        return getSharedPreferenceString(
                mContext.getString(R.string.settings_fruit_veg_enable),
                mContext.getString(R.string.default_fruit_veg_enable)
        );
    }

    public String readItemEnable(String itemName) {
        return getSharedPreferenceString(itemName, mContext.getString(R.string.default_fruit_veg_item));
    }

    public String readItemEnable(String itemKey, String legacyItemName) {
        String value = mPrefs.getString(itemKey, null);
        if (value != null) {
            return value;
        }
        return readItemEnable(legacyItemName);
    }

    public boolean isItemEnabled(String itemKey, String legacyItemName) {
        return !readItemEnable(itemKey, legacyItemName).contains("false");
    }

    public boolean isFruitVegEnabled() {
        return !readFruitVegEnable().contains(AppTechnicalKeys.PREF_VALUE_NO);
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String getSharedPreferenceString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

}


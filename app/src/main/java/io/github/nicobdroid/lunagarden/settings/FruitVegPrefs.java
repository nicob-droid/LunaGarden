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


    // constructor
    public FruitVegPrefs(Context context) {
        this.mContext = context;
        this.mPrefs = context.getSharedPreferences(context.getString(R.string.sharedpref_fruit_veg), MODE_PRIVATE);
    }

    public void saveSettingsDone() {
        Log.i(TAG, "Save Settings Done");
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(mContext.getString(R.string.settings_done), mContext.getString(R.string.settings_done));
        editor.apply();
    }

    public boolean areSettingsDone() {
        boolean bResult = false;
        String str = getSharedPreferenceString(mContext.getString(R.string.settings_done), mContext.getString(R.string.default_leaf_veg_item));
        if (str.contains(mContext.getString(R.string.settings_done))) {
            bResult = true;
        }
        return bResult;
    }

    public void saveFruitVegEnable(String strValue) {
        //Log.i(TAG, "Save Fruit Veg Enable: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(mContext.getString(R.string.settings_fruit_veg_enable), strValue);
        editor.apply();
    }

    public void saveItem(String itemName, String itemValue) {
        //Log.i(TAG, "Save Item: " + itemName + " = " + itemValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(itemName, itemValue);
        editor.apply();
    }

    public String readFruitVegEnable() {
        return getSharedPreferenceString(mContext.getString(R.string.settings_fruit_veg_enable), mContext.getString(R.string.default_fruit_veg_enable));
    }

    public String readItemEnable(String itemName) {
        return getSharedPreferenceString(itemName, mContext.getString(R.string.default_fruit_veg_item));
    }

    public String readItemEnable(String itemKey, String legacyItemName) {
        if (isSharedPreferenceAvailable(itemKey)) {
            return getSharedPreferenceString(itemKey, mContext.getString(R.string.default_fruit_veg_item));
        }
        return readItemEnable(legacyItemName);
    }

    public boolean isItemEnabled(String itemKey, String legacyItemName) {
        return !readItemEnable(itemKey, legacyItemName).contains("false");
    }

    public boolean isFruitVegEnabled() {
        boolean bResult = true;
        String str = readFruitVegEnable();
        if (str.contains(AppTechnicalKeys.PREF_VALUE_NO)) {
            bResult = false;
        }
        return bResult;
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


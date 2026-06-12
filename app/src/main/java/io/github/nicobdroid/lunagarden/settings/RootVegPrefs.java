package io.github.nicobdroid.lunagarden.settings;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.nicobdroid.lunagarden.AppTechnicalKeys;
import io.github.nicobdroid.lunagarden.R;

public class RootVegPrefs {
    public static final String TAG = "RootVegPrefs";
    private Context mContext;
    private SharedPreferences mPrefs;


    // constructor
    public RootVegPrefs(Context context) {
        this.mContext = context;
        this.mPrefs = context.getSharedPreferences(context.getString(R.string.sharedpref_root_veg), MODE_PRIVATE);
    }


    public void saveRootVegEnable(String strValue) {
        //Log.i(TAG, "Save Root Veg Enable: " + strValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(mContext.getString(R.string.settings_root_veg_enable), strValue);
        editor.apply();
    }

    public void saveItem(String itemName, String itemValue) {
        //Log.i(TAG, "Save Item: " + itemName + " = " + itemValue);
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(itemName, itemValue);
        editor.apply();
    }

    public String readRootVegEnable() {
        return getSharedPreferenceString(mContext.getString(R.string.settings_root_veg_enable), mContext.getString(R.string.default_root_veg_enable));
    }

    public String readItemEnable(String itemName) {
        //Log.d(TAG, "Read item enable: " + itemName);
        return getSharedPreferenceString(itemName, mContext.getString(R.string.default_root_veg_item));
    }

    public String readItemEnable(String itemKey, String legacyItemName) {
        if (isSharedPreferenceAvailable(itemKey)) {
            return getSharedPreferenceString(itemKey, mContext.getString(R.string.default_root_veg_item));
        }
        return readItemEnable(legacyItemName);
    }

    public boolean isItemEnabled(String itemName) {
        boolean bResult = true;
        String str = readItemEnable(itemName);
        if (str.contains("false")) {
            bResult = false;
        }
        return bResult;
    }

    public boolean isItemEnabled(String itemKey, String legacyItemName) {
        return !readItemEnable(itemKey, legacyItemName).contains("false");
    }

    public boolean isRootVegEnabled() {
        boolean bResult = true;
        String str = readRootVegEnable();
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


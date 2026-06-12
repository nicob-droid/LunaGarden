package io.github.nicobdroid.lunagarden;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private TimePicker picker;

    public static TimePreferenceDialogFragmentCompat newInstance(String key) {
        TimePreferenceDialogFragmentCompat fragment = new TimePreferenceDialogFragmentCompat();
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateDialogView(android.content.Context context) {
        picker = new TimePicker(context);
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        TimePreference preference = (TimePreference) getPreference();
        setTime(preference.getLastHour(), preference.getLastMinute());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (!positiveResult || picker == null) {
            return;
        }

        TimePreference preference = (TimePreference) getPreference();
        preference.saveTime(getHour(), getMinute());
    }

    private void setTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(hour);
            picker.setMinute(minute);
        } else {
            picker.setCurrentHour(hour);
            picker.setCurrentMinute(minute);
        }
    }

    private int getHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return picker.getHour();
        }
        return picker.getCurrentHour();
    }

    private int getMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return picker.getMinute();
        }
        return picker.getCurrentMinute();
    }
}


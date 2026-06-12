package io.github.nicobdroid.lunagarden;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.PreferenceDialogFragmentCompat;

import org.jetbrains.annotations.NotNull;

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
    protected View onCreateDialogView(@NotNull android.content.Context context) {
        picker = new TimePicker(context);
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(@NotNull View view) {
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
        picker.setHour(hour);
        picker.setMinute(minute);
    }

    private int getHour() {
        return picker.getHour();
    }

    private int getMinute() {
        return picker.getMinute();
    }
}


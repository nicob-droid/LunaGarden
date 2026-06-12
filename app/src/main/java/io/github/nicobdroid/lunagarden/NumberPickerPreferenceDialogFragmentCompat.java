package io.github.nicobdroid.lunagarden;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.preference.PreferenceDialogFragmentCompat;

import org.jetbrains.annotations.NotNull;

public class NumberPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private NumberPicker numberPicker;

    public static NumberPickerPreferenceDialogFragmentCompat newInstance(String key) {
        NumberPickerPreferenceDialogFragmentCompat fragment = new NumberPickerPreferenceDialogFragmentCompat();
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View onCreateDialogView(@NotNull android.content.Context context) {
        numberPicker = new NumberPicker(context);
        NumberPickerPreference preference = (NumberPickerPreference) getPreference();
        numberPicker.setMinValue(preference.getMinDays());
        numberPicker.setMaxValue(preference.getMaxDays());
        numberPicker.setWrapSelectorWheel(false);
        return numberPicker;
    }

    @Override
    protected void onBindDialogView(@NotNull View view) {
        super.onBindDialogView(view);
        NumberPickerPreference preference = (NumberPickerPreference) getPreference();
        numberPicker.setValue(preference.getCurrentDays());
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (!positiveResult || numberPicker == null) {
            return;
        }

        NumberPickerPreference preference = (NumberPickerPreference) getPreference();
        preference.saveDays(numberPicker.getValue());
    }
}


package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {
    private static final int MIN_DAYS = 0;
    private static final int MAX_DAYS = 30;

    private NumberPicker numberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(MIN_DAYS);
        numberPicker.setMaxValue(MAX_DAYS);
        numberPicker.setWrapSelectorWheel(false);
        return numberPicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        numberPicker.setValue(readPersistedDays());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (!positiveResult || numberPicker == null) {
            return;
        }

        int selectedDays = numberPicker.getValue();
        String newValue = String.valueOf(selectedDays);
        if (callChangeListener(newValue)) {
            persistString(newValue);
            updateSummary(selectedDays);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String value = restorePersistedValue
                ? getPersistedString(String.valueOf(MIN_DAYS))
                : defaultValue != null ? String.valueOf(defaultValue) : String.valueOf(MIN_DAYS);
        int days = parseAndClampDays(value);
        persistString(String.valueOf(days));
        updateSummary(days);
    }

    private void updateSummary(int days) {
        setSummary(getContext().getResources().getQuantityString(
                R.plurals.notification_days_earlier_current_value,
                days,
                days));
    }

    private int readPersistedDays() {
        return parseAndClampDays(getPersistedString(String.valueOf(MIN_DAYS)));
    }

    private int parseAndClampDays(String value) {
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            parsedValue = MIN_DAYS;
        }
        return Math.max(MIN_DAYS, Math.min(MAX_DAYS, parsedValue));
    }
}


package io.github.nicobdroid.lunagarden;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

public class NumberPickerPreference extends DialogPreference {
    private static final int MIN_DAYS = 0;
    private static final int MAX_DAYS = 30;

    private int currentDays = MIN_DAYS;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        String fallback = defaultValue != null ? String.valueOf(defaultValue) : String.valueOf(MIN_DAYS);
        String value = getPersistedString(fallback);
        int days = parseAndClampDays(value);
        persistString(String.valueOf(days));
        setDaysInternal(days);
    }

    public int getCurrentDays() {
        return currentDays;
    }

    public int getMinDays() {
        return MIN_DAYS;
    }

    public int getMaxDays() {
        return MAX_DAYS;
    }

    public void saveDays(int days) {
        int clampedDays = parseAndClampDays(String.valueOf(days));
        String newValue = String.valueOf(clampedDays);
        if (callChangeListener(newValue)) {
            persistString(newValue);
            setDaysInternal(clampedDays);
            notifyChanged();
        }
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

    private void setDaysInternal(int days) {
        currentDays = days;
        updateSummary(days);
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


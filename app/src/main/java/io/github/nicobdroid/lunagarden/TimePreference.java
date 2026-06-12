package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import java.util.Locale;

public class TimePreference extends DialogPreference {
    private static final String DEFAULT_TIME = "00:00";
    private int lastHour = 0;
    private int lastMinute = 0;

    public static int getHour(String time) {
        try {
            String[] pieces = time.split(":");
            return Math.max(0, Math.min(23, Integer.parseInt(pieces[0])));
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static int getMinute(String time) {
        try {
            String[] pieces = time.split(":");
            return Math.max(0, Math.min(59, Integer.parseInt(pieces[1])));
        } catch (Exception ignored) {
            return 0;
        }
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText(R.string.dialog_save);
        setNegativeButtonText(R.string.dialog_cancel);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        String fallback = defaultValue == null ? DEFAULT_TIME : defaultValue.toString();
        String time = getPersistedString(fallback);
        setTimeInternal(time);
    }

    public int getLastHour() {
        return lastHour;
    }

    public int getLastMinute() {
        return lastMinute;
    }

    public void saveTime(int hour, int minute) {
        int clampedHour = Math.max(0, Math.min(23, hour));
        int clampedMinute = Math.max(0, Math.min(59, minute));
        String time = String.format(Locale.US, "%02d:%02d", clampedHour, clampedMinute);

        if (callChangeListener(time)) {
            persistString(time);
            setTimeInternal(time);
            notifyChanged();
        }
    }

    private void setTimeInternal(String time) {
        if (time == null || !time.contains(":")) {
            time = DEFAULT_TIME;
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }
}


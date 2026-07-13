package io.github.nicobdroid.lunagarden;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.jetbrains.annotations.NotNull;

public class MyPreferenceActivity extends AppCompatActivity {
    private static final String TAG = "MyPreferenceActivity";
    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.applyToContentRoot(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new MyPreferenceFragment())
                    .commit();
        }
        setTitle(R.string.settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            updateAppVersionSummary();

            bindNotificationPreferenceListener(getString(R.string.settings_notification_enable));
            bindNotificationPreferenceListener(getString(R.string.settings_notification_time));
            bindNotificationPreferenceListener(getString(R.string.settings_notification_nb_days_earlier));
            bindAppearancePreferenceListener();
            bindSimpleListSummaryProvider(getString(R.string.settings_calendar_months_ahead));

        }

        private void bindAppearancePreferenceListener() {
            ListPreference preference = findPreference(AppearanceModeManager.PREF_APPEARANCE_MODE);
            if (preference == null) {
                return;
            }

            preference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            preference.setOnPreferenceChangeListener((changedPreference, newValue) -> {
                boolean modeChanged = AppearanceModeManager.applyModeValue(String.valueOf(newValue));
                if (modeChanged && getActivity() != null) {
                    getActivity().recreate();
                }
                return true;
            });
        }

        private void bindSimpleListSummaryProvider(String key) {
            ListPreference preference = findPreference(key);
            if (preference == null) {
                return;
            }
            preference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        }

        private void bindNotificationPreferenceListener(String key) {
            Preference preference = findPreference(key);
            if (preference == null) {
                return;
            }

            preference.setOnPreferenceChangeListener((changedPreference, newValue) -> {
                if (getContext() == null) {
                    return true;
                }

                if (getString(R.string.settings_notification_enable).equals(changedPreference.getKey())
                        && newValue instanceof Boolean
                        && !((Boolean) newValue)) {
                    NotificationJobService.cancelScheduledJob(getContext());
                } else {
                    NotificationJobService.scheduleNextJob(getContext());
                }

                Log.d(TAG, "Notification preference updated: " + changedPreference.getKey());
                return true;
            });

        }

        @Override
        public void onDisplayPreferenceDialog(@NotNull Preference preference) {
            DialogFragment dialogFragment;
            if (preference instanceof TimePreference) {
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else if (preference instanceof NumberPickerPreference) {
                dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            } else {
                super.onDisplayPreferenceDialog(preference);
                return;
            }

            if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
                return;
            }

            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
        }

        private void updateAppVersionSummary() {
            Preference appVersionPreference = findPreference(getString(R.string.settings_app_version));
            if (appVersionPreference == null || getContext() == null) {
                return;
            }

            try {
                PackageManager packageManager = getContext().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
                String versionName = packageInfo.versionName;
                if (versionName != null && !versionName.isEmpty()) {
                    appVersionPreference.setSummary(versionName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Unable to read app version", e);
            }
        }
    }

}


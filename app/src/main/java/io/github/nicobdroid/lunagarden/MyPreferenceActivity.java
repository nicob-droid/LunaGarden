package io.github.nicobdroid.lunagarden;
import android.app.job.JobScheduler;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;

public class MyPreferenceActivity extends AppCompatPreferenceActivity  {
    private static final String TAG = "MyPreferenceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
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

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            updateAppVersionSummary();

            final Preference myPref = (Preference) findPreference(getString(R.string.settings_notification_enable));
            myPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    boolean switched = ((SwitchPreference) preference).isChecked();
                    boolean update = !switched;
                    Log.d(TAG, "onPreferenceChange = " + update);
                    if (!update) {
                        Log.d(TAG, "onPreferenceChange: Cancel all jobs");
                        // Cancel all jobs
                        JobScheduler mJobScheduler = (JobScheduler) getActivity()
                                .getSystemService(JOB_SCHEDULER_SERVICE);

                        // Cancel all if exist
                        mJobScheduler.cancelAll();
                    }

                    return true;
                }

            });

        }

        private void updateAppVersionSummary() {
            Preference appVersionPreference = findPreference(getString(R.string.settings_app_version));
            if (appVersionPreference == null || getActivity() == null) {
                return;
            }

            try {
                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
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


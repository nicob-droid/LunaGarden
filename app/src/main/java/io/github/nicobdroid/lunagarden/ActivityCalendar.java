package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdListener;

import java.util.Calendar;

public class ActivityCalendar extends AppCompatActivity {
    private static final String TAG = "ActivityCalendar";
    private static final int DEFAULT_MONTHS_AHEAD = 3;
    private static final int MIN_MONTHS_AHEAD = 1;
    private static final int MAX_MONTHS_AHEAD = 12;
    private static final int CURRENT_MONTH_PAGE_INDEX = 2;
    private static final int MAX_AD_RETRY = 2;
    private static final long AD_RETRY_DELAY_MS = 1500L;
    FragmentPagerAdapter adapterViewPager;
    private ViewPager vpPager;
    private int configuredMonthsAhead = -1;
    private AdView adView;
    private int adRetryCount;
    private final Handler adRetryHandler = new Handler(Looper.getMainLooper());
    private Runnable adRetryRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpPager = findViewById(R.id.vpPager);
        setupViewPager();

        adView = findViewById(R.id.adView);
        if (adView != null) {
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adRetryCount = 0;
                    if (adView != null) adView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "AdMob banner loaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.w(TAG, "AdMob banner failed: " + loadAdError.getCode() + " / " + loadAdError.getMessage());
                    if (!isFinishing() && !isDestroyed()) {
                        if (adRetryCount < MAX_AD_RETRY) {
                            adRetryCount++;
                            adRetryRunnable = () -> {
                                if (adView != null && !isFinishing() && !isDestroyed()) {
                                    adView.loadAd(new AdRequest.Builder().build());
                                }
                            };
                            adRetryHandler.postDelayed(adRetryRunnable, AD_RETRY_DELAY_MS);
                        } else {
                            if (adView != null) adView.setVisibility(View.GONE);
                        }
                    }
                }
            });
            adView.loadAd(new AdRequest.Builder().build());
        }

    }

    private void setupViewPager() {
        if (vpPager == null) {
            return;
        }
        int monthsAhead = readMonthsAheadPreference();
        if (monthsAhead == configuredMonthsAhead && adapterViewPager != null && vpPager.getAdapter() != null) {
            return;
        }
        configuredMonthsAhead = monthsAhead;
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getApplicationContext(), monthsAhead);
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(CURRENT_MONTH_PAGE_INDEX);
    }

    private int readMonthsAheadPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String key = getString(R.string.settings_calendar_months_ahead);
        String storedValue = prefs.getString(key, String.valueOf(DEFAULT_MONTHS_AHEAD));
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(storedValue);
        } catch (NumberFormatException e) {
            parsedValue = DEFAULT_MONTHS_AHEAD;
        }
        return Math.max(MIN_MONTHS_AHEAD, Math.min(MAX_MONTHS_AHEAD, parsedValue));
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static final int MONTHS_BEFORE_CURRENT = 2;
        Context mContext;
        private final int monthsAhead;
        static int actualMonth = Calendar.getInstance().get(Calendar.MONTH);
        static int actualYear = Calendar.getInstance().get(Calendar.YEAR);

        public MyPagerAdapter(FragmentManager fragmentManager, Context context, int monthsAhead) {
            super(fragmentManager);
            this.mContext = context;
            this.monthsAhead = monthsAhead;
        }

        // Returns total number of pages.
        @Override
        public int getCount() {
            return MONTHS_BEFORE_CURRENT + 1 + monthsAhead;
        }

        // Returns the fragment to display for a particular page.
        @Override
        @NonNull
        public Fragment getItem(int position) {
            if (position < 0 || position >= getCount()) {
                throw new IllegalArgumentException("Invalid position: " + position);
            }
            return createFragmentForOffset(position - MONTHS_BEFORE_CURRENT);
        }

        private Fragment createFragmentForOffset(int monthOffset) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(actualYear, actualMonth, 1);
            calendar.add(Calendar.MONTH, monthOffset);
            return FragmentCalendar.newInstance(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String strPageTitle = null;
            Calendar calendar = Calendar.getInstance();
            calendar.set(actualYear, actualMonth, 1);
            calendar.add(Calendar.MONTH, position - MONTHS_BEFORE_CURRENT);
            int monthId = calendar.get(Calendar.MONTH);
            switch (monthId) {
                case Calendar.JANUARY:
                    strPageTitle = mContext.getString(R.string.january);
                    break;
                case Calendar.FEBRUARY:
                    strPageTitle = mContext.getString(R.string.february);
                    break;
                case Calendar.MARCH:
                    strPageTitle = mContext.getString(R.string.march);
                    break;
                case Calendar.APRIL:
                    strPageTitle = mContext.getString(R.string.april);
                    break;
                case Calendar.MAY:
                    strPageTitle = mContext.getString(R.string.may);
                    break;
                case Calendar.JUNE:
                    strPageTitle = mContext.getString(R.string.june);
                    break;
                case Calendar.JULY:
                    strPageTitle = mContext.getString(R.string.july);
                    break;
                case Calendar.AUGUST:
                    strPageTitle = mContext.getString(R.string.august);
                    break;
                case Calendar.SEPTEMBER:
                    strPageTitle = mContext.getString(R.string.september);
                    break;
                case Calendar.OCTOBER:
                    strPageTitle = mContext.getString(R.string.october);
                    break;
                case Calendar.NOVEMBER:
                    strPageTitle = mContext.getString(R.string.november);
                    break;
                case Calendar.DECEMBER:
                    strPageTitle = mContext.getString(R.string.december);
                    break;
                default:
                    break;

            }

            strPageTitle += " " + calendar.get(Calendar.YEAR);

            return strPageTitle;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPager();
        if (adView != null) {
            adView.resume();
        }
        startJobScheduler();
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adRetryHandler.removeCallbacksAndMessages(null);
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
        super.onDestroy();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.settings) {
            // Open the settings activity from the toolbar menu.
            intent = new Intent(getApplicationContext(), MyPreferenceActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


    private void startJobScheduler() {
        NotificationJobService.scheduleNextJob(getApplicationContext());
    }

}

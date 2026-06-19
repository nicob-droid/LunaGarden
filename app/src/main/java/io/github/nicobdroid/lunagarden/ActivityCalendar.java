package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdListener;

import java.util.Calendar;

public class ActivityCalendar extends AppCompatActivity {
    private static final String TAG = "ActivityCalendar";
    private static final int MAX_AD_RETRY = 2;
    private static final long AD_RETRY_DELAY_MS = 1500L;
    FragmentPagerAdapter adapterViewPager;
    private AdView adView;
    private int adRetryCount;
    private final Handler adRetryHandler = new Handler(Looper.getMainLooper());
    private Runnable adRetryRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(2);

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
                public void onAdFailedToLoad(LoadAdError loadAdError) {
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



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 6;
        Context mContext;
        static int actualMonth = Calendar.getInstance().get(Calendar.MONTH);
        static int actualYear = Calendar.getInstance().get(Calendar.YEAR);

        public MyPagerAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            this.mContext = context;
        }

        // Returns total number of pages.
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for a particular page.
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return createFragmentForOffset(-2);
                case 1:
                    return createFragmentForOffset(-1);
                case 2:
                    return createFragmentForOffset(0);
                case 3:
                    return createFragmentForOffset(1);
                case 4:
                    return createFragmentForOffset(2);
                case 5:
                    return createFragmentForOffset(3);
                default:
                    return null;
            }
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
            calendar.add(Calendar.MONTH, position - 2);
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

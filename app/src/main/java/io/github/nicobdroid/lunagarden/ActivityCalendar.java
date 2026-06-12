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
import android.widget.Toast;

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
                    Log.d(TAG, "AdMob banner loaded");
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    Log.w(TAG, "AdMob banner failed: " + loadAdError.getCode() + " / " + loadAdError.getMessage());
                    if ((getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                        Toast.makeText(
                                ActivityCalendar.this,
                                "AdMob error " + loadAdError.getCode() + ": " + loadAdError.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    if (adRetryCount < MAX_AD_RETRY) {
                        adRetryCount++;
                        new Handler(Looper.getMainLooper()).postDelayed(
                                () -> adView.loadAd(new AdRequest.Builder().build()),
                                AD_RETRY_DELAY_MS
                        );
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

            int iNewYear = actualYear;
            int iNewMonth = actualMonth;

            switch (position) {
                case 0:
                    if (actualMonth < 2) {
                        iNewYear = actualYear - 1;
                        iNewMonth = actualMonth + 10;
                    } else {
                        iNewMonth = actualMonth - 2;
                    }
                    return FragmentCalendar.newInstance(iNewMonth, iNewYear);
                case 1:
                    if (actualMonth < 1) {
                        iNewYear = actualYear - 1;
                        iNewMonth = actualMonth + 11;
                    } else {
                        iNewMonth = actualMonth - 1;
                    }
                    return FragmentCalendar.newInstance(iNewMonth, iNewYear);
                case 2:
                    return FragmentCalendar.newInstance(actualMonth, actualYear);
                case 3:
                    if (actualMonth > 10) {
                        iNewYear = actualYear + 1;
                    }
                    return FragmentCalendar.newInstance(actualMonth + 1, iNewYear);
                case 4:
                    if (actualMonth > 9) {
                        iNewYear = actualYear + 1;
                    }
                    return FragmentCalendar.newInstance(actualMonth + 2, iNewYear);
                case 5:
                    if (actualMonth > 8) {
                        iNewYear = actualYear + 1;
                    }
                    return FragmentCalendar.newInstance(actualMonth + 3, iNewYear);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String strPageTitle = null;
            int monthId = (12 + position + actualMonth - 2)%12;
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

            if ((position + actualMonth - 2) > Calendar.DECEMBER) {
                strPageTitle += " " + (actualYear + 1);
            } else if ((position + actualMonth - 2) < Calendar.JANUARY) {
                strPageTitle += " " + (actualYear - 1);
            }
            else {
                strPageTitle += " " + actualYear;
            }

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
        if (adView != null) {
            adView.destroy();
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

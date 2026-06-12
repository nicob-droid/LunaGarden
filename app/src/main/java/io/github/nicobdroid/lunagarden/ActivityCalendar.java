package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.Calendar;

public class ActivityCalendar extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(2);

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
        startJobScheduler();
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

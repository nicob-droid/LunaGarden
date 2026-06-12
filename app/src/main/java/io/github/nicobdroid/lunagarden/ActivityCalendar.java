package io.github.nicobdroid.lunagarden;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.github.nicobdroid.lunagarden.settings.FruitVegManager;
import io.github.nicobdroid.lunagarden.settings.LeafVegManager;
import io.github.nicobdroid.lunagarden.settings.RootVegManager;

public class ActivityCalendar extends AppCompatActivity {
    private static final String TAG = "ActivityCalendar";
    private static final int JOB_ID = 52;
    private JobScheduler mScheduler;
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(2);

        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 6;
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
                strPageTitle += " " + String.valueOf(actualYear + 1);
            } else if ((position + actualMonth - 2) < Calendar.JANUARY) {
                strPageTitle += " " + String.valueOf(actualYear - 1);
            }
            else {
                strPageTitle += " " + String.valueOf(actualYear);
            }

            return strPageTitle;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //setAlarmForNotifyUser();

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


    private void setAlarmForNotifyUser() {
        // get notification time
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strNotificationTime = prefs.getString(
                getString(R.string.settings_notification_time),
                AppTechnicalKeys.DEFAULT_NOTIFICATION_TIME
        );
        Log.d(TAG, "onResume: strNotificationTime = " + strNotificationTime);

        // get notification nb days earlier
        String strNotificationDaysEarlier = prefs.getString(
                getString(R.string.settings_notification_nb_days_earlier),
                AppTechnicalKeys.DEFAULT_NOTIFICATION_NB_DAYS_EARLIER
        );
        int iNotificationDaysEarlier = Integer.valueOf(strNotificationDaysEarlier);
        Log.d(TAG, "onResume: iNotificationDaysEarlier = " + iNotificationDaysEarlier);

        String[] dateArray = strNotificationTime.split(":");
        int hour = Integer.parseInt(dateArray[0]);
        int minute = Integer.parseInt(dateArray[1]);

        Calendar cal_dateToSurvey = Calendar.getInstance();
        Date date = new Date();//initializes to now
        cal_dateToSurvey.setTime(date);
        cal_dateToSurvey.add(Calendar.DAY_OF_MONTH, iNotificationDaysEarlier);
        cal_dateToSurvey.set(Calendar.HOUR_OF_DAY, hour);
        cal_dateToSurvey.set(Calendar.MINUTE, minute);
        cal_dateToSurvey.set(Calendar.SECOND, 0);

        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, minute);
        setcalendar.set(Calendar.SECOND, 0);
        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        FragmentCalendar fragmentCalendar = new FragmentCalendar();
        fragmentCalendar.setExternalContext(getApplicationContext());
        ArrayList<ResultVegItem> mResultArray = new ArrayList<>();
        ArrayList<ResultVegItem> collectArray;
        if (fragmentCalendar.isDayRacine(cal_dateToSurvey.get(Calendar.YEAR), cal_dateToSurvey.get(Calendar.MONTH) + 1,
                cal_dateToSurvey.get(Calendar.DAY_OF_MONTH))) {
            mResultArray = RootVegManager.getResultVegForSow(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            collectArray = RootVegManager.getResultVegForCollect(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            mResultArray.addAll(collectArray);

//            for (int i = 0; i < mResultArray.size(); i++) {
//                Toast.makeText(getApplicationContext(), mResultArray.get(i).getMainMessage(), Toast.LENGTH_SHORT).show();
//            }

        } else if (fragmentCalendar.isDayFeuille(cal_dateToSurvey.get(Calendar.YEAR), cal_dateToSurvey.get(Calendar.MONTH) + 1,
                cal_dateToSurvey.get(Calendar.DAY_OF_MONTH))) {
            mResultArray = LeafVegManager.getResultVegForSow(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            collectArray = LeafVegManager.getResultVegForCollect(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            mResultArray.addAll(collectArray);


        } else if (fragmentCalendar.isDayFruit(cal_dateToSurvey.get(Calendar.YEAR), cal_dateToSurvey.get(Calendar.MONTH) + 1,
                cal_dateToSurvey.get(Calendar.DAY_OF_MONTH))) {
            mResultArray = FruitVegManager.getResultVegForSow(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            collectArray = FruitVegManager.getResultVegForCollect(getApplicationContext(), cal_dateToSurvey.get(Calendar.MONTH));
            mResultArray.addAll(collectArray);
        }


        AlarmManager am = (AlarmManager) Objects.requireNonNull(getApplicationContext()).getSystemService(ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), NotificationService.class);
        // put extra intent
        if (!mResultArray.isEmpty()) {
            i.putExtra(AppTechnicalKeys.NOTIFICATION_EXTRA_TITLE, mResultArray.get(0).getMainMessage());
        }


        PendingIntent pi = PendingIntent.getService(
                getApplicationContext(),
                0,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );


        Objects.requireNonNull(am).cancel(pi);
//        am.set(AlarmManager.RTC, cal_alarm.getTimeInMillis(), pi);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);

    }

    private void startJobScheduler() {

        // Read if notifications are enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean bIsNotificationEnabled = prefs.getBoolean(getString(R.string.settings_notification_enable), false);

        Log.d(TAG, "Start Job Scheduler: " + bIsNotificationEnabled);

        if (bIsNotificationEnabled) {
            // Set Extras
            PersistableBundle bundle = new PersistableBundle();
            bundle.putBoolean("notify", false);


            ComponentName serviceName = new ComponentName(getPackageName(),
                    NotificationJobService.class.getName());
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresDeviceIdle(false)
                    //   .setMinimumLatency(timeInMs) // 1 minute
                    .setExtras(bundle)
                    .setRequiresCharging(false);

            JobInfo myJobInfo = builder.build();
            mScheduler.schedule(myJobInfo);
            Toast.makeText(this, R.string.job_scheduled, Toast.LENGTH_SHORT)
                    .show();
        }

    }

}

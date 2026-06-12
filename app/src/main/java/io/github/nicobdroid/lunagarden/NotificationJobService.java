package io.github.nicobdroid.lunagarden;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import io.github.nicobdroid.lunagarden.notificationscheduler.NotificationSchedulerActivity;
import io.github.nicobdroid.lunagarden.settings.FruitVegManager;
import io.github.nicobdroid.lunagarden.settings.LeafVegManager;
import io.github.nicobdroid.lunagarden.settings.RootVegManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationJobService extends JobService {
    private static final String TAG = "NotificationJobService";

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    // Notification manager.
    NotificationManager mNotifyManager;

    String mNotificationMessage = "mNotificationMessage";
    Long mNextNotificationTimeInMs = 10000L; // 10s


    /**
     * Called by the system once it determines it is time to run the job.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether or not the job was offloaded to a
     * separate thread.
     * In this case, it is false since the notification can be posted on
     * the main thread.
     */
    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob");


        // Create the notification channel.
        createNotificationChannel();

        // Set up the notification content intent to launch the app when
        // clicked.
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, NotificationSchedulerActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.job_service))
                .setContentText(mNotificationMessage)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_cancel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "POST_NOTIFICATIONS non accordee, notification ignoree");
            scheduleRefresh();
            return false;
        }

        // Get extra
        if (jobParameters.getExtras().getBoolean("notify")) {
            mNotifyManager.notify(0, builder.build());
        }

        scheduleRefresh();
        return false;
    }

    @SuppressLint("MissingPermission")
    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        // Cancel all if exist
        mJobScheduler.cancelAll();

        // Get notification parameters
        boolean notify = getNotificationParameters();

        // Set Extras
        PersistableBundle bundle = new PersistableBundle();
        bundle.putBoolean("notify", notify);

        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(52,
                        new ComponentName(getPackageName(),
                                NotificationJobService.class.getName()));

        mJobBuilder
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(false)
                .setExtras(bundle)
                .setMinimumLatency(mNextNotificationTimeInMs) // 1 minute
                .setRequiresCharging(false);
        mJobScheduler.schedule(mJobBuilder.build());
    }

    private boolean getNotificationParameters() {
        boolean notify = false;

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

        // put extra intent
        if (!mResultArray.isEmpty()) {
            notify = true;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mResultArray.size(); i++) {
                String strMessage = mResultArray.get(i).getMainMessage() + "\n";
                stringBuilder.append(strMessage);
            }
            mNotificationMessage = stringBuilder.toString();
            Log.d(TAG, "Get notification parameters: Message = " + mNotificationMessage);
        }
        mNextNotificationTimeInMs = setcalendar.getTimeInMillis() - calendar.getTimeInMillis();
        Log.d(TAG, "Get notification parameters: TimeInMs = " + mNextNotificationTimeInMs);
        Log.d(TAG, "Get notification parameters: notify = " + notify);

        return notify;
    }

    /**
     * Called by the system when the job is running but the conditions are no
     * longer met.
     * In this example it is never called since the job is not offloaded to a
     * different thread.
     *
     * @param jobParameters Contains the information about the job.
     * @return Boolean indicating whether the job needs rescheduling.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.job_service_notification),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

}


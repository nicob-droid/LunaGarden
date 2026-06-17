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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import io.github.nicobdroid.lunagarden.settings.FruitVegManager;
import io.github.nicobdroid.lunagarden.settings.LeafVegManager;
import io.github.nicobdroid.lunagarden.settings.RootVegManager;

import java.util.ArrayList;
import java.util.Calendar;


public class NotificationJobService extends JobService {
    private static final String TAG = "NotificationJobService";
    private static final int JOB_ID = 52;
    private static final int NOTIFICATION_ID = 52;
    private static final long MIN_DELAY_MS = 1_000L;
    private static final long DEADLINE_WINDOW_MS = 15 * 60 * 1000L;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification manager.
    NotificationManager mNotifyManager;

    public static void scheduleNextJob(android.content.Context context) {
        if (context == null) {
            return;
        }

        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler == null) {
            return;
        }

        scheduler.cancel(JOB_ID);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNotificationEnabled = prefs.getBoolean(
                context.getString(R.string.settings_notification_enable),
                true
        );

        if (!isNotificationEnabled) {
            return;
        }

        long delay = computeDelayToNextExecution(context, prefs);
        ComponentName serviceName = new ComponentName(context, NotificationJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setPersisted(true)
                .setMinimumLatency(delay)
                .setOverrideDeadline(delay + DEADLINE_WINDOW_MS)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        scheduler.schedule(jobInfo);
    }

    public static void cancelScheduledJob(android.content.Context context) {
        if (context == null) {
            return;
        }
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.cancel(JOB_ID);
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob");

        NotificationPayload payload = buildPayloadForCurrentPreferences();
        if (payload != null) {
            showNotification(payload.title, payload.message);
        }

        scheduleNextJob(getApplicationContext());
        jobFinished(jobParameters, false);
        return false;
    }

    private NotificationPayload buildPayloadForCurrentPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNotificationEnabled = prefs.getBoolean(
                getString(R.string.settings_notification_enable),
                true
        );
        if (!isNotificationEnabled) {
            return null;
        }

        int notificationDaysEarlier = readLeadDays(prefs);
        Calendar dateToSurvey = Calendar.getInstance();
        dateToSurvey.add(Calendar.DAY_OF_MONTH, notificationDaysEarlier);

        ArrayList<ResultVegItem> resultArray = getResultsForDate(dateToSurvey);
        if (resultArray.isEmpty()) {
            return null;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < resultArray.size(); i++) {
            if (i > 0) {
                messageBuilder.append("\n");
            }
            messageBuilder.append(resultArray.get(i).getMainMessage());
        }
        return new NotificationPayload(
                getString(R.string.notification_default_title),
                messageBuilder.toString()
        );
    }

    private ArrayList<ResultVegItem> getResultsForDate(Calendar dateToSurvey) {
        // Utilise MoonDayCalculator directement, sans passer par FragmentCalendar (anti-pattern supprimé)
        MoonDayCalculator calculator = new MoonDayCalculator(getApplicationContext());
        ArrayList<ResultVegItem> resultArray = new ArrayList<>();
        ArrayList<ResultVegItem> collectArray;

        int year  = dateToSurvey.get(Calendar.YEAR);
        int month = dateToSurvey.get(Calendar.MONTH);
        int day   = dateToSurvey.get(Calendar.DAY_OF_MONTH);

        if (calculator.isDayRacine(year, month + 1, day)) {
            resultArray  = RootVegManager.getResultVegForSow(getApplicationContext(), month);
            collectArray = RootVegManager.getResultVegForCollect(getApplicationContext(), month);
            resultArray.addAll(collectArray);
        } else if (calculator.isDayFeuille(year, month + 1, day)) {
            resultArray  = LeafVegManager.getResultVegForSow(getApplicationContext(), month);
            collectArray = LeafVegManager.getResultVegForCollect(getApplicationContext(), month);
            resultArray.addAll(collectArray);
        } else if (calculator.isDayFruit(year, month + 1, day)) {
            resultArray  = FruitVegManager.getResultVegForSow(getApplicationContext(), month);
            collectArray = FruitVegManager.getResultVegForCollect(getApplicationContext(), month);
            resultArray.addAll(collectArray);
        }

        return resultArray;
    }

    private int readLeadDays(SharedPreferences prefs) {
        String value = prefs.getString(
                getString(R.string.settings_notification_nb_days_earlier),
                AppTechnicalKeys.DEFAULT_NOTIFICATION_NB_DAYS_EARLIER
        );
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(0, Math.min(30, parsed));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long computeDelayToNextExecution(android.content.Context context, SharedPreferences prefs) {
        String notificationTime = prefs.getString(
                context.getString(R.string.settings_notification_time),
                AppTechnicalKeys.DEFAULT_NOTIFICATION_TIME
        );

        int hour = 10;
        int minute = 0;
        if (notificationTime.contains(":")) {
            String[] dateArray = notificationTime.split(":");
            if (dateArray.length == 2) {
                try {
                    hour = Integer.parseInt(dateArray[0]);
                    minute = Integer.parseInt(dateArray[1]);
                } catch (NumberFormatException ignored) {
                    // Keep defaults when parsing fails.
                }
            }
        }

        hour = Math.max(0, Math.min(23, hour));
        minute = Math.max(0, Math.min(59, minute));

        Calendar now = Calendar.getInstance();
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, hour);
        nextRun.set(Calendar.MINUTE, minute);
        nextRun.set(Calendar.SECOND, 0);
        nextRun.set(Calendar.MILLISECOND, 0);
        if (!nextRun.after(now)) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1);
        }

        return Math.max(MIN_DELAY_MS, nextRun.getTimeInMillis() - now.getTimeInMillis());
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String message) {
        createNotificationChannel();

        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, Splash.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_cancel)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "POST_NOTIFICATIONS non accordee, notification ignoree");
            return;
        }

        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
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

    private static final class NotificationPayload {
        final String title;
        final String message;

        NotificationPayload(String title, String message) {
            this.title = title;
            this.message = message;
        }
    }

}


package io.github.nicobdroid.lunagarden;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class NotificationService extends Service {
    public static final String TAG = "NotificationService";
	public static final int NOTIFICATION_ID = 52;

	private Intent mIntent;
    private PowerManager.WakeLock mWakeLock;
    private NotificationManager mNotificationManager;

    /**
     * Simply return null, since our Service will not be communicating with * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * * called by the system. We won't do anything with the intent here, and you
     * * probably won't, either.
     */
    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = Objects.requireNonNull(pm).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:wakelock");
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!Objects.requireNonNull(cm).getBackgroundDataSetting()) {
            stopSelf();
            return;
        } // do the actual work, in a separate thread
        new PollTask().execute();
    }

    private class PollTask extends AsyncTask<Void, Void, Void> {
        /** * This is where YOU do YOUR work.
         * There's nothing for me to write here
         * * you have to fill this in. Make your HTTP request(s) or whatever it is
         * * you have to do to get your updates in here, because this is run in a
         * * separate thread
         * */

        @Override protected Void doInBackground(Void... params) {
            // do stuff!
            Log.d(TAG, "doInBackground");
            return null;
        }
        /** * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * * NotificationManager. I will not cover this here, go check the docs on
         * * NotificationManager.
         * * * What you HAVE to do is call stopSelf() after you've pushed your
         * * notification(s). This will:
         * * 1) Kill the service so it doesn't waste precious resources
         * * 2) Call onDestroy() which will release the wake lock, so the device
         * * can go to sleep again and save precious battery.
         * */

        @Override protected void onPostExecute(Void result) {
            // handle your data
            Log.d(TAG, "onPostExecute");
            // ne pas envoyer de notifications pour le moment
            addNotification();
            Toast.makeText(NotificationService.this, R.string.notification_debug_post_execute, Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    /** * This is deprecated, but you have to implement it if you're planning on
     * * supporting devices with an API level lower than 5 (Android 2.0).
     * */

    @Override public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    /** * This is called on 2.0+ (API level 5 or higher). Returning
     * * START_NOT_STICKY tells the system to not restart the service if it is
     * * killed because of poor resource (memory/cpu) conditions.
     * */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;
        handleIntent(mIntent);
        return START_NOT_STICKY;
    }

    /** * In onDestroy() we release our wake lock. This ensures that whenever the
     * * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * * lock will be released.
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }

    private void addNotification() {
        Log.d(TAG, "Add notification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), Splash.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                ii,
                PendingIntent.FLAG_IMMUTABLE
        );

        String title = getString(R.string.notification_default_title);
        String message = getString(R.string.notification_default_text);
        Bundle extras = mIntent != null ? mIntent.getExtras() : null;
        if (extras != null) {
            Object extraTitle = extras.get(AppTechnicalKeys.NOTIFICATION_EXTRA_TITLE);
            if (extraTitle != null) {
                title = String.valueOf(extraTitle);
            }
            Object extraMessage = extras.get(AppTechnicalKeys.NOTIFICATION_EXTRA_MESSAGE);
            if (extraMessage != null) {
                message = String.valueOf(extraMessage);
            }
        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setCategory(Notification.CATEGORY_REMINDER);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.notification_service_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            Log.w(TAG, "POST_NOTIFICATIONS non accordee, notification ignoree");
        }
    }
}


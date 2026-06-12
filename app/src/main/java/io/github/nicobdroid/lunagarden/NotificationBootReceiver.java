package io.github.nicobdroid.lunagarden;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
            NotificationJobService.scheduleNextJob(context.getApplicationContext());
        }
    }
}


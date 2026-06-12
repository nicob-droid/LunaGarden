package io.github.nicobdroid.lunagarden;

public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.github.nicobdroid.lunagarden", appContext.getPackageName());
    }
}
package io.github.nicobdroid.lunagarden.notificationscheduler;

/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;


import io.github.nicobdroid.lunagarden.FragmentCalendar;
import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.ResultVegItem;
import io.github.nicobdroid.lunagarden.settings.FruitVegManager;
import io.github.nicobdroid.lunagarden.settings.LeafVegManager;
import io.github.nicobdroid.lunagarden.settings.RootVegManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * The Service that JobScheduler runs once the conditions are met.
 * In this case it posts a notification.
 */


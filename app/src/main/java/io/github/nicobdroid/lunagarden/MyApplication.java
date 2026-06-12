package io.github.nicobdroid.lunagarden;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        MobileAds.initialize(getApplicationContext(), AppTechnicalKeys.ADMOB_APP_ID);


        MyApplication.context = getApplicationContext();
    }

    public static Context getStaticContext()
    {
        return MyApplication.context;
    }
}


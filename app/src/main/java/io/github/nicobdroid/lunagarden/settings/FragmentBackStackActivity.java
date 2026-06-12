package io.github.nicobdroid.lunagarden.settings;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import io.github.nicobdroid.lunagarden.R;

public class FragmentBackStackActivity extends AppCompatActivity {
    private static final String TAG = "FragmentBackStackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_back_stack);

        setTitle(R.string.app_name);

        manageScreenOrientation();


        // Get FragmentManager and FragmentTransaction object.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create FragmentOne instance.
        FragmentWelcome fragmentWelcome = new FragmentWelcome();

        // Add fragment one with tag name.
        fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentWelcome, FragmentTags.WELCOME);
        fragmentTransaction.commit();

        FragmentUtil.printActivityFragmentList(fragmentManager);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        manageScreenOrientation();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void manageScreenOrientation() {
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            layoutParams.height = (3*screenHeight) / 4;
            layoutParams.width = (9*screenWidth) / 10;
        } else {
            layoutParams.height = (9*screenHeight) / 10;
            layoutParams.width = (4*screenWidth) / 5;
        }
        getWindow().setLayout(layoutParams.width, layoutParams.height);
    }
}


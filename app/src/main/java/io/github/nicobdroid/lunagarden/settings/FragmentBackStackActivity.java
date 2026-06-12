package io.github.nicobdroid.lunagarden.settings;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import androidx.activity.OnBackPressedCallback;
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

        // Gestion du bouton retour via OnBackPressedDispatcher (AndroidX)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed");
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }
        });

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

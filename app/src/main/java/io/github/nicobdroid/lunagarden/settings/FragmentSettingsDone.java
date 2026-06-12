package io.github.nicobdroid.lunagarden.settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.Objects;

import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.Splash;

public class FragmentSettingsDone extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_settings_done, container, false);

//        final FragmentManager fragmentManager = getFragmentManager();

        Button btNext = (Button)retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearStack();

                // Save settings are done
                FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(requireContext());
                fruitVegPrefs.saveSettingsDone();

                // Start calendar activity
                Intent intent = new Intent(getContext(), Splash.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // Clear back stack
                new Thread() {
                    @Override
                    public void run() {
                        requireActivity().finishAffinity();
                    }
                }.start();
            }
        });

        return retView;
    }

    public void clearStack() {
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        //Here we are clearing back stack fragment entries
        int backStackEntry = fragmentManager.getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                fragmentManager.popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (fragmentManager.getFragments() != null && fragmentManager.getFragments().size() > 0) {
            for (int i = 0; i < fragmentManager.getFragments().size(); i++) {
                Fragment mFragment = fragmentManager.getFragments().get(i);
                if (mFragment != null) {
                    fragmentManager.beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }

}


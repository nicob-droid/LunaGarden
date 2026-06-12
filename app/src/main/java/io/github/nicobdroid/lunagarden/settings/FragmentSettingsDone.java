package io.github.nicobdroid.lunagarden.settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.github.nicobdroid.lunagarden.R;
import io.github.nicobdroid.lunagarden.Splash;

public class FragmentSettingsDone extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_settings_done, container, false);

//        final FragmentManager fragmentManager = getFragmentManager();

        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> {
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
        });

        return retView;
    }

}


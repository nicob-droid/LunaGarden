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

        Button btCancel = retView.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> {
            // Save settings are done
            FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(requireContext());
            fruitVegPrefs.saveSettingsDone();

            // Start calendar activity
            Intent intent = new Intent(getContext(), Splash.class);
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

package io.github.nicobdroid.lunagarden.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import io.github.nicobdroid.lunagarden.AppTechnicalKeys;
import io.github.nicobdroid.lunagarden.R;

public class FragmentIsFruitVeg extends Fragment {
    private RadioButton radiobuttonYes;
    private FruitVegPrefs fruitVegPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_is_fruit_veg, container, false);

        final FragmentManager fragmentManager = getFragmentManager();
        fruitVegPrefs = new FruitVegPrefs(requireContext());

        radiobuttonYes = retView.findViewById(R.id.radiobuttonYes);
        radiobuttonYes.setChecked(fruitVegPrefs.isFruitVegEnabled());

        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> clickOnButtonNext(Objects.requireNonNull(fragmentManager)));

        Button btPrevious =  retView.findViewById(R.id.btPrevious);
        btPrevious.setOnClickListener(view -> clickOnButtonPrevious(Objects.requireNonNull(fragmentManager)));


        return retView;
    }

    private void clickOnButtonNext(FragmentManager fragmentManager) {
        FragmentSelectFruitVeg fragmentSelectFruitVeg = new FragmentSelectFruitVeg();
        FragmentSettingsDone fragmentSettingsDone = new FragmentSettingsDone();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment if exist.
        Fragment fragmentIsFruitVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.IS_FRUIT_VEG);
        if (fragmentIsFruitVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment exist in back stack, will hide it now.");
            // Hide fragment two. Only hide not destroy.
            // When user type back menu in Fragment three,
            // this hidden Fragment will be shown again with input text saved.
            fragmentTransaction.hide(fragmentIsFruitVeg);
        }

        String strValue = AppTechnicalKeys.PREF_VALUE_NO;
        if (radiobuttonYes.isChecked()) {
            // for saving in shared preferences
            strValue = AppTechnicalKeys.PREF_VALUE_YES;
            // Add Fragment with special tag name.
            fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentSelectFruitVeg, FragmentTags.SELECT_FRUIT_VEG);
            // Add fragment in back stack.
            fragmentTransaction.addToBackStack(null);
        } else {
            // Add Fragment with special tag name.
            fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentSettingsDone, FragmentTags.SETTINGS_DONE);
            // Add fragment in back stack.
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();

        // Save preferences
        fruitVegPrefs.saveFruitVegEnable(strValue);
    }


    private void clickOnButtonPrevious(FragmentManager fragmentManager) {
        FragmentIsLeafVeg fragmentIsLeafVeg = new FragmentIsLeafVeg();
        FragmentSelectLeafVeg fragmentSelectLeafVeg = new FragmentSelectLeafVeg();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(requireContext());

        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment FragmentIsRootVeg if exist.
        Fragment fragmentIsFruitVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.IS_FRUIT_VEG);
        if (fragmentIsFruitVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment exists in back stack, remove it.");
            // Remove fragment from back stack
            fragmentTransaction.remove(fragmentIsFruitVeg);

            if (leafVegPrefs.isLeafVegEnabled()) {
                // Display fragment instead
                fragmentTransaction.replace(R.id.fragment_back_stack_frame_layout,
                        fragmentSelectLeafVeg, FragmentTags.SELECT_LEAF_VEG);
            } else {
                // Display fragment instead
                fragmentTransaction.replace(R.id.fragment_back_stack_frame_layout,
                        fragmentIsLeafVeg, FragmentTags.IS_LEAF_VEG);
            }

            fragmentTransaction.addToBackStack(null);
            // Commit modifications
            fragmentTransaction.commit();
        }

    }

}


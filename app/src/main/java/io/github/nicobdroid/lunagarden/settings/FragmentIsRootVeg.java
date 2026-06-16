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

public class FragmentIsRootVeg extends Fragment {
    private static final String TAG = "FragmentIsRootVeg";
    private RadioButton radiobuttonYes;
    private RootVegPrefs rootVegPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_is_root_veg, container, false);

        final FragmentManager fragmentManager = getFragmentManager();
        rootVegPrefs = new RootVegPrefs(requireContext());

        radiobuttonYes = retView.findViewById(R.id.radiobuttonYes);
        radiobuttonYes.setChecked(rootVegPrefs.isRootVegEnabled());

        Button btCancel = retView.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> clickOnButtonNext(Objects.requireNonNull(fragmentManager)));

        Button btPrevious = retView.findViewById(R.id.btPrevious);
        btPrevious.setOnClickListener(view -> clickOnButtonPrevious(Objects.requireNonNull(fragmentManager)));

        return retView;
    }

    private void clickOnButtonNext(FragmentManager fragmentManager) {
        FragmentSelectRootVeg fragmentSelectRootVeg = new FragmentSelectRootVeg();
        FragmentIsLeafVeg fragmentIsLeafVeg = new FragmentIsLeafVeg();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment FragmentIsRootVeg if exist.
        Fragment fragmentIsRootVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.IS_ROOT_VEG);
        if (fragmentIsRootVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment Two exist in back stack, will hide it now.");
            // Hide fragment two. Only hide not destroy.
            // When user type back menu in Fragment three,
            // this hidden Fragment will be shown again with input text saved.
            fragmentTransaction.hide(fragmentIsRootVeg);
        }

        String strValue = AppTechnicalKeys.PREF_VALUE_NO;
        if (radiobuttonYes.isChecked()) {
            // for saving in shared preferences
            strValue = AppTechnicalKeys.PREF_VALUE_YES;
            // Add Fragment with special tag name.
            fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentSelectRootVeg, FragmentTags.SELECT_ROOT_VEG);
            // Add fragment in back stack.
            fragmentTransaction.addToBackStack(null);
        } else {
            // Add Fragment with special tag name.
            fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentIsLeafVeg, FragmentTags.IS_LEAF_VEG);
            // Add fragment in back stack.
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();

        // Save preferences
        rootVegPrefs.saveRootVegEnable(strValue);
    }

    private void clickOnButtonPrevious(FragmentManager fragmentManager) {
        FragmentWelcome fragmentWelcome = new FragmentWelcome();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment FragmentIsRootVeg if exist.
        Fragment fragmentIsRootVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.IS_ROOT_VEG);
        if (fragmentIsRootVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment exists in back stack, remove it.");
            // Remove fragment from back stack
            fragmentTransaction.remove(fragmentIsRootVeg);
            // Display Welcome fragment instead
            fragmentTransaction.replace(R.id.fragment_back_stack_frame_layout, fragmentWelcome, FragmentTags.WELCOME);

            fragmentTransaction.addToBackStack(null);
            // Commit modifications
            fragmentTransaction.commit();
        }

    }

}

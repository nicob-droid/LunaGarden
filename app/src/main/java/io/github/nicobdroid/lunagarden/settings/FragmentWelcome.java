package io.github.nicobdroid.lunagarden.settings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import io.github.nicobdroid.lunagarden.R;

public class FragmentWelcome extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_welcome, container, false);

        final FragmentManager fragmentManager = getFragmentManager();



        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> clickOnButtonNext(fragmentManager));

        return retView;
    }

    private void clickOnButtonNext(FragmentManager fragmentManager) {
        Fragment fragmentIsRootVeg = FragmentUtil.getFragmentByTagName(requireFragmentManager(), FragmentTags.IS_ROOT_VEG);

        // Because fragment two has been popup from the back stack, so it must be null.
        if(fragmentIsRootVeg==null)
        {
            fragmentIsRootVeg = new FragmentIsRootVeg();
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace fragment one with fragment two, the second fragment tag name is "Fragment Two".
        // This action will remove Fragment one and add Fragment two.
        fragmentTransaction.replace(R.id.fragment_back_stack_frame_layout, fragmentIsRootVeg, FragmentTags.IS_ROOT_VEG);

        // Add fragment one in back stack.So it will not be destroyed. Press back menu can pop it up from the stack.
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

        FragmentUtil.printActivityFragmentList(fragmentManager);
    }
}


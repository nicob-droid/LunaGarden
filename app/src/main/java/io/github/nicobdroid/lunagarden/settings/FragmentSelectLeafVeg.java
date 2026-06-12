package io.github.nicobdroid.lunagarden.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.Objects;

import io.github.nicobdroid.lunagarden.R;

public class FragmentSelectLeafVeg extends Fragment {
    private static final String TAG = "FragmentSelectLeafVeg";
    // The context variable to use in whole activity in place of
    // "HomeActivity.this".
    private Context mContext;

    // The FruitList Adapter to bind the views and show in listview.
    private VegListAdapter mFruitListAdapter;

    // The ArrayList of Fruits<FruitItem>.
    private ArrayList<VegItem> mFruitList;

    // The ListView of fruit message list.
    private ListView mFruitListview;

    // Check at least one vegetable checked
    private boolean mOneVegChecked = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_select_leaf_vege, container, false);

        mFruitListview = retView.findViewById(R.id.listview);

        final FragmentManager fragmentManager = getFragmentManager();

        mContext = getContext();
        initView();
        mFruitList = LeafVegManager.getFruitItemList(getContext());
        populateFruitList(mFruitList);

        Button btNext = retView.findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> clickOnButtonNext(Objects.requireNonNull(fragmentManager)));

        Button btPrevious = retView.findViewById(R.id.btPrevious);
        btPrevious.setOnClickListener(view -> clickOnButtonPrevious(Objects.requireNonNull(fragmentManager)));

        return retView;
    }

    private void initView() {
        // Setting item click listener
        mFruitListview.setOnItemClickListener((adpView, view, position, itemId) -> {
            Log.d(TAG, "onItemClick");
            if (mFruitListAdapter != null) {
                VegItem item = mFruitListAdapter.getItem(position);
            }
        });
    }


    /**
     * The listener to get list item click events.
     */
    private final VegListAdapter.OnFruitItemClickListener listListener = new VegListAdapter.OnFruitItemClickListener() {

        @Override
        public void onCheckboxClicked(int position, VegItem item) {
            item.setCheckboxChecked(!item.isCheckboxChecked());
            Log.d(TAG, "onCheckboxClicked: " + item.getFruitname() + " is " + item.isCheckboxChecked());
            mFruitListAdapter.notifyDataSetChanged();
        }

    };

    private void populateFruitList(ArrayList<VegItem> list) {
        if (list != null && !list.isEmpty()) {
            if (mFruitListAdapter == null) {
                mFruitListAdapter = new VegListAdapter(mContext, mFruitList);
                mFruitListAdapter.setOnFruitClickListener(listListener);
            }
            mFruitListview.setAdapter(mFruitListAdapter);
        } else {
            mFruitListAdapter.setItemlist(mFruitList);
        }
    }


    private void clickOnButtonNext(FragmentManager fragmentManager) {
        FragmentIsFruitVeg fragmentIsFruitVeg = new FragmentIsFruitVeg();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment if exist.
        Fragment fragmentSelectLeafVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.SELECT_LEAF_VEG);
        if (fragmentSelectLeafVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment exists in back stack, will hide it now.");
            // Hide fragment two. Only hide not destroy.
            // When user type back menu in Fragment three,
            // this hidden Fragment will be shown again with input text saved.
            fragmentTransaction.hide(fragmentSelectLeafVeg);
        }
        // Add Fragment with special tag name.
        fragmentTransaction.add(R.id.fragment_back_stack_frame_layout, fragmentIsFruitVeg, FragmentTags.IS_FRUIT_VEG);
        // Add fragment in back stack.
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


        // Save all prefs
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(requireContext());
        boolean bCheckboxChecked = false;
        for (int i = 0; i < mFruitList.size(); i++) {
            bCheckboxChecked = mFruitList.get(i).isCheckboxChecked();
            leafVegPrefs.saveItem(mFruitList.get(i).getPreferenceKey(), String.valueOf(bCheckboxChecked));
            if (bCheckboxChecked) {
                mOneVegChecked = true;
            }
        }

        if (!mOneVegChecked) {
            leafVegPrefs.saveLeafVegEnable(getString(R.string.default_leaf_veg_enable));
        }
    }

    private void clickOnButtonPrevious(FragmentManager fragmentManager) {
        FragmentIsLeafVeg fragmentIsLeafVeg = new FragmentIsLeafVeg();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentUtil.printActivityFragmentList(fragmentManager);

        // Get fragment FragmentIsRootVeg if exist.
        Fragment fragmentSelectLeafVeg = FragmentUtil.getFragmentByTagName(fragmentManager, FragmentTags.SELECT_LEAF_VEG);
        if (fragmentSelectLeafVeg != null) {
            Log.d(FragmentUtil.TAG_NAME_FRAGMENT, "Fragment exists in back stack, remove it.");
            // Remove fragment from back stack
            fragmentTransaction.remove(fragmentSelectLeafVeg);
            // Display Welcome fragment instead
            fragmentTransaction.replace(R.id.fragment_back_stack_frame_layout, fragmentIsLeafVeg, FragmentTags.IS_LEAF_VEG);

            fragmentTransaction.addToBackStack(null);
            // Commit modifications
            fragmentTransaction.commit();
        }

    }


}


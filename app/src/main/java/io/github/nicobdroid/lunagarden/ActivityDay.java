package io.github.nicobdroid.lunagarden;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class ActivityDay extends AppCompatActivity {
    // The FruitList Adapter to bind the views and show in listview.
    private ResultVegListAdapter mListAdapter;
    // The ArrayList of Fruits<FruitItem>.
    private ArrayList<ResultVegItem> mFruitList;
    // The ListView of fruit message list.
    private ListView mFruitListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        manageScreenOrientation();
        // get extras
        // get date
        String strCurrentDate = Objects.requireNonNull(getIntent().getExtras())
                .getString(AppTechnicalKeys.ACTIVITY_EXTRA_DATE, getString(R.string.app_name));
        // get moon view
        int iMoonView = getIntent().getExtras().getInt(AppTechnicalKeys.ACTIVITY_EXTRA_MOON);
        // get list id for sow
        ArrayList<ResultVegItem> aListIdSow = (ArrayList<ResultVegItem>) getIntent().getSerializableExtra(AppTechnicalKeys.ACTIVITY_EXTRA_LIST_SOW);
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("dd MMMM yyyy");
            strCurrentDate = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // set date and moon view as title
        TextView title = findViewById(R.id.title);
        title.setText(strCurrentDate);
        ImageView moonView = findViewById(R.id.moonView);
        moonView.setImageResource(iMoonView);
        mFruitListview = findViewById(R.id.listview);
        mFruitList = aListIdSow;// FruitVegManager.getFruitItemList(getContext());
        populateFruitList(mFruitList);
    }

    private void populateFruitList(ArrayList<ResultVegItem> list) {
        if (list != null && list.size() > 0) {
            if (mListAdapter == null) {
                mListAdapter = new ResultVegListAdapter(getApplicationContext(), mFruitList);
            }
            mFruitListview.setAdapter(mListAdapter);
        } else {
            mListAdapter.setItemlist(mFruitList);
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

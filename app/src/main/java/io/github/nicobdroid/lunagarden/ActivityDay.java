package io.github.nicobdroid.lunagarden;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityDay extends AppCompatActivity {
    private static final String TAG = "ActivityDay";

    private ResultVegListAdapter mListAdapter;
    private ListView mFruitListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        manageScreenOrientation();

        Bundle extras = getIntent().getExtras();
        String strCurrentDate = extras != null
                ? extras.getString(AppTechnicalKeys.ACTIVITY_EXTRA_DATE, getString(R.string.app_name))
                : getString(R.string.app_name);
        int iMoonView = extras != null
                ? extras.getInt(AppTechnicalKeys.ACTIVITY_EXTRA_MOON, R.drawable.ic_nothing)
                : R.drawable.ic_nothing;

        @SuppressWarnings("unchecked")
        ArrayList<ResultVegItem> aListIdSow = (ArrayList<ResultVegItem>) getIntent()
                .getSerializableExtra(AppTechnicalKeys.ACTIVITY_EXTRA_LIST_SOW);

        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        try {
            Date parsedDate = inputFormat.parse(strCurrentDate);
            if (parsedDate != null) {
                strCurrentDate = outputFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + strCurrentDate, e);
        }

        TextView title = findViewById(R.id.title);
        title.setText(strCurrentDate);

        ImageView moonView = findViewById(R.id.moonView);
        moonView.setImageResource(iMoonView);
        moonView.setContentDescription(getString(R.string.a11y_moon_for_day, strCurrentDate));

        mFruitListview = findViewById(R.id.listview);
        populateFruitList(aListIdSow);
    }

    private void populateFruitList(ArrayList<ResultVegItem> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        if (mListAdapter == null) {
            mListAdapter = new ResultVegListAdapter(getApplicationContext(), list);
            mFruitListview.setAdapter(mListAdapter);
        } else {
            mListAdapter.setItemlist(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void manageScreenOrientation() {
        android.view.WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            layoutParams.height = (3 * screenHeight) / 4;
            layoutParams.width = (9 * screenWidth) / 10;
        } else {
            layoutParams.height = (9 * screenHeight) / 10;
            layoutParams.width = (4 * screenWidth) / 5;
        }
        getWindow().setLayout(layoutParams.width, layoutParams.height);
    }
}

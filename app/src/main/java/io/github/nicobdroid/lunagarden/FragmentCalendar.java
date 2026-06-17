package io.github.nicobdroid.lunagarden;

import static io.github.nicobdroid.lunagarden.ResultVegItem.RESULT_VEG_ITEM_FORBIDDEN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import io.github.nicobdroid.lunagarden.settings.FruitVegManager;
import io.github.nicobdroid.lunagarden.settings.FruitVegPrefs;
import io.github.nicobdroid.lunagarden.settings.LeafVegManager;
import io.github.nicobdroid.lunagarden.settings.LeafVegPrefs;
import io.github.nicobdroid.lunagarden.settings.RootVegManager;
import io.github.nicobdroid.lunagarden.settings.RootVegPrefs;

public class FragmentCalendar extends Fragment {
    public static final String TAG = "FragmentCalendar";

    protected FragmentActivity mActivity;
    private Context mExternalContext;
    /** Calculateur lunaire indépendant du Fragment. */
    private MoonDayCalculator mMoonDayCalculator;

    private static final double MOON_PHASE_LENGTH = 29.530588853;
    private ListView listView;
    private TextView pleaseWait;
    private RelativeLayout mainLayout;
    private CalendarAdapter calendarAdapter;
    private static final int MOON_NODE_ASCENDING_NODE = 0;
    private static final int MOON_NODE_DESCENDING_NODE = 1;
    private static final int MOON_NODE_APOGEE = 2;
    private static final int MOON_NODE_PERIGEE = 3;
    private static final int DAY_KIND_NONE = 0;
    private static final int DAY_KIND_ROOT = 1;
    private static final int DAY_KIND_LEAF = 2;
    private static final int DAY_KIND_FRUIT = 3;

    private static final int SPECIAL_NONE = 0;
    private static final int SPECIAL_ASCENDING = 1;
    private static final int SPECIAL_DESCENDING = 2;
    private static final int SPECIAL_APOGEE = 3;
    private static final int SPECIAL_PERIGEE = 4;

    private ArrayList<String> mDateStringArray;
    private ArrayList<Integer> mMoonArray;
    private ArrayList<Integer> mSowArray;
    private ArrayList<Integer> mCollectArray;
    private ArrayList<String> mActionArray;

    private ArrayList<ResultVegItem> mResultArray;

    //    private String mTitle;
    private int mMonthId;
    private int mYearId;
    private static int actualYear;
    private static int actualMonth;
    private static int actualDay;
    // Les caches sont maintenant dans MoonDayCalculator

    private static final class DayComputation {
        int specialType = SPECIAL_NONE;
        int dayKind = DAY_KIND_NONE;
    }

    public static FragmentCalendar newInstance(int monthId, int yearId) {
        FragmentCalendar fragment = new FragmentCalendar();
        Bundle args = new Bundle();
//        args.putInt("image", resImage);

        int newMonth = (12 + monthId) % 12;

        args.putInt("year", yearId);
        args.putInt("month", newMonth);
        fragment.setArguments(args);


        return fragment;
    }

    public void setExternalContext(Context context) {
        if (context != null) {
            mExternalContext = context.getApplicationContext();
            mMoonDayCalculator = new MoonDayCalculator(mExternalContext);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actualYear = Calendar.getInstance().get(Calendar.YEAR);
        actualMonth = Calendar.getInstance().get(Calendar.MONTH);
        actualDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        Bundle args = requireArguments();
        mMonthId = args.getInt("month", 0);
        mYearId = args.getInt("year", 2018);

        mMoonDayCalculator = new MoonDayCalculator(requireContext());
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);

        pleaseWait = view.findViewById(R.id.pleaseWait);
        listView = view.findViewById(R.id.listView);
        mainLayout = view.findViewById(R.id.mainLayout);

        setListView();

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Log.d(TAG, "onItemClick: " + i);

            if (!mActionArray.get(i).isEmpty()) {
                Intent intent = new Intent(getContext(), ActivityDay.class);
                String date = mDateStringArray.get(i) + " " + mYearId;
                intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_DATE, date);
                intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_MOON, mMoonArray.get(i));

                computeResultDayAction(mYearId, i);
                intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_LIST_SOW, mResultArray);

                startActivity(intent);
            }
        });

        return view;
    }

    private void setListView() {
        final Context safeContext = getSafeContext();
        final FragmentActivity hostActivity = mActivity != null ? mActivity : getActivity();
        if (hostActivity == null) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                // code runs in a thread
                hostActivity.runOnUiThread(() -> {
                    mainLayout.setVisibility(View.GONE);
                    pleaseWait.setVisibility(View.VISIBLE);
                });

                final SimpleDateFormat curFormater = new SimpleDateFormat("MMM dd");
                final GregorianCalendar date = new GregorianCalendar();


                mDateStringArray = new ArrayList<>();
                mMoonArray = new ArrayList<>();
                mSowArray = new ArrayList<>();
                mCollectArray = new ArrayList<>();
                mActionArray = new ArrayList<>();

                mResultArray = new ArrayList<>();
                final MonthVegCache monthVegCache = new MonthVegCache(safeContext, mMonthId);

                date.set(Calendar.YEAR, mYearId);
                date.set(Calendar.MONTH, mMonthId);
                date.set(Calendar.DAY_OF_MONTH, 1);
                final int dayMax = date.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int day = 0; day < dayMax; day++) {
                    double phase = computeMoonPhase(date.get(Calendar.YEAR), mMonthId + 1, day + 1);
                    //Log.i(TAG, "Computed moon phase: " + phase);

                    int phaseValue = ((int) Math.floor(phase)) % 30;
                    //Log.i(TAG, "Discrete phase value: " + phaseValue);

                    mDateStringArray.add(curFormater.format(date.getTime()));

                    mMoonArray.add(IMAGE_LOOKUP[phaseValue]);

                    DayComputation dayComputation = computeDayComputation(
                            date.get(Calendar.YEAR),
                            mMonthId + 1,
                            day + 1
                    );
                    appendDayVisualData(dayComputation, monthVegCache);

                    date.add(Calendar.DAY_OF_MONTH, 1);
                }

                // code runs in a thread
                hostActivity.runOnUiThread(() -> {
                    if (!isAdded()) {
                        return;
                    }
                    calendarAdapter = new CalendarAdapter(getContext(), mDateStringArray,
                            mMoonArray, mSowArray, mCollectArray, mActionArray);
                    listView.setAdapter(calendarAdapter);
             //       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


                    if ((actualMonth == mMonthId) && (actualYear == mYearId)) {
                        listView.setSelection(actualDay - 1);
                        listView.setItemChecked(actualDay - 1, true);
                    }


                    mainLayout.setVisibility(View.VISIBLE);
                    pleaseWait.setVisibility(View.GONE);



                });

            }
        }.start();
    }

    private DayComputation computeDayComputation(int year, int month, int day) {
        DayComputation computation = new DayComputation();

        if (isAscendingMoonNode(year, month, day)) {
            computation.specialType = SPECIAL_ASCENDING;
            return computation;
        }
        if (isDescendingMoonNode(year, month, day)) {
            computation.specialType = SPECIAL_DESCENDING;
            return computation;
        }
        if (isApogee(year, month, day)) {
            computation.specialType = SPECIAL_APOGEE;
            return computation;
        }
        if (isPerigee(year, month, day)) {
            computation.specialType = SPECIAL_PERIGEE;
            return computation;
        }

        if (isDayRacine(year, month, day)) {
            computation.dayKind = DAY_KIND_ROOT;
        } else if (isDayFeuille(year, month, day)) {
            computation.dayKind = DAY_KIND_LEAF;
        } else if (isDayFruit(year, month, day)) {
            computation.dayKind = DAY_KIND_FRUIT;
        }

        return computation;
    }

    private void appendDayVisualData(DayComputation computation, MonthVegCache monthVegCache) {
        switch (computation.specialType) {
            case SPECIAL_ASCENDING:
                mSowArray.add(R.drawable.ic_cancel);
                mCollectArray.add(R.drawable.ic_cancel);
                mActionArray.add(monthVegCache.getSpecialActionText(SPECIAL_ASCENDING));
                return;
            case SPECIAL_DESCENDING:
                mSowArray.add(R.drawable.ic_cancel);
                mCollectArray.add(R.drawable.ic_cancel);
                mActionArray.add(monthVegCache.getSpecialActionText(SPECIAL_DESCENDING));
                return;
            case SPECIAL_APOGEE:
                mSowArray.add(R.drawable.ic_apogee);
                mCollectArray.add(R.drawable.ic_apogee);
                mActionArray.add(monthVegCache.getSpecialActionText(SPECIAL_APOGEE));
                return;
            case SPECIAL_PERIGEE:
                mSowArray.add(R.drawable.ic_perigee);
                mCollectArray.add(R.drawable.ic_perigee);
                mActionArray.add(monthVegCache.getSpecialActionText(SPECIAL_PERIGEE));
                return;
            default:
                break;
        }

        mSowArray.add(monthVegCache.getSowIconForDayKind(computation.dayKind));
        mCollectArray.add(monthVegCache.getCollectIconForDayKind(computation.dayKind));
        mActionArray.add(monthVegCache.getActionForDayKind(computation.dayKind));
    }

    private static final class MonthVegCache {
        private final Context context;
        private final int rootSowIcon;
        private final int rootCollectIcon;
        private final int leafSowIcon;
        private final int leafCollectIcon;
        private final int fruitSowIcon;
        private final int fruitCollectIcon;

        private final String rootSow;
        private final String rootCollect;
        private final String leafSow;
        private final String leafCollect;
        private final String fruitSow;
        private final String fruitCollect;

        MonthVegCache(Context context, int month) {
            this.context = context;
            RootVegPrefs rootVegPrefs = new RootVegPrefs(context);
            if (rootVegPrefs.isRootVegEnabled()) {
                rootSowIcon = RootVegManager.getFirstItemEnabledForSow(context, month);
                rootCollectIcon = RootVegManager.getFirstItemEnabledForCollect(context, month);
                rootSow = RootVegManager.getListForSow(context, month);
                rootCollect = RootVegManager.getListForCollect(context, month);
            } else {
                rootSowIcon = R.drawable.ic_nothing;
                rootCollectIcon = R.drawable.ic_nothing;
                rootSow = "";
                rootCollect = "";
            }

            LeafVegPrefs leafVegPrefs = new LeafVegPrefs(context);
            if (leafVegPrefs.isLeafVegEnabled()) {
                leafSowIcon = LeafVegManager.getFirstItemEnabledForSow(context, month);
                leafCollectIcon = LeafVegManager.getFirstItemEnabledForCollect(context, month);
                leafSow = LeafVegManager.getListForSow(context, month);
                leafCollect = LeafVegManager.getListForCollect(context, month);
            } else {
                leafSowIcon = R.drawable.ic_nothing;
                leafCollectIcon = R.drawable.ic_nothing;
                leafSow = "";
                leafCollect = "";
            }

            FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(context);
            if (fruitVegPrefs.isFruitVegEnabled()) {
                fruitSowIcon = FruitVegManager.getFirstItemEnabledForSow(context, month);
                fruitCollectIcon = FruitVegManager.getFirstItemEnabledForCollect(context, month);
                fruitSow = FruitVegManager.getListForSow(context, month);
                fruitCollect = FruitVegManager.getListForCollect(context, month);
            } else {
                fruitSowIcon = R.drawable.ic_nothing;
                fruitCollectIcon = R.drawable.ic_nothing;
                fruitSow = "";
                fruitCollect = "";
            }
        }

        String getSpecialActionText(int specialType) {
            switch (specialType) {
                case SPECIAL_ASCENDING:
                    return context.getString(R.string.ascending_moon_node);
                case SPECIAL_DESCENDING:
                    return context.getString(R.string.descending_moon_node);
                case SPECIAL_APOGEE:
                    return context.getString(R.string.moon_apogee);
                case SPECIAL_PERIGEE:
                    return context.getString(R.string.moon_perigee);
                default:
                    return "";
            }
        }

        int getSowIconForDayKind(int dayKind) {
            switch (dayKind) {
                case DAY_KIND_ROOT:
                    return rootSowIcon;
                case DAY_KIND_LEAF:
                    return leafSowIcon;
                case DAY_KIND_FRUIT:
                    return fruitSowIcon;
                default:
                    return R.drawable.ic_nothing;
            }
        }

        int getCollectIconForDayKind(int dayKind) {
            switch (dayKind) {
                case DAY_KIND_ROOT:
                    return rootCollectIcon;
                case DAY_KIND_LEAF:
                    return leafCollectIcon;
                case DAY_KIND_FRUIT:
                    return fruitCollectIcon;
                default:
                    return R.drawable.ic_nothing;
            }
        }

        String getActionForDayKind(int dayKind) {
            switch (dayKind) {
                case DAY_KIND_ROOT:
                    return formatAction(rootSow, rootCollect);
                case DAY_KIND_LEAF:
                    return formatAction(leafSow, leafCollect);
                case DAY_KIND_FRUIT:
                    return formatAction(fruitSow, fruitCollect);
                default:
                    return "";
            }
        }

        private String formatAction(String sow, String collect) {
            if (!sow.isEmpty()) {
                String result = context.getString(R.string.action_sow_format, sow);
                if (!collect.isEmpty()) {
                    result = result + "\n" + context.getString(R.string.action_collect_format, collect);
                }
                return result;
            }
            if (!collect.isEmpty()) {
                return context.getString(R.string.action_collect_format, collect);
            }
            return "";
        }
    }

    private void computeResultDayAction(int year, int day) {
        // Clear array first
        mResultArray.clear();
        if (isAscendingMoonNode(year, mMonthId + 1, day + 1)) {
            ResultVegItem item = new ResultVegItem();
            item.setMainMessage(getString(R.string.ascending_moon_node));
            item.setSubMessage(getString(R.string.moon_node_message));
            item.setPictureResId(R.drawable.ic_cancel);
            item.setAction(RESULT_VEG_ITEM_FORBIDDEN);
            mResultArray.add(item);
        } else if (isDescendingMoonNode(year, mMonthId + 1, day + 1)) {
            ResultVegItem item = new ResultVegItem();
            item.setMainMessage(getString(R.string.descending_moon_node));
            item.setSubMessage(getString(R.string.moon_node_message));
            item.setPictureResId(R.drawable.ic_cancel);
            item.setAction(RESULT_VEG_ITEM_FORBIDDEN);
            mResultArray.add(item);
        } else if (isApogee(year, mMonthId + 1, day + 1)) {
            ResultVegItem item = new ResultVegItem();
            item.setMainMessage(getString(R.string.moon_apogee));
            item.setSubMessage(getString(R.string.moon_apogee_message));
            item.setPictureResId(R.drawable.ic_apogee);
            item.setAction(RESULT_VEG_ITEM_FORBIDDEN);
            mResultArray.add(item);
        } else if (isPerigee(year, mMonthId + 1, day + 1)) {
            ResultVegItem item = new ResultVegItem();
            item.setMainMessage(getString(R.string.moon_perigee));
            item.setSubMessage(getString(R.string.moon_perigee_message));
            item.setPictureResId(R.drawable.ic_perigee);
            item.setAction(RESULT_VEG_ITEM_FORBIDDEN);
            mResultArray.add(item);
        } else if (isDayRacine(year, mMonthId + 1, day + 1)) {
            mResultArray = RootVegManager.getResultVegForSow(getContext(), mMonthId);
            ArrayList<ResultVegItem> collectArray = RootVegManager.getResultVegForCollect(getContext(), mMonthId);
            mResultArray.addAll(collectArray);
        } else if (isDayFeuille(year, mMonthId + 1, day + 1)) {
            mResultArray = LeafVegManager.getResultVegForSow(getContext(), mMonthId);
            ArrayList<ResultVegItem> collectArray = LeafVegManager.getResultVegForCollect(getContext(), mMonthId);
            mResultArray.addAll(collectArray);
        } else if (isDayFruit(year, mMonthId + 1, day + 1)) {
            mResultArray = FruitVegManager.getResultVegForSow(getContext(), mMonthId);
            ArrayList<ResultVegItem> collectArray = FruitVegManager.getResultVegForCollect(getContext(), mMonthId);
            mResultArray.addAll(collectArray);
        }
    }

    // Calculs lunaires - délégués à MoonDayCalculator (logique métier découplée de l'UI)

    private double computeMoonPhase(int year, int month, int day) {
        return getCalculator().computeMoonPhase(year, month, day);
    }

    private boolean isAscendingMoonNode(int year, int month, int day) {
        return getCalculator().isMoonNode(MoonDayCalculator.MOON_NODE_ASCENDING_NODE, year, month, day);
    }

    private boolean isDescendingMoonNode(int year, int month, int day) {
        return getCalculator().isMoonNode(MoonDayCalculator.MOON_NODE_DESCENDING_NODE, year, month, day);
    }

    private boolean isApogee(int year, int month, int day) {
        return getCalculator().isMoonNode(MoonDayCalculator.MOON_NODE_APOGEE, year, month, day);
    }

    private boolean isPerigee(int year, int month, int day) {
        return getCalculator().isMoonNode(MoonDayCalculator.MOON_NODE_PERIGEE, year, month, day);
    }

    public boolean isDayRacine(int year, int month, int day) {
        return getCalculator().isDayRacine(year, month, day);
    }

    public boolean isDayFeuille(int year, int month, int day) {
        return getCalculator().isDayFeuille(year, month, day);
    }

    public boolean isDayFruit(int year, int month, int day) {
        return getCalculator().isDayFruit(year, month, day);
    }

    /** Retourne le contexte disponible (Fragment ou externe). */
    private Context getSafeContext() {
        if (mExternalContext != null) return mExternalContext;
        if (mActivity != null) return mActivity.getApplicationContext();
        Context ctx = getContext();
        if (ctx != null) return ctx.getApplicationContext();
        throw new IllegalStateException("Context is not available in FragmentCalendar");
    }

    /** Retourne le calculateur lunaire, en le créant si nécessaire. */
    private MoonDayCalculator getCalculator() {
        if (mMoonDayCalculator == null) {
            mMoonDayCalculator = new MoonDayCalculator(getSafeContext());
        }
        return mMoonDayCalculator;
    }

    private static final int[] IMAGE_LOOKUP = {

            R.drawable.moon0,
            R.drawable.moon1,
            R.drawable.moon2,
            R.drawable.moon3,
            R.drawable.moon4,
            R.drawable.moon5,
            R.drawable.moon6,
            R.drawable.moon7,
            R.drawable.moon8,
            R.drawable.moon9,
            R.drawable.moon10,
            R.drawable.moon11,
            R.drawable.moon12,
            R.drawable.moon13,
            R.drawable.moon14,
            R.drawable.moon15,
            R.drawable.moon16,
            R.drawable.moon17,
            R.drawable.moon18,
            R.drawable.moon19,
            R.drawable.moon20,
            R.drawable.moon21,
            R.drawable.moon22,
            R.drawable.moon23,
            R.drawable.moon24,
            R.drawable.moon25,
            R.drawable.moon26,
            R.drawable.moon27,
            R.drawable.moon28,
            R.drawable.moon29,
    };
}


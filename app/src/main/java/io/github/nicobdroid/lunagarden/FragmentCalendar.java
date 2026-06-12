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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<Integer, Boolean> dayRootCache = new HashMap<>();
    private final Map<Integer, Boolean> dayLeafCache = new HashMap<>();
    private final Map<Integer, Boolean> dayFruitCache = new HashMap<>();
    private final Map<Long, Boolean> moonNodeCache = new HashMap<>();

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
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clearComputationCaches();

        actualYear = Calendar.getInstance().get(Calendar.YEAR);
        actualMonth = Calendar.getInstance().get(Calendar.MONTH);
        actualDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        Bundle args = requireArguments();
        mMonthId = args.getInt("month", 0);
        mYearId = args.getInt("year", 2018);
    }

    private void clearComputationCaches() {
        dayRootCache.clear();
        dayLeafCache.clear();
        dayFruitCache.clear();
        moonNodeCache.clear();
    }

    private static int dayKey(int year, int month, int day) {
        return year * 10_000 + month * 100 + day;
    }

    private static long moonNodeKey(int nodeType, int year, int month, int day) {
        return (((long) nodeType) << 32) | (dayKey(year, month, day) & 0xFFFFFFFFL);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + i);

                if (!mActionArray.get(i).isEmpty()) {
                    Intent intent = new Intent(getContext(), ActivityDay.class);
                    String date = mDateStringArray.get(i) + " " + String.valueOf(mYearId);
                    intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_DATE, date);
                    intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_MOON, mMoonArray.get(i));

                    computeResultDayAction(mYearId, i);
                    intent.putExtra(AppTechnicalKeys.ACTIVITY_EXTRA_LIST_SOW, mResultArray);

                    //TODO


                    startActivity(intent);
                }
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
                hostActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainLayout.setVisibility(View.GONE);
                        pleaseWait.setVisibility(View.VISIBLE);
                    }
                });

                final SimpleDateFormat curFormater = new SimpleDateFormat("MMM dd");
//        DateFormat curFormater = DateFormat.getDateInstance(DateFormat.FULL);
                final GregorianCalendar date = new GregorianCalendar();
//        String[] dateStringArray = new String[date.getActualMaximum(Calendar.DAY_OF_MONTH) + 1];

                mDateStringArray = new ArrayList<String>();
                mMoonArray = new ArrayList<Integer>();
                mSowArray = new ArrayList<Integer>();
                mCollectArray = new ArrayList<Integer>();
                mActionArray = new ArrayList<String>();

                mResultArray = new ArrayList<ResultVegItem>();
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
                hostActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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



                    }
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

    private final class MonthVegCache {
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

    private void computeCollectArray(GregorianCalendar date, int day) {
        if (isAscendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mCollectArray.add(R.drawable.ic_cancel);
        } else if (isDescendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mCollectArray.add(R.drawable.ic_cancel);
        } else if (isApogee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mCollectArray.add(R.drawable.ic_apogee);
        } else if (isPerigee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mCollectArray.add(R.drawable.ic_perigee);
        } else if (isDayRacine(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addRootVegForCollectOnScreen(mMonthId);
        } else if (isDayFeuille(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addLeafVegForCollectOnScreen(mMonthId);
        } else if (isDayFruit(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addFruitVegForCollectOnScreen(mMonthId);
        } else {
            mCollectArray.add(R.drawable.ic_nothing);
        }
    }

    private void computeSowArray(GregorianCalendar date, int day) {
        if (isAscendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mSowArray.add(R.drawable.ic_cancel);
        } else if (isDescendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mSowArray.add(R.drawable.ic_cancel);
        } else if (isApogee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mSowArray.add(R.drawable.ic_apogee);
        } else if (isPerigee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mSowArray.add(R.drawable.ic_perigee);
        } else if (isDayRacine(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addRootVegForSowOnScreen(mMonthId);
        } else if (isDayFeuille(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addLeafVegForSowOnScreen(mMonthId);
        } else if (isDayFruit(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addFruitVegForSowOnScreen(mMonthId);
        } else {
            mSowArray.add(R.drawable.ic_nothing);
        }
    }

    private void computeActionArray(GregorianCalendar date, int day) {
        if (isAscendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mActionArray.add(getString(R.string.ascending_moon_node));
        } else if (isDescendingMoonNode(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mActionArray.add(getString(R.string.descending_moon_node));
        } else if (isApogee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mActionArray.add(getString(R.string.moon_apogee));
        } else if (isPerigee(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            mActionArray.add(getString(R.string.moon_perigee));
        } else if (isDayRacine(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addRootVegAction(mMonthId);
        } else if (isDayFeuille(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addLeafVegAction(mMonthId);
        } else if (isDayFruit(date.get(Calendar.YEAR), mMonthId + 1, day + 1)) {
            addFruitVegAction(mMonthId);
        } else {
            mActionArray.add("");
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

    private void addRootVegAction(int month) {
        String strSow = "";
        String strCollect = "";
        String strResult = "";
        RootVegPrefs rootVegPrefs = new RootVegPrefs(requireContext());
        if (rootVegPrefs.isRootVegEnabled()) {
            strSow = RootVegManager.getListForSow(getContext(), month);
            strCollect = RootVegManager.getListForCollect(getContext(), month);
            if (!strSow.isEmpty()) {
                strResult = getString(R.string.action_sow_format, strSow);
                if (!strCollect.isEmpty()) {
                    strResult = strResult + "\n" + getString(R.string.action_collect_format, strCollect);
                }
            } else if (!strCollect.isEmpty()) {
                strResult = getString(R.string.action_collect_format, strCollect);
            }


        }
        mActionArray.add(strResult);
    }

    private void addRootVegForSowOnScreen(int month) {
        RootVegPrefs rootVegPrefs = new RootVegPrefs(requireContext());
        if (rootVegPrefs.isRootVegEnabled()) {
            mSowArray.add(RootVegManager.getFirstItemEnabledForSow(getContext(), month));
        } else {
            mSowArray.add(R.drawable.ic_nothing);
        }
    }

    private void addRootVegForCollectOnScreen(int month) {
        RootVegPrefs rootVegPrefs = new RootVegPrefs(requireContext());
        if (rootVegPrefs.isRootVegEnabled()) {
            mCollectArray.add(RootVegManager.getFirstItemEnabledForCollect(getContext(), month));
        } else {
            mCollectArray.add(R.drawable.ic_nothing);
        }
    }

    private void addLeafVegForSowOnScreen(int month) {
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(requireContext());
        if (leafVegPrefs.isLeafVegEnabled()) {
            mSowArray.add(LeafVegManager.getFirstItemEnabledForSow(getContext(), month));
        } else {
            mSowArray.add(R.drawable.ic_nothing);
        }
    }

    private void addLeafVegForCollectOnScreen(int month) {
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(requireContext());
        if (leafVegPrefs.isLeafVegEnabled()) {
            mCollectArray.add(LeafVegManager.getFirstItemEnabledForCollect(getContext(), month));
        } else {
            mCollectArray.add(R.drawable.ic_nothing);
        }
    }

    private void addLeafVegAction(int month) {
        String strSow = "";
        String strCollect = "";
        String strResult = "";
        LeafVegPrefs leafVegPrefs = new LeafVegPrefs(requireContext());
        if (leafVegPrefs.isLeafVegEnabled()) {
            strSow = LeafVegManager.getListForSow(getContext(), month);
            strCollect = LeafVegManager.getListForCollect(getContext(), month);
            if (!strSow.isEmpty()) {
                strResult = getString(R.string.action_sow_format, strSow);
                if (!strCollect.isEmpty()) {
                    strResult = strResult + "\n" + getString(R.string.action_collect_format, strCollect);
                }
            } else if (!strCollect.isEmpty()) {
                strResult = getString(R.string.action_collect_format, strCollect);
            }
        }

        //mResultArray = LeafVegManager.getResultVegForSow(getContext(), month);
        //ArrayList<ResultVegItem> collectArray = LeafVegManager.getResultVegForCollect(getContext(), month);
        //mResultArray.addAll(collectArray);

        mActionArray.add(strResult);
    }

    private void addFruitVegForSowOnScreen(int month) {
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(requireContext());
        if (fruitVegPrefs.isFruitVegEnabled()) {
            mSowArray.add(FruitVegManager.getFirstItemEnabledForSow(getContext(), month));
        } else {
            mSowArray.add(R.drawable.ic_nothing);
        }
    }

    private void addFruitVegForCollectOnScreen(int month) {
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(requireContext());
        if (fruitVegPrefs.isFruitVegEnabled()) {
            mCollectArray.add(FruitVegManager.getFirstItemEnabledForCollect(getContext(), month));
        } else {
            mCollectArray.add(R.drawable.ic_nothing);
        }
    }

    private void addFruitVegAction(int month) {
        String strSow = "";
        String strCollect = "";
        String strResult = "";
        FruitVegPrefs fruitVegPrefs = new FruitVegPrefs(requireContext());
        if (fruitVegPrefs.isFruitVegEnabled()) {
            strSow = FruitVegManager.getListForSow(getContext(), month);
            strCollect = FruitVegManager.getListForCollect(getContext(), month);
            if (!strSow.isEmpty()) {
                strResult = getString(R.string.action_sow_format, strSow);
                if (!strCollect.isEmpty()) {
                    strResult = strResult + "\n" + getString(R.string.action_collect_format, strCollect);
                }
            } else if (!strCollect.isEmpty()) {
                strResult = getString(R.string.action_collect_format, strCollect);
            }
        }
        //mResultArray = FruitVegManager.getResultVegForSow(getContext(), month);
        //ArrayList<ResultVegItem> collectArray = FruitVegManager.getResultVegForCollect(getContext(), month);
        //mResultArray.addAll(collectArray);

        mActionArray.add(strResult);
    }


    // Computes moon phase based upon Bradley E. Schaefer's moon phase algorithm.
    private double computeMoonPhase(int year, int month, int day) {
        //Log.i(TAG, "computeMoonPhase: year=" + year + " month=" + month + " day=" + day);

        // Convert the year into the format expected by the algorithm.
        double transformedYear = year - Math.floor((12 - month) / 10);
        //Log.i(TAG, "transformedYear: " + transformedYear);

        // Convert the month into the format expected by the algorithm.
        int transformedMonth = month + 9;
        if (transformedMonth >= 12) {
            transformedMonth = transformedMonth - 12;
        }
        //Log.i(TAG, "transformedMonth: " + transformedMonth);

        // Logic to compute moon phase as a fraction between 0 and 1
        double term1 = Math.floor(365.25 * (transformedYear + 4712));
        double term2 = Math.floor(30.6 * transformedMonth + 0.5);
        double term3 = Math.floor(Math.floor((transformedYear / 100) + 49) * 0.75) - 38;

        double intermediate = term1 + term2 + day + 59;
        if (intermediate > 2299160) {
            intermediate = intermediate - term3;
        }
        //Log.i(TAG, "intermediate: " + intermediate);

        double normalizedPhase = (intermediate - 2451550.1) / MOON_PHASE_LENGTH;
        normalizedPhase = normalizedPhase - Math.floor(normalizedPhase);
        if (normalizedPhase < 0) {
            normalizedPhase = normalizedPhase + 1;
        }
        //Log.i(TAG, "normalizedPhase: " + normalizedPhase);

        // Return the result as a value between 0 and MOON_PHASE_LENGTH
        return normalizedPhase * MOON_PHASE_LENGTH;
    }

    private boolean isAscendingMoonNode(int year, int month, int day) {
        return isMoonNode(MOON_NODE_ASCENDING_NODE, year, month, day);
    }

    private boolean isDescendingMoonNode(int year, int month, int day) {
        return isMoonNode(MOON_NODE_DESCENDING_NODE, year, month, day);
    }

    private boolean isApogee(int year, int month, int day) {
        return isMoonNode(MOON_NODE_APOGEE, year, month, day);
    }

    private boolean isPerigee(int year, int month, int day) {
        return isMoonNode(MOON_NODE_PERIGEE, year, month, day);
    }

    private String getLastRefDayRacine(int dayRacine) {
        // 10 jours racine max par mois
        String strResult;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        strResult = saveMoonInfo.readDayRacine(dayRacine);
        return strResult;
    }

    private String getLastRefDayFeuille(int dayFeuille) {
        // 10 jours racine max par mois
        String strResult;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        strResult = saveMoonInfo.readDayFeuille(dayFeuille);
        return strResult;
    }

    private String getLastRefDayFruit(int dayFruit) {
        String strResult;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        strResult = saveMoonInfo.readDayFruit(dayFruit);
        return strResult;
    }

    private String getLastRefMoonNode(int nodeType) {
        String strResult;
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:
                strResult = saveMoonInfo.readAscendingMoonNodeDate();
                break;
            case MOON_NODE_DESCENDING_NODE:
                strResult = saveMoonInfo.readDescendingMoonNodeDate();
                break;
            case MOON_NODE_APOGEE:
                strResult = saveMoonInfo.readApogeeMoonDate();
                break;
            case MOON_NODE_PERIGEE:
                strResult = saveMoonInfo.readPerigeeMoonDate();
                break;
            default:
                strResult = saveMoonInfo.readAscendingMoonNodeDate();
                break;
        }
        return strResult;
    }

    private void saveLastRefMoonNode(int nodeType, String strValue) {
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());

        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:
                saveMoonInfo.saveAscendingMoonNodeDate(strValue);
                break;
            case MOON_NODE_DESCENDING_NODE:
                saveMoonInfo.saveDescendingMoonNodeDate(strValue);
                break;
            case MOON_NODE_APOGEE:
                saveMoonInfo.saveApogeeMoonDate(strValue);
                break;
            case MOON_NODE_PERIGEE:
                saveMoonInfo.savePerigeeMoonDate(strValue);
                break;
            default:
                break;
        }
    }

    private void saveLastRefDayRacine(int dayRacine, String strValue) {
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        saveMoonInfo.saveDayRacine(dayRacine, strValue);
    }

    private void saveLastRefDayFeuille(int dayFeuille, String strValue) {
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        saveMoonInfo.saveDayFeuille(dayFeuille, strValue);
    }

    private void saveLastRefDayFruit(int dayFruit, String strValue) {
        SaveMoonInfo saveMoonInfo = new SaveMoonInfo(getSafeContext());
        saveMoonInfo.saveDayFruit(dayFruit, strValue);
    }

    private Context getSafeContext() {
        if (mExternalContext != null) {
            return mExternalContext;
        }
        if (mActivity != null) {
            return mActivity.getApplicationContext();
        }
        Context fragmentContext = getContext();
        if (fragmentContext != null) {
            return fragmentContext.getApplicationContext();
        }
        throw new IllegalStateException("Context is not available in FragmentCalendar");
    }

    public boolean isDayRacine(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cachedResult = dayRootCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean bResult = false;

        for (int dayRacine = 0; dayRacine < 10; dayRacine++) {
            String strDateRef1 = getLastRefDayRacine(dayRacine); // seulement le 1er pour l'instant

            DateFormat dateFormatNodeRef = new SimpleDateFormat("d_M_yyyy");
            try {
                Date dateNodeRef1 = (Date) dateFormatNodeRef.parse(strDateRef1);
                Date newDateRef1 = dateNodeRef1;// = (Date)addTime(dateNodeRef1, nodeType);
                for (int i = 0; i < 26; i++) {

                    newDateRef1 = (Date) addTime(newDateRef1, MOON_NODE_ASCENDING_NODE);
                    Calendar calendarRef1 = Calendar.getInstance();
                    calendarRef1.setTime(newDateRef1);

                    // sauvegarder si date plus ancienne que le jour d'aujourdhui - 3 mois
                    Date referenceDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(referenceDate);
                    c.add(Calendar.MONTH, -3);
                    if (c.after(calendarRef1)) {
                        String formatted = dateFormatNodeRef.format(calendarRef1.getTime());
                        saveLastRefDayRacine(dayRacine, formatted);
                    }

                    if (calendarRef1.get(Calendar.DAY_OF_MONTH) >= day) {
                        if (calendarRef1.get(Calendar.MONTH) + 1 >= month) {
                            if (calendarRef1.get(Calendar.YEAR) >= year) {

                                if ((calendarRef1.get(Calendar.DAY_OF_MONTH) == day) &&
                                        (calendarRef1.get(Calendar.MONTH) + 1 == month) &&
                                        (calendarRef1.get(Calendar.YEAR) == year)) {
                                    bResult = true;
                                }

                                // stop
                                break;
                            }
                        }
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dayRootCache.put(cacheKey, bResult);
        return bResult;
    }

    public boolean isDayFeuille(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cachedResult = dayLeafCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean bResult = false;

        for (int dayFeuille = 0; dayFeuille < 10; dayFeuille++) {
            String strDateRef1 = getLastRefDayFeuille(dayFeuille); // seulement le 1er pour l'instant

            DateFormat dateFormatNodeRef = new SimpleDateFormat("d_M_yyyy");
            try {
                Date dateNodeRef1 = (Date) dateFormatNodeRef.parse(strDateRef1);
                Date newDateRef1 = dateNodeRef1;// = (Date)addTime(dateNodeRef1, nodeType);
                for (int i = 0; i < 26; i++) {

                    newDateRef1 = (Date) addTime(newDateRef1, MOON_NODE_ASCENDING_NODE);
                    Calendar calendarRef1 = Calendar.getInstance();
                    calendarRef1.setTime(newDateRef1);

                    // sauvegarder si date plus ancienne que le jour d'aujourdhui - 3 mois
                    Date referenceDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(referenceDate);
                    c.add(Calendar.MONTH, -3);
                    if (c.after(calendarRef1)) {
                        String formatted = dateFormatNodeRef.format(calendarRef1.getTime());
                        saveLastRefDayFeuille(dayFeuille, formatted);
                    }

                    if (calendarRef1.get(Calendar.DAY_OF_MONTH) >= day) {
                        if (calendarRef1.get(Calendar.MONTH) + 1 >= month) {
                            if (calendarRef1.get(Calendar.YEAR) >= year) {

                                if ((calendarRef1.get(Calendar.DAY_OF_MONTH) == day) &&
                                        (calendarRef1.get(Calendar.MONTH) + 1 == month) &&
                                        (calendarRef1.get(Calendar.YEAR) == year)) {
                                    bResult = true;
                                }

                                // stop
                                break;
                            }
                        }
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dayLeafCache.put(cacheKey, bResult);
        return bResult;
    }

    public boolean isDayFruit(int year, int month, int day) {
        int cacheKey = dayKey(year, month, day);
        Boolean cachedResult = dayFruitCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean bResult = false;

        for (int dayFruit = 0; dayFruit < 10; dayFruit++) {
            String strDateRef1 = getLastRefDayFruit(dayFruit); // seulement le 1er pour l'instant

            DateFormat dateFormatNodeRef = new SimpleDateFormat("d_M_yyyy");
            try {
                Date dateNodeRef1 = (Date) dateFormatNodeRef.parse(strDateRef1);
                Date newDateRef1 = dateNodeRef1;// = (Date)addTime(dateNodeRef1, nodeType);
                for (int m = 0; m < 26; m++) {

                    newDateRef1 = (Date) addTime(newDateRef1, MOON_NODE_ASCENDING_NODE);
                    Calendar calendarRef1 = Calendar.getInstance();
                    calendarRef1.setTime(newDateRef1);

                    // sauvegarder si date plus ancienne que le jour d'aujourdhui - 3 mois
                    Date referenceDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(referenceDate);
                    c.add(Calendar.MONTH, -3);
                    if (c.after(calendarRef1)) {
                        String formatted = dateFormatNodeRef.format(calendarRef1.getTime());
                        saveLastRefDayFruit(dayFruit, formatted);
                    }

                    if (calendarRef1.get(Calendar.DAY_OF_MONTH) >= day) {
                        if (calendarRef1.get(Calendar.MONTH) + 1 >= month) {
                            if (calendarRef1.get(Calendar.YEAR) >= year) {

                                if ((calendarRef1.get(Calendar.DAY_OF_MONTH) == day) &&
                                        (calendarRef1.get(Calendar.MONTH) + 1 == month) &&
                                        (calendarRef1.get(Calendar.YEAR) == year)) {
                                    bResult = true;
                                }

                                // stop
                                break;
                            }
                        }
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dayFruitCache.put(cacheKey, bResult);
        return bResult;
    }

    private boolean isMoonNode(int nodeType, int year, int month, int day) {
        long cacheKey = moonNodeKey(nodeType, year, month, day);
        Boolean cachedResult = moonNodeCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean bResult = false;
        String strDateMoonNodeRef1 = getLastRefMoonNode(nodeType);

        DateFormat dateFormatNodeRef = new SimpleDateFormat("d_M_yyyy_HH:mm:ss");
        try {
            Date newDateRef1 = (Date) dateFormatNodeRef.parse(strDateMoonNodeRef1);// = (Date)addTime(dateNodeRef1, nodeType);
            for (int i = 0; i < 26; i++) {

                newDateRef1 = (Date) addTime(newDateRef1, nodeType);
                Calendar calendarRef1 = Calendar.getInstance();
                calendarRef1.setTime(newDateRef1);

                // sauvegarder si date plus ancienne que le jour d'aujourdhui - 3 mois
                Date referenceDate = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(referenceDate);
                c.add(Calendar.MONTH, -3);
                if (c.after(calendarRef1)) {
                    String formatted = dateFormatNodeRef.format(calendarRef1.getTime());
                    saveLastRefMoonNode(nodeType, formatted);
                }

                if (calendarRef1.get(Calendar.DAY_OF_MONTH) >= day) {
                    if (calendarRef1.get(Calendar.MONTH) + 1 >= month) {
                        if (calendarRef1.get(Calendar.YEAR) >= year) {

                            if ((calendarRef1.get(Calendar.DAY_OF_MONTH) == day) &&
                                    (calendarRef1.get(Calendar.MONTH) + 1 == month) &&
                                    (calendarRef1.get(Calendar.YEAR) == year)) {
                                bResult = true;
                            }
                            // stop
                            break;
                        }
                    }
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        moonNodeCache.put(cacheKey, bResult);
        return bResult;
    }

    private static Date addTime(Date date, int nodeType) {
        int days, hours, minutes, seconds;
        switch (nodeType) {
            case MOON_NODE_ASCENDING_NODE:
            case MOON_NODE_DESCENDING_NODE:
                //addTime(dateNodeRef1, 27, 5, 5, 36);
                days = 27;
                hours = 5;
                minutes = 5;
                seconds = 36;
                break;
            case MOON_NODE_APOGEE:
            case MOON_NODE_PERIGEE:
            default:
                days = 27;
                hours = 13;
                minutes = 18;
                seconds = 33;
                break;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
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


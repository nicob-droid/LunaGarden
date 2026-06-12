package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> dates, actions;
    private ArrayList<Integer> moons;
    private ArrayList<Integer> semis;
    private ArrayList<Integer> collects;
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, ArrayList<String> dates, ArrayList<Integer> moons,
                           ArrayList<Integer> semis, ArrayList<Integer> collects, ArrayList<String> actions) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.dates = dates;
        this.moons = moons;
        this.semis = semis;
        this.collects = collects;
        this.actions = actions;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup vg;

        if (convertView != null) {
            vg = (ViewGroup) convertView;
        } else {
            vg = (ViewGroup) inflater.inflate(R.layout.calendar_element, parent, false);
        }

        String date = dates.get(position);
        String action = actions.get(position);
        int iMoon = moons.get(position);
        int iWork = semis.get(position);
        int iCollect = collects.get(position);
    //    String strAction = actions.get(position);
        final TextView tvDate =  vg.findViewById(R.id.date);
        final TextView tvAction = vg.findViewById(R.id.resume);
        final ImageView ivMoon = vg.findViewById(R.id.moon);
        final ImageView ivWork = vg.findViewById(R.id.semis);
        final ImageView ivCollect = vg.findViewById(R.id.collects);

        ivMoon.setImageResource(iMoon);
        ivWork.setImageResource(iWork);
        tvDate.setText(date);
        tvAction.setText(action);
        ivCollect.setImageResource(iCollect);

        return vg;
    }
}


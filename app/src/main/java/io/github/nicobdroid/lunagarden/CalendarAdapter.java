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
    private final Context context;
    private final ArrayList<String> dates;
    private final ArrayList<String> actions;
    private final ArrayList<Integer> moons;
    private final ArrayList<Integer> semis;
    private final ArrayList<Integer> collects;
    private final LayoutInflater inflater;

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
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.calendar_element, parent, false);
            holder = new ViewHolder();
            holder.tvDate = view.findViewById(R.id.date);
            holder.tvAction = view.findViewById(R.id.resume);
            holder.ivMoon = view.findViewById(R.id.moon);
            holder.ivWork = view.findViewById(R.id.semis);
            holder.ivCollect = view.findViewById(R.id.collects);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String date = dates.get(position);
        String action = actions.get(position);
        int iMoon = moons.get(position);
        int iWork = semis.get(position);
        int iCollect = collects.get(position);
        holder.ivMoon.setImageResource(iMoon);
        holder.ivWork.setImageResource(iWork);
        holder.tvDate.setText(date);
        holder.tvAction.setText(action);
        holder.ivCollect.setImageResource(iCollect);
        holder.ivMoon.setContentDescription(context.getString(R.string.a11y_moon_phase_for_date, date));
        holder.ivWork.setContentDescription(context.getString(R.string.a11y_sowing_for_date, date));
        holder.ivCollect.setContentDescription(context.getString(R.string.a11y_harvest_for_date, date));

        return view;
    }

    private static final class ViewHolder {
        TextView tvDate;
        TextView tvAction;
        ImageView ivMoon;
        ImageView ivWork;
        ImageView ivCollect;
    }
}


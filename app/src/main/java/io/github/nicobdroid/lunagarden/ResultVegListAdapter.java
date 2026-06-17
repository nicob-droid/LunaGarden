package io.github.nicobdroid.lunagarden;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultVegListAdapter extends BaseAdapter {
    private static final int COLOR_SOW = 0xFF00FF99;
    private static final int COLOR_COLLECT = 0xFFFF9900;
    private static final int COLOR_FORBIDDEN = 0xFFFF9999;

    // The ArrayList of Users<MessageItem>
    private ArrayList<ResultVegItem> mItemsList;

    // The LayoutInflater to holds layout inflater to inflate list item.
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;


    public ResultVegListAdapter(Context context, ArrayList<ResultVegItem> list) {
        mContext = context;
        mItemsList = list;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setItemlist(ArrayList<ResultVegItem> list) {
        mItemsList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemsList != null ? mItemsList.size() : 0;
    }

    @Override
    public ResultVegItem getItem(int position) {
        if (mItemsList == null || position < 0 || position >= mItemsList.size()) {
            return null;
        }
        return mItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            ViewHolder viewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.result_list_layout, parent,
                    false);
            viewHolder.header_textview = view
                    .findViewById(R.id.header_textview);
            viewHolder.sub_textview = view
                    .findViewById(R.id.sub_textview);
            viewHolder.profile_imageview = view
                    .findViewById(R.id.profile_imageview);
            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        final ResultVegItem item = getItem(position);
        if (item != null && viewHolder != null) {
            viewHolder.header_textview.setText(item.getMainMessage());
            viewHolder.sub_textview.setText(item.getSubMessage());
            viewHolder.profile_imageview.setBackgroundResource(item
                    .getPictureResId());
            viewHolder.profile_imageview.setContentDescription(
                    mContext.getString(R.string.a11y_result_image_named, item.getMainMessage())
            );

            switch (item.getAction()) {
                case ResultVegItem.RESULT_VEG_ITEM_SOW:
                    viewHolder.header_textview.setTextColor(COLOR_SOW);
                    break;
                case ResultVegItem.RESULT_VEG_ITEM_COLLECT:
                    viewHolder.header_textview.setTextColor(COLOR_COLLECT);
                    break;
                case ResultVegItem.RESULT_VEG_ITEM_FORBIDDEN:
                    viewHolder.header_textview.setTextColor(COLOR_FORBIDDEN);
                    break;
                default:
                    break;
            }
        }



        return view;
    }

    public static class ViewHolder {
        TextView header_textview;
        TextView sub_textview;
        ImageView profile_imageview;
    }

}


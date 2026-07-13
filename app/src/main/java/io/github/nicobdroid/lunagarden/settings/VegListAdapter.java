package io.github.nicobdroid.lunagarden.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import io.github.nicobdroid.lunagarden.R;

public class VegListAdapter extends BaseAdapter {

    // The ArrayList of Users<MessageItem>
    private ArrayList<VegItem> mItemsList;

    // The LayoutInflater to holds layout inflater to inflate list item.
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    // The OnMessageClickListener of listener.
    private OnFruitItemClickListener listener;


    public VegListAdapter(Context context, ArrayList<VegItem> list) {
        mContext = context;
        mItemsList = list;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setItemlist(ArrayList<VegItem> list) {
        mItemsList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mItemsList != null) {
            return mItemsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public VegItem getItem(int position) {
        VegItem item = null;
        if (mItemsList != null && !mItemsList.isEmpty()) {
            item = mItemsList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnFruitClickListener(OnFruitItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface to provide click events to the required classes.
     *
     * @author Amruta.Doye
     *
     */
    public interface OnFruitItemClickListener {
        void onCheckboxClicked(int position, VegItem item);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.fruit_item_layout, null);
            new ViewHolder(convertView);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final VegItem item = getItem(position);
        if (item != null && viewHolder != null) {
            viewHolder.header_textview.setText(item.getFruitname());
            viewHolder.sub_textview.setText(item.getMessage());
            viewHolder.profile_imageview.setBackgroundResource(item
                    .getPictureResId());
            viewHolder.profile_imageview.setContentDescription(
                    mContext.getString(R.string.a11y_vegetable_image_named, item.getFruitname())
            );

            viewHolder.checkbox.setTag(position);
            viewHolder.checkbox.setChecked(item.isCheckboxChecked());
            viewHolder.checkbox.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onCheckboxClicked(position, item);
                }
            });

            // Le clic sur toute la ligne declenche la meme action que la checkbox.
            convertView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onCheckboxClicked(position, item);
                }
            });

        }
        return convertView;
    }

    /**
     * View holder class to hold the views to bind on list view.
     *
     * @author Amruta.Doye
     *
     */
    public static class ViewHolder {
        TextView header_textview;
        TextView sub_textview;
        ImageView profile_imageview;
        CheckBox checkbox;

        public ViewHolder(View v) {
            header_textview = v.findViewById(R.id.header_textview);
            sub_textview = v.findViewById(R.id.sub_textview);
            profile_imageview = v.findViewById(R.id.profile_imageview);
            checkbox = v.findViewById(R.id.checkbox_imageview);
            v.setTag(this);
        }
    }

}

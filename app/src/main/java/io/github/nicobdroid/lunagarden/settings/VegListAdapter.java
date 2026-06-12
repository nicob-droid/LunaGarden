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
    private LayoutInflater mLayoutInflater;

    // The OnMessageClickListener of listener.
    private OnFruitItemClickListener listener;

    /**
     * Constructor.
     *
     * @param context
     * @param list
     */
    public VegListAdapter(Context context, ArrayList<VegItem> list) {
        mItemsList = list;
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /**
     * Method to set item list and notify adapter to update its views.
     *
     * @param list
     */
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
        if (mItemsList != null && mItemsList.size() > 0) {
            item = mItemsList.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Method to set the listener class object.
     *
     * @param listener
     */
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

        /**
         * The method declaration for user selected. This method will be fired
         * when user click on check/uncheck the checkbox on the list item.
         *
         * @param position
         * @param item
         */
        public void onCheckboxClicked(int position, VegItem item);

    }

    /**
     *
     * @return update list from array list.
     */
    public ArrayList<VegItem> getUpdatedList() {
        return mItemsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.fruit_item_layout, null);
            new ViewHolder(convertView, listener);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final VegItem item = getItem(position);
        if (item != null && viewHolder != null) {
            viewHolder.header_textview.setText(item.getFruitname());
            viewHolder.sub_textview.setText(item.getMessage());
            viewHolder.profile_imageview.setBackgroundResource(item
                    .getPictureResId());

            viewHolder.checkbox.setTag(position);
            viewHolder.checkbox.setChecked(item.isCheckboxChecked());
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
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

        public ViewHolder(View v, OnFruitItemClickListener listener) {
            header_textview = (TextView) v.findViewById(R.id.header_textview);
            sub_textview = (TextView) v.findViewById(R.id.sub_textview);
            profile_imageview = (ImageView) v.findViewById(R.id.profile_imageview);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox_imageview);
            v.setTag(this);
        }
    }

}

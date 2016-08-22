package com.livepost.javiergzzr.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.livepost.javiergzzr.livepost.R;
import com.livepost.javiergzzr.objects.Session;

import java.util.List;

public class ChatListOfflineAdapter extends ArrayAdapter<Session> {

    private List<Session> mItems;
    public ChatListOfflineAdapter(Context context, int resource, List<Session> items) {
        super(context, resource, items);
        mItems = items;
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Session getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_session, null);
        }

        Session s = getItem(position);
        Log.d("LOL", s.getCategory());
        if (s != null) {
            TextView txtTitle = (TextView) v.findViewById(R.id.title);
            TextView txtCategory = (TextView) v.findViewById(R.id.category);
            if (txtTitle != null) {
                txtTitle.setText(s.getPicture());
            }

            if (txtCategory != null) {
                txtCategory.setText(s.getTitle());
            }
        }

        return v;
    }
}

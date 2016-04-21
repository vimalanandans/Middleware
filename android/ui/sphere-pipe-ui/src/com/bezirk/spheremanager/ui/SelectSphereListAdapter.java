package com.bezirk.spheremanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;

import java.util.List;

public class SelectSphereListAdapter extends ArrayAdapter<AbstractSphereListItem> {

    private LayoutInflater inflater;

    public SelectSphereListAdapter(Context context,
                                   List<AbstractSphereListItem> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return ListItems.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getViewSelectSphere(inflater, null);
    }

    public enum ListItems {
        SPHERE_ITEM, ACTION_ITEM, HEADER_ITEM
    }

}

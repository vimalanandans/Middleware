package com.bezirk.spheremanager.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;

public class SphereListAdapter extends ArrayAdapter<AbstractSphereListItem> {

	public enum ListItems {
		SPHERE_ITEM, ACTION_ITEM, HEADER_ITEM
	}

	private LayoutInflater inflater;

	public SphereListAdapter(Context context,
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
		return getItem(position).getView(inflater, null);
	}

}

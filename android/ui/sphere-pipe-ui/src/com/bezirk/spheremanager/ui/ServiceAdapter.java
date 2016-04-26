package com.bezirk.spheremanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.spheremanager.R;

import java.util.List;

public class ServiceAdapter extends ArrayAdapter<BezirkZirkInfo> {

    private final List<BezirkZirkInfo> service;
    private LayoutInflater inflater;
    private SharedPreferences sharedPrefs;

    public ServiceAdapter(Context context, List<BezirkZirkInfo> service) {
        super(context, 0, service);
        this.service = service;
        inflater = LayoutInflater.from(context);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        BezirkZirkInfo item = service.get(position);
        view = (View) inflater.inflate(R.layout.layout_service_entry,
                parent, false);

        TextView service_name = (TextView) view.findViewById(R.id.service_name);

        String temp = sharedPrefs.getString(item.getZirkId(), null);
        service_name.setText(temp == null ? item.getZirkName() : temp);

		/*CheckBox service_active = (CheckBox) view
                .findViewById(R.id.check_service);*/

		/*if (item.isActive()) {
			service_active.setChecked(true);
		} else {
			service_active.setChecked(false);
		}*/

        return view;
    }

}

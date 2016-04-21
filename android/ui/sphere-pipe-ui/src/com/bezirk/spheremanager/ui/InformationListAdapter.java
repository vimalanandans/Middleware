package com.bezirk.spheremanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.listitems.AbstractInformationListItem;
import com.bezirk.spheremanager.ui.listitems.InformationListItem;

import java.util.List;

public class InformationListAdapter extends ArrayAdapter<AbstractInformationListItem> {

    public static final String TAG = "InformationListAdapter";
    private final List<AbstractInformationListItem> information;
    private LayoutInflater inflater;
    private String filterSetting;

    public InformationListAdapter(Context context,
                                  List<AbstractInformationListItem> information, String filterSetting) {
        super(context, 0, information);
        this.information = information;
        this.filterSetting = filterSetting;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final InformationListItem item = (InformationListItem) getItem(position);
        View view = getItem(position).getView(inflater, null);
        final CheckBox information_active = (CheckBox) view
                .findViewById(R.id.check_information);

        // FOR USABILITY STUDY: if we test version 1.x we need to make information not editable
        //information_active.setEnabled(false);
//		information_active.setVisibility(view.GONE);
        return view;
    }

}

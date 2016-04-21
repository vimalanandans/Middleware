package com.bezirk.spheremanager.ui.listitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface AbstractDeviceListItem {

    public String getId();

    public int getViewType();

    public View getView(LayoutInflater layoutInflater, ViewGroup parent);
}

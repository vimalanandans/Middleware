package com.bosch.upa.spheremanager.ui.listitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface AbstractSphereListItem {

	public String getId();
	
	public int getViewType();
    public View getView(LayoutInflater layoutInflater, ViewGroup parent);
    public View getViewSelectSphere(LayoutInflater layoutInflater, ViewGroup parent);
}

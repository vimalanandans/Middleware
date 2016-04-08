package com.bosch.upa.spheremanager.ui.listitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;

public class InformationListItem implements AbstractInformationListItem {
	public static final String TAG = "InformationListItem";
	private final String informationName;
	private final String informationReason;
	private boolean isActive;

	public enum InformationDirection {
		INBOUND, 
		OUTBOUND 
	}
	
	public InformationListItem(String informationName,
			String informationReason, boolean isActive) {
		super();
		this.informationName = informationName;
		this.informationReason = informationReason;
		this.isActive = isActive;
	}

	
	public boolean isActive() {
		return isActive;
	}


	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
		View view;
		view = (View) layoutInflater.inflate(R.layout.layout_information_entry,
				parent);
		TextView textViewName = (TextView) view.findViewById(R.id.information_name);
		textViewName.setText(informationName);
		TextView textViewReason = (TextView) view
				.findViewById(R.id.information_reason);
		textViewReason.setText(informationReason);

		final CheckBox information_active = (CheckBox) view
				.findViewById(R.id.check_information);

		if (isActive()) {
			information_active.setChecked(true);
		} else {
			information_active.setChecked(false);
		}



		return view;
	}

}

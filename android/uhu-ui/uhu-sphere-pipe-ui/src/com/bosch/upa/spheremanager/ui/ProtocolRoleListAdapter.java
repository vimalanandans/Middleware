package com.bosch.upa.spheremanager.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;
import com.bosch.upa.spheremanager.ui.listitems.ProtocolItem;

public class ProtocolRoleListAdapter extends ArrayAdapter<ProtocolItem> {

	private final List<ProtocolItem> policies;

	public static final String TAG = "PolicyListAdapter";
	private LayoutInflater inflater;
	private String filterSetting;
	

	public ProtocolRoleListAdapter(Context context,
			List<ProtocolItem> policies, String filterSetting) {
		super(context, 0, policies);
		this.policies = policies;
		this.filterSetting = filterSetting;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//Iterates Protocol Roles
		final ProtocolItem item = (ProtocolItem) getItem(position);
		//View view = getItem(position).getView(inflater, null);
		//try to put getView here:
		
		View view;
		view = (View) inflater.inflate(R.layout.layout_policy_entry,
				parent, false);
		TextView textViewName = (TextView) view.findViewById(R.id.policy_name);
		textViewName.setText(item.getProtocolName());
		TextView textViewReason = (TextView) view
				.findViewById(R.id.policy_reason);
		//textViewReason.setText(item.getDescription());
		textViewReason.setVisibility(view.GONE);
		TextView textViewChanged = (TextView) view
				.findViewById(R.id.policy_changed);
		textViewChanged.setVisibility(view.GONE);
		final CheckBox policy_active = (CheckBox) view
				.findViewById(R.id.check_policy);

		if (item.isActive()) {
			policy_active.setChecked(true);
		} else {
			policy_active.setChecked(false);
		}

		if (item.isNew()) {
			TextView textViewNew = (TextView) view
					.findViewById(R.id.policy_changed);
			textViewNew.setText("New!");
		}
		//end.get View.
		

//		policy_active.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				policy_active.setChecked(false);
//				policy_active.setClickable(false);
//				
//			}
//		});
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				policy_active.setChecked(false);
//				policy_active.setClickable(false);
				if (policy_active.isChecked()) {
					policy_active.setChecked(false);
					item.setActive(false);
					
//					if (filterSetting.equals("inbound")) {
//						DummyContent.policyListInbound.set(position, item);				
//						} else {
//						DummyContent.policyListOutbound.set(position, item);	
//					}
				} else {
					policy_active.setChecked(true);
					item.setActive(true);
//					if (filterSetting.equals("inbound")) {
//						DummyContent.policyListInbound.set(position, item);				
//						} else {
//						DummyContent.policyListOutbound.set(position, item);	
//					}
				}

			
			}
		});
		return view;
	}

}

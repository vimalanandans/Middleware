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
import com.bosch.upa.spheremanager.dummy.DummyContent;
import com.bosch.upa.spheremanager.ui.listitems.AbstractPolicyListItem;
import com.bosch.upa.spheremanager.ui.listitems.ProtocolItem;
import com.bosch.upa.uhu.pipe.core.PipePolicyUtility;
import com.bosch.upa.uhu.pipe.policy.ext.UhuPipePolicy;

public class PolicyListAdapter extends ArrayAdapter<AbstractPolicyListItem> {
	public static final String TAG = "PolicyListAdapter";
    private final String pipeReqId;
    private int pipeId;
    private final LayoutInflater inflater;
	private final String filterSetting;
	

	public PolicyListAdapter(Context context,
                             List<AbstractPolicyListItem> policy, String filterSetting, String pipeReqId) {
		super(context, 0, policy);
		this.filterSetting = filterSetting;
		inflater = LayoutInflater.from(context);
        this.pipeReqId = pipeReqId;
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
		textViewReason.setText(item.getDescription());

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
		

		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                UhuPipePolicy policyIn = PipePolicyUtility.policyInMap.get(pipeReqId);
                UhuPipePolicy policyOut = PipePolicyUtility.policyOutMap.get(pipeReqId);
				if (policy_active.isChecked()) {
					policy_active.setChecked(false);
					item.setActive(false);
                    if (filterSetting.equals("inbound")) {
                        policyIn.unAuthorize(item.getProtocolName());
                       // DummyContent.policyListInbound.set(position, item);
						} else {
                        policyOut.unAuthorize(item.getProtocolName());
                        //DummyContent.policyListOutbound.set(position, item);
					}
				} else {
					policy_active.setChecked(true);
					item.setActive(true);
					if (filterSetting.equals("inbound")) {
                        policyIn.authorize(item.getProtocolName());
                        //DummyContent.policyListInbound.set(position, item);
						} else {
                        policyOut.authorize(item.getProtocolName());
                       // DummyContent.policyListOutbound.set(position, item);
					}
				}

			}
		});
		return view;
	}

 }

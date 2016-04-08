package com.bosch.upa.spheremanager.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.bosch.upa.spheremanager.DeviceListActivity;
import com.bosch.upa.spheremanager.SphereListActivity;
import com.bosch.upa.spheremanager.ui.listitems.AbstractPolicyListItem;
import com.bosch.upa.spheremanager.ui.listitems.ProtocolItem;
import com.bezirk.api.addressing.PipePolicy;
import com.bosch.upa.uhu.pipe.core.PipePolicyUtility;
import com.bosch.upa.uhu.pipe.core.PipeRecord;
import com.bosch.upa.uhu.pipe.core.PipeRegistry;
import com.bosch.upa.uhu.pipe.policy.ext.UhuPipePolicy;
import com.bosch.upa.uhu.starter.MainService;

/**
 * A fragment representing a single Sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class PolicyListFragment extends ListFragment {
	private String filterSetting = "inbound";
	private String pipeReqId = null;
    private int pipeId=0;
    private Boolean isNew = true;
	public static final String TAG = "PolicyListFragment";
    private List<AbstractPolicyListItem> policyListInbound = new ArrayList<AbstractPolicyListItem>();
    private List<AbstractPolicyListItem> policyListOutbound = new ArrayList<AbstractPolicyListItem>();


    public PolicyListFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if(pipeReqId != null){ // during pipe request trigger from service
			PolicyListAdapter adapter ;
			if (filterSetting.equals("inbound")) {

				adapter = new PolicyListAdapter(getActivity()
						.getApplicationContext(), policyListInbound, filterSetting, pipeReqId);

			} else {
				adapter = new PolicyListAdapter(getActivity()
						.getApplicationContext(), policyListOutbound, filterSetting, pipeReqId);
			}
		    setListAdapter(adapter);
		}
        else{ // during pipe Managmenet UI trigger
            PipeRegistry pipeRegistry  = MainService.getPipeRegistryHandle();

            if(pipeRegistry != null) {

                ArrayList<PipeRecord> pipeRecords =  new ArrayList<PipeRecord> (pipeRegistry.allPipes());

                if ((pipeRecords != null) &&
                        (pipeRecords.get(pipeId) != null)) {
                    //accessed via ManagePipeActitvity using new data-structure
                    //We need to put data from hashmap to arraylist..
                    List<ProtocolItem> policies = new ArrayList<ProtocolItem>();
                    PipePolicy pipeInPolicy = pipeRecords.get(pipeId).getAllowedOut();

                    for (String protocolName : pipeInPolicy.getProtocolNames()) {
                        // FIXME: setting pipe active is active always and new is true. change it later
                        ProtocolItem item = new ProtocolItem(protocolName,pipeInPolicy.getReason(protocolName),true, true);
                        policies.add(item);
                    }
                    ProtocolRoleListAdapter adapter = new ProtocolRoleListAdapter(getActivity()
                            .getApplicationContext(), policies, filterSetting);
                    if (filterSetting.equals("inbound")) {
                        adapter = new ProtocolRoleListAdapter(getActivity()
                                .getApplicationContext(), policies, filterSetting);
                    } else {
                        policies = new ArrayList<ProtocolItem>();
                        PipePolicy pipeOutPolicy = pipeRecords.get(pipeId).getAllowedIn();
                        for (String protocolName :  pipeOutPolicy.getProtocolNames()) {
                            // FIXME: setting pipe active is active always and new is true. change it later
                            ProtocolItem item = new ProtocolItem(protocolName,pipeOutPolicy.getReason(protocolName),true, true);
                            policies.add(item);
                        }
                        adapter = new ProtocolRoleListAdapter(getActivity()
                                .getApplicationContext(), policies, filterSetting);
                    }
                    setListAdapter(adapter);
                }
                else{
                    Log.e(TAG,"No records in pipe registry");
                }
            }
            else{
                Log.e(TAG,"unable to get pipe registry");
            }
        }


	}

	

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.v(TAG, "onViewCreated");

	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
	}

    public void updateInboundList(){
        UhuPipePolicy policyIn = PipePolicyUtility.policyInMap.get(pipeReqId);
        for(String protocolName : policyIn.getReasonMap().keySet()){
            policyListInbound.add(new ProtocolItem(protocolName, policyIn.getReasonMap().get(protocolName), true, true));
        }
    }

    public void updateOutboundList() {
        UhuPipePolicy policyOut = PipePolicyUtility.policyOutMap.get(pipeReqId);
        for (String protocolName : policyOut.getReasonMap().keySet()) {
            policyListOutbound.add(new ProtocolItem(protocolName, policyOut.getReasonMap().get(protocolName), true, true));
        }
    }

    public void setFilter(String filterSetting){
        this.filterSetting = filterSetting;
    }

    public void setPipeReqId(String pipeId){
        //isNew = false;
        this.pipeReqId = pipeId;
    }


    public int getPipeId() {
        return pipeId;
    }

    public void setPipeId(int pipeId) {
        this.pipeId = pipeId;
    }


}

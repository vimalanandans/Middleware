package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.pipe.core.PipeApprovalException;
import com.bezirk.pipe.core.PipePolicyUtility;
import com.bezirk.pipe.core.PipeRequester;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.PolicyListFragment;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bezirk.actions.BezirkActions.KEY_PIPE_NAME;
import static com.bezirk.actions.BezirkActions.KEY_PIPE_REQ_ID;
import static com.bezirk.actions.BezirkActions.KEY_PIPE_SPHEREID;
import static com.bezirk.actions.BezirkActions.KEY_SENDER_ZIRK_ID;
import static com.bezirk.util.BezirkValidatorUtility.checkBezirkZirkId;

public class PipePolicyActivity extends FragmentActivity implements OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger(PipePolicyActivity.class);

    static final String TAG = PipePolicyActivity.class.getSimpleName();
    private String filterSetting = "inbound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent rxIntent = this.getIntent();
        final String pipeName = rxIntent.getStringExtra(KEY_PIPE_NAME);
        final String serviceIdAsString = rxIntent.getStringExtra(KEY_SENDER_ZIRK_ID);
        final String pipeReqId = rxIntent.getStringExtra(KEY_PIPE_REQ_ID);
        final String sphereId = rxIntent.getStringExtra(KEY_PIPE_SPHEREID);

        ZirkId serviceId = serviceIdFromString(serviceIdAsString);
        if (serviceId == null) {
            logger.error("Intent not valid because there was a failure validating zirkId");
            return;
        }

        final String serviceName = BezirkCompManager.getSphereForPubSubBroker().getZirkName(serviceId);

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        view = (View) layoutInflater.inflate(
                R.layout.activity_pipe_policy_smartphone, null);
        TextView service_name = (TextView) view
                .findViewById(R.id.name_of_service);
//		service_name.setText("www.bosch.com");
        service_name.setText(serviceName);


        //SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP
        //		.get(getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        final String sphereID = getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        SphereListItem sphere = null;

        BezirkSphereAPI api = MainService.getSphereHandle();

        if (BezirkValidatorUtility.isObjectNotNull(api)) {
            BezirkSphereInfo sphereInfo = api.getSphere(sphereID);
            if (sphereInfo != null) {
                sphere = new SphereListItem(sphereInfo);
            } else {
                Log.e(TAG, "sphere contains : " + sphereID + " not found");
            }
        } else {
            Log.e(TAG, "MainService is not available");
        }


        TextView sphere_name = (TextView) view
                .findViewById(R.id.sphere_associated_to_service);
        sphere_name.setText("Requests Pipe " + pipeName + " communication through sphere " + sphere.getmSphere().getSphereName());
        Button addPipeConfirm = (Button) view.findViewById(R.id.add_pipe_confirm);
        addPipeConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO Send back selected Policy
                BezirkPipePolicy policyIn = PipePolicyUtility.policyInMap.get(pipeReqId);
                BezirkPipePolicy policyOut = PipePolicyUtility.policyOutMap.get(pipeReqId);
                for (String role : policyIn.getReasonMap().keySet()) {
                    logger.info("In Protocol: " + role + " is Authorized?: " + policyIn.isAuthorized(role));
                }
                for (String role : policyOut.getReasonMap().keySet()) {
                    logger.info("Out Protocol: " + role + " is Authorized?: " + policyOut.isAuthorized(role));
                }
                //FIXME: instead of calling the pipe approved
                PipeRequester requester = PipePolicyUtility.pipeRequesterMap.get(pipeReqId);
                try {
                    requester.pipeApproved(true, pipeReqId, null, sphereId);
                } catch (PipeApprovalException e) {
                    logger.error("Exception in pipe approval.", e);
                }
                startActivity(createBackIntent());
            }
        });
        Button addPipeCancel = (Button) view.findViewById(R.id.add_pipe_cancel);
        addPipeCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //TODO keep track of changes in policies and don't save them
                startActivity(createBackIntent());


            }
        });


        final Button filterInbound = (Button) view.findViewById(R.id.filter_inbound);

        final Button filterOutbound = (Button) view.findViewById(R.id.filter_outbound);
        filterInbound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (filterSetting.equals("inbound")) {
                    // do nothing
                } else {
                    filterSetting = "inbound";
                    // set color active
                    filterInbound.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    filterOutbound
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    updateList(pipeReqId);
                }
            }
        });
        filterOutbound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (filterSetting.equals("outbound")) {
                    // do nothing
                } else {
                    filterSetting = "outbound";
                    // set color active
                    filterInbound
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            R.color.buttonInactive));
                    filterOutbound.setBackgroundColor(getResources().getColor(
                            R.color.buttonActive));
                    updateList(pipeReqId);
                }
            }
        });

        updateList(pipeReqId);
        setContentView(view);
    }

    private void updateList(String pipeReqId) {
        PolicyListFragment policyListFragment = new PolicyListFragment();
        policyListFragment.setFilter(filterSetting);
        //FIXME: Don't call the fragments method directly. Send via intent parameter
        policyListFragment.setPipeReqId(pipeReqId);
        policyListFragment.updateInboundList();
        policyListFragment.updateOutboundList();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.policy_container_in_activity, policyListFragment);
        ft.commit();

    }

    private Intent createBackIntent() {

        Intent backIntent = new Intent(this, SphereListActivity.class);
        // don't remember the history
        backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return backIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pipe_policy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ZirkId serviceIdFromString(String serviceIdAsString) {
        Gson gson = new Gson();
        ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
        if (!checkBezirkZirkId(serviceId)) {
            logger.error("zirkId not valid: " + serviceId);
            return null;
        }

        return serviceId;
    }


    @Override
    public void onClick(View v) {

    }
}

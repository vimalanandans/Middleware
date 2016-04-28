package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.pipe.core.PipeRecord;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.DialogDeleteFragment;
import com.bezirk.spheremanager.ui.PolicyListFragment;
import com.bezirk.spheremanager.ui.SphereListOfOnePipeFragment;
import com.bezirk.spheremanager.ui.SphereListOfOnePipeFragment.SphereListOfOnePipeFragmentCallbacks;
import com.bezirk.starter.MainService;

import java.util.ArrayList;

public class ManagePipesActivity extends FragmentActivity implements SphereListOfOnePipeFragmentCallbacks {

    private String TAG = ManagePipesActivity.class.getName();
    private String filterSetting = "policies";
    private String filterSettingPolicies = "outbound";
    private String callingActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view;
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = (View) layoutInflater.inflate(
                R.layout.activity_manage_pipes_smartphone, null);
        final int pipeId = getIntent().getIntExtra("pipeId", 0);

        TextView service_name = (TextView) view.findViewById(R.id.name_of_pipe);

        PipeRegistry pipeRegistry = MainService.getPipeRegistryHandle();

        if (pipeRegistry != null) {

            ArrayList<PipeRecord> pipeRecords = new ArrayList<PipeRecord>(pipeRegistry.allPipes());
            if ((pipeRecords != null) &&
                    (pipeRecords.get(pipeId) != null)) {

                //service_name.setText(DummyContent.pipeList.get(pipeId).getPipe().getName());
                service_name.setText(pipeRecords.get(pipeId).getPipe().getName());
            } else {
                Log.e(TAG, "No records in pipe registry");
            }
        } else {
            Log.e(TAG, "unable to get pipe registry");
        }


        final Button filterInbound = (Button) view
                .findViewById(R.id.filter_policies_inbound);
        final Button filterOutbound = (Button) view
                .findViewById(R.id.filter_policies_outbound);
        final Button filterPolicies = (Button) view
                .findViewById(R.id.filter_policies);
        final Button filterSpheres = (Button) view
                .findViewById(R.id.filter_spheres);

        // Switch Policies / Spheres
        filterPolicies.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View filterView) {
                // TODO Auto-generated method stub
                if (filterSetting.equals("policies")) {
                    // do nothing
                } else {
                    filterSetting = "policies";
                    // set color active
                    filterPolicies.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    filterSpheres
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    // show In and Outbound Switch Toggles
                    filterInbound.setVisibility(view.VISIBLE);
                    filterOutbound.setVisibility(view.VISIBLE);
                    updateList(pipeId);
                }
            }
        });
        filterSpheres.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View filterSphereView) {
                if (filterSetting.equals("spheres")) {
                    // do nothing
                } else {
                    filterSetting = "spheres";
                    // set color active
                    filterPolicies
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    filterSpheres.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    // hide In and Outbound Switch Toggles
                    filterInbound.setVisibility(view.GONE);
                    filterOutbound.setVisibility(view.GONE);
                    updateList(pipeId);
                }
            }
        });

        filterInbound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View filterInbondView) {
                if (filterSettingPolicies.equals("inbound")) {
                    // do nothing
                } else {
                    filterSettingPolicies = "inbound";
                    // set color active
                    filterInbound.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    filterOutbound
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    updateList(pipeId);
                }
            }
        });
        filterOutbound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View filterOutboundView) {
                // TODO Auto-generated method stub
                if (filterSettingPolicies.equals("outbound")) {
                    // do nothing
                } else {
                    filterSettingPolicies = "outbound";
                    // set color active
                    filterInbound
                            .setBackgroundColor(getResources()
                                    .getColor(
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    filterOutbound.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    updateList(pipeId);
                }
            }
        });

        updateList(pipeId);

        setContentView(view);
    }


    private void updateList(int pipeReqId) {
        if ("policies".equals(filterSetting)) {
            //PolicyListFragment has to be replaced to show new DataStructure
            PolicyListFragment policyListFragment = new PolicyListFragment();
            policyListFragment.setFilter(filterSettingPolicies);
            policyListFragment.setPipeId(getIntent().getIntExtra("pipeId", pipeReqId));

            FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm
                    .beginTransaction();
            ft.replace(R.id.management_container_in_activity,
                    policyListFragment);
            ft.commit();
        } else {
            // TODO: Show Spheres!
            SphereListOfOnePipeFragment sphereListFragment = new SphereListOfOnePipeFragment();
            sphereListFragment.setPipeId(getIntent().getIntExtra("pipeId", pipeReqId));
            FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.management_container_in_activity,
                    sphereListFragment);
            ft.commit();
        }

    }

    public Intent createBackToLastActivityIntent() {
        callingActivity = getCallingActivity().getClassName();
        Intent backIntent = new Intent(this, PipeListActivity.class);

        if (callingActivity
                .equals("PipeListActivity")) {
            //backIntent = new Intent(this, PipeListActivity.class);
            // don't remember the history
            backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        } else if (callingActivity
                .equals("DeviceListActivity")) {
            backIntent = new Intent(this, DeviceListActivity.class);
            // don't remember the history
            backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            backIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                    .getStringExtra(DeviceListFragment.ARG_ITEM_ID));
        }
        return backIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_pipes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            NavUtils.navigateUpTo(this, createBackToLastActivityIntent());
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // change action of back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            NavUtils.navigateUpTo(this, createBackToLastActivityIntent());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onItemSelected(String id) {
        Intent detailIntent = new Intent(this, DeviceListActivity.class);
        detailIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, id);
        startActivity(detailIntent);

    }


    @Override
    public void onSphereOfOnePipeLongClicked(String id) {
        FragmentManager fm = getSupportFragmentManager();
        DialogDeleteFragment deleteFragment = new DialogDeleteFragment();
        deleteFragment.setText("Do you want to remove the association with this sphere?");
        deleteFragment.show(fm, "Dialog Fragment");

    }


}

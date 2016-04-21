package com.bezirk.controlui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.bezirk.actions.UhuActions;
import com.bezirk.controlui.R;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuActionCommands;
import com.bezirk.starter.UhuPreferences;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class DeviceControlActivity extends ActionBarActivity
        implements GenericListItemView.ItemToggleListener {
    final Context context = this;
    private static final Logger log = LoggerFactory.getLogger(DeviceControlActivity.class);
    // UI Create
    List<DataModel> listData = new ArrayList<DataModel>();
    UhuPreferences preferences;
    private GenericListItemView adapter;
    private DeviceControlActivityHelper deviceControlActivityHelper;
    private DeviceIntentReceiver deviceIntentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        // preference for storing the settings
        preferences = new UhuPreferences(this);
        deviceControlActivityHelper = new DeviceControlActivityHelper(this, context);

        /** set up the device list
         * TODO: read it from xml file
         *  */
        String appName = getString(R.string.app_name);
        listData.add(new DataModel(R.drawable.upa_control, appName + " On / OFF", "Turn " + appName + " On / Off", true, true, false));

        listData.add(new DataModel(R.drawable.ic_device_name, "Set Device Name", "Change the Device Name from : "
                + preferences.getString(preferences.DEVICE_NAME_TAG_PREFERENCE, "")
                , false, false, false));

        listData.add(new DataModel(R.drawable.ic_action_device_type, "Set Device Type", "Change the Device Type from : "
                + preferences.getString(preferences.DEVICE_TYPE_TAG_PREFERENCE, "")
                , false, false, false));
        listData.add(new DataModel(R.drawable.ic_action_sphere_name, "Set Default Sphere Name",
                "Change the Default Sphere Name from : "
                        + preferences.getString(preferences.DEFAULT_SPHERE_NAME_TAG_PREFERENCE, "")
                , false, false, false));

        listData.add(new DataModel(R.drawable.ic_delete_database, "Clear the Data", "Clear the Spheres, Service and Pipes internal data ", false, false, false));

        listData.add(new DataModel(R.drawable.ic_action_diag, "Diagnosis",
                "Diagnosis of Uhu. Communication test and service logs", false, false, false));

        //request current status of development mode, update the list based on the asynchronous response in Broadcast Receiver
        deviceControlActivityHelper.getStatus();

        // set the list
        AbsListView list;

        list = (AbsListView) findViewById(R.id.list);

        GenericListItemView adapter = new
                GenericListItemView(DeviceControlActivity.this, listData, this);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long clickId) {
                deviceControlActivityHelper.deviceControlListClick(listData, position);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceIntentReceiver = new DeviceIntentReceiver();
        registerReceiver(deviceIntentReceiver, new IntentFilter("com.bosch.upa.uhu.controluinotfication"));
        log.debug("Registered DeviceIntentReceiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(deviceIntentReceiver);
        log.debug("Unregistered DeviceIntentReceiver");
    }

    /**
     * handle the result activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is RESULT_DEVICE_ITEM_SELECT
        switch (resultCode) {
            case DeviceTypeSelection.RESULT_DEVICE_ITEM_SELECT: //result for device type selection
                String deviceType = data.getStringExtra(DeviceTypeSelection.DEVICE_ITEM_SELECTED_TEXT);
                deviceControlActivityHelper.setDeviceType(deviceType);
                break;
            default:
                log.error("unknown result from settings activity");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemToggleListener(int position, boolean checkStatus) {
        log.info("toggle button pressed at: " + String.valueOf(position), " state : " + String.valueOf(checkStatus));
        Intent intent;
        String action;
        // we selecting based on image id hence list must have image id and it has to be unique
        if (UhuValidatorUtility.isObjectNotNull(listData) && !listData.isEmpty()) {
            DataModel dataModel = listData.get(position);
            if (UhuValidatorUtility.isObjectNotNull(dataModel)) {
                intent = new Intent(context, MainService.class);
                switch (dataModel.getImageId()) {
                    case R.drawable.upa_control: // Bezirk On/OFF
                        action = checkStatus ? UhuActions.ACTION_START_UHU : UhuActions.ACTION_STOP_UHU;
                        intent.setAction(action);
                        startService(intent);
                        break;
                    case R.drawable.ic_action_dev_mode: //dev mode on/off
                        action = checkStatus ? UhuActions.ACTION_DEV_MODE_ON : UhuActions.ACTION_DEV_MODE_OFF;
                        intent.setAction(action);
                        startService(intent);
                        break;
                    default:
                        log.error("Unknown toggle button pressed");
                        break;
                }
            }

        }
    }

    /**
     * Broadcast event receiver for results
     */
    private class DeviceIntentReceiver extends BroadcastReceiver {
        private final String TAG = "DeviceIntentReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received Intent for Device control >" + intent.getAction());
            Log.d(TAG, "Command >" + intent.getStringExtra("Command"));

            if (intent.getStringExtra("Command").equalsIgnoreCase(UhuActionCommands.CMD_DEV_MODE_STATUS)) {
                deviceControlActivityHelper.updateList((IUhuDevMode.Mode) intent.getSerializableExtra("Mode"), listData);
            }
        }
    }


}

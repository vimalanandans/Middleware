package com.bezirk.spheremanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.pipe.core.PipeRecord;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.DeviceListFragment.DeviceListFragmentCallbacks;
import com.bezirk.spheremanager.ui.DialogServiceListFragment;
import com.bezirk.spheremanager.ui.PipeListFragment.ShowPipesCallbacks;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;
import com.bezirk.starter.BezirkActionCommands;
import com.bezirk.util.BezirkValidatorUtility;

import java.util.ArrayList;
import java.util.List;

import bezirk.zbarscanner.ScannerActivity;

/**
 * An activity representing a single sphere detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link SphereListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link DeviceListFragment}.
 * </p>
 */
public class DeviceListActivity extends FragmentActivity implements
        DeviceListFragmentCallbacks, ShowPipesCallbacks, DialogServiceListFragment.DialogServiceListFragmentCallback {
    public static final String ARG_DEVICE_ID = "device_id";
    static final int DETAIL_CODE_REQUEST = 5;
    static final int DETAIL_PIPE_REQUEST = 23;
    static final int USER_CREDENTIALS_REQUEST = 24;
    static final String SHARE = "share";
    static final String CATCH = "catch";
    static final String SCANTYPE = "scantype";
    private static final String TAG = "DeviceListActivity";
    private SphereListItem entry;
    private String sphereName;
    private String sphereID;

    private SphereIntentReceiver sphereIntentReceiver;
    private DeviceListActivityHelper deviceListActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceListActivityHelper = new DeviceListActivityHelper(this);

        sphereID = getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        BezirkSphereAPI api = MainService.getSphereHandle();

        if (BezirkValidatorUtility.isObjectNotNull(api)) {

            BezirkSphereInfo sphereInfo = api.getSphere(sphereID);
            if (BezirkValidatorUtility.isObjectNotNull(sphereInfo)) {
                entry = new SphereListItem(sphereInfo);
            } else {
                Log.e(TAG, "sphere contains : " + sphereID + " not found");
            }

        } else {
            Log.e(TAG, "MainService is not available");
        }


        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(
                R.layout.activity_devicelist_smartphone, null);

        deviceListActivityHelper.configureFilterDevicesAndPipes(view, entry);

        // Embed list via Fragment
        deviceListActivityHelper.updateContainer(entry);
        Log.v(TAG, "inflate");
        sphereName = entry.getmSphere().getSphereName();
        TextView textView = (TextView) view.findViewById(R.id.name_of_sphere);

        if (entry.getmSphere().isThisDeviceOwnsSphere()) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        textView.setText("sphere: " + sphereName);

        ImageView img = (ImageView) view.findViewById(R.id.sphere_icon);

        deviceListActivityHelper.setImageSourceForSphereType(img, entry);

        Button catchDeviceButton = deviceListActivityHelper.configureCatchButton(view);

        Button shareSphereButton = deviceListActivityHelper.configureSphereButton(view);

        //TODO: Just for Survey:
        catchDeviceButton.setVisibility(view.GONE);
        shareSphereButton.setVisibility(view.GONE);
        Log.v(TAG, entry.getmSphere().getSphereID());
        setContentView(view);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        sphereIntentReceiver = new SphereIntentReceiver(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        // register the intent to receive the Bezirk sphere Results

        IntentFilter filter = new IntentFilter();

        filter.addAction(BezirkActionCommands.SPHERE_NOTIFICATION_ACTION);

        registerReceiver(sphereIntentReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister the intent so that it doesn't receive when it goes out of the scope
        unregisterReceiver(sphereIntentReceiver);
    }

    @Override
    public void onAddServicesToSphere() {
        deviceListActivityHelper.updateContainer(entry);
    }

    @Override
    public void onItemSelectedDeviceList(int device_id) {

        // SavedInstace has to be provided?
        Intent detailIntent = new Intent(this, SelectServiceActivity.class);
        // Device Name (or ID) and sphere ID has to be provided.
        detailIntent.putExtra(DeviceListActivity.ARG_DEVICE_ID, device_id);
        detailIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                .getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        startActivityForResult(detailIntent, DETAIL_CODE_REQUEST);

        Log.v(TAG, "onItemSelectedDeviceList +" + device_id);

    }

    @Override
    public void onItemLongClickedDeviceList(int itemId) {
        //Not required for now
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sphere_detail_actions, menu);

        if (BezirkValidatorUtility.isObjectNotNull(entry)) {
            // disable the menu items for member sphere
            if (!entry.getmSphere().isThisDeviceOwnsSphere()) {
                MenuItem item = menu.findItem(R.id.action_scan_qr_service_share);
                item.setVisible(false);

                item = menu.findItem(R.id.action_scan_qr_service_catch);
                item.setVisible(false);

                item = menu.findItem(R.id.action_share_sphere);
                item.setVisible(false);

                item = menu.findItem(R.id.action_add_device_services_to_sphere);
                item.setVisible(false);
            }
            // disable sharing menu item if owner sphere and no devices within the sphere
            else if (entry.getmSphere().getDeviceList() == null || entry.getmSphere().getDeviceList().isEmpty()) {
                MenuItem item = menu.findItem(R.id.action_scan_qr_service_share);
                item.setVisible(false);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu items for use in the action bar

        // to make sphere management ui as library.
        // the below is changed to if-else solve the compiler error.
        // refer http://stackoverflow.com/questions/9092712/switch-case-statement-error-case-expressions-must-be-constant-expression

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this,
                    SphereListActivity.class));
            return true;
        } else if (itemId == R.id.action_scan_qr_service_catch) { // For Catch services
            deviceListActivityHelper.scanQR(CATCH);
            return true;
        } else if (itemId == R.id.action_scan_qr_service_share) { // For Share zirk
            deviceListActivityHelper.scanQR(SHARE);
            return true;
        } else if (itemId == R.id.action_share_sphere) { // invite to the sphere
            deviceListActivityHelper.shareSphere();
            return true;
        } else if (itemId == R.id.action_discover_sphere_details) {
            BezirkCompManager.getSphereUI().discoverSphere(getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));
            return true;
        } else if (itemId == R.id.action_add_device_services_to_sphere) {
            addLocalServicesToSphere();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    /* add the local services to the sphere
    * */
    void addLocalServicesToSphere() {
        if (BezirkValidatorUtility.isObjectNotNull(MainService.getSphereHandle().getServiceInfo())) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            DialogServiceListFragment dialogServiceListFragment = new DialogServiceListFragment();
            dialogServiceListFragment.setSphereId(sphereID);
            dialogServiceListFragment.setTitle("Add services to sphere");
            dialogServiceListFragment.show(fragmentManager, "Dialog Zirk List");
        } else {
            Toast.makeText(getApplicationContext(), "No services in the device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScannerActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Log.d(TAG, "Getting value passed to bundle: " + bundle.getString(SCANTYPE));
            deviceListActivityHelper.processQRcode(data.getStringExtra(ScannerActivity.DATA), bundle.getString(SCANTYPE), sphereID);
        }
    }

    @Override
    public void onItemSelected(int position) {
        Intent pipeManagementIntent = new Intent(getApplicationContext(), ManagePipesActivity.class);
        pipeManagementIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                .getStringExtra(DeviceListFragment.ARG_ITEM_ID));
        pipeManagementIntent.putExtra("pipeId", position);
        startActivityForResult(pipeManagementIntent,
                DETAIL_PIPE_REQUEST);

    }

    @Override
    public void onItemLongClicked(int position) {

        PipeRegistry pipeRegistry = MainService.getPipeRegistryHandle();

        if (BezirkValidatorUtility.isObjectNotNull(pipeRegistry)) {

            List<PipeRecord> pipeRecords = new ArrayList<PipeRecord>(pipeRegistry.allPipes());

            if (BezirkValidatorUtility.isObjectNotNull(pipeRecords) && BezirkValidatorUtility.isObjectNotNull(pipeRecords.get(position))) {

                PipeRecord pipeRecord = pipeRecords.get(position);

                if (BezirkValidatorUtility.isObjectNotNull(pipeRecord.getPassword())) {

                    Intent pipeManagementIntent = new Intent(getApplicationContext(), UpdateUserCredentialsActivity.class);
                    pipeManagementIntent.putExtra("pipeId", position);
                    pipeManagementIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                            .getStringExtra(DeviceListFragment.ARG_ITEM_ID));
                    startActivityForResult(pipeManagementIntent,
                            USER_CREDENTIALS_REQUEST);
                } else {
                    Log.e(TAG, "No password in pipe record");
                }
            } else {
                Log.e(TAG, "No records in pipe registry");
            }
        } else {
            Log.e(TAG, "unable to get pipe registry");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpTo(this, new Intent(this,
                SphereListActivity.class));
    }

    /**
     * Broadcast event receiver for Bezirk Stack sphere management results
     */
    public class SphereIntentReceiver extends BroadcastReceiver {
        private final String TAG = "SphereIntentReceiver";

        private final DeviceListActivity parent;

        SphereIntentReceiver(DeviceListActivity activity) {
            parent = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received Intent for Device control >" + intent.getAction());
            String command = intent.getStringExtra(BezirkActionCommands.BEZIRK_ACTION_COMMANDS);
            Log.i(TAG, "Command > " + command);
            if (command.equals(BezirkActionCommands.CMD_SPHERE_DISCOVERY_STATUS)) {
                boolean Status = intent.getBooleanExtra(BezirkActionCommands.BEZIRK_ACTION_COMMAND_STATUS, false);
                if (Status) { // when status is true
                    if (deviceListActivityHelper != null) {
                        deviceListActivityHelper.updateContainer(entry);
                    }
                } else {
                    Toast.makeText(parent, "FAILED : " + command, Toast.LENGTH_SHORT).show();
                }

            } else if (command.equals(BezirkActionCommands.CMD_SPHERE_CATCH_STATUS) ||
                    command.equals(BezirkActionCommands.CMD_SPHERE_SHARE_STATUS)) {
                String message = intent.getStringExtra(BezirkActionCommands.BEZIRK_ACTION_COMMAND_MESSAGE);
                Toast.makeText(parent, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}


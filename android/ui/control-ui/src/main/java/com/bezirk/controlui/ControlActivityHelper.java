package com.bezirk.controlui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.util.BezirkVersion;
import com.bezirk.comms.CommsFeature;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.starter.MainService;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class ControlActivityHelper {
    private static final Logger logger = LoggerFactory.getLogger(ControlActivity.class);

    private final List<DataModel> listData = new ArrayList<DataModel>();
    private final String BR_SYSTEM_STATUS_ACTION = "com.bezirk.systemstatus";
    private final ControlActivity controlActivity;
    boolean stackVersionMismatch;
    private String receivedBezirkVersion = BezirkVersion.BEZIRK_VERSION;
    /**
     * Broadcast receiver to receive the status from the stack if there is  a version mismatch
     */
    final BroadcastReceiver systemStatusBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stackVersionMismatch = true;
            receivedBezirkVersion = intent.getExtras().getString("misMatchVersiosion");
            controlActivity.invalidateOptionsMenu();
        }
    };
    private AlertDialog mAlertDialog;
    private GenericListItemView adapter;

    public ControlActivityHelper(ControlActivity controlActivity) {
        this.controlActivity = controlActivity;
    }

    /**
     * Initialize the UI
     */
    void initUI() {
        listData.add(new DataModel(R.drawable.ic_action_sphere_control, "Device Control", "Total Device Control", false, false, false));

        listData.add(new DataModel(R.drawable.ic_sphere, "sphere Management", "Control Spheres and Services", false, false, false));

        listData.add(new DataModel(R.drawable.ic_action_pipes, "Pipe Management", "Control and configure Pipes", false, false, false));

        //listData.add(new DataModel(R.drawable.ic_cloud, "Rest Pipe", "Control and configure Rest Server", false, false, false));

        String appName = controlActivity.getString(R.string.app_name);

        listData.add(new DataModel(R.drawable.upa_about, "About " + appName, "Details about " + appName, false, false, false));


        AbsListView list;

        list = (AbsListView) controlActivity.findViewById(R.id.list);

        adapter = new GenericListItemView(controlActivity, listData, null);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long clickId) {
                bezirkStartActivity(position);

            }
        });
    }

    /**
     * Start the sphere Managmenet Activity
     */
    private void bezirkStartActivity(int position) {
        Intent intent;
        switch (position) {
            case 0: // Device control
                intent = new Intent(controlActivity, DeviceControlActivity.class);
                controlActivity.startActivity(intent);
                break;
            case 1: // Spheres
                intent = new Intent(controlActivity, SphereListActivity.class);
                controlActivity.startActivity(intent);
                break;
            case 2:
                Toast.makeText(controlActivity, "This Feature is not available!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (CommsFeature.HTTP_BEZIRK_COMMS.isActive()) {
                    intent = new Intent(controlActivity, RestConfigActivity.class);
                    controlActivity.startActivity(intent);
                } else {
                    Toast.makeText(controlActivity, "This Feature is not available!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                showAboutDialog();
                break;
            default:
                logger.error("unknown item pressed");
        }
    }

    void showAlertDialogToShowSystemStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(controlActivity);
        builder.setTitle("STACK STATUS");
        View alertView = LayoutInflater.from(controlActivity).inflate(R.layout.layout_alert_dialog_system_status, null);
        final TextView bezirkVersion = (TextView) alertView.findViewById(R.id.versionBezirk);
        final TextView bezirkStatus = (TextView) alertView.findViewById(R.id.versionStatus);
        final TextView bezirkExpectedVersionStatus = (TextView) alertView.findViewById(R.id.receivedVersionBezirk);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (ValidatorUtility.isObjectNotNull(mAlertDialog)) {
                    mAlertDialog.cancel();
                    mAlertDialog = null;
                }
            }
        });

        bezirkVersion.setText("Expected Bezirk-Version: " + BezirkVersion.BEZIRK_VERSION);
        if (ValidatorUtility.isObjectNotNull(receivedBezirkVersion)) {
            bezirkExpectedVersionStatus.setText("Received Bezirk-Version: " + receivedBezirkVersion);
        } else {
            bezirkExpectedVersionStatus.setText("Received Bezirk-Version: " + BezirkVersion.BEZIRK_VERSION);
        }

        if (stackVersionMismatch) {
            bezirkStatus.setText("Different versions of Bezirk exist in the network, there might be failure in the communication");
        }

        builder.setView(alertView);

        mAlertDialog = builder.create();
        mAlertDialog.show();

    }

    private void showAboutDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(controlActivity);
        // Include dialog.xml file
        dialog.setContentView(R.layout.about);
        //update the dialog with the newest version.
        final TextView aboutVersionText = (TextView) dialog.findViewById(R.id.about_version_text);

        String appName = controlActivity.getString(R.string.app_name);
        String aboutText = appName + " v" + BezirkVersion.BEZIRK_VERSION + ", Jan 2016, " + controlActivity.getString(R.string.about_copyright_text);
        aboutVersionText.setText(aboutText);
        //aboutVersionText.setText("UPA v"+ BezirkVersion.BEZIRK_VERSION + ", July 2015, � Bosch");

        // Set dialog title
        dialog.setTitle("About " + appName);

        Button dialogButton = (Button) dialog.findViewById(R.id.about_ok);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    void bezirkInitialization(Activity activity) {
        //Intialize preferences
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);

        //Start Bezirk
        Intent serviceIntent = new Intent(activity.getApplicationContext(), MainService.class);
        serviceIntent.setAction("START_BEZIRK");
        activity.startService(serviceIntent);

        // register a broadcast receiver
        controlActivity.registerReceiver(systemStatusBroadcastReceiver, new IntentFilter(BR_SYSTEM_STATUS_ACTION));
    }

}

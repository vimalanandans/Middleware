package com.bezirk.spheremanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.PipeListFragment;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;

import bezirk.zbarscanner.ScannerActivity;

public class DeviceListActivityHelper {

    private static final String TAG = DeviceListActivityHelper.class.getSimpleName();
    private static final int SHARE_CODE_REQUEST = 9;

    private DeviceListActivity deviceListActivity;
    private String filterSetting = "devices";

    public DeviceListActivityHelper(DeviceListActivity deviceListActivity) {
        this.deviceListActivity = deviceListActivity;
    }

    void configureFilterDevicesAndPipes(View view, final SphereListItem entry) {
        final Button filterDevices = (Button) view
                .findViewById(R.id.filter_devices);
        final Button filterPipes = (Button) view
                .findViewById(R.id.filter_pipes);

        // Switch Policies / Spheres
        class FilterDeviceClickListener implements View.OnClickListener {

            @Override
            public void onClick(View currentView) {
                if ("devices".equals(filterSetting)) {
                    // do nothing
                } else {
                    filterSetting = "devices";
                    // set color active
                    filterDevices.setBackgroundColor(deviceListActivity.getResources().getColor(
                            R.color.buttonActive));
                    filterPipes
                            .setBackgroundColor(deviceListActivity.getResources()
                                    .getColor(
                                            R.color.buttonInactive));
                    updateContainer(entry);
                }
            }
        }

        filterDevices.setOnClickListener(new FilterDeviceClickListener());

        class FilterPipeClickListener implements View.OnClickListener {

            @Override
            public void onClick(View currentView) {
                if ("pipes".equals(filterSetting)) {
                    // do nothing
                } else {
                    filterSetting = "pipes";
                    // set color active
                    filterDevices
                            .setBackgroundColor(deviceListActivity.getResources()
                                    .getColor(
                                            R.color.buttonInactive));
                    filterPipes.setBackgroundColor(deviceListActivity.getResources().getColor(
                            R.color.buttonActive));
                    updateContainer(entry);
                }
            }
        }

        filterPipes.setOnClickListener(new FilterPipeClickListener());
    }

    Button configureSphereButton(View view) {
        Button shareSphereButton = (Button) view
                .findViewById(R.id.share_sphere_button);

        class ShareSphereButtonClickListener implements View.OnClickListener {

            @Override
            public void onClick(View currentView) {
                shareSphere();
            }
        }

        shareSphereButton.setOnClickListener(new ShareSphereButtonClickListener());
        return shareSphereButton;
    }

    Button configureCatchButton(View view) {
        Button catchDeviceButton = (Button) view
                .findViewById(R.id.catch_device_button);
        class CatchDeviceButtonClickListener implements View.OnClickListener {

            @Override
            public void onClick(View currentView) {
                scanQR("Catch");
            }
        }
        catchDeviceButton.setOnClickListener(new CatchDeviceButtonClickListener());
        return catchDeviceButton;
    }

    void setImageSourceForSphereType(ImageView img, SphereListItem entry) {
        if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME)) {
            img.setImageResource(R.drawable.ic_home_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT)) {
            img.setImageResource(R.drawable.ic_default_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_CAR)) {
            img.setImageResource(R.drawable.ic_car_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_OFFICE)) {
            img.setImageResource(R.drawable.ic_office_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME_CONTROL)) {
            img.setImageResource(R.drawable.ic_home_control_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME_ENTERTAINMENT)) {
            img.setImageResource(R.drawable.ic_home_entertainment_sphere);
        } else if (entry.getmSphere().getSphereType().equals(BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME_SECURITY)) {
            img.setImageResource(R.drawable.ic_home_security_sphere);
        } else {
            // do nothing
        }
    }

    void shareSphere() {
        Intent shareIntent = new Intent(deviceListActivity.getApplicationContext(),
                ShareSphereActivity.class);
        // to make a backintent to DeviceListActivity from ShareSphereActivity,
        // you need
        shareIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, deviceListActivity.getIntent()
                .getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        deviceListActivity.startActivityForResult(shareIntent, SHARE_CODE_REQUEST);
    }

    void scanQR(String scanType) {
        Intent intent = new Intent(deviceListActivity, ScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DeviceListActivity.SCANTYPE, scanType);
        intent.putExtras(bundle);
        deviceListActivity.startActivityForResult(intent, ScannerActivity.REQUEST_CODE);
    }

    boolean processQRcode(String data, String scanType, String sphereID) {
        boolean qrProcessStatus = false;
        BezirkSphereAPI api = MainService.getSphereHandle();
        if (BezirkValidatorUtility.isObjectNotNull(api)) {
            if (scanType.equals(DeviceListActivity.CATCH)) {
                Log.d(TAG, "processing as catch");
                qrProcessStatus = api.processCatchQRCodeRequest(data, sphereID);
            } else if (scanType.equals(DeviceListActivity.SHARE)) {
                Log.d(TAG, "processing as share");
                qrProcessStatus = api.processShareQRCode(data, sphereID);
            } else {
                Log.e(TAG, "invalid type provided");
            }
        } else {
            Log.e(TAG, "MainService is not available");
        }
        return qrProcessStatus;
    }

    void updateContainer(SphereListItem entry) {
        if ("devices".equals(filterSetting)) {
            DeviceListFragment deviceListFragment = new DeviceListFragment();
            FragmentManager fragmentManager = deviceListActivity.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.device_list_container_in_activity,
                    deviceListFragment);
            fragmentTransaction.commit();
        } else {
            PipeListFragment showPipesFragment = new PipeListFragment();
            // set the sphere to display or not
            showPipesFragment.setSphereId(entry.getmSphere().getSphereID());

            FragmentManager fragmentManager = deviceListActivity.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.device_list_container_in_activity, showPipesFragment);
            fragmentTransaction.commit();
        }
    }

}

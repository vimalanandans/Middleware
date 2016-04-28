package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.device.BezirkDeviceType;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.SelectServiceFragment;
import com.bezirk.spheremanager.ui.listitems.SwipeDetector;
import com.bezirk.starter.MainService;

import java.util.List;

public class SelectServiceActivity extends FragmentActivity implements
        SelectServiceFragment.DeviceDetailCallbacks {
    static final int DEVICE_DETAIL_CODE_REQUEST = 6;
    static final int INFORMATION_CODE_REQUEST = 14;
    static final String TAG = SelectServiceActivity.class.getSimpleName();
    private String sphereID;
    private String callingActivity;
    private int deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        view = (View) layoutInflater.inflate(
                R.layout.activity_select_service_smartphone, null);

        callingActivity = getCallingActivity().getClassName();

        sphereID = getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);


        deviceID = getIntent().getIntExtra(DeviceListActivity.ARG_DEVICE_ID, 0);

        ///SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP.get(sphereID);
        BezirkSphereInfo sphereInfo = null;

        IUhuSphereAPI api = MainService.getSphereHandle();

        if (api != null) {
            sphereInfo = api.getSphere(sphereID);
        } else {
            Log.e(TAG, "MainService is not available");
        }

        //TODO Replace with real Device data
        //Bob's Phone  

        if (sphereInfo != null) {
            BezirkDeviceInfo item = sphereInfo.getDeviceList().get(deviceID);

            TextView device_name = (TextView) view
                    .findViewById(R.id.name_of_device);
            device_name.setText(item.getDeviceName());

            if (item.getDeviceRole() == BezirkDeviceInfo.BezirkDeviceRole.UHU_CONTROL) {
                device_name.setTypeface(null, Typeface.BOLD);
            }

            TextView sphere_name = (TextView) view
                    .findViewById(R.id.sphere_of_device);

            sphere_name.setText("Services in " + sphereInfo.getSphereName());

            if (sphereInfo.isThisDeviceOwnsSphere()) {
                sphere_name.setTypeface(null, Typeface.BOLD);
            }

            // set icons
            ImageView imageView = (ImageView) view
                    .findViewById(R.id.device_type_detail);

            // set icons
            if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_SMARTPHONE)) {
                imageView.setImageResource(R.drawable.ic_smartphone);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_TABLET)) {
                imageView.setImageResource(R.drawable.ic_tablet);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_FAN)) {
                imageView.setImageResource(R.drawable.ic_fan);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_LIGHT)) {
                imageView.setImageResource(R.drawable.ic_light);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_PRINTER)) {
                imageView.setImageResource(R.drawable.ic_printer);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_THERMOSTAT)) {
                imageView.setImageResource(R.drawable.ic_thermostat);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_PC)) {
                imageView.setImageResource(R.drawable.ic_pc);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_WASHING_MACHINE)) {
                imageView.setImageResource(R.drawable.ic_washingmachine);
            } else if (item.getDeviceType().startsWith("Chainsaw")) { //sorry no chainsaw now
                imageView.setImageResource(R.drawable.ic_chainsaw);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_TV)) {
                imageView.setImageResource(R.drawable.ic_tv);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_COFFEE)) {
                imageView.setImageResource(R.drawable.ic_coffee);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_HEATING)) {
                imageView.setImageResource(R.drawable.ic_heating);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_MICROWAVE)) {
                imageView.setImageResource(R.drawable.ic_microwave);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_GAME)) {
                imageView.setImageResource(R.drawable.ic_controller);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_CAR)) {
                imageView.setImageResource(R.drawable.ic_car);
            } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.UHU_DEVICE_TYPE_CLOUD)) {
                imageView.setImageResource(R.drawable.ic_cloud);
            } else {
                // do nothing
            }


            // setContentView(R.layout.activity_device_detail_smartphone);

            //		if (getFragmentManager().findFragmentById(
            //				R.id.device_detail_container_in_activity) == null) {
            SelectServiceFragment deviceDetailFragment = new SelectServiceFragment();
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm
                    .beginTransaction();
            ft.replace(R.id.device_detail_container_in_activity,
                    deviceDetailFragment);
            ft.commit();
            //		}

            // Swipe Detector to change sphere
            final SwipeDetector swipeDetector = new SwipeDetector();
            view.setOnTouchListener(swipeDetector);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (swipeDetector.swipeDetected()) {
                        // do the onSwipe action --> Change sphere
                        SwipeDetector.Action direction = swipeDetector.getAction();
                        //Log.i("SWIPE", direction.toString());
                        //onSwiped(direction.toString());
                    }
                }
            });


//            if (callingActivity.equals("com.bosch.upa.spheremanager.ScanActivity")) {
//                sphere_name.setText("Please select services for sphere "
//                        + sphereInfo.getSphereName());
//                setTitle("Select services");
//                LinearLayout finish_button_container = (LinearLayout) view.findViewById(R.id.finish_button_container);
//                finish_button_container.setVisibility(View.VISIBLE);
//                Button finishButton = (Button) view.findViewById(R.id.finish_button);
//                finishButton.setVisibility(View.VISIBLE);
//                finishButton.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(createBackIntent());
//
//                    }
//                });
//
//            }

            //Handle Buttons to get to InformationActivity
            //		Button confirmButton = (Button) view.findViewById(R.id.service_confirm);
            //		confirmButton.setOnClickListener(new OnClickListener() {
            //			@Override
            //			public void onClick(View v) {
            //				Intent informationIntent = new Intent(getApplicationContext(),InformationActivity.class);
            //				informationIntent.putExtra(DeviceListFragment.ARG_ITEM_ID,
            //						sphereID);
            //				informationIntent.putExtra(DeviceListActivity.ARG_DEVICE_ID,deviceID);
            //				startActivityForResult(informationIntent,
            //						INFORMATION_CODE_REQUEST);
            //
            //			}
            //		});
            //
            //		Button cancel = (Button) view.findViewById(R.id.service_cancel);
            //		cancel.setOnClickListener(new OnClickListener() {
            //			@Override
            //			public void onClick(View v) {
            //				startActivity(createBackIntent());
            //
            //			}
            //		});
        } else {
            Log.e(TAG, "sphere id : " + sphereID + " not found in sphere list");
        }

        setContentView(view);
    }

    @Override
    public void onItemClicked() {
        // TODO Change data
        // Toast.makeText(getApplicationContext(),
        // "Change Data now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_detail_actions, menu);
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

            NavUtils.navigateUpTo(this, createBackIntent());
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
            NavUtils.navigateUpTo(this, createBackIntent());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private Intent createBackIntent() {
        Intent backIntent = new Intent(this, DeviceListActivity.class);
        // don't remember the history
        backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//		if (callingActivity.equals("com.bosch.upa.spheremanager.ScanActivity")) {
//			if (getIntent().getStringExtra(ScanActivity.SCAN_CALLING).equals(
//					"SphereListActivity")) {
//				backIntent = new Intent(this, SphereListActivity.class);
//
//			} else if (getIntent().getStringExtra(ScanActivity.SCAN_CALLING)
//					.equals("DeviceListActivity")) {
//				backIntent = new Intent(this, DeviceListActivity.class);
//			}
//		}

        // go back without remembering history
        backIntent.addFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        backIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, sphereID);
        return backIntent;
    }

    @Override
    public void onSwiped(String direction) {
        if (callingActivity.equals("com.bosch.upa.spheremanager.ScanActivity")) {
            // don't allow swipe if intent is from ScanActivity
        } else {
            String newSphereID = "";
            String firstSphereID = "";
            String lastEntry = "";
            boolean foundSphere = false;

            IUhuSphereAPI api = MainService.getSphereHandle();
            if (api == null) {
                Log.e(TAG, "MainService is not available");
                return;
            }

            List<BezirkSphereInfo> sphereInfos = (List) api.getSpheres();

            String[] previousEnties = new String[sphereInfos.size()];
            int iteration = 0;
      /* FIXME - commented by Vimal
        Iterator<Entry<String, AbstractSphereListItem>> entries = DummyContent.ITEM_MAP
                .entrySet().iterator();
        // find last entry
        for (String key : DummyContent.ITEM_MAP.keySet()) {
            lastEntry = key;
        }*/
            // find the last entry
            BezirkSphereInfo sphereInfo = sphereInfos.get(sphereInfos.size() - 1);

            if (direction.equals("TB") || direction.equals("BT")) {
                // do nothing
            } else {
                Toast.makeText(getApplicationContext(), "FIXME SelectServiceActivity -> onSwiped", Toast.LENGTH_SHORT);
                //FIXME - commented by Vimal
           /* while (entries.hasNext()) {
                Entry<String, AbstractSphereListItem> entry = entries
                        .next();
                previousEnties[iteration] = entry.getKey();
                if (firstSphereID.equals("")) {
                    firstSphereID = entry.getKey();
                }
                if (entry.getKey().equals(sphereID)) {
                    foundSphere = true;
                    // check if it's the last entry of the list
                    if (sphereID.equals(lastEntry)) {
                        if (direction.equals("RL")) {
                            newSphereID = firstSphereID;
                        } else {
                            newSphereID = previousEnties[iteration - 1];
                        }

                    }
                } else if (foundSphere) {
                    // RL or LR
                    if (direction.equals("RL")) {
                        newSphereID = entry.getKey();

                        // LR
                    } else {

                        if (previousEnties[iteration - 1] == firstSphereID) {
                            newSphereID = lastEntry;
                        } else {
                            newSphereID = previousEnties[iteration - 2];
                        }
                    }
                    foundSphere = false;
                }
                iteration++;
            }*/
            }

            Intent changeSphereIntent = new Intent(this,
                    SelectServiceActivity.class);
            changeSphereIntent.putExtra(DeviceListFragment.ARG_ITEM_ID,
                    newSphereID);
            changeSphereIntent.putExtra(DeviceListActivity.ARG_DEVICE_ID,
                    deviceID);
            startActivityForResult(changeSphereIntent,
                    DEVICE_DETAIL_CODE_REQUEST);

        }
    }
}

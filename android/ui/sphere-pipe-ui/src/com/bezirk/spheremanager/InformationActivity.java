package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bezirk.device.BezirkDeviceType;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.InformationListFragment;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;

public class InformationActivity extends FragmentActivity {

    static final String TAG = InformationActivity.class.getSimpleName();
    static final int BACKTOSERVICE_CODE_REQUEST = 15;
    static final int BACKTOMEMBER_CODE_REQUEST = 16;
    private String filterSetting = "outbound";
    private String sphereID;
    private int deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sphereID = getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);
        deviceID = getIntent().getIntExtra(DeviceListActivity.ARG_DEVICE_ID, 0);



		/*SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP
                .get(sphereID);*/
        SphereListItem sphere = null;

        IUhuSphereAPI api = MainService.getSphereHandle();

        if (BezirkValidatorUtility.isObjectNotNull(api)) {
            BezirkSphereInfo sphereInfo = api.getSphere(sphereID);
            if (BezirkValidatorUtility.isObjectNotNull(sphereInfo)) {
                sphere = new SphereListItem(sphereInfo);
            } else {
                Log.e(TAG, "sphere contains : " + sphereID + " not found");
            }
        } else {
            Log.e(TAG, "MainService is not available");
        }

        BezirkDeviceInfo item = sphere.getmSphere().getDeviceList()
                .get(getIntent().getIntExtra(DeviceListActivity.ARG_DEVICE_ID,
                        0));
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        view = (View) layoutInflater.inflate(
                R.layout.activity_information_smartphone, null);
        TextView thing_name = (TextView) view
                .findViewById(R.id.name_of_thing);

        thing_name.setText(item.getDeviceName());

        if (item.getDeviceRole() == BezirkDeviceInfo.BezirkDeviceRole.UHU_CONTROL) ;
        {
            thing_name.setTypeface(null, Typeface.BOLD);
        }


        TextView sphere_name = (TextView) view
                .findViewById(R.id.sphere_of_thing);
        sphere_name.setText("Information in " + sphere.getmSphere().getSphereName());

        // set icons
        ImageView imageView = (ImageView) view.findViewById(R.id.icon_thing);
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


        final Button filterInbound = (Button) view.findViewById(R.id.filter_inbound);
        final Button filterOutbound = (Button) view.findViewById(R.id.filter_outbound);
        filterInbound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view1) {
                // TODO Auto-generated method stub
                if ("inbound".equals(filterSetting)) {
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
                    updateList();
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
                                            com.bezirk.spheremanager.R.color.buttonInactive));
                    filterOutbound.setBackgroundColor(getResources().getColor(
                            com.bezirk.spheremanager.R.color.buttonActive));
                    updateList();
                }
            }
        });

        // FOR USABILITY STUDY: if we test versions x.1 we need to hide filters and just outbound will be displayed
//		filterOutbound.setVisibility(view.GONE);
//		filterInbound.setVisibility(view.GONE);

        updateList();


        final Button backToService = (Button) view.findViewById(R.id.information_back_to_services);
        final Button backToDevice = (Button) view.findViewById(R.id.information_confirm);
        backToService.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View backToServiceView) {
                //Listener for Back to zirk
                startActivityForResult(createBackToServiceIntent(), BACKTOSERVICE_CODE_REQUEST);

            }
        });
        backToDevice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View backToDeviceView) {
                //Listener for Back to Member/DeviceList
                startActivityForResult(createBackToDeviceListIntent(), BACKTOMEMBER_CODE_REQUEST);

            }
        });

        setContentView(view);
    }

    private void updateList() {
        InformationListFragment informationListFragment = new InformationListFragment();
        informationListFragment.setFilter(filterSetting);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.information_container_in_activity, informationListFragment);
        fragmentTransaction.commit();

    }

    private Intent createBackToServiceIntent() {
        Intent backToServiceIntent = new Intent(this, SelectServiceActivity.class);

        // don't remember the history
        backToServiceIntent.setFlags(backToServiceIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Member Name (or ID) and sphere ID has to be provided.
        backToServiceIntent.putExtra(DeviceListActivity.ARG_DEVICE_ID, getIntent()
                .getStringExtra(DeviceListActivity.ARG_DEVICE_ID));

        backToServiceIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                .getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        return backToServiceIntent;

    }

    private Intent createBackToDeviceListIntent() {
        Intent backToDeviceListIntent = new Intent(this, DeviceListActivity.class);
        backToDeviceListIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
                .getStringExtra(DeviceListFragment.ARG_ITEM_ID));
        return backToDeviceListIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();


        if (itemId == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            NavUtils.navigateUpTo(this, createBackToServiceIntent());
            return true;
        }

        if (itemId == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

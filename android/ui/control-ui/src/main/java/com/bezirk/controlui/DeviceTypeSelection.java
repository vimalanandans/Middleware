package com.bezirk.controlui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.bezirk.controlui.R;
import com.bezirk.device.UhuDeviceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vnd2kor on 12/28/2014.
 */
public class DeviceTypeSelection extends ActionBarActivity {

    public static final int RESULT_DEVICE_ITEM_SELECT = 0x100;
    public static final String DEVICE_ITEM_SELECTED_TEXT = "DEVICE_ITEM_SELECTED_TEXT";
    private static final Logger log = LoggerFactory.getLogger(DeviceTypeSelection.class);
    // UI Create
    List<DataModel> listData = new ArrayList<DataModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reusing the device control for displaying the devce type.
        // TODO change the activity_device_control layout into generic naming
        setContentView(R.layout.activity_device_control);

        // populate data model
        listData.add(new DataModel(R.drawable.ic_smartphone, UhuDeviceType.UHU_DEVICE_TYPE_SMARTPHONE, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_tablet, UhuDeviceType.UHU_DEVICE_TYPE_TABLET, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_car, UhuDeviceType.UHU_DEVICE_TYPE_CAR, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_heating, UhuDeviceType.UHU_DEVICE_TYPE_HEATING, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_light, UhuDeviceType.UHU_DEVICE_TYPE_LIGHT, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_thermostat, UhuDeviceType.UHU_DEVICE_TYPE_THERMOSTAT, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_microwave, UhuDeviceType.UHU_DEVICE_TYPE_MICROWAVE, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_tv, UhuDeviceType.UHU_DEVICE_TYPE_TV, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_washingmachine, UhuDeviceType.UHU_DEVICE_TYPE_WASHING_MACHINE, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_coffee, UhuDeviceType.UHU_DEVICE_TYPE_COFFEE, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_controller, UhuDeviceType.UHU_DEVICE_TYPE_GAME, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_fan, UhuDeviceType.UHU_DEVICE_TYPE_FAN, "", false, false, false));

        listData.add(new DataModel(R.drawable.ic_pc, UhuDeviceType.UHU_DEVICE_TYPE_PC, "", false, false, false));

        AbsListView list;

        list = (AbsListView) findViewById(R.id.list);

        GenericListItemView adapter = new
                GenericListItemView(DeviceTypeSelection.this, listData, null);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long clickId) {

                log.info("list item is pressed at " + listData.get(position).getTitleText());

                Intent intent = new Intent();

                intent.putExtra(DEVICE_ITEM_SELECTED_TEXT, listData.get(position).getTitleText());

                setResult(RESULT_DEVICE_ITEM_SELECT, intent);

                finish();
            }
        });
    }


}

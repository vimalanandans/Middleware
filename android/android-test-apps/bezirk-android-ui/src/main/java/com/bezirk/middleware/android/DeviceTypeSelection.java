package com.bezirk.middleware.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;


import com.bezirk.middleware.android.ui.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DeviceTypeSelection extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeSelection.class);

    public static final int RESULT_DEVICE_ITEM_SELECT = 0x100;
    public static final String DEVICE_ITEM_SELECTED_TEXT = "DEVICE_ITEM_SELECTED_TEXT";
    // UI Create
    private final List<DataModel> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reusing the device control for displaying the device type.
        // TODO change the activity_device_control layout into generic naming
        setContentView(R.layout.activity_device_control);

        AbsListView list;

        list = (AbsListView) findViewById(R.id.list);

        GenericListItemView adapter = new
                GenericListItemView(DeviceTypeSelection.this, listData, null);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long clickId) {

                logger.info("list item is pressed at " + listData.get(position).getTitleText());

                Intent intent = new Intent();

                intent.putExtra(DEVICE_ITEM_SELECTED_TEXT, listData.get(position).getTitleText());

                setResult(RESULT_DEVICE_ITEM_SELECT, intent);

                finish();
            }
        });
    }


}

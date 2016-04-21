package com.bezirk.controlui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.bezirk.controlui.R;
import com.bezirk.controlui.commstest.CommsTestActivity;
import com.bezirk.controlui.logging.SphereSelectLoggingActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class DiagnosisActivity extends ActionBarActivity
        implements GenericListItemView.ItemToggleListener {

    private final Logger log = LoggerFactory.getLogger(DiagnosisActivity.class);

    // UI Create
    List<DataModel> listData = new ArrayList<DataModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        listData.add(new DataModel(R.drawable.ic_action_comms_test, "Communication Test",
                "Test the default communication is supported with your network and devices", false, false, false));

        listData.add(new DataModel(R.drawable.ic_action_logging, "Event Logging",
                "Enable service communication message logs.", false, false, false));

        // set the list
        AbsListView list;

        list = (AbsListView) findViewById(R.id.list);

        GenericListItemView adapter = new
                GenericListItemView(DiagnosisActivity.this, listData, this);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long clickId) {
                diagListClick(parent, view, position, clickId);

            }
        });

    }

    /**
     * Handle the device click
     */
    void diagListClick(AdapterView<?> parent, View view, int position, long clickId) {
        // we selecting based on image id hence list must have image id and it has to be unique
        switch (listData.get(position).getImageId()) {
            case R.drawable.ic_action_logging: // logging
                loggingFeatureClick(view);
                break;
            case R.drawable.ic_action_comms_test: //comms test
                commsTestFeatureClick(view);

                break;
            default:
                log.error("Unknown item pressed");
                return;
        }

    }

    public void loggingFeatureClick(View view) {
        Intent sphereSelectActivityIntent = new Intent(DiagnosisActivity.this, SphereSelectLoggingActivity.class);
        startActivity(sphereSelectActivityIntent);
    }

    public void commsTestFeatureClick(View view) {
        Intent commsTestActivityIntent = new Intent(DiagnosisActivity.this, CommsTestActivity.class);
        startActivity(commsTestActivityIntent);
    }

    @Override
    public void onItemToggleListener(int position, boolean checkStatus) {
        // Toggle button is not configured.
    }
}

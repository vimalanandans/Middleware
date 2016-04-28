package com.bezirk.controlui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bezirk.actions.BezirkActions;
import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.rest.BezirkRestCommsManager;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestConfigActivity extends ActionBarActivity implements DialogSphereList.OnSphereSelectCallback, GenericListItemView.ItemToggleListener {
    BezirkRestCommsManager restCommsManager = BezirkRestCommsManager.getInstance();
    private ListView restConfigList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rest_pipe_layout);
        restConfigList = (ListView) findViewById(R.id.rest_configure_list);

        List<DataModel> list = new ArrayList();
        list.add(new DataModel(0, "Rest Server ON / OFF", "Turn ON or OFF Rest server", true, false, false));
        list.add(new DataModel(1, "Select Rest sphere", "select sphere", false, false, false));

        GenericListItemView adapter = new GenericListItemView(this, list, this);
        restConfigList.setAdapter(adapter);

        restConfigList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    //no change..
                } else if (position == 1) {
                    showSelectSphereDialog();
                }

            }
        });
    }

    @Override
    public void onSphereSelectCallback(int position) {

        Map<String, String> sphereList = new LinkedHashMap();
        Iterator<BezirkSphereInfo> sphereInfoIterator = BezirkCompManager.getSphereUI().getSpheres().iterator();
        while (sphereInfoIterator.hasNext()) {
            BezirkSphereInfo bezirkSphereInfo = sphereInfoIterator.next();
            sphereList.put(bezirkSphereInfo.getSphereID(), bezirkSphereInfo.getSphereName());
        }

        int i = 0;
        for (Map.Entry<String, String> entry : sphereList.entrySet()) {
            if (position == i) {
                restCommsManager.setSelectedSphere(entry.getKey());
                restCommsManager.setSelectedSphereName(entry.getKey());
            }
            i++;
        }

    }

    /**
     * Show the select sphere list... dialog
     */
    private void showSelectSphereDialog() {
        List<String> sphereList = new ArrayList();

        sphereList.clear();
        Iterator<BezirkSphereInfo> sphereInfoIterator = BezirkCompManager.getSphereUI().getSpheres().iterator();
        while (sphereInfoIterator.hasNext()) {
            sphereList.add(sphereInfoIterator.next().getSphereName());
        }
        DialogSphereList dialogSphereList = new DialogSphereList(sphereList, this);
        dialogSphereList.show(getSupportFragmentManager(), "DialogSphereList");
    }


    @Override
    public void onItemToggleListener(int position, boolean checkStatus) {

        String action;
        Intent intent = new Intent(this, MainService.class);

        if (checkStatus) {
            action = BezirkActions.ACTION_REST_START_BEZIRK;
            restCommsManager.setStarted(true);
        } else {
            action = BezirkActions.ACTION_REST_STOP_BEZIRK;
            restCommsManager.setStarted(false);
        }
        intent.setAction(action);
        startService(intent);

    }
}

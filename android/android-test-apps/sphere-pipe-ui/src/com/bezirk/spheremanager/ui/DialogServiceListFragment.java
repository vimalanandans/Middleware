package com.bezirk.spheremanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.R;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DialogServiceListFragment extends DialogFragment {

    final BezirkSphereAPI sphereAPI = MainService.getSphereHandle();
    final List<BezirkZirkInfo> servicesToBeAdded = new ArrayList<BezirkZirkInfo>();
    ListView listView;
    Button addBtn, cancelBtn;
    ArrayAdapter<String> arrayAdapter;
    String[] services;
    DialogServiceListFragmentCallback dialogInterface;
    private String sphereId, title;
    private SharedPreferences sharedPrefs;
    private Map<String, String> serviceDisplayNameToServiceName;

    public void setSphereId(String sphereId) {
        this.sphereId = sphereId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_service_list_fragment, container);
        listView = (ListView) view.findViewById(R.id.listView);
        addBtn = (Button) view.findViewById(R.id.add_button);
        cancelBtn = (Button) view.findViewById(R.id.cancel_button);
        serviceDisplayNameToServiceName = new HashMap<String, String>();
        getDialog().setTitle(title);
        getDialog().setCanceledOnTouchOutside(false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        final List<BezirkZirkInfo> serviceInfo = sphereAPI.getServiceInfo();
        if (serviceInfo != null) {
            services = new String[serviceInfo.size()];
            Iterator<BezirkZirkInfo> serviceInfoIterator = serviceInfo.iterator();
            int i = 0;
            while (serviceInfoIterator.hasNext()) {
                BezirkZirkInfo bezirkZirkInfo = serviceInfoIterator.next();
                String temp = sharedPrefs.getString(bezirkZirkInfo.getZirkId(), null);
                services[i] = (temp == null) ? bezirkZirkInfo.getZirkName() : temp;
                if (temp != null) {
                    serviceDisplayNameToServiceName.put(temp, bezirkZirkInfo.getZirkName());
                }
                i++;
            }
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, services);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final int len = listView.getCount();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < len; i++)
                    if (checked.get(i)) {
                        String item = listView.getItemAtPosition(i).toString();
                        if (serviceInfo.get(i).getZirkName().equals(item) ||
                                serviceInfo.get(i).getZirkName().equals(serviceDisplayNameToServiceName.get(item))) {
                            servicesToBeAdded.add(serviceInfo.get(i));
                        }
                    }
                dismiss();
                sphereAPI.addLocalServicesToSphere(sphereId, servicesToBeAdded);
                if (getActivity() instanceof DeviceListActivity) {
                    dialogInterface = (DeviceListActivity) getActivity();
                    dialogInterface.onAddServicesToSphere();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public interface DialogServiceListFragmentCallback {
        public void onAddServicesToSphere();
    }
}

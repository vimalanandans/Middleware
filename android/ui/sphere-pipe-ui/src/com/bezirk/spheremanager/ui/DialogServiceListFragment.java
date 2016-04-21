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

import com.bezirk.middleware.objects.UhuServiceInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.R;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by MCA7KOR on 3/19/2015.
 */
public class DialogServiceListFragment extends DialogFragment {

    final IUhuSphereAPI sphereAPI = MainService.getSphereHandle();
    final List<UhuServiceInfo> servicesToBeAdded = new ArrayList<UhuServiceInfo>();
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
        final List<UhuServiceInfo> serviceInfo = sphereAPI.getServiceInfo();
        if (serviceInfo != null) {
            services = new String[serviceInfo.size()];
            Iterator<UhuServiceInfo> serviceInfoIterator = serviceInfo.iterator();
            int i = 0;
            while (serviceInfoIterator.hasNext()) {
                UhuServiceInfo uhuServiceInfo = serviceInfoIterator.next();
                String temp = sharedPrefs.getString(uhuServiceInfo.getServiceId(), null);
                services[i] = (temp == null) ? uhuServiceInfo.getServiceName() : temp;
                if (temp != null) {
                    serviceDisplayNameToServiceName.put(temp, uhuServiceInfo.getServiceName());
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
                        if (serviceInfo.get(i).getServiceName().equals(item) ||
                                serviceInfo.get(i).getServiceName().equals(serviceDisplayNameToServiceName.get(item))) {
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

package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A fragment representing a single sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class ScanDeviceSelectSphereFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    public static final String TAG = "ScanDeviceSelectSphereFragment";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link SphereListFragment.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static CallbacksScanDeviceSelectSphereFragment sDummyCallbacks = new CallbacksScanDeviceSelectSphereFragment() {

        @Override
        public void selectedSphereForDevice(String sphereID) {
            // TODO Auto-generated method stub

        }

    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private CallbacksScanDeviceSelectSphereFragment mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScanDeviceSelectSphereFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_scan_device_select_sphere, container, false);

        ListView spehreListView = (ListView) view
                .findViewById(R.id.sphere_list_for_adding);

        //SphereListAdapter sla = new SphereListAdapter(getActivity()
        //		.getApplicationContext(), DummyContent.ITEMS);
        SphereListAdapter sla = null;

        BezirkSphereAPI api = MainService.getSphereHandle();

        List<AbstractSphereListItem> sphereItemList = new ArrayList<AbstractSphereListItem>();

        if (api != null) {
            Iterator<BezirkSphereInfo> sphereInfo = api.getSpheres().iterator();
            // convert the list of BezirkSphereInfo to SphereListItem
            while (sphereInfo.hasNext())
                sphereItemList.add(new SphereListItem(sphereInfo.next()));

        } else {
            Log.e(TAG, "MainService is not available");
        }

        if (sphereItemList != null) {
            sla = new SphereListAdapter(
                    getActivity().getApplicationContext(), sphereItemList);
        } else {
            sla = new SphereListAdapter(
                    getActivity().getApplicationContext(), null);
        }

        spehreListView.setAdapter(sla);
        spehreListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mCallbacks.selectedSphereForDevice(getSphereId(position));

            }
        });

        return view;

    }

    String getSphereId(int position) {
        String itemID = "";
        BezirkSphereAPI api = MainService.getSphereHandle();

        if (api != null) {
            List<BezirkSphereInfo> sphereList = (List) api.getSpheres();
            if (sphereList != null) {
                BezirkSphereInfo sphereInfo = sphereList.get(position);
                if (sphereInfo != null) {
                    itemID = sphereInfo.getSphereID();
                } else {
                    Log.e(TAG, "Unable to get the sphere id from position" + String.valueOf(position));
                }
            } else {
                Log.e(TAG, "Unable to get the sphere list");
            }
        } else {
            Log.e(TAG, "MainService is not available");
        }

        return itemID;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof CallbacksScanDeviceSelectSphereFragment)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (CallbacksScanDeviceSelectSphereFragment) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbacksScanDeviceSelectSphereFragment {
        /**
         * Callback for when an item has been selected.
         */
        void selectedSphereForDevice(String sphereID);

    }

}

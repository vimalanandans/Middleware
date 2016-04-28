package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.spheremanager.ui.SphereListFragment.Callbacks;
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
public class ScanDeviceFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String TAG = "ScanDeviceFragment";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static CallbacksDevice sDummyCallbacks = new CallbacksDevice() {

        @Override
        public void addDevice() {
            // TODO Auto-generated method stub

        }

        @Override
        public void declineDevice() {
            // TODO Auto-generated method stub

        }

        @Override
        public void addDeviceAfterSphereSelection(String id) {
            // TODO Auto-generated method stub

        }
    };
    private RadioButton previousClickedButton = null;
    private String callingActivity;
    private BezirkSphereInfo sphereInfo;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private CallbacksDevice mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScanDeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_device, container,
                false);

        // show different text to add device to sphere and then zirk
        // selecting or to intent sphere selecting and then zirk selecting
        callingActivity = getActivity().getCallingActivity().getClassName();

        /*entry = (SphereListItem) DummyContent.ITEM_MAP.get(getActivity()
                .getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));*/
        String sphereID = getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        final IUhuSphereAPI api = MainService.getSphereHandle();

        List<AbstractSphereListItem> sphereItemList = new ArrayList<AbstractSphereListItem>();

        if (api != null) {

            sphereInfo = api.getSphere(sphereID);

            Iterator<BezirkSphereInfo> sphereInfo = api.getSpheres().iterator();
            // convert the list of BezirkSphereInfo to SphereListItem
            while (sphereInfo.hasNext())
                sphereItemList.add(new SphereListItem(sphereInfo.next()));

        } else {
            Log.e(TAG, "MainService is not available");
        }
        if (sphereInfo == null) {
            Log.e(TAG, "sphere contains : " + sphereID + " not found");
        }

        if (callingActivity
                .equals("DeviceListActivity")) {
            TextView text = (TextView) view
                    .findViewById(R.id.text_catch_device);
            text.setText("Do you want to add this device to the sphere "
                    + sphereInfo.getSphereName() + "?");

        } else {

            // Show sphere selection
            view = inflater.inflate(
                    R.layout.fragment_scan_device_select_sphere, container,
                    false);
            ListView spehreListView = (ListView) view
                    .findViewById(R.id.sphere_list_for_adding);

           /*SelectSphereListAdapter sla = new SelectSphereListAdapter(
					getActivity().getApplicationContext(), DummyContent.ITEMS);*/

            SelectSphereListAdapter sla = null;

            if (sphereItemList != null) {
                sla = new SelectSphereListAdapter(
                        getActivity().getApplicationContext(), sphereItemList);
            } else {
                sla = new SelectSphereListAdapter(
                        getActivity().getApplicationContext(), null);
            }

            spehreListView.setAdapter(sla);
            // spehreListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            spehreListView.setOnItemClickListener(new OnItemClickListener() {

                //TODO Handle event if nothing is selected & backclick
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (api != null) {
                        //entry = (SphereListItem) DummyContent.ITEMS.get(position);
                        List<BezirkSphereInfo> spherList = (List) api.getSpheres();
                        if (spherList != null) {
                            sphereInfo = spherList.get(position);
                        } else {
                            Log.e(TAG, "sphere info is not found in " + String.valueOf(position));
                        }
                    }
                    RadioButton clickedButton = (RadioButton) view
                            .findViewById(R.id.sphere_select_entry);
                    clickedButton.setChecked(true);
                    if (previousClickedButton == null) {
                        previousClickedButton = clickedButton;
                    } else if (previousClickedButton.equals(clickedButton)) {
                        // handle a second click on same entry
                    } else {
                        // set previous selection unchecked
                        previousClickedButton.setChecked(false);
                        previousClickedButton = clickedButton;
                    }

                    // selectedSphereForDevice(DummyContent.ITEMS.get(position).getId());

                }
            });
        }

        Button yes = (Button) view.findViewById(R.id.add_device_yes);
        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callingActivity
                        .equals("DeviceListActivity")) {
                    mCallbacks.addDevice();
                } else {
                    String text = "Please select a sphere.";
                    if (previousClickedButton == null) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                text, Toast.LENGTH_SHORT).show();
                    } else if (previousClickedButton.isChecked()) {
                        mCallbacks.addDeviceAfterSphereSelection(sphereInfo.getSphereID());
                    }
                }

            }
        });
        Button no = (Button) view.findViewById(R.id.add_device_no);
        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallbacks.declineDevice();

            }
        });

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // View lv = getView();
        // // Swipe Detector
        // final SwipeDetector swipeDetector = new SwipeDetector();
        // lv.setOnTouchListener(swipeDetector);
        //
        // Log.v(TAG, "onActivityCreated");
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
        if (!(activity instanceof CallbacksDevice)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (CallbacksDevice) activity;
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
    public interface CallbacksDevice {
        /**
         * Callback for when an item has been selected.
         */
        void addDevice();

        void addDeviceAfterSphereSelection(String id);

        void declineDevice();
    }

}

package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bezirk.spheremanager.ui.listitems.SwipeDetector;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.api.objects.UhuDeviceInfo;
import com.bezirk.api.objects.UhuServiceInfo;
import com.bezirk.api.objects.UhuSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.starter.MainService;

import java.util.List;
import java.util.Map;

/**
 * A fragment representing a single Sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class SelectServiceFragment extends ListFragment {

	public static final String ARG_ITEM_ID = "item_id";

	public static final String TAG = SelectServiceFragment.class.getSimpleName();

	private UhuDeviceInfo item;

	private List<UhuServiceInfo> sl;

    private SharedPreferences sharedPreferences;

    private ServiceAdapter serviceAdapter;
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private DeviceDetailCallbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface DeviceDetailCallbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		void onItemClicked();

		void onSwiped(String direction);
	}

	/**
	 * A dummy implementation of the {@link SphereListFragment.Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static DeviceDetailCallbacks sDummyCallbacks = new DeviceDetailCallbacks() {

		@Override
		public void onItemClicked() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSwiped(String direction) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SelectServiceFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP
		//		.get(getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        String sphereID = getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        UhuSphereInfo sphereInfo  = null;

        IUhuSphereAPI api = MainService.getSphereHandle();

        if(api != null) {
            sphereInfo  = api.getSphere(sphereID);
        }else {
            Log.e(TAG, "MainService is not available");
        }

        if(sphereInfo != null) {
            int devicePos = getActivity().getIntent().getIntExtra(DeviceListActivity.ARG_DEVICE_ID, 0);

            item = (UhuDeviceInfo) sphereInfo.getDeviceList().get(devicePos);

            sl = item.getServiceList();

            serviceAdapter = new ServiceAdapter(getActivity()
                    .getApplicationContext(), sl);
            setListAdapter(serviceAdapter);
        }
        else{
            Log.e(TAG,"Unable to get the sphere info from id "+ String.valueOf(sphereID));
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Log.v(TAG, "onCreate");

	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// return inflater.inflate(R.layout.fragment_devicelist,container,false);
	//
	// }

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView lv = getListView();
		// Swipe Detector
		final SwipeDetector swipeDetector = new SwipeDetector();
		lv.setOnTouchListener(swipeDetector);
//TODO: HAndle Data changes
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action on the list --> Change Sphere
					SwipeDetector.Action direction = swipeDetector.getAction();
					//Log.i("SWIPE", direction.toString());
					//mCallbacks.onSwiped(direction.toString());

				} /*else {
					// do the onItemClick action --> check/uncheck service
					// Notify the active callbacks interface (the activity, if
					// the
					// fragment is attached to one) that an item has been
					// selected.
					CheckBox box = (CheckBox) view
							.findViewById(R.id.check_service);
					UhuServiceInfo service = sl.get(position);
					if (box.isChecked()) {
						service.setActive(false);
						box.setChecked(false);
					} else {
						service.setActive(true);
						box.setChecked(true);
					}
					//sl.add(position, service);

					// TODO: Implementation to change data-source
					// mCallbacks.onItemSelectedDeviceList(deviceName);
				}*/
                showAlertDialog(position);
			}
		});
		/*lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action on the list --> Change Sphere
					Action direction = swipeDetector.getAction();
					Log.i("SWIPE", direction.toString());
					mCallbacks.onSwiped(direction.toString());
					return false;
				} else {
					// do the onItemLongClick action --> do nothing
					return true;
				}

			}
		});*/
		Log.v(TAG, "onActivityCreated");
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
		if (!(activity instanceof DeviceDetailCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (DeviceDetailCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// CheckBox box = (CheckBox) view.findViewById(R.id.check_service);
		// if(box.isChecked()){
		// mCallbacks.onItemClicked();
		// box.setChecked(false);
		// }else{
		// mCallbacks.onItemClicked();
		// box.setChecked(true);
		// }

	}

    private void showAlertDialog(final int position) {
        final EditText editText = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle("Set user defined service name")
                .setView(editText)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savePreference(sl.get(position).getServiceId(),editText.getText().toString());
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void savePreference(String serviceId, String displayName) {
        Map<String, ?> values = sharedPreferences.getAll();
        if(!values.containsValue(displayName)) {
            sharedPreferences.edit().putString(serviceId,displayName).apply();
        }else{
            Toast.makeText(getActivity(), "Name already exists", Toast.LENGTH_SHORT).show();
        }
        serviceAdapter.notifyDataSetChanged();
    }

}

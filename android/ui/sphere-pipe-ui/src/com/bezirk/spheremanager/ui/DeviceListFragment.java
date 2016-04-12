package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.spheremanager.ui.listitems.SwipeDetector;
import com.bezirk.spheremanager.R;
import com.bezirk.api.objects.UhuSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.starter.MainService;

/**
 * A fragment representing a single Sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class DeviceListFragment extends ListFragment {

	public static final String ARG_ITEM_ID = "item_id";

	public static final String TAG = "DeviceListFragment";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private DeviceListFragmentCallbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface DeviceListFragmentCallbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelectedDeviceList(int id);

		void onItemLongClickedDeviceList(int id);
	}

	/**
	 * A dummy implementation of the {@link SphereListFragment.Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static DeviceListFragmentCallbacks sDummyCallbacks = new DeviceListFragmentCallbacks() {
		@Override
		public void onItemSelectedDeviceList(int id) {
		}

		@Override
		public void onItemLongClickedDeviceList(int id) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DeviceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP
				.get(getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));
        */
        String sphereID = getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        IUhuSphereAPI api = MainService.getSphereHandle();

        if(api != null) {

            UhuSphereInfo sphereInfo  = api.getSphere(sphereID);
            if(sphereInfo != null) {
                if(sphereInfo.getDeviceList() != null) {
                    setListAdapter(new DeviceListAdapter(getActivity()
                            .getApplicationContext(), sphereInfo.getDeviceList()));
                }
                Log.i(TAG, "Sphere : "+sphereID+" doesn't contain any device");
            }
            else{
                Log.e(TAG, "Sphere contains : "+sphereID+" not found");
            }

        }else {
            Log.e(TAG,"MainService is not available");
        }

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
        TextView textView = (TextView) getActivity().findViewById(R.id.emptyListContent);
        if(getListAdapter()==null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Sphere doesn't contain any device");
            setListShown(true);
            return;
        }else {
            textView.setVisibility(View.GONE);
        }
		// Swipe Detector
		final SwipeDetector swipeDetector = new SwipeDetector();
		lv.setOnTouchListener(swipeDetector);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action

					mCallbacks.onItemLongClickedDeviceList(position);

				} else {
					// do the onItemClick action

					// Notify the active callbacks interface (the activity, if
					// the
					// fragment is attached to one) that an item has been
					// selected.

					// provice position, to read data in DeviceDetailFragment
					mCallbacks.onItemSelectedDeviceList(position);
				}
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action

					return false;
				} else {
					// do the onItemLongClick action

					mCallbacks.onItemLongClickedDeviceList(position);
					return true;
				}

			}
		});
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
		if (!(activity instanceof DeviceListFragmentCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (DeviceListFragmentCallbacks) activity;
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

	}

}

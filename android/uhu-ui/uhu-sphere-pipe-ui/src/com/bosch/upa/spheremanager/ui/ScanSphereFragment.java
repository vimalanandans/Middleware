package com.bosch.upa.spheremanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.bosch.upa.spheremanager.DeviceListActivity;
import com.bosch.upa.spheremanager.R;
import com.bosch.upa.spheremanager.SphereListActivity;
import com.bosch.upa.spheremanager.ui.SphereListFragment.Callbacks;

/**
 * A fragment representing a single Sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class ScanSphereFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	public static final String TAG = "ScanSphereFragment";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private CallbacksSphere mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface CallbacksSphere {
		/**
		 * Callback for when an item has been selected.
		 */
		void joinSphere();

		void declineSphere();
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static CallbacksSphere sDummyCallbacks = new CallbacksSphere() {

		@Override
		public void joinSphere() {
			// TODO Auto-generated method stub

		}

		@Override
		public void declineSphere() {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ScanSphereFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_scan_sphere, container,
				false);
		Button yes = (Button) view.findViewById(R.id.join_sphere_yes);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallbacks.joinSphere();

			}
		});
		Button no = (Button) view.findViewById(R.id.join_sphere_no);
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallbacks.declineSphere();

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
		if (!(activity instanceof CallbacksSphere)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (CallbacksSphere) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

}

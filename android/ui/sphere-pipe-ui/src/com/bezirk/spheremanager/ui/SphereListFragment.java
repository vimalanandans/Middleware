package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.spheremanager.ui.listitems.SwipeDetector;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list fragment representing a list of Spheres. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link DeviceListFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SphereListFragment extends ListFragment {
    public static final String TAG = "SphereListFragment";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }

        @Override
        public void onItemLongClicked(String id) {
        }
    };
    List<AbstractSphereListItem> sphereList = new ArrayList<AbstractSphereListItem>();
    ListView lv = null;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SphereListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
    /*	setListAdapter(new SphereListAdapter(getActivity()
				.getApplicationContext(), DummyContent.ITEMS));*/

        IUhuSphereAPI api = MainService.getSphereHandle();
        if (api != null) {
            Iterator<UhuSphereInfo> sphereInfo = api.getSpheres().iterator();

            while (sphereInfo.hasNext()) {
                UhuSphereInfo info = sphereInfo.next();

                sphereList.add(new SphereListItem(info));
            }
            setListAdapter(new SphereListAdapter(getActivity()
                    .getApplicationContext(), sphereList));

        } else {
            Log.d(TAG, "main service object is not live. ");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }
        lv = getListView();
        registerForContextMenu(lv);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        // Swipe Detector
        final SwipeDetector swipeDetector = new SwipeDetector();
        lv.setOnTouchListener(swipeDetector);

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    // do the onSwipe action
                    //String itemID = DummyContent.ITEMS.get(position).getId();
                    String itemID = getSphereId(position);

                    mCallbacks.onItemLongClicked(itemID);

                } else {
                    // do the onItemClick action

                    // Notify the active callbacks interface (the activity, if
                    // the
                    // fragment is attached to one) that an item has been
                    // selected.
                    mCallbacks.onItemSelected(sphereList.get(position).getId());
                }
            }
        });
		/*lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
                //String itemID = DummyContent.ITEMS.get(position).getId();
                String itemID = getSphereId(position);
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action
					mCallbacks.onItemLongClicked(itemID);
					return false;
				} else {
					// do the onItemLongClick action
					mCallbacks.onItemLongClicked(itemID);
					return true;
				}

			}
		});*/

    }

    /**
     * returns the sphere id from the given position
     */
    String getSphereId(int position) {
        String itemID = "";
        IUhuSphereAPI api = MainService.getSphereHandle();

        if (api != null) {
            List<UhuSphereInfo> sphereList = (List) api.getSpheres();
            if (sphereList != null) {
                UhuSphereInfo sphereInfo = sphereList.get(position);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    // @Override
    // public void onListItemClick(ListView listView, View view, int position,
    // long id) {
    // super.onListItemClick(listView, view, position, id);
    //
    // // Notify the active callbacks interface (the activity, if the
    // // fragment is attached to one) that an item has been selected.
    // mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).getId());
    // }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);

        void onItemLongClicked(String id);
    }
}

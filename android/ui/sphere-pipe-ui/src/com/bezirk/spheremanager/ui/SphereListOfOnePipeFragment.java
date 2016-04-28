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

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.pipe.core.PipeRecord;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.spheremanager.ui.listitems.SwipeDetector;
import com.bezirk.starter.MainService;

import java.util.ArrayList;
import java.util.List;

/**
 * A list fragment representing a list of Spheres. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link DeviceListFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {Callbacks}
 * interface.
 */
public class SphereListOfOnePipeFragment extends ListFragment {
    public static final String TAG = "SphereListFragment";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the { Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static SphereListOfOnePipeFragmentCallbacks sDummyCallbacks = new SphereListOfOnePipeFragmentCallbacks() {
        @Override
        public void onItemSelected(String id) {
        }

        @Override
        public void onSphereOfOnePipeLongClicked(String id) {
        }
    };
    private int pipeId = 0;
    private List<AbstractSphereListItem> sphereListItems = null;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private SphereListOfOnePipeFragmentCallbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SphereListOfOnePipeFragment() {

    }

    public void setPipeId(int intExtra) {
        this.pipeId = intExtra;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PipeRegistry pipeRegistry = MainService.getPipeRegistryHandle();

        if (pipeRegistry != null) {

            ArrayList<PipeRecord> pipeRecords = new ArrayList<PipeRecord>(pipeRegistry.allPipes());
            if ((pipeRecords != null) &&
                    (pipeRecords.get(pipeId) != null)) {

                // FIXME: Get the list of sphere id and populate
                String sphereId = pipeRecords.get(pipeId).getSphereId();

                sphereListItems = new ArrayList<AbstractSphereListItem>();

                BezirkSphereInfo sphereInfo = MainService.getSphereHandle().getSphere(sphereId);

                sphereListItems.add(new SphereListItem(sphereInfo));

                // TODO: replace with a real list adapter.
                setListAdapter(new SphereListAdapter(getActivity()
                        .getApplicationContext(), sphereListItems));
            } else {
                Log.e(TAG, "No records in pipe registry");
            }
        } else {
            Log.e(TAG, "unable to get pipe registry");
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
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        ListView lv = getListView();
        // Swipe Detector
        final SwipeDetector swipeDetector = new SwipeDetector();
        lv.setOnTouchListener(swipeDetector);

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (sphereListItems != null) {
                        // do the onSwipe action
                        String itemID = sphereListItems.get(position).getId();
                        mCallbacks.onSphereOfOnePipeLongClicked(itemID);
                    }

                } else {
                    // do the onItemClick action

                    // Notify the active callbacks interface (the activity, if
                    // the
                    // fragment is attached to one) that an item has been
                    // selected.
                    mCallbacks.onItemSelected(sphereListItems.get(position)
                            .getId());
                }
            }
        });
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    if (sphereListItems != null) {
                        // do the onSwipe action
                        String itemID = sphereListItems.get(position).getId();
                        mCallbacks.onSphereOfOnePipeLongClicked(itemID);
                    }
                    return false;
                } else {
                    if (sphereListItems != null) {
                        // do the onItemLongClick action
                        String itemID = sphereListItems.get(position).getId();
                        mCallbacks.onSphereOfOnePipeLongClicked(itemID);
                    }
                    return true;
                }

            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof SphereListOfOnePipeFragmentCallbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (SphereListOfOnePipeFragmentCallbacks) activity;
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
    public interface SphereListOfOnePipeFragmentCallbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);

        void onSphereOfOnePipeLongClicked(String id);
    }


}

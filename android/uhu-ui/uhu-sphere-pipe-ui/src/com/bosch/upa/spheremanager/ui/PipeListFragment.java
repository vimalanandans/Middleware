package com.bosch.upa.spheremanager.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;
import com.bosch.upa.spheremanager.dummy.DummyContent;
import com.bosch.upa.spheremanager.ui.listitems.SwipeDetector;
import com.bosch.upa.uhu.pipe.core.PipeRecord;
import com.bosch.upa.uhu.pipe.core.PipeRegistry;
import com.bosch.upa.uhu.starter.MainService;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A list fragment representing a list of Spheres. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link DeviceListFragment}.
 * <p>
 * Activities containing this fragment MUST implement the { Callbacks}
 * interface.
 */
public class PipeListFragment extends ListFragment {
	public static final String TAG = "ShowPipesFragment";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /** Sphere id which holds the pipe list */
    private String sphereId = null;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private ShowPipesCallbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface ShowPipesCallbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(int position);

		void onItemLongClicked(int position);
	}

	/**
	 * A dummy implementation of the {Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static ShowPipesCallbacks sDummyCallbacks = new ShowPipesCallbacks() {
		@Override
		public void onItemSelected(int position) {
		}

		@Override
		public void onItemLongClicked(int position) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PipeListFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        PipeRegistry pipeRegistry  = MainService.getPipeRegistryHandle();

        if(pipeRegistry != null) {

            ArrayList<PipeRecord> pipeRecords =  new ArrayList<PipeRecord> (pipeRegistry.allPipes());

            if (pipeRecords != null && (sphereId != null)) {
                //FIXME: Instead of getting all the pipes. get only which belongs to this sphere
                ArrayList<PipeRecord> pipeRecordsDisplay =  new ArrayList<PipeRecord> ();

                Iterator<PipeRecord>pipeRecordIt = pipeRecords.iterator();

                while(pipeRecordIt.hasNext()){
                    PipeRecord record = pipeRecordIt.next();
                    // add which ever has matching sphere id
                    if(record.getSphereId().equals(sphereId))
                        pipeRecordsDisplay.add(record);
                }
                if(pipeRecordsDisplay.size() > 0 ) // set only if it has elements
                    setListAdapter(new PipeListAdapter(getActivity()
                        .getApplicationContext(), pipeRecordsDisplay));
            }
            else{
                Log.e(TAG,"No records in pipe registry or sphere id is not set");
            }
        }else{
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
        TextView textView = (TextView)getActivity().findViewById(R.id.emptyListContent);
        if(getListAdapter()==null){
            textView.setVisibility(View.VISIBLE);
            textView.setText("No records in pipe registry or sphere id is not set");
            setListShown(true);
            lv.setEmptyView(textView);
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
//					String itemID = DummyContent.ITEMS.get(position).getId();
//					mCallbacks.onItemLongClicked(itemID);

				} else {
					// do the onItemClick action

					// Notify the active callbacks interface (the activity, if
					// the
					// fragment is attached to one) that an item has been
					// selected.
					mCallbacks.onItemSelected(position);
				}
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (swipeDetector.swipeDetected()) {
					// do the onSwipe action
					//String itemID = DummyContent.ITEMS.get(position).getId();
					//mCallbacks.onItemLongClicked(position);
					return false;
				} else {
					// do the onItemLongClick action
					//String itemID = DummyContent.ITEMS.get(position).getId();
					mCallbacks.onItemLongClicked(position);
					return true;
				}

			}
		});

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof ShowPipesCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (ShowPipesCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

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

    /* set the sphere id */
    public void setSphereId(String sphereId){
        this.sphereId = sphereId;
    }
}

package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bezirk.spheremanager.R;

/**
 * A list fragment representing a list of Spheres. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link DeviceListFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link SphereListFragment.Callbacks}
 * interface.
 */
public class ShowCertFragment extends Fragment {
    public static final String TAG = "UpdateUserCredentialsFragment";
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link SphereListFragment.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static ShowCertFragmentCallbacks sDummyCallbacks = new ShowCertFragmentCallbacks() {
        @Override
        public void certUpdated() {
        }

    };
    private int pipeId = 0;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private ShowCertFragmentCallbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShowCertFragment() {

    }

    public void setPipeId(int intExtra) {
        this.pipeId = intExtra;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(
                R.layout.fragment_show_cert, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof ShowCertFragmentCallbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (ShowCertFragmentCallbacks) activity;
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
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface ShowCertFragmentCallbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void certUpdated();

    }

}

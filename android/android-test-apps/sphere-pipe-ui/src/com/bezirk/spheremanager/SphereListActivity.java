package com.bezirk.spheremanager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.DeviceListFragment.DeviceListFragmentCallbacks;
import com.bezirk.spheremanager.ui.DialogAddSphereFragment;
import com.bezirk.spheremanager.ui.DialogServiceListFragment;
import com.bezirk.spheremanager.ui.SphereListFragment;
//import com.bezirk.starter.MainService;
//import com.bezirk.starter.ActionCommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * An activity representing a list of Spheres. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link DeviceListActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SphereListFragment} and the item details (if present) is a
 * {@link DeviceListFragment}.
 * </p>
 * This activity also implements the required
 * {@link SphereListFragment.Callbacks} interface to listen for item selections.
 */
public class SphereListActivity extends FragmentActivity implements
        SphereListFragment.Callbacks, DeviceListFragmentCallbacks,
        DialogAddSphereFragment.addNewSphereCallback {

    static final String TAG = SphereListActivity.class.getSimpleName();

    // for startActivityForResult() in scanCode()
    static final int SCAN_CODE_REQUEST = 1;

    // FIXME: use generic method for share and catch
    private String shareCatchType = "Share";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private SphereIntentSphereListReceiver sphereIntentReceiver = null;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spherelist_smartphone);

        if (findViewById(R.id.sphere_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            /*((SphereListFragment) getFragmentManager().findFragmentById(
                    R.id.sphere_list)).setActivateOnItemClick(true);*/
        }
        if (mTwoPane) {
            getFragmentManager().beginTransaction().add(R.id.sphere_list, new SphereListFragment()).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.smartphone_sphere_list, new SphereListFragment()).commit();
        }

        sphereIntentReceiver = new SphereIntentSphereListReceiver(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sphere_list_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu items for use in the action bar
        // to make sphere management ui as library.
        // the below is changed to if-else solve the compiler error.
        // refer http://stackoverflow.com/questions/9092712/switch-case-statement-error-case-expressions-must-be-constant-expression

        int i = item.getItemId();
        if (i == R.id.action_create_sphere) {
            FragmentManager fm = getSupportFragmentManager();
            DialogAddSphereFragment addFragment = new DialogAddSphereFragment();
            // Show DialogFragment
            addFragment.show(fm, "Dialog Fragment");
            // Callback from Dialog to addNewSphere()
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Callback method from {@link SphereListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (id != null) {
            Intent detailIntent = new Intent(this, DeviceListActivity.class);
            detailIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);

        }
    }

    @Override
    public void onItemLongClicked(String id) {


    }

    @Override
    public void addNewSphere(String name, String type) {
        // add a new sphere
        UUID newID = UUID.randomUUID();
        CreateSphereAsyncTask task = new CreateSphereAsyncTask();
        task.execute(new String[]{name, type});
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the intent to receive the Bezirk sphere Results
        IntentFilter filter = new IntentFilter();
        //filter.addAction(ActionCommands.SPHERE_NOTIFICATION_ACTION);
        registerReceiver(sphereIntentReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister the intent so that it doesn't receive when it goes out of the scope
        unregisterReceiver(sphereIntentReceiver);
    }

    // TODO implement Callbacks if Tablet is used
    @Override
    public void onItemSelectedDeviceList(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemLongClickedDeviceList(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.position == 0) {
            return;
        }
        menu.setHeaderTitle("sphere Actions");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sphere_actions, menu);
        /*final List<BezirkZirkInfo> serviceInfo = MainService.getSphereHandle().getServiceInfo();
        if (serviceInfo == null) {
            menu.getItem(0).setEnabled(false);
        }*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogServiceListFragment dialogServiceListFragment = new DialogServiceListFragment();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
       // Iterator<BezirkSphereInfo> itr = MainService.getSphereHandle().getSpheres().iterator();

        ArrayList<String> arrayList = new ArrayList<String>();
       /* while (itr.hasNext()) {
            arrayList.add(itr.next().getSphereID());
        }*/
        if (item.getTitle().equals("Add services to sphere")) {
            String sphereId = arrayList.get(menuInfo.position);
            dialogServiceListFragment.setSphereId(sphereId);
            dialogServiceListFragment.setTitle("Add services to sphere");
            dialogServiceListFragment.show(fragmentManager, "Dialog Zirk List");
        }
        /**
         * @MCA7KOR Delete functionality not yet available
         */
        /*else if (item.getTitle().equals("Delete sphere")) {
            Toast.makeText(getApplicationContext(), "delete sphere code", Toast.LENGTH_LONG).show();
        }*/
        return true;
    }

    private class CreateSphereAsyncTask extends AsyncTask<String, Void, String> {
        //final SphereAPI sphereAPI = MainService.getSphereHandle();
        SphereAPI sphereAPI=null;
        final List<BezirkZirkInfo> servicesToBeAdded = new ArrayList<BezirkZirkInfo>();
        String sphereId = null;

        @Override
        protected String doInBackground(String... data) {
            String name = data[0];
            String type = data[1];

            if (sphereAPI != null) {

                sphereId = sphereAPI.createSphere(name, type);
                if (sphereId != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mTwoPane) {
                                getFragmentManager().beginTransaction().replace(R.id.sphere_list, new SphereListFragment()).commit();
                            } else {
                                getFragmentManager().beginTransaction().replace(R.id.smartphone_sphere_list, new SphereListFragment()).commit();
                            }
                        }
                    });
                    return name;
                }

            } else {
                Log.e(TAG, "MainService sphere interface not available");
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SphereListActivity.this);
            progressDialog.setMessage("Creating sphere");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "New sphere created as : " + result, Toast.LENGTH_SHORT).show();
                final List<BezirkZirkInfo> serviceInfo = sphereAPI.getServiceInfo();
                if (serviceInfo != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    DialogServiceListFragment dialogServiceListFragment = new DialogServiceListFragment();
                    dialogServiceListFragment.setTitle("Add services to new sphere");
                    dialogServiceListFragment.setSphereId(sphereId);
                    dialogServiceListFragment.show(fragmentManager, "Dialog Zirk List");
                }
            } else {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "sphere not created", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Broadcast event receiver for Bezirk Stack sphere management results
     */
    public class SphereIntentSphereListReceiver extends BroadcastReceiver {
        private final String TAG = "SphereIntentReceiver";

        private SphereListActivity parent = null;

        SphereIntentSphereListReceiver(SphereListActivity activity) {
            parent = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            Log.i(TAG, "Received Intent for Device control >" + intent.getAction());

    /*        String command = intent.getStringExtra(ActionCommands.BEZIRK_ACTION_COMMANDS);

            Log.i(TAG, "Command > " + command);


            if (command.equals(ActionCommands.CMD_SPHERE_DISCOVERY_STATUS)) {
                boolean Status = intent.getBooleanExtra(ActionCommands.BEZIRK_ACTION_COMMAND_STATUS, false);

                if (Status) // when status is true
                {
                    if (parent != null) {
                        //parent.updateContainer();
                    }
                } else {
                    Toast.makeText(parent, "FAILED : " + command, Toast.LENGTH_SHORT).show();
                }

            } else if (command.equals(ActionCommands.CMD_SPHERE_CATCH_STATUS) ||
                    command.equals(ActionCommands.CMD_SPHERE_SHARE_STATUS)) {

                String Status = intent.getStringExtra(ActionCommands.BEZIRK_ACTION_COMMAND_STATUS);
                String message = intent.getStringExtra(ActionCommands.BEZIRK_ACTION_COMMAND_MESSAGE);
                Toast.makeText(parent, message, Toast.LENGTH_LONG).show();
            }*/


        }
    }
}

package com.bezirk.spheremanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.SelectSphereListAdapter;
import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewPipeActivity extends Activity {
    public static final String TAG = "PipeActivity";
    public static final String PIPE_CALLING = "pipe_caller";
    static final int BACK_INTENT = 10;
    private RadioButton previousClickedButton = null;
    private SphereListItem entry;


//	private String callingActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //callingActivity = getCallingActivity().getClassName();
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View) layoutInflater.inflate(R.layout.activity_new_pipe_smartphone, null);

        //Change Text for First Pipe Screen here
        IUhuSphereAPI api = MainService.getSphereHandle();

        if (BezirkValidatorUtility.isObjectNotNull(api)) {

            Iterator<BezirkSphereInfo> sphereInfo = api.getSpheres().iterator();

            final List<AbstractSphereListItem> sphereList = new ArrayList<AbstractSphereListItem>();

            while (sphereInfo.hasNext()) {
                BezirkSphereInfo info = sphereInfo.next();
                sphereList.add(new SphereListItem(info));
            }

            ListView spehreListView = (ListView) view
                    .findViewById(R.id.sphere_list_for_adding);
            SelectSphereListAdapter sla = new SelectSphereListAdapter(getApplicationContext(), sphereList);
            spehreListView.setAdapter(sla);
//		spehreListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            spehreListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long itemId) {

                    entry = (SphereListItem) sphereList.get(position);
                    RadioButton clickedButton = (RadioButton) view
                            .findViewById(R.id.sphere_select_entry);
                    clickedButton.setChecked(true);
                    if (previousClickedButton == null) {
                        previousClickedButton = clickedButton;
                    } else if (previousClickedButton.equals(clickedButton)) {
                        //handle a second click on same entry
                    } else {
                        //set previous selection unchecked
                        previousClickedButton.setChecked(false);
                        previousClickedButton = clickedButton;
                    }

                    // selectedSphereForDevice(DummyContent.ITEMS.get(position).getId());

                }
            });
        } else {

            Log.d(TAG, "main zirk object is not live. ");

        }
        Button yes = (Button) view.findViewById(R.id.add_pipe_yes);
        yes.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View buttonView) {
                String text = "Please select a sphere.";
                if (previousClickedButton == null) {
                    Toast.makeText(getApplicationContext(),
                            text, Toast.LENGTH_SHORT).show();
                } else if (previousClickedButton.isChecked()) {
                    Intent addPipeIntent = new Intent(getApplicationContext(),
                            PipePolicyActivity.class);
                    addPipeIntent.putExtra(DeviceListFragment.ARG_ITEM_ID,
                            entry.getId());
                    startActivity(addPipeIntent);
                    // Toast.makeText(getApplicationContext(),previousClickedButton.getText()
                    // +
                    // entry.getmSphere().sphereName,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            text, Toast.LENGTH_SHORT).show();
                }
                // Intent

            }
        });
        Button no = (Button) view.findViewById(R.id.add_pipe_no);
        no.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View noButtonView) {
                backToLastActivity();

            }
        });

        setContentView(view);

    }


    public void backToLastActivity() {
        Intent backIntent = new Intent(this, SphereListActivity.class);
        // don't remember the history
        backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(backIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

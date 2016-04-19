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
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.SelectSphereListAdapter;
import com.bezirk.spheremanager.ui.listitems.AbstractSphereListItem;
import com.bezirk.spheremanager.ui.listitems.SphereListItem;
import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.starter.MainService;
import com.bezirk.util.UhuValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.bezirk.actions.UhuActions.KEY_PIPE_NAME;
import static com.bezirk.actions.UhuActions.KEY_PIPE_REQ_ID;
import static com.bezirk.actions.UhuActions.KEY_PIPE_SPHEREID;
import static com.bezirk.actions.UhuActions.KEY_SENDER_SERVICE_ID;
import static com.bezirk.util.UhuValidatorUtility.checkForString;
import static com.bezirk.util.UhuValidatorUtility.checkUhuServiceId;

public class PipeActivity extends Activity  {
    private static final Logger log = LoggerFactory.getLogger(PipeActivity.class);

    public static final String TAG = "PipeActivity";
	public static final String PIPE_CALLING = "pipe_caller";
	static final int BACK_INTENT = 10;
	private RadioButton previousClickedButton = null;
	private SphereListItem entry;
    private String sphereId;
	
//	private String callingActivity = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        final Intent rxIntent = getIntent();
        final String pipeName = rxIntent.getStringExtra(KEY_PIPE_NAME);
        final String serviceIdAsString = rxIntent.getStringExtra(KEY_SENDER_SERVICE_ID);
        final String reqId = rxIntent.getStringExtra(KEY_PIPE_REQ_ID);

        /*
         * Validate rxIntent data
         */

        if (!stringsValid(serviceIdAsString, pipeName)) {
            log.error("Intent not valid because rxIntent extra strings not set correctly");
            return ;
        }


        UhuServiceId serviceId = serviceIdFromString(serviceIdAsString);
        if (serviceId == null) {
            log.error("Intent not valid because there was a failure validating serviceId");
            return;
        }

        final String serviceName = UhuCompManager.getSphereForSadl().getServiceName(serviceId);

      	//callingActivity = getCallingActivity().getClassName();
		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = (View) layoutInflater.inflate(R.layout.activity_pipe_smartphone, null);
		
		//Change Text for First Pipe Screen here
		TextView textPipe = (TextView) view.findViewById(R.id.text_pipe);
		textPipe.setText("Service "+ serviceName +" has requested for pipe: "+ pipeName);
		ListView sphereListView = (ListView) view
				.findViewById(R.id.sphere_list_for_adding);


        IUhuSphereAPI api = MainService.getSphereHandle();

        List<AbstractSphereListItem> sphereList = new ArrayList<AbstractSphereListItem>();

        if(api != null)
        {
            Iterator<UhuSphereInfo> sphereInfo =  api.getSpheres().iterator();

            while(sphereInfo.hasNext())
            {
                UhuSphereInfo info = sphereInfo.next();

                sphereList.add(new SphereListItem(info));
            }

        }
        else{
            Log.d(TAG,"main service object is not live. ");
        }
        SelectSphereListAdapter sla = new SelectSphereListAdapter(getApplicationContext(), sphereList);

        sphereListView.setAdapter(sla);
		sphereListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                IUhuSphereAPI api = MainService.getSphereHandle();

                if(UhuValidatorUtility.isObjectNotNull(api)) {

                    List<UhuSphereInfo> sphereInfoList = (List)api.getSpheres();
                    if(sphereInfoList != null) {
                        UhuSphereInfo sphereInfo = sphereInfoList.get(position);
                        sphereId = sphereInfo.getSphereID();
                        entry = new SphereListItem(sphereInfo);
                    }
                    else {
                        Log.e(TAG,"sphere not found at position : "+String.valueOf(position));
                    }
                }else {
                    Log.e(TAG,"MainService is not available");
                }

				RadioButton clickedButton = (RadioButton) view
						.findViewById(R.id.sphere_select_entry);
				clickedButton.setChecked(true);
				if (previousClickedButton == null) {
					previousClickedButton = clickedButton;
				}else if(previousClickedButton.equals(clickedButton)){
					//handle a second click on same entry
				}else {
					//set previous selection unchecked
					previousClickedButton.setChecked(false);
					previousClickedButton = clickedButton;
				}

			}
		});
		Button yes = (Button) view.findViewById(R.id.add_pipe_yes);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view1) {
				String text = "Please select a sphere.";
				if (previousClickedButton == null) {
					Toast.makeText(getApplicationContext(),
							text, Toast.LENGTH_SHORT).show();
				}else if(previousClickedButton.isChecked()){
					Intent addPipeIntent = new Intent(getApplicationContext(),
							PipePolicyActivity.class);
					addPipeIntent.putExtra(DeviceListFragment.ARG_ITEM_ID,
							entry.getId());
                    addPipeIntent.putExtra(KEY_PIPE_NAME, pipeName);
                    addPipeIntent.putExtra(KEY_SENDER_SERVICE_ID, serviceIdAsString);
                    addPipeIntent.putExtra(KEY_PIPE_REQ_ID, reqId);
                    addPipeIntent.putExtra(KEY_PIPE_SPHEREID, sphereId);
                    startActivity(addPipeIntent);
				}else {
					Toast.makeText(getApplicationContext(),
							text, Toast.LENGTH_SHORT).show();
				}
				// Intent

			}
		});
		Button no = (Button) view.findViewById(R.id.add_pipe_no);
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view1) {
				backToLastActivity();

			}
		});

		setContentView(view);
	}


	   
	public void backToLastActivity() {
		Intent backIntent = new Intent(this, SphereListActivity.class);
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

    private UhuServiceId serviceIdFromString(String serviceIdAsString) {
        Gson gson = new Gson();
        UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
        if ( !checkUhuServiceId(serviceId) ) {
            log.error("serviceId not valid: " + serviceId);
            return null;
        }

        return serviceId;
    }

    private boolean stringsValid(String serviceId, String pipeName) {

        boolean stringsValid= true;
        String errorSuffix = "String is null or empty";

        if ( !checkForString(serviceId)) {
            log.error("serviceId " + errorSuffix);
            stringsValid = false;
        }
        if ( !checkForString(pipeName) ) {
            log.error("pipeName " + errorSuffix);
            stringsValid = false;
        }

        return stringsValid;
    }
}

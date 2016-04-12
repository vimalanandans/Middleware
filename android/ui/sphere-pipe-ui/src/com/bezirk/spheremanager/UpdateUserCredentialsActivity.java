package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.DeviceListFragment;
import com.bezirk.spheremanager.ui.DialogShowCertificateFragment;
import com.bezirk.spheremanager.ui.ShowCertFragment;
import com.bezirk.spheremanager.ui.ShowCertFragment.ShowCertFragmentCallbacks;
import com.bezirk.spheremanager.ui.UpdateUserCredentialsFragment;
import com.bezirk.spheremanager.ui.UpdateUserCredentialsFragment.UpdateUserCredentialsFragmentCallbacks;
import com.bezirk.pipe.core.PipeRecord;
import com.bezirk.pipe.core.PipeRegistry;
import com.bezirk.starter.MainService;

import java.util.ArrayList;

public class UpdateUserCredentialsActivity extends FragmentActivity implements UpdateUserCredentialsFragmentCallbacks, ShowCertFragmentCallbacks {
    private final String TAG = UpdateUserCredentialsActivity.class.getName();
    private String filterSetting = "login";
	private String callingActivity = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view;
		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (View) layoutInflater.inflate(
				R.layout.activity_update_user_credentials_smartphone, null);
		Button showCertificate = (Button) view.findViewById(R.id.filter_view_cert);
		showCertificate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				DialogShowCertificateFragment certificateFragment = new DialogShowCertificateFragment();
				certificateFragment.setText("SSL Certificate Text \n Bacon ipsum dolor amet doner ham hock jowl meatball chicken, pig ground round bacon bresaola tri-tip pork loin pork belly filet mignon sirloin strip steak. Frankfurter ground round hamburger sirloin pork shankle kielbasa turkey ball tip kevin. Ball tip meatball pastrami tail frankfurter sausage. ");
				certificateFragment.show(fm, "Dialog Fragment");
				
			}
		});
		TextView title = (TextView) view.findViewById(R.id.name_of_pipe);
		final int pipeId = getIntent().getIntExtra("pipeId", 0);

        PipeRegistry pipeRegistry  = MainService.getPipeRegistryHandle();

        if(pipeRegistry != null) {

            ArrayList<PipeRecord> pipeRecords = new ArrayList<PipeRecord>(pipeRegistry.allPipes());

            if ((pipeRecords != null) &&
                    (pipeRecords.get(pipeId) != null)) {

                title.setText(pipeRecords.get(pipeId).getPipe().getName());
            }
            else{
                Log.e(TAG, "No records in pipe registry");
            }
        }
        else{
            Log.e(TAG,"unable to get pipe registry");
        }

		//Switch has to be enabled
		final Button filterLogin = (Button) view
				.findViewById(R.id.filter_login);
		final Button filterCert = (Button) view
				.findViewById(R.id.filter_view_cert);

		filterLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (filterSetting.equals("login")) {
					// do nothing
				} else {
					filterSetting = "login";
					// set color active
					filterLogin.setBackgroundColor(getResources().getColor(
							com.bezirk.spheremanager.R.color.buttonActive));
					filterCert
							.setBackgroundColor(getResources()
									.getColor(
											com.bezirk.spheremanager.R.color.buttonInactive));
					updateContainer();
				}
			}
		});
		filterCert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (filterSetting.equals("cert")) {
					// do nothing
				} else {
					filterSetting = "cert";
					// set color active
					filterLogin
							.setBackgroundColor(getResources()
									.getColor(
											com.bezirk.spheremanager.R.color.buttonInactive));
					filterCert.setBackgroundColor(getResources().getColor(
							com.bezirk.spheremanager.R.color.buttonActive));
					updateContainer();
				}
			}
		});



		updateContainer();


		setContentView(view);
	}


	private void updateContainer() {
		if (filterSetting == "login") {
			//PolicyListFragment has to be replaced to show new DataStructure
			UpdateUserCredentialsFragment updateUserCredentialsFragment = new UpdateUserCredentialsFragment();
			updateUserCredentialsFragment.setPipeId(getIntent().getIntExtra("pipeId", 1));
			FragmentManager fm = getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction ft = fm
					.beginTransaction();
			ft.replace(R.id.user_container_in_activity,
					updateUserCredentialsFragment);
			ft.commit();
		} else {
			ShowCertFragment showCert = new ShowCertFragment();
//			updateUserCredentialsFragment.setPipeId(getIntent().getIntExtra("pipeId", 1));
			FragmentManager fm = getSupportFragmentManager();
			android.support.v4.app.FragmentTransaction ft = fm
					.beginTransaction();
			ft.replace(R.id.user_container_in_activity,
					showCert);
			ft.commit();
		}

	}
	public Intent createBackToLastActivityIntent() {
		callingActivity = getCallingActivity().getClassName();
		Intent backIntent = new Intent(this, PipeListActivity.class);
		if (callingActivity
				.equals("PipeListActivity")) {
			backIntent = new Intent(this, PipeListActivity.class);
            backIntent.setFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
		} else if (callingActivity
				.equals("DeviceListActivity")) {
			backIntent = new Intent(this, DeviceListActivity.class);
            backIntent.setFlags(backIntent.getFlags()| Intent.FLAG_ACTIVITY_NO_HISTORY);
			backIntent.putExtra(DeviceListFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(DeviceListFragment.ARG_ITEM_ID));
		}
		return backIntent;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_user_credentials, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//

			NavUtils.navigateUpTo(this, createBackToLastActivityIntent());
			return true;
		}

		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// change action of back button
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				NavUtils.navigateUpTo(this, createBackToLastActivityIntent());
				return true;
			}

			return super.onKeyDown(keyCode, event);
		}



	@Override
	public void passwordUpdated() {
		Toast.makeText(getApplicationContext(), "Password updated" , Toast.LENGTH_SHORT).show();
		
	}


	@Override
	public void certUpdated() {
		// TODO Auto-generated method stub
		
	}
}

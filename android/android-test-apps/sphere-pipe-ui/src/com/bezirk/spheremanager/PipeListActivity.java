package com.bezirk.spheremanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.ui.PipeListFragment;
import com.bezirk.spheremanager.ui.PipeListFragment.ShowPipesCallbacks;
//import com.bezirk.starter.MainService;

import java.util.ArrayList;


public class PipeListActivity extends FragmentActivity implements ShowPipesCallbacks {
    static final int PipeListActivityRequestCode = 20;
    static final int PipeListActivityRequestCodeToUserCredentials = 21;
    final String TAG = PipeListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = (View) layoutInflater.inflate(
                R.layout.activity_pipelist_smartphone, null);
        PipeListFragment showPipesFragment = new PipeListFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.pipe_container_in_activity, showPipesFragment);
        fragmentTransaction.commit();

        setContentView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_pipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(int position) {

        Intent pipeManagementIntent = new Intent(getApplicationContext(), ManagePipesActivity.class);

        pipeManagementIntent.putExtra("pipeId", position);
        startActivityForResult(pipeManagementIntent,
                PipeListActivityRequestCode);
//		Toast.makeText(getApplicationContext(), "Pipe "+ DummyContent.pipeList.get(position).getPipe().getName()+ " selected" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClicked(int position) {/*

        PipeRegistry pipeRegistry = MainService.getPipeRegistryHandle();

        if (pipeRegistry != null) {

            ArrayList<PipeRecord> pipeRecords = new ArrayList<PipeRecord>(pipeRegistry.allPipes());

            if ((pipeRecords != null) &&
                    (pipeRecords.get(position) != null)) {

                PipeRecord pipeRecord = pipeRecords.get(position);

                if (pipeRecord.getPassword() != null) {
                    Intent pipeManagementIntent = new Intent(getApplicationContext(), UpdateUserCredentialsActivity.class);
                    pipeManagementIntent.putExtra("pipeId", position);
                    startActivityForResult(pipeManagementIntent,
                            PipeListActivityRequestCodeToUserCredentials);
                    //			Toast.makeText(getApplicationContext(), "Long press to provide or change the username/password or view any SSL cert associated with it" , Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "No password in pipe record");
                }
            } else {
                Log.e(TAG, "No records in pipe registry");
            }
        } else {
            Log.e(TAG, "unable to get pipe registry");
        }*/
    }
}

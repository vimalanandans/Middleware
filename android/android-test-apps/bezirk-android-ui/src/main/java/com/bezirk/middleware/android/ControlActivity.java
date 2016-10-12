package com.bezirk.middleware.android;
/**
 * Bezirk Control UI Application to launch Bezirk-Android Stack
 * Vimal
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bezirk.middleware.android.ui.R;

public class ControlActivity extends AppCompatActivity {

    private ControlActivityHelper controlActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controlActivityHelper = new ControlActivityHelper(this);
        setContentView(R.layout.activity_main);

        // initialize UI
        controlActivityHelper.initUI();

        //setup bezirk
        controlActivityHelper.bezirkInitialization(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ctrl_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.warning) {
            controlActivityHelper.showAlertDialogToShowSystemStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem warningMenuItem = menu.findItem(R.id.warning);
        if (controlActivityHelper.stackVersionMismatch) {
            warningMenuItem.setVisible(true);
        } else {
            warningMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(controlActivityHelper.systemStatusBroadcastReceiver);
        BezirkMiddleware.stop();
    }
}

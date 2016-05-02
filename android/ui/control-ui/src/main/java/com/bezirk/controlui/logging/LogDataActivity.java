package com.bezirk.controlui.logging;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.bezirk.controlui.R;
import com.bezirk.remotelogging.manager.BezirkLoggingManager;
import com.bezirk.starter.MainService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activity that is used to display the logger messages.
 */
public class LogDataActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(LogDataActivity.class);

    /**
     * RESULT CODE to send the data to back activity.
     */
    private final int RESULT_CODE_FOR_RETURN_ACTIVITY = 122;


    boolean isDeveloperModeEnabled;
    /**
     * List of selected Spheres
     */
    String[] selSpheres;

    /**
     * BezirkLoggingManager that manages the LoggingService
     */
    BezirkLoggingManager mBezirkLoggingManager;

    private LogDataActivityHelper logDataActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logDataActivityHelper = new LogDataActivityHelper(this);
        setContentView(R.layout.activity_log_data);
        logDataActivityHelper.startLogService();
        Intent receivedIntent = getIntent();
        isDeveloperModeEnabled = receivedIntent.getBooleanExtra("isDeveloperModeEnabled", false);
        logDataActivityHelper.sendLogServiceMsg(receivedIntent.getStringArrayExtra("selectedSphereList"), receivedIntent.getBooleanExtra("isAnySphereFlag", false));
        logDataActivityHelper.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.stop_logging) {
            logDataActivityHelper.showConfirmDialogToStopLogging();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            logDataActivityHelper.showConfirmDialogToStopLogging();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mBezirkLoggingManager != null) {
            try {
                mBezirkLoggingManager.stopLoggingService();
                new ShutDownLoggingServiceTask().execute(selSpheres);
                logDataActivityHelper.printToast("STOPPING LOG SERVICE ABRUPTLY...");
                logDataActivityHelper.mHandler = null;
            } catch (Exception e) {
                logDataActivityHelper.printToast("ERROR IN STOPPING LOG SERVICE...");
                logger.error("Error in stopping logger zirk.", e);
            }
        }
        setResult(RESULT_CODE_FOR_RETURN_ACTIVITY);
        finish();
        super.onDestroy();
    }

    /**
     * Task that is used to send LoggingMessage across all spheres.
     */
    class ShutDownLoggingServiceTask extends AsyncTask<String[], Void, Void> {
        private String[] spheres;

        @Override
        protected Void doInBackground(String[]... params) {
            spheres = params[0];
            // it is not a good idea to access the main zirk directly. the best way to do is via IBinder
            MainService.sendLoggingServiceMsgToClients(selSpheres, spheres, false);
            return null;
        }
    }
}

/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.controlui.logging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.controlui.R;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.remotelogging.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Activity that displays the list of available spheres and allows the user to select the sphere.
 * and also provides an option to start the logging.
 */
public class SphereSelectLoggingActivity extends ActionBarActivity {
    private static final Logger logger = LoggerFactory.getLogger(SphereSelectLoggingActivity.class);
    /**
     * RESULT code to send intent to LogDataActivity.
     */
    private final int RESULT_CODE_FOR_THIS_ACTIVITY = 122;
    /**
     * Handler Values
     */
    private final int FETCH_SUCCESSFUL = 101;
    private final int FETCH_ERROR = 102;
    /**
     * Max no of taps to enable developer mode
     */
    private final int NO_OF_TAPS_FOR_ENABLING_DEVELOPER_MODE = 7;
    /**
     * list of all the available Spheres
     */
    private final List<String> sphereList = new ArrayList<String>();
    /**
     * list of all the selected Spehres.
     */
    private final List<String> selectedSpheres = new ArrayList<String>();
    /**
     * Handler that is used to update the UI
     */
    private Handler mHandler;
    /**
     * Layout that is used to populate the sphere dynaically.
     */
    private LinearLayout mLinearLayoutSphereList;
    /**
     * TextView that display the Bezirk version at the bottom of the screen
     */
    private TextView mTextViewLoggingVersion, mTextViewDevModeEnableLabel;
    /**
     * sphere Check Box
     */
    private CheckBox mCheckBoxSpheres[];
    /**
     * Callback when CheckBox is clicked
     */
    private final View.OnClickListener checkBoxClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            final CheckBox checkBox = (CheckBox) view;
            final String checkBoxText = checkBox.getText().toString();

            mLinearLayoutSphereList.post(new Runnable() {
                @Override
                public void run() {
                    if (checkBox.isChecked()) {//when ticked
                        if (checkBox.getText().equals(Util.ANY_SPHERE)) {
                            enableDisableSphereList(false);
                            selectedSpheres.clear();
                            selectedSpheres.addAll(sphereList);
                        }
                        selectedSpheres.add(checkBoxText);
                    } else {// when unticked
                        if (checkBox.getText().equals(Util.ANY_SPHERE)) {
                            enableDisableSphereList(true);
                            selectedSpheres.clear();
                        }
                        selectedSpheres.remove(checkBoxText);
                    }
                    logger.info(selectedSpheres.toString());
                }
            });


        }
    };
    /**
     * Handle the callback. setup all the checkbox dynamically to the linear layout.
     */
    private final Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FETCH_SUCCESSFUL:
                    logger.debug("Size->" + sphereList.size());
                    addView();
                    break;
                case FETCH_ERROR:
                    printToast("Cannot fetch Spheres");
                    break;
                default:
            }
            return true;
        }

        private void addView() {
            mLinearLayoutSphereList.post(new Runnable() {
                @Override
                public void run() {
                    mCheckBoxSpheres = new CheckBox[sphereList.size() + 1];
                    mCheckBoxSpheres[0] = getCheckBox(Util.ANY_SPHERE);
                    mLinearLayoutSphereList.addView(mCheckBoxSpheres[0]);
                    for (int i = 0; i < sphereList.size(); i++) {
                        mCheckBoxSpheres[i + 1] = getCheckBox(sphereList.get(i));
                        mLinearLayoutSphereList.addView(mCheckBoxSpheres[i + 1]);
                    }
                }
            });
        }
    };
    /**
     * count of no of taps
     */
    private int noOfTaps = NO_OF_TAPS_FOR_ENABLING_DEVELOPER_MODE;
    /**
     * Developermode Flag
     */
    private boolean isDeveloperModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sphere_select_logging);
        init();
    }

    /**
     * Init the UI and setup the Handler
     */
    private void init() {
        mLinearLayoutSphereList = (LinearLayout) findViewById(R.id.sphereCheckBoxListLayout);

        mTextViewLoggingVersion = (TextView) findViewById(R.id.loggingVersionLabel);
        mTextViewLoggingVersion.setText("Version: " + Util.LOGGING_VERSION + " (Beta)");
        mTextViewDevModeEnableLabel = (TextView) findViewById(R.id.devModeEnableLabel);
        mHandler = new Handler(handlerCallback);
    }

    /**
     * Callback when Get Spheres Button is clicked
     */
    public void fetchSpheresButtonClick(View view) {
        sphereList.clear();
        mLinearLayoutSphereList.removeAllViews();
        new FetchSpheresTask().execute();
    }

    /**
     * Callback when Start Logging Button is clicked
     */
    public void startLoggingButtonClick(View view) {
        if (selectedSpheres.isEmpty()) {
            printToast("Select a sphere from the list");
            return;
        }
        boolean isAnySphereFlag = false;
        if (selectedSpheres.contains(Util.ANY_SPHERE)) {
            isAnySphereFlag = true;
            selectedSpheres.remove(Util.ANY_SPHERE);
        }
        String[] tempSphereList = new String[selectedSpheres.size()];
        for (int i = 0; i < tempSphereList.length; i++) {
            tempSphereList[i] = selectedSpheres.get(i);
        }
        Intent logDataActivity = new Intent(SphereSelectLoggingActivity.this, LogDataActivity.class);
        logDataActivity.putExtra("selectedSphereList", tempSphereList);
        logDataActivity.putExtra("isAnySphereFlag", isAnySphereFlag);
        logDataActivity.putExtra("isDeveloperModeEnabled", isDeveloperModeEnabled);
        startActivityForResult(logDataActivity, RESULT_CODE_FOR_THIS_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_FOR_THIS_ACTIVITY) {
            //TODO Close
            this.finish();
        }
    }

    /**
     * Returns a checkBox
     */
    private CheckBox getCheckBox(final String sphereName) {
        if (sphereName == null)
            return null;
        CheckBox checkBox = new CheckBox(SphereSelectLoggingActivity.this);
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox.setText(sphereName);
        checkBox.setOnClickListener(checkBoxClickListener);
        return checkBox;
    }

    /**
     * Method that is used to enable and disable all the sphere check boxes
     */
    private void enableDisableSphereList(final boolean isEnable) {
        final int size = sphereList.size();
        mLinearLayoutSphereList.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < size; i++) {
                    mCheckBoxSpheres[i + 1].setChecked(!isEnable);
                    mCheckBoxSpheres[i + 1].setEnabled(isEnable);
                }
            }
        });

    }

    public void developerModeTapClick(View view) {
        --noOfTaps;
        if (noOfTaps == 0) {
            isDeveloperModeEnabled = true;
            noOfTaps = NO_OF_TAPS_FOR_ENABLING_DEVELOPER_MODE;
            mTextViewDevModeEnableLabel.setVisibility(View.VISIBLE);
            view.setEnabled(false);
        } else {
            printToast(noOfTaps + " taps away from enabling logging for developer mode");
        }
    }

    /**
     * Prints the TOast msg
     */
    private void printToast(String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Task that is used to fetch the Spheres from the sphere Module
     */
    protected class FetchSpheresTask extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SphereSelectLoggingActivity.this);
            mProgressDialog.setMessage("Fetching Spheres...");
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Iterator<BezirkSphereInfo> sphereInfoIterator = BezirkCompManager.getSphereUI().getSpheres().iterator();
                while (sphereInfoIterator.hasNext()) {
                    sphereList.add(sphereInfoIterator.next().getSphereID());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return FETCH_ERROR;
            }
            return FETCH_SUCCESSFUL;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if (mProgressDialog != null) {
                mHandler.sendEmptyMessage(status);
                mProgressDialog.cancel();
                mProgressDialog = null;
            }
        }
    }

}

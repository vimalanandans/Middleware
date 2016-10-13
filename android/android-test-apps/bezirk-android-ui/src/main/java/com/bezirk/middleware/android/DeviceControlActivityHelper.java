/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bezirk.middleware.android.ui.R;
import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class DeviceControlActivityHelper {
    private static final Logger logger = LoggerFactory.getLogger(DeviceControlActivityHelper.class);

    // result codes
    private final int RESULT_DEVICE_NAME_CHANGE = 1000;

    private final int RESULT_SPHERE_NAME_CHANGE = 1001;

    private final int RESULT_DATABASE_CLEAR = 1002;

    private final DeviceControlActivity deviceControlActivity;
    private final Context context;

    DeviceControlActivityHelper(DeviceControlActivity deviceControlActivity, Context context) {
        this.deviceControlActivity = deviceControlActivity;
        this.context = context;
    }

    /**
     * Handle the device click
     */
    void deviceControlListClick(List<DataModel> listData, int position) {
        String actions = null;
        // we selecting based on image id hence list must have image id and it has to be unique
        switch (listData.get(position).getImageId()) {
            case R.drawable.upa_control: // Bezirk On/OFF
                // NOT used. we are handling the toggle button inside the onItemToggleListener
                break;
            case R.drawable.ic_device_name: //Set Device Name
                promptSettingTextChange(listData.get(position).getTitleText(), RESULT_DEVICE_NAME_CHANGE);
                break;
            case R.drawable.ic_action_device_type:
                // set device type
                Intent activityIntent = new Intent(deviceControlActivity, DeviceTypeSelection.class);
                // Activity is started with requestCode 2
                deviceControlActivity.startActivityForResult(activityIntent,
                        DeviceTypeSelection.RESULT_DEVICE_ITEM_SELECT);
                break;
            case R.drawable.ic_action_sphere_name: // set default sphere name
                promptSettingTextChange(listData.get(position).getTitleText(), RESULT_SPHERE_NAME_CHANGE);
                break;
            case R.drawable.ic_action_sphere_type: // set default sphere name
                actions = BezirkAction.ACTION_CHANGE_SPHERE_TYPE.getName();
                break;
            case R.drawable.ic_delete_database:
                promptClearConfirmation(listData.get(position).getTitleText(),
                        "Do you want to Clear the sphere data?", RESULT_DATABASE_CLEAR);
                break;
            case R.drawable.ic_action_diag: // diag // to be implemented
                break;
            default:
                logger.error("Unknown item pressed");
                return;
        }

        if (ValidatorUtility.isObjectNotNull(actions)) {
            Intent intent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);
            intent.setAction(actions);
            deviceControlActivity.startService(intent);
        }
    }

    /**/
    private void onPromptTextResult(int resultCode, String data) {
        // check if the request code is same as what is passed  here it is RESULT_DEVICE_ITEM_SELECT
        switch (resultCode) {
            case RESULT_DEVICE_NAME_CHANGE: //result for device type selection
                setDeviceName(data);
                break;
            case RESULT_SPHERE_NAME_CHANGE: //result for device type selection
                setDefaultSphereName(data);
                break;
            case RESULT_DATABASE_CLEAR:
                clearDataBase();
                break;
            default:
                logger.error("unknown result from settings prompt");
                break;
        }
    }

    /**
     * clear the device database
     */
    private void clearDataBase() {
        String actions = BezirkAction.ACTION_CLEAR_PERSISTENCE.getName();
        Intent intent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);

        intent.setAction(actions);
        deviceControlActivity.startService(intent);
    }

    /**
     * store the device type and invoke the services
     */
    void setDeviceType(String deviceType) {
        logger.info("device type changed to " + deviceType);
        // store the device type
        //preferences.putString(MainStackPreferences.DEVICE_TYPE_TAG_PREFERENCE, deviceType);

        String actions = BezirkAction.ACTION_CHANGE_DEVICE_TYPE.getName();
        Intent intent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);

        intent.setAction(actions);
        deviceControlActivity.startService(intent);
    }

    // set the device name
    private void setDeviceName(String deviceName) {
        // TODO: Currently this stored local to stack not sent to other devices.
        // needs to fix this in the stack

        logger.info("device name changed to " + deviceName);

        // store the device name
        //preferences.putString(MainStackPreferences.DEVICE_NAME_TAG_PREFERENCE, deviceName);

        String actions = BezirkAction.ACTION_CHANGE_DEVICE_NAME.getName();
        Intent intent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);

        intent.setAction(actions);
        deviceControlActivity.startService(intent);
    }

    // set the device name
    private void setDefaultSphereName(String sphereName) {
        // TODO: Currently this stored local to stack not sent to other devices.
        // needs to fix this in the stack

        logger.info("default sphere name changed to " + sphereName);

        // store the device name
        //preferences.putString(MainStackPreferences.DEFAULT_SPHERE_NAME_TAG_PREFERENCE, sphereName);

        String actions = BezirkAction.ACTION_CHANGE_SPHERE_NAME.getName();
        Intent intent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);

        intent.setAction(actions);
        deviceControlActivity.startService(intent);
    }

    // prompt for text edit box
    private void promptSettingTextChange(final String titleText, final int resultId) {
// get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.prompt_text, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set the title
        final TextView userInputTitle = (TextView) promptsView
                .findViewById(R.id.promptTextHeader);

        userInputTitle.setText(titleText);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.promptEditTextDialog);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int clickId) {
                                // get user input and set it to result
                                String data = userInput.getText().toString();
                                logger.info("device name is set to " + data);
                                onPromptTextResult(resultId, data);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int clickId) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // prompt for confirmation box
    private void promptClearConfirmation(final String titleText, final String confirmText, final int resultId) {
//// get prompts.xml view
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View promptsView = layoutInflater.inflate(R.layout.prompt_confirm, null);
//        SphereAPI sphereAPI = ComponentManager.getSphereHandle();
//        final List<BezirkZirkInfo> bezirkZirkInfos = sphereAPI.getServiceInfo();
//        final SharedPreferences sharedPrefs =
//               PreferenceManager.getDefaultSharedPreferences(deviceControlActivity.getApplicationContext());
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                context);
//
//        // set prompts.xml to alertdialog builder
//        alertDialogBuilder.setView(promptsView);
//
//        // set the title
//        final TextView userInputTitle = (TextView) promptsView
//                .findViewById(R.id.promptConfirmTextDialog);
//
//        userInputTitle.setText(titleText);
//
//        final TextView confirmTextView = (TextView) promptsView
//                .findViewById(R.id.promptConfirmTextDialog);
//        userInputTitle.setText(confirmText);
//        // set dialog message
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int clickId) {
//                                // get user input and set it to result
//                                String data = confirmTextView.getText().toString();
//                                logger.info("clear database confirmed " + data);
//                                onPromptTextResult(resultId, data);
//                                if (bezirkZirkInfos != null && !bezirkZirkInfos.isEmpty()) {
//                                    for (BezirkZirkInfo info : bezirkZirkInfos) {
//                                        if (sharedPrefs.getAll().containsKey(info.getZirkId())) {
//                                            sharedPrefs.edit().remove(info.getZirkId()).apply();
//                                        }
//                                    }
//                                }
//                            }
//                        })
//                .setNegativeButton("No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int clickId) {
//                                dialog.cancel();
//                            }
//                        });
//
//        // create alert dialog
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
    }

    /**
     * Get status of development mode by sending an broadcast intent
     */
    void getStatus() {
        Intent devIntent = new Intent(deviceControlActivity.getApplicationContext(), ComponentManager.class);
        devIntent.setAction(BezirkAction.ACTION_DEV_MODE_STATUS.getName());
        deviceControlActivity.startService(devIntent);
    }

    /**
     * Update/add the development mode information to the list
     */
//    void updateList(DevMode.Mode mode, List<DataModel> listData) {
//        if(mode == null)
//            return;
//
//        logger.debug("mode received: " + mode);
//
//
//        boolean switchState = false;
//        switch (mode) {
//            case ON:
//                switchState = true;
//                break;
//            default:
//        }
//        listData.add(new DataModel(R.drawable.ic_action_dev_mode, "Developer Mode",
//                "Common sphere across devices", true, switchState, false));
//
//    }

}

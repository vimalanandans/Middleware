package com.bezirk.controlui.logging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.UhuComms;
import com.bezirk.controlui.R;
import com.bezirk.remotelogging.loginterface.IUhuLogging;
import com.bezirk.remotelogging.manager.UhuLoggingManager;
import com.bezirk.remotelogging.messages.UhuLoggingMessage;
import com.bezirk.remotelogging.util.Util;
import com.bezirk.starter.MainService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AJC6KOR on 12/29/2015.
 */
class LogDataActivityHelper {
    /**
     * Logging Util.
     */
    private static final Logger log = LoggerFactory.getLogger(LogDataActivityHelper.class);
    /**
     * ANY_SPHERE label
     */
    private final String[] ANY_SPHERE_VALUE = {Util.ANY_SPHERE};
    /**
     * To print the timestamp of the recieved msg
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS", Locale.ENGLISH);

    /**
     * CONSTANT for New Row
     */
    private final int NEW_ROW_FOR_TABLE = -1,
    /**
     * SIZE of the Linked HashMap to keep track of the MsgId
     */
    SIZE_OF_LOG_MSG_MAP = 128;
    /**
     * Value to be displayed for the Recipient during MULTICAST
     */
    private final String RECIPIENT_MULTICAST_VALUE = "MULTI-CAST";
    /**
     * ConcurrentHashMap that is used to store the logger messages and update them.
     */
    private final ConcurrentMap<String, Integer> logMsgMap = new ConcurrentHashMap<String, Integer>(SIZE_OF_LOG_MSG_MAP);
    private final LogDataActivity logDataActivity;
    /**
     * UI Handler
     */
    Handler mHandler;
    /**
     * IUhuLogging Implementation to handle the logmessage. It receives the logger message and gives it to the handler to update the UI.
     */
    private final IUhuLogging loggingHandler = new IUhuLogging() {
        @Override
        public void handleLogMessage(UhuLoggingMessage uhuLogMessage) {
            Message msg = mHandler.obtainMessage();
            msg.obj = uhuLogMessage;
            mHandler.sendMessage(msg);
        }
    };
    /**
     * Integer value referring to the row tag, that will be assigned to each of the logger table row
     */
    private int tableRowTagCount;
    /**
     * Table Layout that displays the logs.(defined in the xml)
     */
    private TableLayout mTableLayoutLogData;
    /**
     * Handle the callback. Update the table layout with the logger
     */
    final Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(final Message msg) {
            UhuLoggingMessage logMsg = (UhuLoggingMessage) msg.obj;

            if (!logDataActivity.isDeveloperModeEnabled && (logMsg.typeOfMessage.equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name()) ||
                    logMsg.typeOfMessage.equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_SEND.name()))) {
                return true;
            }
            int rowTagVal = getLogRowPosition(logMsg.uniqueMsgId, logMsg.sphereName);
            if (NEW_ROW_FOR_TABLE == rowTagVal)
                mTableLayoutLogData.addView(getLogTableRow(logMsg));
            else
                updateTableRow(rowTagVal);
            return true;
        }
    };

    LogDataActivityHelper(LogDataActivity logDataActivity) {
        this.logDataActivity = logDataActivity;
    }

    /**
     * Init the UI and setup the Handler
     */
    void init() {
        mHandler = new Handler(handlerCallback);
        mTableLayoutLogData = (TableLayout) logDataActivity.findViewById(R.id.logdataTableLayout);
    }

    /**
     * Returns the row tag assicociated with the table row.
     *
     * @param logMsgId - Message Id of the logger msg
     * @param sphereId - of the logger msg
     * @return the row tag if already updted on the table else NEW_ROW_FOR_TABLE value
     */
    Integer getLogRowPosition(final String logMsgId, final String sphereId) {
        StringBuilder tempMapKey = new StringBuilder();
        tempMapKey.append(sphereId).append(":").append(logMsgId);

        if (logMsgMap.containsKey(tempMapKey.toString()))
            return logMsgMap.get(tempMapKey.toString());
        ++tableRowTagCount;
        if (logMsgMap.size() == SIZE_OF_LOG_MSG_MAP) {
            logMsgMap.remove(logMsgMap.keySet().iterator().next());
        }
        logMsgMap.put(tempMapKey.toString(), tableRowTagCount);
        return NEW_ROW_FOR_TABLE;
    }

    /**
     * Updates the row of the table by incrementing the value
     *
     * @param rowTagValue tag of the row that needs to be updated
     */
    void updateTableRow(final int rowTagValue) {
        try {
            TableRow updatingTableRow = (TableRow) mTableLayoutLogData.findViewWithTag(String.valueOf(rowTagValue));
            TextView countTextView = (TextView) updatingTableRow.getChildAt(5); // 5th Clid
            int countValue = Integer.valueOf(countTextView.getText().toString()) + 1;
            countTextView.setText(String.valueOf(countValue));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            printToast("Error in updating table Row");
        }
    }

    /**
     * Starts the Logging Service and LogReceiverProcessor.
     */
    void startLogService() {
        try {
            logDataActivity.mUhuLoggingManager = new UhuLoggingManager();
            logDataActivity.mUhuLoggingManager.startLoggingService(UhuComms.getREMOTE_LOGGING_PORT(), loggingHandler);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Send the LogServiceMsg across all the spheres.
     */
    void sendLogServiceMsg(final String[] selectedSphereList, boolean isAnySphereSelectedFlag) {
        String[] tempLoggingSphereList = null;
        if (isAnySphereSelectedFlag) {
            tempLoggingSphereList = ANY_SPHERE_VALUE;
        } else {
            tempLoggingSphereList = selectedSphereList;
        }
        logDataActivity.selSpheres = selectedSphereList.clone();

        // it is not a good idea to access the main service directly. the best way to do is via IBinder
        MainService.sendLoggingServiceMsgToClients(logDataActivity.selSpheres, tempLoggingSphereList, true);

    }

    /**
     * Returns the Table Row setup with all the contents with properties to be displayed
     */
    TableRow getLogTableRow(final UhuLoggingMessage logMsg) {
        final TableRow.LayoutParams tableRowLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        final TableRow.LayoutParams tableLayoutLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        tableLayoutLayoutParams.gravity = Gravity.CENTER;
        tableLayoutLayoutParams.rightMargin = 2;

        // compute the colour  for the row based on the type of the message
        int rowColour = getColourForRow(logMsg.typeOfMessage);

        final TableRow tableRow = new TableRow(logDataActivity);
        tableRow.setLayoutParams(tableRowLayoutParams);

        TextView textViewSphereName = new TextView(logDataActivity);
        textViewSphereName.setLayoutParams(tableLayoutLayoutParams);
        textViewSphereName.setBackgroundResource(rowColour);
        textViewSphereName.setText(getSphereNameFromSphereId(logMsg.sphereName));

        TextView timeStamp = new TextView(logDataActivity);
        timeStamp.setLayoutParams(tableLayoutLayoutParams);
        timeStamp.setBackgroundResource(rowColour);
        timeStamp.setText(sdf.format(Long.valueOf(logMsg.timeStamp)));

        TextView textViewSender = new TextView(logDataActivity);
        textViewSender.setLayoutParams(tableLayoutLayoutParams);
        textViewSender.setBackgroundResource(rowColour);
        textViewSender.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textViewSender.setHorizontallyScrolling(true);
        textViewSender.setFocusableInTouchMode(true);
        textViewSender.setSelected(true);
        textViewSender.setText(logMsg.sender);

        TextView textViewRecipient = new TextView(logDataActivity);
        textViewRecipient.setLayoutParams(tableLayoutLayoutParams);
        textViewRecipient.setBackgroundResource(rowColour);
        textViewRecipient.setText(getDeviceNameFromDeviceId(logMsg.recipient));

        TextView textViewTopic = new TextView(logDataActivity);
        textViewTopic.setLayoutParams(tableLayoutLayoutParams);
        textViewTopic.setBackgroundResource(rowColour);
        textViewTopic.setText(logMsg.topic);

        TextView textViewNoOfReceivers = new TextView(logDataActivity);
        textViewNoOfReceivers.setLayoutParams(tableLayoutLayoutParams);
        textViewNoOfReceivers.setBackgroundResource(rowColour);
        textViewNoOfReceivers.setGravity(Gravity.CENTER);
        textViewNoOfReceivers.setText("0");

        tableRow.addView(textViewSphereName);
        tableRow.addView(timeStamp);
        tableRow.addView(textViewSender);
        tableRow.addView(textViewRecipient);
        tableRow.addView(textViewTopic);
        tableRow.addView(textViewNoOfReceivers);

        tableRow.setTag(String.valueOf(tableRowTagCount));
        return tableRow;
    }

    /**
     * Returns the DeviceName associated with the deviceId
     *
     * @param deviceId Device Id whose name is to be fetched
     * @return DeviceName if exists, null otherwise
     */
    String getDeviceNameFromDeviceId(final String deviceId) {
        if (deviceId == null)
            return RECIPIENT_MULTICAST_VALUE;
        String tempDeviceName = UhuCompManager.getSphereForSadl().getDeviceNameFromSphere(deviceId);
        return (null == tempDeviceName) ? deviceId : tempDeviceName;
    }

    /**
     * Returns a particular type of colour based on the msg Type
     */
    private int getColourForRow(final String msgType) {
        if (msgType.equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_RECEIVE.name())) {
            return R.color.table_bg_ctrlMsg_receive;
        } else if (msgType.equals(Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_SEND.name())) {
            return R.color.table_bg_ctrlMsg_send;
        } else if (msgType.equals(Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_RECEIVE.name())) {
            return R.color.table_bg_event_receive;
        } else if (msgType.equals(Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_SEND.name())) {
            return R.color.table_bg_event_send;
        }
        return R.color.list_item_table_cell_bg;
    }

    /**
     * Pop the Confirm Dialog before closing the actitity.
     */
    void showConfirmDialogToStopLogging() {
        AlertDialog dialog = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(logDataActivity);
        alertDialogBuilder.setMessage(R.string.alert_dialog_message);
        alertDialogBuilder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandler = null;
                try {
                    logDataActivity.mUhuLoggingManager.stopLoggingService();
                    logDataActivity.mUhuLoggingManager = null;
                    logDataActivity.new ShutDownLoggingServiceTask().execute(logDataActivity.selSpheres);
                    printToast("STOPPING LOG SERVICE...");
                    logDataActivity.onDestroy();
                } catch (Exception e) {
                    printToast("ERROR IN STOPPING LOG SERVICE...");
                    log.error("Error in stopping logger service.", e);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = alertDialogBuilder.create();
        dialog.show();
    }

    /**
     * Gets the sphere name from the SPhere UI if available, "Un-defined" if not available.
     *
     * @param sphereId SPhereId of the sphere
     * @return sphere Name associated with the sphere Id.
     */
    String getSphereNameFromSphereId(final String sphereId) {
        StringBuilder tempSphereName = new StringBuilder();
        try {
            tempSphereName.append(UhuCompManager.getSphereUI().getSphere(sphereId).getSphereName());
        } catch (NullPointerException ne) {
            log.error("Error in fetching sphereName from sphere UI", ne);
            tempSphereName.append("Un-defined");
        }
        return tempSphereName.toString();
    }

    /**
     * Print Toast Message
     */
    void printToast(String toastMsg) {
        Toast.makeText(logDataActivity, toastMsg, Toast.LENGTH_SHORT).show();
    }
}

package com.bezirk.starter;

import android.content.Intent;

import com.bezirk.control.messages.MessageLedger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callback to handle version mismatch. When it receives the Callback it will broadcast an intent
 * to display warning on the Control UI
 */
public class CommsNotification implements com.bezirk.comms.CommsNotification {
    private static final Logger logger = LoggerFactory.getLogger(CommsNotification.class);

    private final String BR_SYSTEM_STATUS_ACTION = "com.bezirk.systemstatus";
    private final String BR_COMMS_DIAG_RESPONSE = "com.bezirk.comms.diag";
    private final NotificationCallback callback;
    /**
     * Keeps track of no of error messages notified. Each time a notification is received, its value is
     * incremented and after reaching MAX_ERROR_REPEAT_COUNT the value is reset to 0 and notified.
     */
    private int errorCallbackCount = -1;

    public CommsNotification(NotificationCallback callback) {
        this.callback = callback;
    }


    @Override
    public void versionMismatch(String misMatchVersionId) {
        logger.error("Version mismatch Callback received > {}", misMatchVersionId);
        /**
         * Max value for the notification
         */
        int MAX_ERROR_REPEAT_COUNT = 100;
        if ((++errorCallbackCount) % MAX_ERROR_REPEAT_COUNT == 0) {
            Intent systemFailureIntent = new Intent();
            systemFailureIntent.setAction(BR_SYSTEM_STATUS_ACTION);
            systemFailureIntent.putExtra("misMatchVersion", misMatchVersionId);
            callback.sendBroadCast(systemFailureIntent);
        }


    }

    @Override
    public void diagMsg(MessageLedger msg) {
        Intent systemFailureIntent = new Intent();
        systemFailureIntent.setAction(BR_COMMS_DIAG_RESPONSE);
        systemFailureIntent.putExtra("ADDRESS", msg.getSender().device);
        systemFailureIntent.putExtra("MSG", msg.getMsg());
        callback.sendBroadCast(systemFailureIntent);
    }

    @Override
    public void handleError(String errorMsg) {
        //No implementation required now.
    }
}

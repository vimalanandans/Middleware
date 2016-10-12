package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.control.messages.MessageLedger;

/**
 * Created by vnd2kor on 12/9/2015.
 * Notification from comms to platform specific bezirk middleware application regarding
 * diagnostic, error messages
 */
public interface CommsNotification {
    /**
     * Will be invoked if there is mismatch in the versions.
     *
     * @param mismatchedVersionId details Message regarding the version mismatch
     */
    void versionMismatch(String mismatchedVersionId);

    // Diag Ping and pong message for displaying
    void diagMsg(MessageLedger msgLedger);


    // error message from comms
    void handleError(String errorMsg);

}

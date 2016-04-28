package com.bezirk.comms;

import com.bezirk.control.messages.MessageLedger;

/**
 * Created by vnd2kor on 12/9/2015.
 * Notification from comms to platform specific bezirk middleware application regarding
 * diagnostic, error messages
 */
public interface CommsNotification {
    /**
     * Will be invoked if there is mismatch in the versions.
     *
     * @param - details Message regarding the version mistach
     */
    public void versionMismatch(String mismatchedVersionId);

    // Diag Ping and pong message for displaying
    public void diagMsg(MessageLedger msgLedger);


    // error message from comms
    public void handleError(String errorMsg);

}

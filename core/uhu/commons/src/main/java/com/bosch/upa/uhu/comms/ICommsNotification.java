package com.bosch.upa.uhu.comms;

import com.bosch.upa.uhu.control.messages.MessageLedger;

/**
 * Created by vnd2kor on 12/9/2015.
 * Notification from comms to platform specific uhu middleware application regarding
 * diagnostic, error messages
 */
public interface ICommsNotification {
    /**
     * Will be invoked if there is mismatch in the versions.
     * @param - details Message regarding the version mistach
     */
    public void versionMismatch(String mismatchedVersionId);

    // Diag Ping and pong message for displaying
    public void diagMsg(MessageLedger msgLedger);


    // error message from comms
    public void handleError(String errorMsg);

}

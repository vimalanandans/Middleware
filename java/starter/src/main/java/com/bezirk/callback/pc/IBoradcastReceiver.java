package com.bezirk.callback.pc;

import com.bezirk.messagehandler.ServiceIncomingMessage;

/**
 * Common BroadcastReceiver Interface that is defined to mimin the android BroadcastReceiver onto PC side.
 */
public interface IBoradcastReceiver {
    /**
     * Receives the UhuCallbackMessge from the Uhu-pc and handles accordingly.
     * @param callbackMessage - one of the subclasses of UhuCallbackMessage.
     */
    void onReceive(ServiceIncomingMessage incomingMessage);
}

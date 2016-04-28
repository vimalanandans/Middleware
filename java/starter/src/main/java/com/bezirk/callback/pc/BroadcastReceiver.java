package com.bezirk.callback.pc;

import com.bezirk.messagehandler.ServiceIncomingMessage;

/**
 * Common BroadcastReceiver Interface that is defined to mimin the android BroadcastReceiver onto PC side.
 */
public interface BroadcastReceiver {
    /**
     * Receives the UhuCallbackMessge from the Bezirk-pc and handles accordingly.
     *
     * @param incomingMessage
     */
    void onReceive(ServiceIncomingMessage incomingMessage);
}

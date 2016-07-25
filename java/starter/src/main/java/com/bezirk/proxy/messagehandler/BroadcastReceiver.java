package com.bezirk.proxy.messagehandler;

/**
 * Common BroadcastReceiver Interface that is defined to mimin the android BroadcastReceiver onto PC side.
 */
public interface BroadcastReceiver {
    /**
     * Receives the BezirkCallbackMessge from the Bezirk-pc and handles accordingly.
     *
     * @param incomingMessage
     */
    void onReceive(ServiceIncomingMessage incomingMessage);
}

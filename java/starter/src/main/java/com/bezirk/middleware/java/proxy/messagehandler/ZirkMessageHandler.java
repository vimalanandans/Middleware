package com.bezirk.middleware.java.proxy.messagehandler;

import com.bezirk.middleware.core.proxy.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZirkMessageHandler implements MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageHandler.class);

    private final BroadcastReceiver zirkBroadcastReceiver;

    public ZirkMessageHandler(BroadcastReceiver zirkBroadcastReceiver) {
        this.zirkBroadcastReceiver = zirkBroadcastReceiver;
    }

    @Override
    public void onIncomingEvent(com.bezirk.middleware.core.actions.UnicastEventAction eventIncomingMessage) {
        if (zirkBroadcastReceiver != null) {
            zirkBroadcastReceiver.onReceive(eventIncomingMessage);
        } else {
            logger.debug("Broadcast Receiver For Zirk is null, cannot give callback");
        }
    }
}

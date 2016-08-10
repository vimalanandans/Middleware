package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZirkMessageHandler implements com.bezirk.proxy.MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageHandler.class);

    private final BroadcastReceiver zirkBroadcastReceiver;

    public ZirkMessageHandler(BroadcastReceiver zirkBroadcastReceiver) {
        this.zirkBroadcastReceiver = zirkBroadcastReceiver;
    }

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        if (zirkBroadcastReceiver != null) {
            zirkBroadcastReceiver.onReceive(eventIncomingMessage);
        } else {
            logger.debug("Broadcast Receiver For Zirk is null, cannot give callback");
        }
    }

    @Override
    public void onIncomingStream(ReceiveFileStreamAction receiveFileStreamAction) {
        if (zirkBroadcastReceiver != null) {
            zirkBroadcastReceiver.onReceive(receiveFileStreamAction);
        } else {
            logger.debug("Broadcast Receiver For Zirk is null, cannot give callback");
        }
    }
}

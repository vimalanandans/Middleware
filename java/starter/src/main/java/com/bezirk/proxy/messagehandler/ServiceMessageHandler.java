package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.StreamStatusAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PC specific callback implementation that is used to give the callback to the ProxyServiceLegacy.
 */
public final class ServiceMessageHandler implements com.bezirk.proxy.MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceMessageHandler.class);

    private static final String RECEIVER_NULL_ERROR = "Broadcast Receiver For Zirk is null, cannot give callback";
    private final BroadcastReceiver brForService;

    public ServiceMessageHandler(BroadcastReceiver brForService) {
        this.brForService = brForService;
    }

    @Override
    public void onIncomingEvent(UnicastEventAction eventIncomingMessage) {
        if (ValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(eventIncomingMessage);
        } else {
            logger.debug(RECEIVER_NULL_ERROR);
        }
    }

    @Override
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        if (ValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamIncomingMessage);
        } else {

            logger.debug(RECEIVER_NULL_ERROR);
        }
    }

    @Override
    public void onStreamStatus(StreamStatusAction streamStatusAction) {
        if (ValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamStatusAction);
        } else {
            logger.debug(RECEIVER_NULL_ERROR);
        }
    }
}

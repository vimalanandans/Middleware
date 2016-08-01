package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.componentManager.LifecycleManager;
import com.bezirk.networking.NetworkManager;
import com.bezirk.streaming.Streaming;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * Bezirk Communication manager for android zyre - jni
 */
public class ZyreCommsManager extends CommsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ZyreCommsManager.class);
    private ZyreCommsJni comms;
    private boolean delayedInit;

    public ZyreCommsManager(NetworkManager networkManager, CommsNotification commsNotification, Streaming streaming) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this);
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        if (comms != null) {
            return comms.sendToAllZyre(msg);
        }
        return false;
    }

    /**
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        if (comms != null) {
            return comms.sendToOneZyre(msg, nodeId);
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        LifecycleManager lifecycleManager = (LifecycleManager) observable;
        switch (lifecycleManager.getState()) {
            case STARTED:
                logger.debug("Starting comms");
                if (comms != null) {
                    comms.initZyre(delayedInit);
                    delayedInit = true;
                    comms.startZyre();
                    super.startComms();
                }
                break;
//            case RESTARTED:
//                if (comms != null && comms.getZyre() != null) {
//                    comms.stopZyre();
//                }
//                comms = new ZyreCommsJni(this);
//                comms.initZyre(delayedInit);
//                comms.startZyre();
//                break;
            case STOPPED:
                logger.debug("Stopping comms");
                if (comms != null) {
                    comms.stopZyre();
                }
                super.stopComms();
                break;
            case DESTROYED:
                logger.debug("Destroying comms");
                if (comms != null) {
                    comms.closeComms();
                }
                break;

        }
    }
}

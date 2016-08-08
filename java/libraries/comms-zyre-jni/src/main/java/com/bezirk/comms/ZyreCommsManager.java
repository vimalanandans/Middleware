package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.componentManager.LifecycleManager;
import com.bezirk.networking.NetworkManager;
import com.bezirk.streaming.Streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * Bezirk Communication manager for java zyre - jni
 */
public class ZyreCommsManager extends CommsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ZyreCommsManager.class);
    private ZyreCommsJni comms = null;

    public ZyreCommsManager(Streaming streaming, CommsNotification commsNotification, NetworkManager networkManager) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this);
            comms.initZyre(); //TODO: Not sure if this needs to be done here or when starting comms
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        if (comms != null) {
            return comms.sendToAllZyre(msg, isEvent);
        }
        return false;
    }

    /**
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        if (comms != null) {
            return comms.sendToAllZyre(msg, isEvent);
        }
        return false;
    }

    //TODO: manage lifecycle of comms based on bezirk-lifecycle events appropriately
    @Override
    public void update(Observable o, Object arg) {
        LifecycleManager lifecycleManager = (LifecycleManager) o;
        switch (lifecycleManager.getState()) {
            case STARTED:
                logger.debug("Starting comms");
                if (comms != null) {
                    comms.startZyre();
                    super.startComms();
                }
                break;
//            case RESTARTED:
//                break;
            case STOPPED:
                logger.debug("Stopping comms");
                if (comms != null) {
                    comms.stopZyre();
                    comms.closeComms();
                }
                super.stopComms();
                break;
//            case DESTROYED:
//                logger.debug("Destroying comms");
//                if (comms != null) {
//                    comms.closeComms();
//                }
//                break;
        }
    }
}

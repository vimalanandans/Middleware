package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.componentManager.LifecycleManager;
import com.bezirk.networking.NetworkManager;
import com.bezirk.streaming.Streaming;

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

    /**
     *
     * @param networkManager - Network manager to get TCP/IP related device configurations
     * @param groupName - Name to channel your application
     */
    public ZyreCommsManager(NetworkManager networkManager, String groupName, CommsNotification commsNotification, Streaming streaming) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this, groupName);
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        logger.debug("send to all");
        /*if (comms != null) {
            logger.debug("comms not null in ZyreCommsManager");
            return comms.sendToAllZyre(msg);
        }
        return false;*/
        return comms != null && comms.sendToAllZyre(msg);
    }

    /**
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        return comms != null && comms.sendToOneZyre(msg, nodeId);
    }

    //TODO: manage lifecycle of comms based on bezirk-lifecycle events appropriately
    @Override
    public void update(Observable observable, Object data) {
        LifecycleManager lifecycleManager = (LifecycleManager) observable;
        switch (lifecycleManager.getState()) {
            case STARTED:
                logger.debug("Starting comms");
                if (comms != null) {
                    logger.debug("comms not null in zyreCommsManager");
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
                    comms.closeComms();
                }
                super.stopComms();
//            case DESTROYED:
//                logger.debug("Destroying comms");
//                if (comms != null) {
//                    comms.closeComms();
//                }
//                break;

        }
    }
}

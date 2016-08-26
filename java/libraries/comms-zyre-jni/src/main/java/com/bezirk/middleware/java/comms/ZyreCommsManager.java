package com.bezirk.middleware.java.comms;

import com.bezirk.middleware.core.comms.processor.CommsProcessor;
import com.bezirk.middleware.core.componentManager.LifecycleManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.streaming.Streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Observable;


/**
 * Bezirk Communication manager for java zyre - jni
 */
public class ZyreCommsManager extends CommsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ZyreCommsManager.class);
    private ZyreCommsJni comms = null;

    public ZyreCommsManager(NetworkManager networkManager, String groupName, com.bezirk.middleware.core.comms.CommsNotification commsNotification, Streaming streaming) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this,groupName);
            comms.initZyre(); //TODO: Not sure if this needs to be done here or when starting comms
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        return comms != null && comms.sendToAllZyre(msg, isEvent);
    }


 /*   @Override
    public boolean stopComms() {

        if (comms != null) {
            // close zyre
            comms.stopZyre();
            // close the comms process comms
        }

        return super.stopComms();
    }

    @Override
    public boolean closeComms() {
        if (comms != null) {
            comms.closeComms();

        }
        return super.closeComms();
    }


    *//**
     * send to all : Multicast message
     *//*
    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        logger.debug("send to all");
        return comms.sendToAllZyre(msg, isEvent);
    }*/


    /**
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        return comms != null && comms.sendToAllZyre(msg, isEvent);
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

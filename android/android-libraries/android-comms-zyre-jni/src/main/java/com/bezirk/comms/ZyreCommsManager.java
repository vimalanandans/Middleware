package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.networking.NetworkManager;
import com.bezirk.streaming.Streaming;
import com.bezirk.util.ValidatorUtility;

/**
 * Bezirk Communication manager for android zyre - jni
 */
public class ZyreCommsManager extends CommsProcessor {
    private ZyreCommsJni comms;
    private boolean delayedInit;

    public ZyreCommsManager(NetworkManager networkManager, CommsNotification commsNotification, Streaming streaming) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this);
        }
    }

    @Override
    public boolean startComms() {
        if (comms != null) {
            comms.initZyre(delayedInit);
            delayedInit = true;
            comms.startZyre();
            return super.startComms();
        }
        return false;
    }

    @Override
    public boolean stopComms() {
        if (comms != null) {
            comms.stopZyre();
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

    /**
     * Create a new Zyre context, required during wifi reset.
     */
    @Override
    public boolean restartComms() {
        if (comms != null && comms.getZyre() != null) {
            comms.stopZyre();
        }
        comms = new ZyreCommsJni(this);
        comms.initZyre(delayedInit);
        comms.startZyre();
        return true;
    }

}

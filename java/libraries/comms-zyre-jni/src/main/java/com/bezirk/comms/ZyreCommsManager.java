package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.networking.NetworkManager;
import com.bezirk.streaming.Streaming;

/**
 * Bezirk Communication manager for java zyre - jni
 */
public class ZyreCommsManager extends CommsProcessor {
    private ZyreCommsJni comms = null;

    public ZyreCommsManager(Streaming streaming, CommsNotification commsNotification, NetworkManager networkManager) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new ZyreCommsJni(this);
            comms.initZyre(); //TODO: Not sure if this needs to be done here or when starting comms
        }
    }

    @Override
    public boolean startComms() {
        if (comms != null) {
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

    /**
     * Create a new Zyre context, required during wifi reset.
     */
    @Override
    public boolean restartComms() {
        return false;
    }

}

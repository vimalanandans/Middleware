package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.CommsProcessor;
import com.bezirk.middleware.core.componentManager.LifecycleManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.streaming.Streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * Bezirk Communication manager for android zyre - jni
 */
public class JmqCommsManager extends CommsProcessor implements OnMessageReceivedListener {
    private static final Logger logger = LoggerFactory.getLogger(JmqCommsManager.class);
    private Jp2p comms;

    /**
     * @param networkManager - Network manager to get TCP/IP related device configurations
     * @param groupName      - Name to channel your application
     */
    public JmqCommsManager(NetworkManager networkManager, String groupName, CommsNotification commsNotification, Streaming streaming) {
        super(networkManager, commsNotification, streaming);
        if (comms == null) {
            comms = new Jp2p(this);
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        return comms != null && comms.shout(msg);
    }

    /**
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        return comms != null && comms.whisper(nodeId, msg);
    }

    //TODO: manage lifecycle of comms based on bezirk-lifecycle events appropriately
    @Override
    public void update(Observable observable, Object data) {
        LifecycleManager lifecycleManager = (LifecycleManager) observable;
        switch (lifecycleManager.getState()) {
            case STARTED:
                if (comms != null) {
                    logger.debug("Starting comms");
                    comms.start();
                    super.startComms();
                }
                break;
            case STOPPED:
                if (comms != null) {
                    logger.debug("Stopping comms");
                    comms.stop();
                    super.stopComms();
                    comms = null;
                }

        }
    }

    @Override
    public boolean processIncomingMessage(String nodeId, byte[] data) {
        logger.debug("Msg from node " + nodeId + ", msg " + new String(data));
        processWireMessage(nodeId, new String(data));
        return true;
    }

    @Override
    public String getNodeId() {
        return (comms != null) ? comms.getNodeId().toString() : null;
    }
}


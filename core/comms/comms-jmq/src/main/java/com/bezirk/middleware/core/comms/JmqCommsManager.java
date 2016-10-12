package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.CommsProcessor;
import com.bezirk.middleware.core.comms.processor.WireMessage;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.networking.NetworkManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Observable;

public class JmqCommsManager extends CommsProcessor implements ZMQReceiver.OnMessageReceivedListener {
    private static final Logger logger = LoggerFactory.getLogger(JmqCommsManager.class);
    private JmqComms comms;

    /**
     * @param networkManager - Network manager to get TCP/IP related device configurations
     * @param groupName      - Name to channel your application
     */
    public JmqCommsManager(NetworkManager networkManager, String groupName, CommsNotification commsNotification) {
        super(networkManager, commsNotification);
        if (comms == null) {
            comms = new JmqComms(this, groupName);
        }
    }

    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {
        return comms != null && comms.shout(msg);
    }

    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        return comms != null && comms.whisper(nodeId, msg);
    }

    //TODO: manage lifecycle of comms based on bezirk-lifecycle events appropriately
    @Override
    public void update(Observable observable, Object data) {
        LifeCycleObservable lifeCycleObservable = (LifeCycleObservable) observable;
        switch (lifeCycleObservable.getState()) {
            case RUNNING:
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
        try {
            processWireMessage(nodeId, new String(data, WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
        return true;
    }

    @Override
    public String getNodeId() {
        return (comms != null) ? (comms.getNodeId() != null) ? comms.getNodeId().toString() : null : null;
    }
}


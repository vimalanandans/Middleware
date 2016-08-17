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
public class JmqCommsManager extends CommsProcessor implements MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(JmqCommsManager.class);
    private Jp2p comms;

    /**
     *
     * @param networkManager - Network manager to get TCP/IP related device configurations
     * @param groupName - Name to channel your application
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
        return comms != null && comms.whisper(nodeId,msg);
    }

    //TODO: manage lifecycle of comms based on bezirk-lifecycle events appropriately
    @Override
    public void update(Observable observable, Object data) {
        LifecycleManager lifecycleManager = (LifecycleManager) observable;
        switch (lifecycleManager.getState()) {
            case STARTED:
                logger.debug("Starting comms");
                if (comms != null) {
                    comms.init();

                    comms.start();
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
                    comms.stop();
                    comms.close();
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

    @Override
    public boolean processIncomingMessage(String nodeId, byte[] data) {

        logger.info(" Data in >> "+nodeId + " >> "+ data);
        processWireMessage(nodeId, new String (data));
        return true;
    }
}


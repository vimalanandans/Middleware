package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Jp2p Peer-2-Peer communication layer using jeromq. inspired from zyre, but tailored implementation
 */
public class Jp2p {
    private static final Logger logger = LoggerFactory.getLogger(Jp2p.class);
    private final Node selfNode;
    private final Peers peers;
    private final NodeDiscovery nodeDiscovery;
    private final ZMQReceiver2 zmqReceiver;
    private final OnMessageReceivedListener onMessageReceivedListener;

    public Jp2p(@NotNull final OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListener = onMessageReceivedListener;
        this.zmqReceiver = new ZMQReceiver2(this);

        // to make sure the port is ready and bind by that that time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selfNode = new Node(zmqReceiver.getPort());
        peers = new Peers(selfNode);
        nodeDiscovery = new NodeDiscovery(selfNode, peers);
    }

    public boolean processIncomingMessage(final String nodeId, final byte[] data) {
        onMessageReceivedListener.processIncomingMessage(nodeId, data);
        return true;
    }

    public boolean start() {
        peers.start();
        //new Thread(zmqReceiver).start();
        zmqReceiver.start();
        nodeDiscovery.start();
        return true;
    }

    public boolean stop() {
        logger.debug("Stopping Jp2p");
        peers.stop();
        nodeDiscovery.stop();
        zmqReceiver.stop();
        logger.debug("Stopped Jp2p");
        return true;
    }

    public boolean shout(final byte[] data) {
        return peers.shout(data);
    }

    public boolean whisper(final String recipient, final byte[] data) {
        return peers.whisper(recipient, data);
    }

    public UUID getNodeId() {
        return (selfNode != null) ? selfNode.getUuid() : null;
    }

}

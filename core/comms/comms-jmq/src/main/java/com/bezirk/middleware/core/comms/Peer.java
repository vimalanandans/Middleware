package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * A single Peer Node and its relevant operations
 */
public class Peer {
    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private final Node node;
    private ZMQ.Socket client;

    public Peer(Node node) {
        this.node = node;
    }

    public void connect(Node selfNode) {
        ZContext ctx = new ZContext();
        client = ctx.createSocket(ZMQ.DEALER);
        // Client identifies as same name as this node id, so that receiver identifies it
        client.setIdentity(selfNode.getUuid().toString().getBytes());

        //FIXME: get actual endpoint
        client.connect("tcp:/" + node.getSender() + ":" + node.getPort());
    }

    public void send(byte[] data) {
        if (client == null) {
            logger.debug("client socket not initialized, not sending message");
            return;
        }
        client.send(data);

    }

}

package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public void send(final byte[] data) {
        if (client == null) {
            logger.debug("client socket not initialized, not sending message");
            return;
        }

        //fire and forget, use the future return value in case required
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return client.send(data);
            }
        });
    }

}

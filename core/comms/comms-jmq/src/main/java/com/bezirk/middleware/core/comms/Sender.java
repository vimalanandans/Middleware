package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private final ZMQ.Context context;
    private final Map<UUID, ZMQ.Socket> connections;
    private final UUID myId;

    public Sender(@NotNull final UUID myId) {
        context = ZMQ.context(1);
        connections = new HashMap<>();
        this.myId = myId;
    }

    public void addConnection(@NotNull final UUID uuid, @NotNull final PeerMetaData peerMetaData) {
        logger.trace("adding connection");
        synchronized (connections) {
            if (connections.containsKey(uuid)) {
                logger.trace("Connection to the node {} already exist", uuid);
                return;
            }
        }
        final ZMQ.Socket socket = context.socket(ZMQ.DEALER);
        socket.setIdentity(myId.toString().getBytes(ZMQ.CHARSET));
        socket.connect("tcp:/" + peerMetaData.getInetAddress() + ":" + peerMetaData.getPort());
        logger.debug("connecting to peer");
        synchronized (connections) {
            connections.put(uuid, socket);
        }
    }

    public void send(@NotNull final byte[] data) {
        final Set<ZMQ.Socket> recipients;
        synchronized (connections) {
            if (connections.size() > 0) {
                recipients = new HashSet<>(connections.values());
            } else {
                return;
            }
        }
        for (ZMQ.Socket socket : recipients) {
            socket.send(data);
        }
    }

    public void send(@NotNull final UUID recipient, @NotNull final byte[] data) {
        synchronized (connections) {
            if (connections.containsKey(recipient)) {
                connections.get(recipient).send(data);
            }
        }
    }

    public void close() {
        synchronized (connections) {
            for (ZMQ.Socket socket : connections.values()) {
                socket.close();
            }
        }
        context.term();
    }
}

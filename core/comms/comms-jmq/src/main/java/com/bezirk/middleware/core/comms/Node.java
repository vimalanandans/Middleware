package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.util.UUID;

public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private final UUID uuid;
    private final InetAddress inetAddress;
    private final int port;
    private final ZMQ.Socket socket;

    public Node(@NotNull final int port) {
        this.uuid = UUID.randomUUID();
        this.port = port;
        this.inetAddress = null;
        this.socket = null;
    }

    public Node(@NotNull final UUID uuid, @NotNull final InetAddress inetAddress, @NotNull final int port, final ZMQ.Socket socket) {
        this.uuid = uuid;
        this.port = port;
        this.inetAddress = inetAddress;
        this.socket = socket;
    }

    public synchronized void send(@NotNull ZContext context, @NotNull final Node toNode, @NotNull final byte[] data) {
        socket.send(data);
    }

    public void close() {
        if (socket != null) {
            logger.debug("Closing socket for " + uuid);
            socket.close();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public ZMQ.Socket getSocket() {
        return socket;
    }
}


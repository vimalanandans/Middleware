package com.bezirk.middleware.core.comms;

import java.net.InetAddress;
import java.util.UUID;

public class Node {
    private final UUID uuid;
    private final InetAddress sender;
    private final int port;

    public Node(final int port) {
        this.uuid = UUID.randomUUID();
        this.port = port;
        this.sender = null;
    }

    public Node(final UUID uuid, final InetAddress sender, final int port) {
        this.uuid = uuid;
        this.port = port;
        this.sender = sender;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getSender() {
        return sender;
    }
}


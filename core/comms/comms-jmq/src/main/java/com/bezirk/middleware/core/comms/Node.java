package com.bezirk.middleware.core.comms;

import java.net.InetAddress;
import java.util.Random;
import java.util.UUID;

/**
 * representation of the current Node
 */
public class Node {
    UUID uuid;
    InetAddress sender;
    int port;

    public InetAddress getSender() {
        return sender;
    }

    public Node(int port)
    {
        uuid = UUID.randomUUID();
        this.port = port;

    }
    public Node(UUID uuid, InetAddress sender, int port)
    {
        this.uuid = uuid;
        this.port = port;
        this.sender = sender;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

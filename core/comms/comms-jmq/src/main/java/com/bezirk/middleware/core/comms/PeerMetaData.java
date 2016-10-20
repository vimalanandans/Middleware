package com.bezirk.middleware.core.comms;

import java.net.InetAddress;

public class PeerMetaData {
    private final InetAddress inetAddress;
    private final int port;

    public PeerMetaData(final InetAddress inetAddress, final int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }
}

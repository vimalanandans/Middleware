package com.bezirk.networking;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class InterfaceInetPair {
    private final NetworkInterface networkInterface;
    private final InetAddress inet;

    public InterfaceInetPair(NetworkInterface networkInterface, InetAddress inet) {
        this.networkInterface = networkInterface;
        this.inet = inet;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public InetAddress getInet() {
        return inet;
    }


}

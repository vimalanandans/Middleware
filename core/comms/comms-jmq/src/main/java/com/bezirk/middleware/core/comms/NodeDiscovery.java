package com.bezirk.middleware.core.comms;

import org.zeromq.ZBeacon;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

public class NodeDiscovery {
    private static final String prefix = "bezirk";
    private static final String SEPERATOR = "::";
    private static final String beaconHost = "255.255.255.255";
    private static final int beaconPort = 5670; // this is zyre port
    private String beaconData;
    private ZBeacon zbeacon;
    private final Node node;
    private final Peers peers;

    public NodeDiscovery(Node node, Peers peers) {
        this.node = node;
        this.peers = peers;
        start();
    }

    private void processBeacon(InetAddress sender, byte[] beacon) {
        String beaconString = new String(beacon);
        String[] data = beaconString.split(SEPERATOR);

        if (data.length == 4) // right format
        {
            long lsb = Long.parseLong(data[1]);
            long msb = Long.parseLong(data[2]);
            UUID uuid = new UUID(msb, lsb);
            int port = Integer.parseInt(data[3]);
            peers.validatePeer(uuid, sender, port);
        }
    }

    public void start() {
        beaconData = prefix + SEPERATOR + node.getUuid().getLeastSignificantBits() +
                SEPERATOR + node.getUuid().getMostSignificantBits() +
                SEPERATOR + String.valueOf(node.getPort());
        zbeacon = new ZBeacon(beaconHost, beaconPort, beaconData.getBytes(), false);
        zbeacon.setPrefix(prefix.getBytes());
        zbeacon.setListener(new ZBeacon.Listener() {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                // ignore self id
                if (!Arrays.equals(beacon, beaconData.getBytes())) {
                    processBeacon(sender, beacon);
                }
            }
        });
        zbeacon.start();
    }

    public void stop() {
        try {
            zbeacon.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

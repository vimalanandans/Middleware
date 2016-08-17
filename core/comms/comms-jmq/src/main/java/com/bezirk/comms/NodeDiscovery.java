package com.bezirk.comms;

import org.zeromq.ZBeacon;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

/**
 * NodeDiscovery of peersMap
 */
public class NodeDiscovery {

    String prefix = "bezirk";
    String SEPERATOR = "::";
    String beagonHost = "255.255.255.255";
    String beaconData ;
    ZBeacon zbeacon ;
    int beagonPort = 5670; // this is zyre port

    UUID nodeId; // this Node id
    String nodeServerPort;
    Peers peers = null;

    public NodeDiscovery(Node node, Peers peers) {
        nodeId = node.getUuid();
        nodeServerPort = String.valueOf(node.getPort());
        this.peers = peers;
        start();
    }

    void processBeacon( InetAddress sender, byte[] beacon)
    {
        String beaconString = new String(beacon);

        String[] data = beaconString.split(SEPERATOR);

        if(data.length == 4) // right format
        {
            //data[0] // prefix
            long lsb = Long.parseLong(data[1]);
            long msb = Long.parseLong(data[2]);
            UUID uuid = new UUID(msb,lsb);
            int port = Integer.parseInt(data[3]);

           // System.out.println("beacon >> " + " >> "+uuid + " >> "+port );
            peers.validatePeer(uuid,sender, port);
        }
    }

    void start()
    {

        beaconData = prefix + SEPERATOR + nodeId.getLeastSignificantBits() +
                 SEPERATOR + nodeId.getMostSignificantBits() +
                 SEPERATOR + nodeServerPort;

        zbeacon = new ZBeacon(beagonHost, beagonPort, beaconData.getBytes(), false);

        //System.out.println("beagon started > "+beaconData);

        zbeacon.setPrefix( prefix.getBytes());

        zbeacon.setListener(new ZBeacon.Listener()
        {
            @Override
            public void onBeacon(InetAddress sender, byte[] beacon) {
                // ignore self id
                if (!Arrays.equals(beacon, beaconData.getBytes()))
                {
                    processBeacon( sender,beacon);

                }
            }
        });


        zbeacon.start();
    }

    public void stop()
    {
        try {
            zbeacon.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}

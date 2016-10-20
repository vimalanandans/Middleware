package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A peer has the following functionalities
 * <ul>
 * <li>Discover other <code>Peer</code>(s) on the same machine and network</li>
 * <li>Send messages to a single <code>Peer</code> or all <code>Peer</code>(s)
 * </li>
 * <li>Receive messages from the discovered peers</li>
 * <li>Clean up resources like sockets, contexts, etc when stopped</li>
 * <li>TODO Manage network re-connections</li>
 * <li>TODO Remove peers from which a beacon is not received for a particular amount of time</li>
 * </ul>
 */
public class Peer implements Beacon.BeaconCallback {
    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private final String name;
    private final Receiver receiver;
    private final UUID uuid;
    private final Beacon beacon;
    private final Sender sender;
    private final Map<UUID, PeerMetaData> myPeers;
    private final Receiver.OnMessageReceivedListener onMessageReceivedListener;

    public Peer(final String name, final Receiver.OnMessageReceivedListener onMessageReceivedListener) {
        this.name = name;
        this.onMessageReceivedListener = onMessageReceivedListener;
        receiver = new Receiver(onMessageReceivedListener);
        uuid = UUID.randomUUID();
        if (receiver.getPort() <= 0) {
            throw new AssertionError("port not initialized, port is " + receiver.getPort());
        }
        beacon = new Beacon(null, receiver.getPort(), uuid, this);
        myPeers = new HashMap<>();
        sender = new Sender(uuid);
    }

    public void start() {
        receiver.start();
        beacon.start();
    }

    public void stop() {
        receiver.close();
        beacon.stop();
        sender.close();
    }

    public UUID getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void processPeer(UUID uuid, InetAddress senderInetAddress, int port) {
        final PeerMetaData peerMetaData;
        synchronized (myPeers) {
            if (myPeers.containsKey(uuid)) {
                return;
            } else {
                peerMetaData = new PeerMetaData(senderInetAddress, port);
                myPeers.put(uuid, peerMetaData);
                logger.trace("{} added to myPeers, total peers {}", uuid, myPeers.size());
            }
        }
        sender.addConnection(uuid, peerMetaData);
    }

    public void send(String data) {
        sender.send(data);
    }

    public void send(UUID recipient, String data) {
        sender.send(recipient, data);
    }


}
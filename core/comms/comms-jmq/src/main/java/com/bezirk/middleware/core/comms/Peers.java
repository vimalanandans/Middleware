package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import zmq.ZError;

public class Peers {
    private static final Logger logger = LoggerFactory.getLogger(Peers.class);
    private final Node selfNode;
    private final Map<UUID, Peer> peersMap;
    // TODO : Avoid concurrentHashMap, impacts performance
    // used by the beacon and sender

    public Peers(final Node selfNode) {
        this.selfNode = selfNode;
        this.peersMap = new HashMap<>();
    }

    public synchronized void validatePeer(UUID uuid, InetAddress sender, int port) {
        //System.out.println("Peer found " + uuid);
        if (peersMap.containsKey(uuid)) {
            //System.out.println("Peer exist in map " + uuid + " number of peers " + peersMap.size());
            //valid peer.
            // TODO add other validations and cleanup
        } else {
            //new peer
            createNewPeer(uuid, sender, port);
        }
    }

    public void createNewPeer(UUID uuid, InetAddress sender, int port) {
        Peer peer = new Peer(new Node(uuid, sender, port));
        peersMap.put(uuid, peer);
        peer.connect(selfNode);
        //System.out.println("Current Node id " + selfNode.getUuid().toString() + "::Peer with " + uuid + " added with port " + port);
    }

    /**
     * send to all
     */
    public boolean shout(final byte[] data) {
        for (Peer peer : peersMap.values()) {
            try {
                peer.send(data);
            } catch (ZError.IOException e) {
                logger.error("ZError.IOException");
            }
            //  System.out.println(" shout to "+peer.selfNode.getUuid()+" port > " +peer.selfNode.getPort());
        }
        return true;
    }

    /**
     * send to one
     */
    public boolean whisper(String receiver, byte[] data) {
        UUID uuid = UUID.fromString(receiver);
        if (peersMap.containsKey(uuid)) {
            peersMap.get(uuid).send(data);
            return true;
        }
        return false;
    }
}

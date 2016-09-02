package com.bezirk.middleware.core.comms;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Peers {

    private final Node selfNode;
    private final Map<UUID, Peer> peersMap;
    // TODO : Avoid concurrentHashMap, impacts performance
    // used by the beacon and sender

    public Peers(final Node selfNode) {
        this.selfNode = selfNode;
        this.peersMap = new ConcurrentHashMap<>();
    }

    public void validatePeer(UUID uuid, InetAddress sender, int port) {
        //System.out.println("Peer found " + uuid);
        if (peersMap.containsKey(uuid)) {
            System.out.println("Peer exist in map " + uuid + " number of peers " + peersMap.size());
            //valid peer.
            // TODO add other validations and cleanup
        } else {
            //new peer
            createNewPeer(uuid, sender, port);
        }
    }

    public void createNewPeer(UUID uuid, InetAddress sender, int port) {
        Peer peer = new Peer(new Node(uuid, sender, port));
        peer.connect(selfNode);
        peersMap.put(uuid, peer);
        System.out.println("Peer with " + uuid + " added with port " + port);
    }

    /**
     * send to all
     */
    public boolean shout(final byte[] data) {
        for (Peer peer : peersMap.values()) {
            peer.send(data);
            //  System.out.println(" shout to "+peer.selfNode.getUuid()+" port > " +peer.selfNode.getPort());
        }
        return false;
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

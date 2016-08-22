package com.bezirk.comms;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * peer selfNode list
 */
public class Peers {

    Node selfNode;

    Map<UUID,Peer> peersMap = new ConcurrentHashMap <UUID, Peer> ();
    // TODO : Avoid concurrentHashMap, impacts performance
    // used by the beacon and sender

    public Peers(Node selfNode)
    {
        this.selfNode = selfNode;
    }
    /** on beagans heartbeat */
    public void validatePeer(UUID uuid, InetAddress sender, int port)
    {
        if(peersMap.containsKey(uuid)) {
            //valid peer.
            // TODO add other validations and cleanup
        }
        else{
            //new peer
            createNewPeer(uuid, sender, port);
        }
    }

    public void createNewPeer(UUID uuid, InetAddress sender, int port){

        Peer peer = new Peer(new Node(uuid, sender, port));
        peer.connect(selfNode);
        peersMap.put(uuid, peer );


        System.out.println("new peer >> "+uuid + " port " + port + " added ");
    }

    /** send to all*/
    public boolean shout(final byte[] data)
    {
        for (Peer peer : peersMap.values ()) {
            peer.send(data);
            //  System.out.println(" shout to "+peer.selfNode.getUuid()+" port > " +peer.selfNode.getPort());
        }

        return false;
    }

    /** send to one*/
    public  boolean whisper(String receiver, byte[] data)
    {
        UUID uuid = UUID.fromString(receiver);
        if(peersMap.containsKey(uuid))
        {
            peersMap.get(uuid).send(data);
            return true;
        }
        return false;
    }
}

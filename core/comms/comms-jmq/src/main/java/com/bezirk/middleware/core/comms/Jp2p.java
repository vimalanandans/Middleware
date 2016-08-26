package com.bezirk.middleware.core.comms;

import java.util.UUID;

/**
 * Jp2p Peer-2-Peer communication layer using jeromq. inspired from zyre, but tailored implementation
 */
public class Jp2p  {

    private com.bezirk.middleware.core.comms.Node selfNode;
    private Peers peers ;
    private com.bezirk.middleware.core.comms.NodeDiscovery nodeDiscovery;
    private Receiver receiver;

    private MessageReceiver msgReceiver = null;


    public Jp2p(MessageReceiver msgReceiver)
    {
        this.msgReceiver = msgReceiver;

        receiver = new Receiver(this);

        Thread thread = new Thread(receiver);
        thread.start();

        // to make sure the port is ready and bind by that that time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        selfNode = new com.bezirk.middleware.core.comms.Node(receiver.getPort());


        peers = new Peers(selfNode);

        nodeDiscovery = new com.bezirk.middleware.core.comms.NodeDiscovery(selfNode, peers);

    }



    public boolean processIncomingMessage(String nodeId, byte[] data){

        if(receiver != null) {
            //System.out.println("Received : "+nodeId + " data > " + data);
            msgReceiver.processIncomingMessage(nodeId, data);
        }
        return true;
    }

    public boolean init()
    {
        return true;
    }

    public boolean stop()
    {
        return true;
    }
    public boolean close()
    {
        return true;
    }

    public boolean shout(byte[] data)
    {
        return peers.shout(data);
    }

    public boolean whisper(String recipient, byte[] data)
    {
        return peers.whisper(recipient, data);
    }


    public UUID getNodeId()
    {
        return selfNode.getUuid();
    }

    public boolean start()
    {return true;}



}

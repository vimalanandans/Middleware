package com.bezirk.comms;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * A single Peer Node and its relevant operations
 */
public class Peer {

    Node node;


    ZMQ.Socket client;

    public Peer(Node node)
    {
        this.node = node;
    }

    public void connect(Node selfNode)
    {

        ZContext ctx = new ZContext();

        client = ctx.createSocket(ZMQ.DEALER);

        // Client identifies as same name as this node id, so that receiver identifies it
        client.setIdentity(selfNode.getUuid().toString().getBytes());

        //FIXME: get actual endpoint
        client.connect("tcp:/"+node.getSender()+":"+node.port);
    }

    public void send(byte[] data)
    {
        client.send(data);
    }

}

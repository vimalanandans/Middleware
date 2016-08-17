package com.bezirk.comms;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

/**
 * Jp2p Peer-2-Peer communication layer using jeromq. inspired from zyre, but tailored implementation
 */
public class Jp2p {

    Node selfNode;
    Peers peers ;
    NodeDiscovery nodeDiscovery;
    ReceiverTask receiverTask;

    MessageReceiver receiver = null;

    public Jp2p(MessageReceiver receiver)
    {
        receiverTask = new ReceiverTask();
        new Thread(receiverTask).start();
        selfNode = new Node(receiverTask.getPort());
        peers = new Peers(selfNode);
        nodeDiscovery = new NodeDiscovery(selfNode,peers);
        this.receiver = receiver;
    }

    public boolean processIncomingMessage(String nodeId, byte[] data){

        if(receiver != null) {
            //System.out.println("Received : "+nodeId + " data > " + data);
            receiver.processIncomingMessage(nodeId, data);
        }
        return true;
    }

    public boolean init()
    {
        return true;
    }

    public boolean start()
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


    public class ReceiverTask implements Runnable {

        int port;
        ZContext ctx = new ZContext();

        //  Frontend socket talks to clients over TCP
        ZMQ.Socket frontend = ctx.createSocket(ZMQ.ROUTER);

        //  Backend socket talks to workers over inproc
        ZMQ.Socket backend = ctx.createSocket(ZMQ.DEALER);

        public int getPort() {
            return port;
        }

        public ReceiverTask(){

            port = frontend.bindToRandomPort ("tcp://*", 0xc000, 0xffff);

            if(port == 0) {
                System.out.println(" communication incoming port is null, unable to start ");
            }

          //  System.out.println(" Server Port >> "+port);


            backend.bind("inproc://backend");

        }
        public void run() {


            //  Launch pool of worker threads, precise number is not critical
            for (int threadNbr = 0; threadNbr < 5; threadNbr++)
                new Thread(new ReceiverWorker(ctx)).start();

            //  Connect backend to frontend via a proxy
            ZMQ.proxy(frontend, backend, null);

            ctx.destroy();
        }
    }

    //Each worker task works on one request at a time and sends a random number
    //of replies back, with random delays between replies:

    private class ReceiverWorker implements Runnable {
        private ZContext ctx;

        public ReceiverWorker(ZContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            ZMQ.Socket worker = ctx.createSocket(ZMQ.DEALER);

            worker.connect("inproc://backend");

            while (!Thread.currentThread().isInterrupted()) {
                //  The DEALER socket gives us the address envelope and message
                ZMsg msg = ZMsg.recvMsg(worker);

                ZFrame address = msg.pop();
                ZFrame content = msg.pop();

                processIncomingMessage(new String (address.getData()),content.getData());

            }
            ctx.destroy();
        }
    }




}

package com.bezirk.middleware.core.comms;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

/**
 * Receiver to get all incoming data
 */
public class Receiver implements Runnable {

    private final Jp2p jp2p;
    private final ZContext ctx;
    //  Frontend socket talks to clients over TCP
    private final ZMQ.Socket frontend;
    //  Backend socket talks to workers over inproc
    private final ZMQ.Socket backend;
    private int port;

    public Receiver(Jp2p jp2p) {
        this.jp2p = jp2p;
        this.ctx = new ZContext();
        this.frontend = ctx.createSocket(ZMQ.ROUTER);
        this.backend = ctx.createSocket(ZMQ.DEALER);
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        port = frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);

        if (port == 0) {
            System.out.println(" communication incoming port is null, unable to start ");
            ctx.destroy();
            return;
        }

        //  System.out.println(" Server Port >> "+port);

        backend.bind("inproc://backend");

        //  Launch pool of worker threads, precise number is not critical
        for (int threadNbr = 0; threadNbr < 5; threadNbr++)
            new Thread(new ReceiverWorker(ctx)).start();

        //  Connect backend to frontend via a proxy
        ZMQ.proxy(frontend, backend, null);

        ctx.destroy();
    }

    /*Each worker task works on one request at a time and sends a random number
    of replies back, with random delays between replies*/

    private class ReceiverWorker implements Runnable {
        private final ZContext ctx;

        public ReceiverWorker(final ZContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            ZMQ.Socket worker = ctx.createSocket(ZMQ.DEALER);
            worker.connect("inproc://backend");

            while (!Thread.currentThread().isInterrupted()) {
                //The DEALER socket gives us the address envelope and message
                ZMsg msg = ZMsg.recvMsg(worker);

                ZFrame address = msg.pop();
                ZFrame content = msg.pop();

                jp2p.processIncomingMessage(new String(address.getData()), content.getData());
            }
            ctx.destroy();
        }
    }
}

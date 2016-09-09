package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Receiver to get all incoming data
 */
public class ZMQReceiver implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ZMQReceiver.class);
    private final Jp2p jp2p;
    private final ZContext ctx;
    //  Frontend socket talks to clients over TCP
    private final ZMQ.Socket frontend;
    //  Backend socket talks to workers over inproc
    private final ZMQ.Socket backend;
    private int port;
    private boolean stopped = false;

    public ZMQReceiver(Jp2p jp2p) {
        this.jp2p = jp2p;
        this.ctx = new ZContext();
        this.frontend = ctx.createSocket(ZMQ.ROUTER);
        this.backend = ctx.createSocket(ZMQ.DEALER);

        //initialize port in a separate thread to prevent NetworkOnMainThread issue on android
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);
            }
        });
        try {
            port = future.get(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        logger.debug("tcp port: " + port);
        if (port == 0) {
            logger.debug("Incoming port is null, unable to start receiver");
            ctx.destroy();
            return;
        }
    }

    public void stop() {
        stopped = true;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        while (!stopped) {
            if (port == 0) {
                logger.error("Not starting worker threads for " + ZMQReceiver.class.getSimpleName());
                return;
            }
            backend.bind("inproc://backend");

            //  Launch pool of worker threads, precise number is not critical
            for (int threadNbr = 0; threadNbr < 5; threadNbr++) {
                new Thread(new ReceiverWorker(ctx)).start();
            }

            // Connect backend to frontend via a proxy, will return only if the context is closed
            ZMQ.proxy(frontend, backend, null);
        }
        Thread.currentThread().interrupt();
        //ctx.destroy();
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

package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZMsg;


public class ZMQReceiver implements Runnable {

    public interface ReceiverPortInitializedCallback {
        void onSuccess(int port);

        void onFailure(String errorMessage);
    }

    public interface OnMessageReceivedListener {
        boolean processIncomingMessage(String nodeId, byte[] data);
    }

    private static final Logger logger = LoggerFactory.getLogger(ZMQReceiver.class);

    private ZContext ctx;
    //  Frontend socket talks to clients over TCP
    private ZMQ.Socket frontend;
    //  Backend socket talks to workers over inproc
    private ZMQ.Socket backend;
    private int port;
    private final ReceiverPortInitializedCallback callback;
    private final OnMessageReceivedListener onMessageReceivedListener;

    public ZMQReceiver(@Nullable final ReceiverPortInitializedCallback callback, @Nullable final OnMessageReceivedListener onMessageReceivedListener) {
        this.callback = callback;
        this.onMessageReceivedListener = onMessageReceivedListener;
    }

    @Override
    public void run() {

        try {
            this.ctx = new ZContext();
            this.frontend = ctx.createSocket(ZMQ.ROUTER);
            this.backend = ctx.createSocket(ZMQ.DEALER);
            port = frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);
            logger.trace("tcp port: " + port);

            if (port == 0) {
                logger.debug("Incoming port is null, unable to start receiver");
                ctx.close();
                if (callback != null) {
                    callback.onFailure("Unable to initialize Receiver port");
                }
                return;
            } else {
                if (callback != null) {
                    callback.onSuccess(port);
                }
            }

            backend.bind("inproc://backend");

            for (int i = 0; i < 5; i++) {
                new Thread(new ReceiverWorker(ctx), "ZMQThread" + i).start();
            }

            // Connect backend to frontend via a proxy, will return only when the context is closed
            boolean b = ZMQ.proxy(frontend, backend, null);
            logger.debug("ZMQ.proxy returned with value " + b);
        } catch (ZMQException e) {
            logger.debug("ZMQException in ZMQReceiver" + e);
        } catch (Exception e) {
            logger.debug("Exception in ZMQReceiver" + e);
        } finally {
            frontend.close();
            backend.close();
        }
    }

    public void stop() {
        if (ctx != null) {
            ctx.close();
        }
    }

    private class ReceiverWorker implements Runnable {
        private final ZContext ctx;

        public ReceiverWorker(final ZContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            ZMQ.Socket worker = ctx.createSocket(ZMQ.DEALER);
            worker.connect("inproc://backend");

            while (true) {
                try {
                    //The DEALER socket gives us the address envelope and message
                    ZMsg msg = ZMsg.recvMsg(worker);

                    ZFrame address = msg.pop();
                    ZFrame content = msg.pop();
                    logger.trace(Thread.currentThread().getName() + " address: " + address + " content: " + content);

                    if (onMessageReceivedListener != null) {
                        onMessageReceivedListener.processIncomingMessage(new String(address.getData()), content.getData());
                    }
                    //jp2p.processIncomingMessage(new String(address.getData()), content.getData());
                } catch (ZMQException e) {
                    logger.debug("ZMQException in ReceiverWorker" + e);
                    worker.close();
                    break;
                } catch (Exception e) {
                    logger.debug("Exception in ReceiverWorker" + e);
                    worker.close();
                    break;
                }
            }
        }
    }
}

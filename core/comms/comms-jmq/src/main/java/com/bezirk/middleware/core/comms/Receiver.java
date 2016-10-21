package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZMsg;

class Receiver extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private static final int NO_OF_DEALERS = 5;
    private final ZMQ.Context context;
    private volatile int port;
    private final ZMQ.Socket frontend;
    private final ZMQ.Socket backend;
    private Dealer[] dealers;
    private final OnMessageReceivedListener onMessageReceivedListener;

    interface OnMessageReceivedListener {
        boolean processIncomingMessage(String nodeId, byte[] data);
    }

    Receiver(@Nullable final OnMessageReceivedListener onMessageReceivedListener) {
        this.onMessageReceivedListener = onMessageReceivedListener;
        this.context = ZMQ.context(1);
        frontend = context.socket(ZMQ.ROUTER);
        port = frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);
        logger.debug("frontend port " + port);
        backend = context.socket(ZMQ.DEALER);
        backend.bind("inproc://backend");
    }

    @Override
    public void run() {
        dealers = new Dealer[NO_OF_DEALERS];

        for (int i = 0; i < NO_OF_DEALERS; i++) {
            dealers[i] = new Dealer(context, "Dealer" + i, onMessageReceivedListener);
            dealers[i].start();
        }

        logger.debug("Starting proxy...");

        ZMQ.proxy(frontend, backend, null);

        logger.debug("Closing frontend and backend");
        frontend.close();
        backend.close();
    }

    public void close() {
        context.term();
        try {
            for (Dealer dealer : dealers) {
                dealer.interrupt();
                dealer.join();
                logger.debug(dealer.getName() + " finished");
            }
        } catch (InterruptedException e) {
        }
    }

    public int getPort() {
        return port;
    }

    private static class Dealer extends Thread {
        private final ZMQ.Context context;
        private final ZMQ.Socket socket;
        private final OnMessageReceivedListener onMessageReceivedListener;

        public Dealer(@NotNull final ZMQ.Context context, @Nullable final String name,
                      @Nullable final OnMessageReceivedListener onMessageReceivedListener) {
            this.context = context;
            this.onMessageReceivedListener = onMessageReceivedListener;
            setName(name);
            socket = context.socket(ZMQ.DEALER);
        }

        @Override
        public void run() {
            logger.debug("Starting dealer " + getName());
            socket.connect("inproc://backend");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final ZMsg msg = ZMsg.recvMsg(socket);

                    final ZFrame address = msg.pop();
                    final ZFrame content = msg.pop();

                    final String nodeId;
                    nodeId = new String(address.getData(), ZMQ.CHARSET);

                    if (onMessageReceivedListener != null) {
                        onMessageReceivedListener.processIncomingMessage(nodeId, content.getData());
                    }

                } catch (ZMQException e) {
                    if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                        break;
                    }
                }
            }
            socket.close();
        }

    }

}

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
    public static final int MINIMUM_PORT_NUMBER = 0xc000;
    public static final int MAXIMUM_PORT_NUMBER = 0xffff;

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

        initializePort();

        logger.debug("frontend port {}", port);
        backend = context.socket(ZMQ.DEALER);
        backend.bind("inproc://backend");
    }

    private void initializePort() {
        // We bind to a random port on a separate thread because otherwise this code could
        // run on an Android activity thread. Android does not allow network operations
        // on such threads.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                port = frontend.bindToRandomPort("tcp://*", MINIMUM_PORT_NUMBER, MAXIMUM_PORT_NUMBER);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            logger.error("Interrupted while fetching Receiver port", e);
            Thread.currentThread().interrupt();
        }
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
            logger.error("JMQ Receiver interrupted during close");
            Thread.currentThread().interrupt();
        }
    }

    public int getPort() {
        return port;
    }

    private static class Dealer extends Thread {
        private final ZMQ.Socket socket;
        private final OnMessageReceivedListener onMessageReceivedListener;

        public Dealer(@NotNull final ZMQ.Context context, @Nullable final String name,
                      @Nullable final OnMessageReceivedListener onMessageReceivedListener) {
            this.onMessageReceivedListener = onMessageReceivedListener;
            setName(name);
            socket = context.socket(ZMQ.DEALER);
        }

        @Override
        public void run() {
            logger.debug("Starting dealer {}", getName());
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
                        // We just log without throwing/logging the exception again as this is always
                        // called when the ZMQ.Context is terminated. This is common practice in jeromq.
                        // See this issue for details (https://github.com/zeromq/jeromq/issues/116)
                        logger.debug("Ending JMQ receiver loop due to ETERM error code");
                        break;
                    }
                }
            }
            socket.close();
        }

    }

}

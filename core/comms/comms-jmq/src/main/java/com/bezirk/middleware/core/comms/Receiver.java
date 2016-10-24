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

        logger.debug("frontend port " + port);
        backend = context.socket(ZMQ.DEALER);
        backend.bind("inproc://backend");
    }


    private void initializePort(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                port = frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

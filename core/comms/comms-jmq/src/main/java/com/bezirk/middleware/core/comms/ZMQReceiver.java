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

import com.bezirk.middleware.core.comms.processor.WireMessage;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZMsg;

import java.io.UnsupportedEncodingException;


class ZMQReceiver implements Runnable {
    interface ReceiverPortInitializedCallback {
        void onSuccess(int port);

        void onFailure(String errorMessage);
    }

    interface OnMessageReceivedListener {
        boolean processIncomingMessage(String nodeId, byte[] data);
    }

    private static final Logger logger = LoggerFactory.getLogger(ZMQReceiver.class);
    private static final int MAX_RECEIVER_THREADS = 5;

    private ZContext ctx;
    //  Frontend socket talks to clients over TCP
    private ZMQ.Socket frontend;
    //  Backend socket talks to workers over inproc
    private ZMQ.Socket backend;
    private final ReceiverPortInitializedCallback callback;
    private final OnMessageReceivedListener onMessageReceivedListener;

    ZMQReceiver(@Nullable final ReceiverPortInitializedCallback callback,
                @Nullable final OnMessageReceivedListener onMessageReceivedListener) {
        this.callback = callback;
        this.onMessageReceivedListener = onMessageReceivedListener;
    }

    @Override
    public void run() {

        try {
            this.ctx = new ZContext();
            this.frontend = ctx.createSocket(ZMQ.ROUTER);
            this.backend = ctx.createSocket(ZMQ.DEALER);
            final int port = frontend.bindToRandomPort("tcp://*", 0xc000, 0xffff);
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

            for (int i = 0; i < MAX_RECEIVER_THREADS; i++) {
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

        ReceiverWorker(final ZContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            ZMQ.Socket worker = ctx.createSocket(ZMQ.DEALER);
            worker.connect("inproc://backend");

            while (true) {
                try {
                    //The DEALER socket gives us the address envelope and message
                    final ZMsg msg = ZMsg.recvMsg(worker);

                    final ZFrame address = msg.pop();
                    final ZFrame content = msg.pop();
                    if (logger.isTraceEnabled()) {
                        logger.trace("{} address: {} content: {}",
                                Thread.currentThread().getName(), address, content);
                    }

                    final String nodeId;
                    try {
                        nodeId = new String(address.getData(), WireMessage.ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getLocalizedMessage());
                        throw new AssertionError(e);
                    }

                    if (onMessageReceivedListener != null) {
                        onMessageReceivedListener.processIncomingMessage(
                                nodeId, content.getData());
                    }
                } catch (ZMQException e) {
                    logger.debug("ZMQException in ReceiverWorker", e);
                    worker.close();
                    break;
                } catch (Exception e) {
                    logger.debug("Exception in ReceiverWorker", e);
                    worker.close();
                    break;
                }
            }
        }
    }
}

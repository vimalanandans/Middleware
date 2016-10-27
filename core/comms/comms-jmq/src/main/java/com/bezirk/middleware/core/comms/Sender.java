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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private final ZMQ.Context context;
    private final Map<UUID, ZMQ.Socket> connections;
    private final UUID myId;
    private final String groupName;
    private final long startTime;
    private final Queue<byte[]> messageQueue;
    private boolean timeElapsed = false;
    private boolean timerTaskInitialized = false;
    private volatile boolean timerTaskFinished = false;
    private static final int DELAY = 2000;
    private static final int MAX_MESSAGES_IN_QUEUE = 50;
    private Timer timer;


    public Sender(@NotNull final UUID myId, @NotNull final String groupName) {
        startTime = System.currentTimeMillis();
        messageQueue = new LinkedList<>();
        context = ZMQ.context(1);
        connections = new HashMap<>();
        this.myId = myId;
        this.groupName = groupName;
    }

    public void addConnection(@NotNull final UUID uuid, @NotNull final PeerMetaData peerMetaData) {
        logger.trace("adding connection");
        synchronized (connections) {
            if (connections.containsKey(uuid)) {
                logger.trace("Connection to the node {} already exist", uuid);
                return;
            }
        }
        final ZMQ.Socket socket = context.socket(ZMQ.DEALER);
        socket.setIdentity(myId.toString().getBytes(ZMQ.CHARSET));
        socket.connect("tcp:/" + peerMetaData.getInetAddress() + ":" + peerMetaData.getPort());
        logger.debug("connecting to peer");
        synchronized (connections) {
            connections.put(uuid, socket);
        }
    }

    public void removeConnection(@NotNull final UUID uuid) {
        synchronized (connections) {
            if (connections.containsKey(uuid)) {
                ZMQ.Socket socket = connections.get(uuid);
                socket.close();
                connections.remove(uuid);
                logger.debug("Removing connection to peer {}, no. of connected peer(s) {}", uuid, connections.size());
            }
        }
    }

    public void send(@NotNull final byte[] data) {
        if (timeElapsed) {
            if (timerTaskInitialized) {
                if (timerTaskFinished) {
                    sendMsg(data);
                } else {
                    logger.debug("Queue is being flushed, this message is being dropped. Try sending messages with greater intervals between them.");
                }
            } else {
                sendMsg(data);
            }
        } else {
            if (checkTimeElapsed()) {
                timeElapsed = true;
                send(data);
            } else {
                //TODO remove synchronization when not accessing the queue
                synchronized (messageQueue) {
                    if (messageQueue.size() == MAX_MESSAGES_IN_QUEUE) {
                        //TODO throw decent error message and return???
                        logger.warn("Dropping message as the maximum allowed messages during first {}ms of initializing bezirk are {}", DELAY, MAX_MESSAGES_IN_QUEUE);
                        return;
                    } else {
                        logger.trace("message added to queue");
                        messageQueue.add(data);
                        if (!timerTaskInitialized) {
                            final long currentTime = System.currentTimeMillis();
                            final long delayForTaskToRun = DELAY - (currentTime - startTime);
                            initializeTimerTask(delayForTaskToRun);
                            timerTaskInitialized = true;
                            logger.debug("Timer Initialized. Delay set for cleaning the message queue {}", delayForTaskToRun);
                        } else {
                            logger.trace("Timer for message queue cleanup already initialized.");
                        }
                    }
                }
            }
        }
    }

    private void initializeTimerTask(final long delay) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (messageQueue) {
                    if (messageQueue.peek() == null) {
                        timerTaskFinished = true;
                        this.cancel();
                        logger.debug("Timer task is finished");
                    } else {
                        logger.debug("Timer task is sending message");
                        sendMsg(messageQueue.remove());
                    }
                }
            }
        }, delay, 5);
    }

    private boolean checkTimeElapsed() {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > DELAY) {
            //logger.debug("");
            return true;
        }
        return false;
    }

    public void sendMsg(@NotNull final byte[] data) {
        final Set<ZMQ.Socket> recipients;
        synchronized (connections) {
            if (connections.size() > 0) {
                recipients = new HashSet<>(connections.values());
            } else {
                logger.warn("No remote peers found for group {}. Message will not be received by any other bezirk instance.", groupName);
                return;
            }
        }
        for (ZMQ.Socket socket : recipients) {
            socket.send(data);
        }
    }

    public void send(@NotNull final UUID recipient, @NotNull final byte[] data) {
        synchronized (connections) {
            if (connections.containsKey(recipient)) {
                connections.get(recipient).send(data);
                logger.debug("sending to recipient {}", recipient);
            }
        }
    }

    public void close() {
        synchronized (connections) {
            for (ZMQ.Socket socket : connections.values()) {
                socket.close();
            }
        }
        context.term();
        if (timer != null) {
            timer.cancel();
            logger.debug("cancelling the timer");
        }
    }


}

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private final ZMQ.Context context;
    private final Map<UUID, ZMQ.Socket> connections;
    private final UUID myId;

    public Sender(@NotNull final UUID myId) {
        context = ZMQ.context(1);
        connections = new HashMap<>();
        this.myId = myId;
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
        final Set<ZMQ.Socket> recipients;
        synchronized (connections) {
            if (connections.size() > 0) {
                recipients = new HashSet<>(connections.values());
            } else {
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
    }
}

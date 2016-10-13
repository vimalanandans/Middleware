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
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetAddress;
import java.util.UUID;

public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    private final UUID uuid;
    private final InetAddress inetAddress;
    private final int port;
    private final ZMQ.Socket socket;

    public Node(final int port) {
        this.uuid = UUID.randomUUID();
        this.port = port;
        this.inetAddress = null;
        this.socket = null;
    }

    public Node(@NotNull final UUID uuid, @NotNull final InetAddress inetAddress, final int port, final ZMQ.Socket socket) {
        this.uuid = uuid;
        this.port = port;
        this.inetAddress = inetAddress;
        this.socket = socket;
    }

    public synchronized void send(@NotNull ZContext context, @NotNull final Node toNode, @NotNull final byte[] data) {
        socket.send(data);
    }

    public void close() {
        if (socket != null) {
            logger.debug("Closing socket for " + uuid);
            socket.close();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public ZMQ.Socket getSocket() {
        return socket;
    }
}


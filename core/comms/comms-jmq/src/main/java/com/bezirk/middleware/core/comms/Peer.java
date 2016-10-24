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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A peer has the following functionalities
 * <ul>
 * <li>Discover other <code>Peer</code>(s) on the same machine and network</li>
 * <li>Send messages to a single <code>Peer</code> or all <code>Peer</code>(s)
 * </li>
 * <li>Receive messages from the discovered peers</li>
 * <li>Clean up resources like sockets, contexts, etc when stopped</li>
 * <li>TODO Manage network re-connections</li>
 * <li>TODO Remove peers from which a beacon is not received for a particular amount of time</li>
 * </ul>
 */
public class Peer implements Beacon.BeaconCallback {
    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    private final String name;
    private final Receiver receiver;
    private final UUID uuid;
    private final Beacon beacon;
    private final Sender sender;
    private final Map<UUID, PeerMetaData> myPeers;
    private final Receiver.OnMessageReceivedListener onMessageReceivedListener;

    public Peer(@Nullable final String name, @Nullable final Receiver.OnMessageReceivedListener onMessageReceivedListener) {
        this.name = name;
        this.onMessageReceivedListener = onMessageReceivedListener;
        receiver = new Receiver(onMessageReceivedListener);
        uuid = UUID.randomUUID();
        if (receiver.getPort() <= 0) {
            throw new AssertionError("port not initialized, port is " + receiver.getPort());
        }
        beacon = new Beacon(null, receiver.getPort(), uuid, this);
        myPeers = new HashMap<>();
        sender = new Sender(uuid);
    }

    public void start() {
        receiver.start();
        beacon.start();
    }

    public void stop() {
        receiver.close();
        beacon.stop();
        sender.close();
    }

    public UUID getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void processPeer(@NotNull final UUID uuid, @NotNull final InetAddress senderInetAddress, @NotNull final int port) {
        final PeerMetaData peerMetaData;
        synchronized (myPeers) {
            if (myPeers.containsKey(uuid)) {
                return;
            } else {
                peerMetaData = new PeerMetaData(senderInetAddress, port);
                myPeers.put(uuid, peerMetaData);
                logger.trace("{} added to myPeers, total peers {}", uuid, myPeers.size());
            }
        }
        sender.addConnection(uuid, peerMetaData);
    }

    public void send(byte[] data) {
        sender.send(data);
    }

    public void send(UUID recipient, byte[] data) {
        sender.send(recipient, data);
    }


}
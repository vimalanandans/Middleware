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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A peer has the following functionalities
 * <ul>
 * <li>Discover other <code>Peer</code>(s) on the same machine and network</li>
 * <li>Send messages to a single <code>Peer</code> or all <code>Peer</code>(s)
 * </li>
 * <li>Receive messages from the discovered peers</li>
 * <li>Clean up resources like sockets, contexts, etc when stopped</li>
 * <li>TODO Manage network re-connections</li>
 * </ul>
 */
public class Peer implements Beacon.BeaconCallback {
    private static final Logger logger = LoggerFactory.getLogger(Peer.class);
    //maximum wait time(in ms) within which if a udp-beacon from another peer is not received, the peer is removed.
    private static final int MAX_IDLE_TIME = 5000;
    private final Receiver receiver;
    private final UUID uuid;
    private final Beacon beacon;
    private final Sender sender;
    private final Map<UUID, PeerMetaData> myPeers;
    private final ScheduledExecutorService service;

    public Peer(@Nullable final String groupName, @Nullable final Receiver.OnMessageReceivedListener onMessageReceivedListener) {
        receiver = new Receiver(onMessageReceivedListener);
        uuid = UUID.randomUUID();
        if (receiver.getPort() < Receiver.MINIMUM_PORT_NUMBER ||
                receiver.getPort() > Receiver.MAXIMUM_PORT_NUMBER) {
            throw new AssertionError(String.format(Locale.getDefault(),
                    "Port not initialized to an expected value. Expected range = %d to %d, actual = %d",
                    Receiver.MINIMUM_PORT_NUMBER, Receiver.MAXIMUM_PORT_NUMBER, receiver.getPort()));
        }
        beacon = new Beacon(groupName, receiver.getPort(), uuid, this);
        myPeers = new HashMap<>();
        sender = new Sender(uuid, groupName);
        service = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        receiver.start();
        beacon.start();
        service.scheduleAtFixedRate(new CleanupRunnable(), 3, 3, TimeUnit.SECONDS);
    }

    public void stop() {
        stopExecutor();
        receiver.close();
        beacon.stop();
        sender.close();
    }

    public UUID getId() {
        return uuid;
    }

    public void processPeer(@NotNull final UUID uuid, @NotNull final InetAddress senderInetAddress, final int port) {
        final PeerMetaData peerMetaData;
        synchronized (myPeers) {
            if (myPeers.containsKey(uuid)) {
                //update lastSeen time
                myPeers.get(uuid).setLastSeen(System.currentTimeMillis());
                return;
            } else {
                peerMetaData = new PeerMetaData(senderInetAddress, port, System.currentTimeMillis());
                myPeers.put(uuid, peerMetaData);
                logger.trace("{} added to myPeers, total peers {}", uuid, myPeers.size());
            }
        }
        sender.addConnection(uuid, peerMetaData);
    }

    public void send(byte[] data) {
        if (logger.isTraceEnabled()) {
            for (UUID id : sender.getConnections().keySet()) {
                if (myPeers.containsKey(id)) {
                    final PeerMetaData peerMetaData = myPeers.get(id);
                    logger.trace("Sending to {}:{} last seen {}", peerMetaData.getInetAddress(),
                            peerMetaData.getPort(), peerMetaData.getLastSeen());
                } else {
                    logger.trace("Sender map contains a uuid that does not exist in the known peers map");
                }
            }
        }

        sender.send(data);
    }

    public void send(UUID recipient, byte[] data) {
        if (logger.isTraceEnabled()) {
            if (sender.getConnections().containsKey(recipient)) {
                final PeerMetaData peerMetaData = myPeers.get(recipient);
                logger.trace("Unicasting to {}:{} last seen {} with id {}", peerMetaData.getInetAddress(),
                        peerMetaData.getPort(), peerMetaData.getLastSeen(), recipient);
            }
        }

        if (!sender.send(recipient, data)) {
            logger.trace("Failed to send data to recipient with id {}", recipient);
        }
    }

    /**
     * Responsible for checking {@link #myPeers} and removing those peers, from which a beacon has not been received for atleast {@link #MAX_IDLE_TIME} time
     */
    private class CleanupRunnable implements Runnable {
        @Override
        public void run() {
            synchronized (myPeers) {
                if (myPeers.size() > 0) {
                    final long currentTime = System.currentTimeMillis();
                    for (Iterator<Map.Entry<UUID, PeerMetaData>> it = myPeers.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<UUID, PeerMetaData> entry = it.next();
                        if (currentTime - entry.getValue().getLastSeen() > MAX_IDLE_TIME) {
                            logger.debug("Removing peer {}", entry.getKey());
                            sender.removeConnection(entry.getKey());
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    private void stopExecutor() {
        if (service != null) {
            // Disable new tasks from being submitted
            service.shutdown();
            try {
                // Wait a while for existing tasks to terminate
                if (!service.awaitTermination(200, TimeUnit.MILLISECONDS)) {
                    service.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!service.awaitTermination(200, TimeUnit.MILLISECONDS)) {
                        logger.error("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                service.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}
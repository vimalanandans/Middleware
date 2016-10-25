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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZBeacon;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

public class JmqComms implements ZMQReceiver.ReceiverPortInitializedCallback {
    private static final Logger logger = LoggerFactory.getLogger(JmqComms.class);
    private static final int THREAD_POOL_SIZE = 5;
    private static final int POOL_TIMEOUT = 200;
    private static final int MSG_FIELDS = 4;
    private Node selfNode;
    private final Map<UUID, Node> nodeMap;
    private final ZMQReceiver zmqReceiver;
    private NodeDiscovery nodeDiscovery;

    //context used for sending data to other nodes
    private final ZContext context;
    private ExecutorService sendMessageService;
    private final String groupName;

    public JmqComms(@Nullable final ZMQReceiver.OnMessageReceivedListener onMessageReceivedListener,
                    @Nullable final String groupName) {
        this.groupName = groupName;
        nodeMap = new ConcurrentHashMap<>();
        // create the ZMQReceiver
        zmqReceiver = new ZMQReceiver(this, onMessageReceivedListener);
        context = new ZContext();
    }

    public void start() {
        new Thread(zmqReceiver).start();
        // manage further initialization using ReceiverPortInitializedCallback method
    }

    @Override
    public void onSuccess(final int port) {
        logger.debug("Port initialized successfully, initializing JmqComms");
        selfNode = new Node(port);
        nodeDiscovery = new NodeDiscovery(groupName);
        nodeDiscovery.start();
        sendMessageService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public void onFailure(String errorMessage) {
        logger.debug("Unable to start jeromq comms layer");
    }

    public void stop() {
        //closing is done in a thread so that android does not complain about NetworkOnMainThreadException
        new Thread(new Runnable() {
            @Override
            public void run() {
                zmqReceiver.stop();
                nodeDiscovery.stop();
                for (Node node : nodeMap.values()) {
                    node.close();
                }
                selfNode.close();
                if (context != null) {
                    context.close();
                }
                shutdownAndAwaitTermination(sendMessageService);
            }
        }).start();
    }

    public synchronized void addNode(UUID uuid, InetAddress sender, int port) {
        logger.trace("Peer found " + uuid);
        if (nodeMap.containsKey(uuid)) {
            logger.trace("Peer exist in map " + uuid + " number of peers " + nodeMap.size());
        } else {
            ZMQ.Socket socket = context.createSocket(ZMQ.DEALER);
            // Client identifies as same name as this node id, so that receiver identifies it
            try {
                socket.setIdentity(selfNode.getUuid().toString().getBytes(WireMessage.ENCODING));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
            socket.connect("tcp:/" + sender + ":" + port);
            nodeMap.put(uuid, new Node(uuid, sender, port, socket));
            logger.debug("Current Node id {}::Peer with id {} added with port {}",
                    selfNode.getUuid().toString(), uuid, port);

        }
    }


    public boolean shout(final byte[] data) {
        logger.debug("sending data from node {} to {} nodes",
                selfNode.getUuid(), nodeMap.values().size());

        for (Node node : nodeMap.values()) {
            logger.trace("at node {} whispering to {}" + selfNode.getUuid(), node.getUuid());
            whisper(node, data);
        }
        return true;
    }

    public boolean whisper(final String recipient, final byte[] data) {
        UUID uuid = UUID.fromString(recipient);
        return nodeMap.containsKey(uuid) && whisper(nodeMap.get(uuid), data);
    }

    private boolean whisper(@NotNull final Node node, @NotNull final byte[] data) {
        if (sendMessageService != null) {
            if (!sendMessageService.isShutdown() && !sendMessageService.isTerminated()) {
                sendMessageService.submit(new Runnable() {
                    @Override
                    public void run() {
                        node.getSocket().send(data);
                    }
                });
            } else {
                logger.debug("ExecutorService for sending messages is either shutdown or terminated.");
            }
        } else {
            logger.debug("ExecutorService is not initialized.");
        }

        return true;
    }

    public UUID getNodeId() {
        return (selfNode != null) ? selfNode.getUuid() : null;
    }


    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        // Disable new tasks from being submitted
        pool.shutdown();
        logger.debug("sendMessageService shutdown");
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(POOL_TIMEOUT, TimeUnit.MILLISECONDS)) {
                logger.debug("sendMessageService shutdownNow");
                // Cancel currently executing tasks
                pool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(POOL_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    logger.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private class NodeDiscovery {
        private static final String DEFAULT_GROUP_NAME = "bezirk";
        private static final String SEPARATOR = "::";
        private static final String beaconHost = "255.255.255.255";
        private static final int beaconPort = 5670;
        private byte[] beaconDataArr;
        private ZBeacon zbeacon;
        private final String groupName;

        NodeDiscovery(@Nullable final String groupName) {
            this.groupName = (groupName != null) ? groupName : DEFAULT_GROUP_NAME;
            logger.info("GroupName for current bezirk instance {}", this.groupName);
        }

        private void processBeacon(final InetAddress sender, final byte[] beacon) {
            String beaconString;
            try {
                beaconString = new String(beacon, WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
            String[] data;
            try {
                data = beaconString.split(SEPARATOR);
            } catch (PatternSyntaxException e) {
                logger.error("Invalid beacon received, error splitting beacon string", e);
                return;
            }

            if (data.length == MSG_FIELDS) {
                try {
                    long lsb = Long.parseLong(data[1]);
                    long msb = Long.parseLong(data[2]);
                    UUID uuid = new UUID(msb, lsb);
                    int port = Integer.parseInt(data[3]);
                    addNode(uuid, sender, port);
                } catch (NumberFormatException e) {
                    logger.error("NumberFormatException while processing beacon", e);
                }
            }
        }

        public void start() {
            final String beaconData = groupName + SEPARATOR + selfNode.getUuid().getLeastSignificantBits() +
                    SEPARATOR + selfNode.getUuid().getMostSignificantBits() +
                    SEPARATOR + String.valueOf(selfNode.getPort());
            try {
                beaconDataArr = beaconData.getBytes(WireMessage.ENCODING);
                zbeacon = new ZBeacon(beaconHost, beaconPort, beaconDataArr, false);
                zbeacon.setPrefix(groupName.getBytes(WireMessage.ENCODING));
                //this ensures only beacon with this prefix is processed
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }

            zbeacon.setUncaughtExceptionHandlers(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.warn("Unable to beacon. This generally happens due to loss of network connectivity. " +
                            "When network connectivity is resumed, existing nodes can communicate again. " +
                            "Communication between existing & new nodes would require Bezirk to be restarted.");
                }
            }, new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.warn("Unable to beacon. This generally happens due to loss of network connectivity. " +
                            "When network connectivity is resumed, existing nodes will still be able to communicate. " +
                            "Communication between existing & new nodes would require Bezirk to be restarted.");
                }
            });
            zbeacon.setListener(new ZBeacon.Listener() {
                @Override
                public void onBeacon(InetAddress sender, byte[] beacon) {
                    // ignore self id
                    if (!Arrays.equals(beacon, beaconDataArr)) {
                        processBeacon(sender, beacon);
                    } else {
                        logger.trace("self node, sender {} data {}", sender, Arrays.toString(beacon));
                    }
                }
            });
            zbeacon.start();
        }

        public void stop() {
            try {
                if (zbeacon != null) {
                    zbeacon.stop();
                }
            } catch (InterruptedException e) {
                logger.error("Failed to close JMQ comms instance", e);
                Thread.currentThread().interrupt();
            }
        }
    }

}

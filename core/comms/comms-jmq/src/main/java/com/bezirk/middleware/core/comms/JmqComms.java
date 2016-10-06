package com.bezirk.middleware.core.comms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZBeacon;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

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
    private Node selfNode;
    private final Map<UUID, Node> nodeMap;
    private final ZMQReceiver zmqReceiver;
    private NodeDiscovery nodeDiscovery;
    private ZContext context; //context used for sending data to other nodes
    private ExecutorService sendMessageService;
    private final String groupName;

    public JmqComms(@Nullable final ZMQReceiver.OnMessageReceivedListener onMessageReceivedListener, @Nullable final String groupName) {
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
        logger.trace("Port initialized successfully, initializing JmqComms");
        selfNode = new Node(port);
        nodeDiscovery = new NodeDiscovery(groupName);
        nodeDiscovery.start();
        sendMessageService = Executors.newFixedThreadPool(5);
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
            // TODO add other validations and cleanup
        } else {
            ZMQ.Socket socket = context.createSocket(ZMQ.DEALER);
            // Client identifies as same name as this node id, so that receiver identifies it
            socket.setIdentity(selfNode.getUuid().toString().getBytes());
            //FIXME: get actual endpoint
            socket.connect("tcp:/" + sender + ":" + port);
            nodeMap.put(uuid, new Node(uuid, sender, port, socket));
            logger.trace("Current Node id " + selfNode.getUuid().toString() + "::Peer with " + uuid + " added with port " + port);
        }
    }


    public boolean shout(final byte[] data) {
        logger.trace("at node " + selfNode.getUuid() + " no of nodes " + nodeMap.values().size() + " for data " + Arrays.toString(data));
        for (Node node : nodeMap.values()) {
            logger.trace("at node " + selfNode.getUuid() + " whispering to " + node.getUuid() + " data " + Arrays.toString(data));
            whisper(node, data);
        }
        return true;
    }

    public boolean whisper(final String recipient, final byte[] data) {
        UUID uuid = UUID.fromString(recipient);
        if (nodeMap.containsKey(uuid)) {
            return whisper(nodeMap.get(uuid), data);
        } else {
            return false;
        }
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


    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        logger.debug("sendMessageService shutdown");
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(200, TimeUnit.MILLISECONDS)) {
                logger.debug("sendMessageService shutdownNow");
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(200, TimeUnit.MILLISECONDS))
                    System.err.println("Pool did not terminate");
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
        private static final String SEPERATOR = "::";
        private static final String beaconHost = "255.255.255.255";
        private static final int beaconPort = 5670; // this is zyre port
        private String beaconData;
        private ZBeacon zbeacon;
        private String groupName;

        NodeDiscovery(@Nullable final String groupName) {
            this.groupName = (groupName != null) ? groupName : DEFAULT_GROUP_NAME;
            logger.info("GroupName for current bezirk instance " + groupName);
        }

        private void processBeacon(final InetAddress sender, final byte[] beacon) {
            String beaconString = new String(beacon);
            String[] data;
            try {
                data = beaconString.split(SEPERATOR);
            } catch (PatternSyntaxException e) {
                logger.error("PatternSyntaxException " + e);
                return;
            }

            if (data.length == 4) // right format
            {
                try {
                    long lsb = Long.parseLong(data[1]);
                    long msb = Long.parseLong(data[2]);
                    UUID uuid = new UUID(msb, lsb);
                    int port = Integer.parseInt(data[3]);
                    addNode(uuid, sender, port);
                } catch (NumberFormatException n) {
                    logger.error("NumberFormatException while processing beacon " + n);
                }
            }
        }

        public void start() {
            beaconData = groupName + SEPERATOR + selfNode.getUuid().getLeastSignificantBits() +
                    SEPERATOR + selfNode.getUuid().getMostSignificantBits() +
                    SEPERATOR + String.valueOf(selfNode.getPort()); //this ensures only beacon with this prefix is processed
            zbeacon = new ZBeacon(beaconHost, beaconPort, beaconData.getBytes(), false);
            zbeacon.setPrefix(groupName.getBytes());
            zbeacon.setUncaughtExceptionHandlers(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.warn("Unable to beacon. This generally happens due to loss of network connectivity. When network connectivity is resumed, existing nodes will still be able to communicate. Communication between existing & new nodes would require Bezirk to be restarted.");
                }
            }, new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.warn("Unable to beacon. This generally happens due to loss of network connectivity. When network connectivity is resumed, existing nodes will still be able to communicate. Communication between existing & new nodes would require Bezirk to be restarted.");
                }
            });
            zbeacon.setListener(new ZBeacon.Listener() {
                @Override
                public void onBeacon(InetAddress sender, byte[] beacon) {
                    // ignore self id
                    if (!Arrays.equals(beacon, beaconData.getBytes())) {
                        processBeacon(sender, beacon);
                    } else {
                        logger.trace("self node, sender " + sender + " data " + Arrays.toString(beacon));
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
                e.printStackTrace();
            }
        }
    }

}

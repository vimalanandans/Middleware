package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zmq.ZError;

public class Peers {
    private static final Logger logger = LoggerFactory.getLogger(Peers.class);
    private final Node selfNode;
    private final Map<UUID, Peer> peersMap;
    private ExecutorService executorService;
    // TODO : Avoid concurrentHashMap, impacts performance
    // used by the beacon and sender

    public Peers(final Node selfNode) {
        this.selfNode = selfNode;
        this.peersMap = new HashMap<>();
    }

    public synchronized void validatePeer(UUID uuid, InetAddress sender, int port) {
        //System.out.println("Peer found " + uuid);
        if (peersMap.containsKey(uuid)) {
            //System.out.println("Peer exist in map " + uuid + " number of peers " + peersMap.size());
            //valid peer.
            // TODO add other validations and cleanup
        } else {
            //new peer
            createNewPeer(uuid, sender, port);
        }
    }

    public void createNewPeer(UUID uuid, InetAddress sender, int port) {
        Peer peer = new Peer(new Node(uuid, sender, port));
        peersMap.put(uuid, peer);
        peer.connect(selfNode);
        //System.out.println("Current Node id " + selfNode.getUuid().toString() + "::Peer with " + uuid + " added with port " + port);
    }

    /**
     * send to all
     */
    public boolean shout(final byte[] data) {
        for (Peer peer : peersMap.values()) {
            try {
                //peer.send(data);
                send(peer, data);
            } catch (ZError.IOException e) {
                logger.error("ZError.IOException");
            }
            //  System.out.println(" shout to "+peer.selfNode.getUuid()+" port > " +peer.selfNode.getPort());
        }
        return true;
    }

    /**
     * send to one
     */
    public boolean whisper(String receiver, byte[] data) {
        UUID uuid = UUID.fromString(receiver);
        if (peersMap.containsKey(uuid)) {
            //peersMap.get(uuid).send(data);
            send(peersMap.get(uuid), data);
            return true;
        }
        return false;
    }

    private void send(final Peer peer, final byte[] data) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    peer.send(data);
                }
            });
        } else {
            logger.debug("executorService is null or now shutdown");
        }
    }

    public void start() {
        logger.debug("Starting peers");
        executorService = Executors.newFixedThreadPool(5);
    }

    public void stop() {
        logger.debug("Stopping peers");
        if (executorService != null) {
            shutdownAndAwaitTermination(executorService);
        }
    }

    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        logger.info("Peers shutdown");
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                logger.info("Peers shutdownNow");
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}

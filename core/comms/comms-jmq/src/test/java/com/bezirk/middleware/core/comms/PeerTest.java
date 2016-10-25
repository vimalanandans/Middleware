package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.WireMessage;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import ch.qos.logback.classic.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PeerTest {

    private static final Logger logger = LoggerFactory.getLogger(PeerTest.class);

    private enum Peers {PEER_A, PEER_B}

    private Peer p1;
    private Peer p2;

    /**
     * Start peer - send msgs - stop peer - repeat 3 times. Successful test ensures no thread leakage especially reapers and io-threads.
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        for (int i = 0; i < 3; i++) {
            Peer peer = new Peer("Peer" + i, null);
            peer.start();
            int count = 0;
            while (count < 50) {
                peer.send(("Msg" + (count++)).getBytes());
                Thread.sleep(5);
            }
            peer.stop();
            Thread.sleep(500);
        }
        int endThreads = Utils.getNumberOfThreadsInSystem(false);
        assertEquals(startThreads, endThreads);
    }

    /**
     * Start 2 peers - each send's multicast out - for each multicast received the received sends a unicast back.
     *
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        Utils.setLogLevel(Level.ERROR);
        final int startThreads = Utils.getNumberOfThreadsInSystem(false);
        //total multicasts to send out by each node
        final int TOTAL_MSGS_TO_SEND = 100;

        Listener p1Listener = new Listener(Peers.PEER_A);
        Listener p2Listener = new Listener(Peers.PEER_B);
        p1 = new Peer(Peers.PEER_A.toString(), p1Listener);
        p2 = new Peer(Peers.PEER_B.toString(), p2Listener);

        p1.start();
        p2.start();

        //allow peers to discover & connect
        Thread.sleep(1500);
        int count = 0;
        while (count < TOTAL_MSGS_TO_SEND) {
            p1.send((Peers.PEER_A.toString() + Integer.toString(count)).getBytes());
            p2.send((Peers.PEER_B.toString() + Integer.toString(count)).getBytes());
            count++;
            //sleep between each message
            Thread.sleep(5);
        }
        p1.stop();
        p2.stop();

        //allow graceful shutdown of components
        Thread.sleep(1000);

        logger.error("responses received at {} {}, responses received at {} {}", Peers.PEER_A.toString(), p1Listener.getTotalResponsesReceived(), Peers.PEER_B.toString(), p2Listener.getTotalResponsesReceived());

        assertTrue((p1Listener.getTotalResponsesReceived() == p2Listener.getTotalResponsesReceived()) && (p1Listener.getTotalResponsesReceived() == TOTAL_MSGS_TO_SEND));

        int endThreads = Utils.getNumberOfThreadsInSystem(false);
        assertEquals(startThreads, endThreads);
    }

    private void send(final Peers fromPeer, final String data) {
        switch (fromPeer) {
            case PEER_A:
                p1.send(p2.getId(), data.getBytes());
                break;
            case PEER_B:
                p2.send(p1.getId(), data.getBytes());
                break;
        }
    }

    private static Peers getCounterPart(Peers peer) {
        switch (peer) {
            case PEER_A:
                return Peers.PEER_B;
            case PEER_B:
                return Peers.PEER_A;
        }
        return null;
    }

    private class Listener implements Receiver.OnMessageReceivedListener {
        private final Peers peer;
        private final Peers counterPeer;
        private int totalResponsesReceived;

        Listener(Peers peer) {
            this.peer = peer;
            this.counterPeer = getCounterPart(peer);
            logger.info("Listener setup for peer {}, counterPart peer {}", peer, counterPeer);
        }

        @Override
        public boolean processIncomingMessage(String nodeId, byte[] data) {
            final String stringData;
            final String dataToSendBack;
            try {
                stringData = new String(data, WireMessage.ENCODING);
                logger.trace("at peer {} received data {}", peer.toString(), stringData);
                if (stringData.contains(counterPeer.toString())) {
                    //respond to other peer's multicast
                    dataToSendBack = stringData.substring(counterPeer.toString().length(), stringData.length());
                    logger.trace("at peer {} dataToSendBack {}", peer.toString(), dataToSendBack);
                    send(peer, dataToSendBack);
                } else {
                    //handle unicast response from the other peer
                    int response = Integer.parseInt(stringData);
                    totalResponsesReceived++;
                    logger.trace("at peer {}, received response {}", peer.toString(), response);
                }
            } catch (UnsupportedEncodingException e) {
                return false;
            }
            return true;
        }

        public int getTotalResponsesReceived() {
            return totalResponsesReceived;
        }
    }
}

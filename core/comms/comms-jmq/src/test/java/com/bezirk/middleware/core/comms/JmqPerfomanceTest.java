package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.WireMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.classic.Level;

import static org.junit.Assert.assertTrue;

public class JmqPerfomanceTest {
    private static final Logger logger = LoggerFactory.getLogger(JmqPerfomanceTest.class);
    private static final int NO_MSGS_TO_SEND_FROM_EACH_NODE = 10000;
    private static final int SLEEP_BETWEEN_EACH_MESSAGE = 5; //gap between each sent message in milliseconds
    private static final int NO_OF_NODES = 4; //no of nodes to be used for testing
    private static long testStartTime;
    private static long testStopTime;
    private static final long NO_OF_EXPECTED_MSGS_PER_NODE = (NO_OF_NODES - 1) * NO_MSGS_TO_SEND_FROM_EACH_NODE;
    private static final long TOTAL_EXPECTED_MSGS = NO_OF_EXPECTED_MSGS_PER_NODE * NO_OF_NODES;
    private static long numberOfReceivedMessages;
    private static int totalResultsCollected;

    static class TestJp2p implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(TestJp2p.class);
        private final JmqComms jmqComms;
        private final Map<String, Integer> peerInfoMap;
        private int shoutCount = 1;
        private int currentMsgData;
        private final ZMQReceiver.OnMessageReceivedListener onMessageReceivedListener;

        public TestJp2p() throws InterruptedException {
            this.peerInfoMap = new ConcurrentHashMap<>();
            this.onMessageReceivedListener = new Listener();
            jmqComms = new JmqComms(onMessageReceivedListener, null);
            jmqComms.start();

            //wait until nodeId is generated
            while (jmqComms.getNodeId() == null) {
                Thread.sleep(100);
            }
            logger.debug("Created node: " + jmqComms.getNodeId());
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SLEEP_BETWEEN_EACH_MESSAGE);
                    if (shoutCount <= NO_MSGS_TO_SEND_FROM_EACH_NODE) {
                        logger.trace("Shouting: " + shoutCount);
                        String data = String.valueOf(shoutCount++);
                        jmqComms.shout(data.getBytes());

                        if (shoutCount == NO_MSGS_TO_SEND_FROM_EACH_NODE + 1) {
                            //ensure last few messages are received before evaluating the count and closing comms
                            Thread.sleep(100);
                            int totalMsgsReceived = 0;
                            for (int values : peerInfoMap.values()) {
                                totalMsgsReceived += values;
                            }
                            logger.debug("Total msgs received by {} : {}", jmqComms.getNodeId(), totalMsgsReceived);
                            addResult(totalMsgsReceived);
                            jmqComms.stop();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    logger.debug(Thread.currentThread().getName() + " interrupted");
                    break;
                }
            }

        }

        private class Listener implements ZMQReceiver.OnMessageReceivedListener {
            @Override
            public synchronized boolean processIncomingMessage(String nodeId, byte[] data) {
                try {
                    currentMsgData = Integer.parseInt(new String(data, WireMessage.ENCODING));
                } catch (UnsupportedEncodingException uee) {
                    logger.error(uee.getLocalizedMessage());
                    throw new AssertionError(uee);
                } catch (NumberFormatException e) {
                    logger.debug("Receiving messages from other sources, ignoring");
                }
                logger.trace("Node {} received message {} from node {}", jmqComms.getNodeId(), currentMsgData, nodeId);
                if (!peerInfoMap.containsKey(nodeId)) {
                    assert (currentMsgData == 1);
                    peerInfoMap.put(nodeId, currentMsgData);
                } else {
                    final int currentValue = peerInfoMap.get(nodeId);
                    logger.trace("current value {}, msgData received {}", currentValue, currentMsgData);
                    assert (currentValue + 1 == currentMsgData);
                    peerInfoMap.put(nodeId, currentMsgData);
                }
                return true;
            }
        }
    }


    //@org.junit.Test
    public void test() throws InterruptedException {
        Utils.setLogLevel(Level.INFO);
        Thread[] nodes = new Thread[NO_OF_NODES];

        //setup the nodes
        for (int i = 0; i < NO_OF_NODES; i++) {
            nodes[i] = new Thread(new TestJp2p(), "Node" + i);
        }

        //allow all nodes to discover each other
        Thread.sleep(2000);

        testStartTime = System.currentTimeMillis();

        //start the nodes
        for (int i = 0; i < NO_OF_NODES; i++) {
            nodes[i].start();
        }

        //main thread keeps running until all results from threads are collected.
        while (totalResultsCollected != NO_OF_NODES) {
            Thread.sleep(500);
        }

        //allow for graceful shutdown of comms
        if (totalResultsCollected == NO_OF_NODES) {
            Thread.sleep(1000);
        }
    }

    static synchronized void addResult(final int numberOfReceivedMessages) throws InterruptedException {
        JmqPerfomanceTest.numberOfReceivedMessages += numberOfReceivedMessages;
        if (++totalResultsCollected == NO_OF_NODES) {
            testStopTime = System.currentTimeMillis();

            final float reliability = (JmqPerfomanceTest.numberOfReceivedMessages / TOTAL_EXPECTED_MSGS);
            //achieve atleast 95% reliability
            assertTrue(reliability > 0.95 && reliability <= 1);

            logger.info("****************************RESULT****************************");
            logger.info("Total time taken for test ==> " + (testStopTime - testStartTime) + " ms");
            logger.info("Number of nodes: " + NO_OF_NODES);
            logger.info("Number of Expected messages: " + TOTAL_EXPECTED_MSGS);
            logger.info("Number of Received messages: " + JmqPerfomanceTest.numberOfReceivedMessages);
            logger.info("Message reliability: " + (reliability * 100) + "%");
        }
    }
}

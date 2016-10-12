package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.WireMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;

import ch.qos.logback.classic.Level;

public class JmqStressTest {
    private static final Logger logger = LoggerFactory.getLogger(JmqStressTest.class);
    private static final int TEST_RUNTIME = 2000;
    private long testStartTime;
    private long testStopTime;
    private static int numberOfNodes;
    private static long numberOfExpectedMessages;
    private static long numberOfReceivedMessages;

    static class TestJp2p implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(TestJp2p.class);
        private static final int MAX_MESSAGES_TO_SHOUT = 20000000; //max messages that can be shouted from a node
        private static final int SLEEP_BETWEEN_EACH_MESSAGE = 20; //in milliseconds
        private static final String AT_NODE_TEXT = "At Node : ";
        private static final String FROM_NODE_TEXT = " From Node : ";
        private static final String DATA_RECEIVED_TEXT = " Data Received : ";

        private final String atNodeText;
        private final JmqComms jmqComms;
        private final Map<String, PeerData> peerInfoMap;
        private PeerData peerData;
        private int shoutCount;
        private int currentMsgData;
        private final ZMQReceiver.OnMessageReceivedListener onMessageReceivedListener;

        public TestJp2p() {
            this.peerInfoMap = new ConcurrentHashMap<>();
            this.onMessageReceivedListener = new Listener();
            jmqComms = new JmqComms(onMessageReceivedListener, null);
            jmqComms.start();

            //sleep till nodeId is generated
            while (jmqComms.getNodeId() == null) {
                logger.info("waiting for nodeId to be initialized ... ");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            atNodeText = AT_NODE_TEXT + jmqComms.getNodeId();
            logger.debug("Created node: " + jmqComms.getNodeId());
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SLEEP_BETWEEN_EACH_MESSAGE);
                    if (shoutCount < MAX_MESSAGES_TO_SHOUT) {
                        shoutCount++;
                        String data = String.valueOf(shoutCount);
                        jmqComms.shout(data.getBytes());
                        logger.trace("Shouted: " + shoutCount);
                    }
                } catch (InterruptedException e) {
                    logger.debug(Thread.currentThread().getName() + " interrupted");
                    addResult(jmqComms.getNodeId().toString(), peerInfoMap.entrySet());
                    jmqComms.stop();
                    break;
                }
            }

        }

        private class Listener implements ZMQReceiver.OnMessageReceivedListener {
            @Override
            public synchronized boolean processIncomingMessage(String nodeId, byte[] data) {
                try {
                    currentMsgData = Integer.parseInt(new String(data, WireMessage.ENCODING));
                }catch (UnsupportedEncodingException uee){
                    logger.error(uee.getLocalizedMessage());
                    throw new AssertionError(uee);
                }
                catch (NumberFormatException e) {
                    logger.debug("Other messages being received");
                }
                logger.trace(atNodeText + FROM_NODE_TEXT + nodeId + DATA_RECEIVED_TEXT + currentMsgData);
                if (!peerInfoMap.containsKey(nodeId)) {
                    peerInfoMap.put(nodeId, new PeerData(currentMsgData));
                } else {
                    peerData = peerInfoMap.get(nodeId);
                    if (peerData.getLastValue() + 1 != currentMsgData) {
                        logger.debug("Ordering issue => " + atNodeText + FROM_NODE_TEXT + nodeId + DATA_RECEIVED_TEXT + currentMsgData + " Last seen data: " + peerData.getLastValue());
                        return false;
                    } else {
                        logger.trace("order is fine");
                    }
                    PeerData peerData = peerInfoMap.get(nodeId);
                    peerData.setLastValue(currentMsgData);
                    peerInfoMap.put(nodeId, peerData);
                }
                return true;
            }
        }
    }


    //@org.junit.Test
    public void test() throws InterruptedException {
        Utils.setLogLevel(Level.DEBUG);
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        logger.trace("before start, no of threads " + startThreads);
        TestJp2p testJp2p1 = new TestJp2p();
        TestJp2p testJp2p2 = new TestJp2p();
        TestJp2p testJp2p3 = new TestJp2p();
        TestJp2p testJp2p4 = new TestJp2p();
        Thread t1 = new Thread(testJp2p1, "TestJp2p1");
        Thread t2 = new Thread(testJp2p2, "TestJp2p2");
        Thread t3 = new Thread(testJp2p3, "TestJp2p3");
        Thread t4 = new Thread(testJp2p4, "TestJp2p4");

        testStartTime = System.currentTimeMillis();

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        logger.info("Test has started");

        try {
            Thread.sleep(TEST_RUNTIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testStopTime = System.currentTimeMillis();

        logger.info("no of threads " + Utils.getNumberOfThreadsInSystem(false));

        //stop the threads
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();

        logger.info("shutting down threads ... ");
        Thread.sleep(2000); //wait for shutdown to complete

        printResults();

        float reliability = (numberOfReceivedMessages / numberOfExpectedMessages);
        assertTrue(reliability > 0.95 && reliability <= 1); //achieve atleast 95% reliability

        logger.info("no of threads " + Utils.getNumberOfThreadsInSystem(true));
    }

    static synchronized void addResult(String nodeId, Set<Map.Entry<String, PeerData>> set) {
        numberOfNodes++;
        for (Map.Entry<String, PeerData> peerInfo : set) {
            String peerId = peerInfo.getKey();
            PeerData peerData = peerInfo.getValue();
            int expectedTotal = peerData.getLastValue() - peerData.getFirstValue();
            numberOfExpectedMessages += expectedTotal;
            numberOfReceivedMessages += peerData.getTotalValuesReceived();
        }
    }

    private void printResults() {
        logger.info("****************************RESULT****************************");
        logger.info("Total time taken for test ==> " + (testStopTime - testStartTime) + " ms");
        logger.info("Number of nodes: " + numberOfNodes);
        logger.info("Number of Expected messages: " + numberOfExpectedMessages);
        logger.info("Number of Received messages: " + numberOfReceivedMessages);
        logger.info("Message reliability: " + ((numberOfReceivedMessages / numberOfExpectedMessages) * 100) + "%");
    }

    private static class PeerData {
        private final int firstValue; //first data value received from the peer
        private int lastValue; //last data value received from the peer
        private int totalValuesReceived;

        public PeerData(final int firstValue) {
            this.firstValue = firstValue;
            this.lastValue = firstValue;
        }

        public int getFirstValue() {
            return firstValue;
        }

        public int getLastValue() {
            return lastValue;
        }

        public int getTotalValuesReceived() {
            return totalValuesReceived;
        }

        public void setLastValue(int lastValue) {
            this.lastValue = lastValue;
            this.totalValuesReceived++;
        }

    }
}

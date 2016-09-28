package com.bezirk.middleware.core.comms;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;

import ch.qos.logback.classic.Level;

public class Jp2pStressTest {
    private static final Logger logger = LoggerFactory.getLogger(Jp2pStressTest.class);
    private static final int TEST_RUNTIME = 2000;
    private long testStartTime;
    private long testStopTime;
    private static int numberOfNodes;
    private static long numberOfExpectedMessages;
    private static long numberOfReceivedMessages;

    static class TestJp2p implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(TestJp2p.class);
        private static final int MAX_MESSAGES_TO_SHOUT = 20000000; //max messages that can be shouted from a node
        private static final int SLEEP_BETWEEN_EACH_MESSAGE = 400; //in milliseconds
        private static final String AT_NODE_TEXT = "At Node : ";
        private static final String FROM_NODE_TEXT = " From Node : ";
        private static final String DATA_RECEIVED_TEXT = " Data Received : ";

        private final String atNodeText;
        private final Jp2p jp2p;
        private final Map<String, PeerData> peerInfoMap;
        private PeerData peerData;
        private int shoutCount;
        private int currentMsgData;
        private final OnMessageReceivedListener onMessageReceivedListener;

        public TestJp2p() {
            this.peerInfoMap = new ConcurrentHashMap<>();
            this.onMessageReceivedListener = new Listener();
            jp2p = new Jp2p(onMessageReceivedListener);
            jp2p.start();
            atNodeText = AT_NODE_TEXT + jp2p.getNodeId();
            logger.debug("Created node: " + jp2p.getNodeId());
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SLEEP_BETWEEN_EACH_MESSAGE);
                    if (shoutCount < MAX_MESSAGES_TO_SHOUT) {
                        shoutCount++;
                        String data = String.valueOf(shoutCount);
                        jp2p.shout(data.getBytes());
                        logger.trace("Shouted: " + shoutCount);
                    }
                } catch (InterruptedException e) {
                    addResult(jp2p.getNodeId().toString(), peerInfoMap.entrySet());
                    jp2p.stop();
                }
            }

        }

        private class Listener implements OnMessageReceivedListener {
            @Override
            public synchronized boolean processIncomingMessage(String nodeId, byte[] data) {
                try {
                    currentMsgData = Integer.parseInt(new String(data));
                } catch (NumberFormatException e) {
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
                    }
                    PeerData peerData = peerInfoMap.get(nodeId);
                    peerData.setLastValue(currentMsgData);
                    peerInfoMap.put(nodeId, peerData);
                }
                return true;
            }
        }
    }


    @org.junit.Test
    public void test() {

//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//        System.out.println(threadSet.size());

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO); //change log level here
        logger.info("before start, no of threads "+getNumberOfThreadsInSystem());
        Thread t1 = new Thread(new TestJp2p(), "TestJp2p1");
        Thread t2 = new Thread(new TestJp2p(), "TestJp2p2");
        Thread t3 = new Thread(new TestJp2p(), "TestJp2p3");
        Thread t4 = new Thread(new TestJp2p(), "TestJp2p4");

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

        //stop the threads using interrupts
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        t4.interrupt();

        //wait for results to be aggregated
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printResults();
        float reliability = (numberOfReceivedMessages / numberOfExpectedMessages);
        assertTrue(reliability > 0.95 && reliability <= 1); //achieve atleast 95% reliability

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("no of threads "+getNumberOfThreadsInSystem());
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

    private int getNumberOfThreadsInSystem(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for(Thread t : threadSet){
            logger.info(t.getName()+t.getState()+t.getThreadGroup()+t.isAlive());
//            for(StackTraceElement e : t.getStackTrace()) {
//                logger.info(e.toString());
//            }
        }
        return threadSet.size();
    }

    @Test
    public void test1(){
        getNumberOfThreadsInSystem();
    }

}

package com.bezirk.middleware.core.comms;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    static class TestJp2p implements Runnable {
        private static final int MAX_MESSAGES_TO_SHOUT = 300;
        private static final int SLEEP_BETWEEN_EACH_MESSAGE = 100;
        private int shoutCount = 0;
        private final Jp2p jp2p;
        private final Map<String, Integer> peerData;
        private int lastSeenData;
        private int currentMsgData;

        public TestJp2p() {
            this.peerData = new ConcurrentHashMap<>();
            OnMessageReceivedListener onMessageReceivedListener = new OnMessageReceivedListener() {
                @Override
                public boolean processIncomingMessage(String nodeId, byte[] data) {
                    currentMsgData = Integer.parseInt(new String(data));
                    if (!peerData.containsKey(nodeId)) {
                        peerData.put(nodeId, currentMsgData);
                        //System.out.println("Current Node: " + jp2p.getNodeId() + " Received from Node: " + nodeId + ". Received data " + currentMsgData);
                    } else {
                        lastSeenData = peerData.get(nodeId);
                        if (lastSeenData + 1 != currentMsgData) {
                            System.out.println("Ordering issue => Current Node: " + jp2p.getNodeId() + " Received from Node: " + nodeId + ". Last seen data" + lastSeenData + " received data " + currentMsgData);
                            return false;
                        }
                        System.out.println(jp2p.getNodeId() + ":Order is fine");
                        peerData.put(nodeId, currentMsgData);
                    }
                    return true;
                }
            };
            jp2p = new Jp2p(onMessageReceivedListener);
            jp2p.start();
            System.out.println("Created node: " + jp2p.getNodeId());
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SLEEP_BETWEEN_EACH_MESSAGE);
                    if (shoutCount < MAX_MESSAGES_TO_SHOUT) {
                        shoutCount++;
                        String data = String.valueOf(shoutCount);
                        jp2p.shout(data.getBytes());
                        System.out.println("Shouted: " + shoutCount);
                    }

                } catch (InterruptedException e) {
                    System.out.println("finishing up");
                    //e.printStackTrace();
                }

            }

        }
    }


    @org.junit.Test
    public void test() {
        Thread t1 = new Thread(new TestJp2p());
        Thread t2 = new Thread(new TestJp2p());
        Thread t3 = new Thread(new TestJp2p());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Thread t4 = new Thread(new TestJp2p());
        t1.start();
        t2.start();
        t3.start();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread thread : threadSet) {
            System.out.println(thread.getName() + " : " + thread.getThreadGroup() + " : " + thread.getStackTrace());
        }
        //t4.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t1.interrupt();
        t2.interrupt();
        t3.interrupt();
        //t4.interrupt();
    }


}

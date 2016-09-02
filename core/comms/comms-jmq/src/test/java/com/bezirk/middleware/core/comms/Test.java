package com.bezirk.middleware.core.comms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    private static class TestJp2p implements Runnable {
        private int brCount = 0;
        private final Jp2p jp2p;
        private final Map<String, Integer> peerData;

        public TestJp2p() {
            this.peerData = new ConcurrentHashMap<>();
            MessageReceiver msg = new MessageReceiver() {
                @Override
                public boolean processIncomingMessage(String nodeId, byte[] data) {
                    int currentMsgData = Integer.parseInt(new String(data));
                    if (!peerData.containsKey(nodeId)) {
                        peerData.put(nodeId, currentMsgData);
                        System.out.println("Current Node: " + jp2p.getNodeId() + "Received from Node: " + nodeId + ". Received data " + currentMsgData);
                    } else {
                        int lastSeenData = peerData.get(nodeId);
                        if (lastSeenData + 1 != currentMsgData) {
                            System.out.println("Current Node: " + jp2p.getNodeId() + "Received from Node: " + nodeId + ". Last seen data" + lastSeenData + " received data " + currentMsgData);
                        }
                        peerData.put(nodeId, currentMsgData);
                    }
                    return false;
                }
            };
            jp2p = new Jp2p(msg);
        }

        public void run() {
            System.out.println("Testing shout started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                brCount++;
                String data = String.valueOf(brCount);
                jp2p.shout(data.getBytes());
            }
        }
    }

    public static void main(String[] args) {

        new Thread(new TestJp2p()).start();
        new Thread(new TestJp2p()).start();
//        new Thread(new TestJp2p()).start();
//        new Thread(new TestJp2p()).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @org.junit.Test
    public void test() {
        Test.main(null);
    }


}

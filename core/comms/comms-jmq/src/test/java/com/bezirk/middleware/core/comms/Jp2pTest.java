package com.bezirk.middleware.core.comms;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Jp2pTest {

    private int shoutCount = 0;
    private final Jp2p jp2p;
    private final Map<String, Integer> peerData;
    private Timer timer;
    private int id;

    public Jp2pTest() {
        this.peerData = new ConcurrentHashMap<>();
        this.jp2p = new Jp2p(new Listener());
    }

    public void start(final int id) {
        this.id = id;
        jp2p.start();
        System.out.println(id + ":Created node: " + jp2p.getNodeId());

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (shoutCount == 100) {
                    cancel();
                }
                shoutCount++;
                String data = String.valueOf(shoutCount);
                jp2p.shout(data.getBytes());
                System.out.println(id + ":Shouted: " + shoutCount);
            }
        }, 0, 50);
    }

    private final class Listener implements OnMessageReceivedListener {
        @Override
        public boolean processIncomingMessage(String nodeId, byte[] data) {
            System.out.println("got it");
            int currentMsgData = Integer.parseInt(new String(data));
            if (!peerData.containsKey(nodeId)) {
                peerData.put(nodeId, currentMsgData);
                System.out.println("Current Node: " + jp2p.getNodeId() + " Received from Node: " + nodeId + ". Received data " + currentMsgData);
            } else {
                int lastSeenData = peerData.get(nodeId);
                if (lastSeenData + 1 != currentMsgData) {
                    System.out.println("Ordering issue => Current Node: " + jp2p.getNodeId() + " Received from Node: " + nodeId + ". Last seen data" + lastSeenData + " received data " + currentMsgData);
                }
                System.out.println("At " + id + " lastSeenData +1 == currentMsgData == " + currentMsgData);
                peerData.put(nodeId, currentMsgData);
            }
            return false;
        }
    }

    private void stop() {
        jp2p.stop();
        timer.cancel();
    }

    @org.junit.Test
    public void test() {
        Jp2pTest jp2pTest1 = new Jp2pTest();
        Jp2pTest jp2pTest2 = new Jp2pTest();
//        Jp2pTest jp2pTest3 = new Jp2pTest();
//        Jp2pTest jp2pTest4 = new Jp2pTest();

        jp2pTest1.start(1);
        jp2pTest1.start(2);
//        jp2pTest1.start(3);
//        jp2pTest1.start(4);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jp2pTest1.stop();
        jp2pTest2.stop();
//        jp2pTest3.stop();
//        jp2pTest4.stop();
    }


}

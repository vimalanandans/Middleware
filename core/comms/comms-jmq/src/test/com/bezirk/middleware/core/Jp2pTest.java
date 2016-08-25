package com.bezirk.test;


import com.bezirk.comms.Jp2p;
import com.bezirk.comms.MessageReceiver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Jp2pTest {

    private static class TestJp2p implements Runnable {

        int brCount = 0;

        Jp2p Jp2p ;

        Map<String,Integer> peerData = new ConcurrentHashMap<String, Integer>();

        public TestJp2p()
        {
            MessageReceiver msg = new MessageReceiver() {
                @Override
                public boolean processIncomingMessage(String nodeId, byte[] data) {

                    int count = Integer.parseInt(new String(data));

                    if(!peerData.containsKey(nodeId))
                    {
                        // new node
                        peerData.put(nodeId,count);
                        System.out.println(nodeId + " : data received from new node. value > " + count);

                    }else{
                       // System.out.println(nodeId + " : data  > " + count + " old value " + peerData.get(nodeId));
                        int oldCount = peerData.get(nodeId)+1;

                        if(oldCount != count)
                        {
                            System.out.println(nodeId + " : data missed > " +  count + " difference " +  (oldCount-count) );
                        }
                        peerData.put(nodeId,count);
                    }

                    return false;
                }
            };
            Jp2p = new Jp2p(msg);
        }
        public void run() {
            System.out.println("Testing shout started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                brCount++;
                //brCount++;
                String data = String.valueOf(brCount);

                if (Jp2p.shout(data.getBytes())) {

                } else {
                    System.out.println("no one is there to listen to the shout");
                }
            }
        }
    }
    public static void main(String[] args) {
        //System.out.println("test Jp2p");

        new Thread(new TestJp2p()).start();
        new Thread(new TestJp2p()).start();
        new Thread(new TestJp2p()).start();
        new Thread(new TestJp2p()).start();


        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}

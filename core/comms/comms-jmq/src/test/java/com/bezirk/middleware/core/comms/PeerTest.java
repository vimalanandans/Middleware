package com.bezirk.middleware.core.comms;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PeerTest {

    /**
     * Start peer - send msgs - stop peer - repeat. Successful test ensures no thread leakage especially reapers and io-threads.
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        for (int i = 0; i < 3; i++) {
            Peer peer = new Peer("Peer" + i, null);
            peer.start();
            // Thread.sleep(50);
            int count = 0;
            while (count < 50) {
                peer.send("Msg" + (count++));
                Thread.sleep(5);
            }
            peer.stop();
            Thread.sleep(500);
        }
        int endThreads = Utils.getNumberOfThreadsInSystem(false);
        assertEquals(startThreads, endThreads);
    }
}

package com.bezirk.middleware.core.comms;

import org.junit.Test;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import static org.junit.Assert.assertEquals;

public class ReceiverTest {
    @Test
    public void test() throws InterruptedException {
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        Receiver receiver = new Receiver(null);
        receiver.start();
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.DEALER);

        final int receiverPort = receiver.getPort();
        //receiver binds to random ports starting 49152. zeromq chooses the first available. This test assumes 49152 port is available
        socket.connect("tcp://*:"+receiverPort);
        final byte[] bytes = {1};
        for (int i = 0; i < 5000; i++) {
            socket.send(bytes);
        }

        //wait for messages to be received
        Thread.sleep(1000);
        context.close();
        receiver.close();
        Thread.sleep(1000);
        int finalThreads = Utils.getNumberOfThreadsInSystem(false);
        assertEquals(startThreads, finalThreads);
    }
}

package com.bezirk.middleware.core.comms;

import org.junit.Test;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import static org.junit.Assert.assertEquals;

import java.util.Set;

public class ZMQReceiverTest {
    @Test
    public void test() throws InterruptedException {
        int startThreads = getNumberOfThreadsInSystem();
        ZMQReceiver zmqReceiver = new ZMQReceiver(null, null);
        Thread t = new Thread(zmqReceiver);
        t.start();
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(ZMQ.DEALER);

        //receiver binds to random ports starting 49152. zeromq chooses the first available. This test assumes 49152 port is available
        socket.connect("tcp://*:49152");
        for (int i = 0; i < 5000; i++) {
            socket.send("test message".getBytes());
        }

        //wait for messages to be received
        Thread.sleep(1000);
        context.close();
        zmqReceiver.stop();

        int finalThreads = getNumberOfThreadsInSystem();
        assertEquals(startThreads, finalThreads);
    }

    private int getNumberOfThreadsInSystem() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet) {
            System.out.println(t.getName() + t.getState() + t.getThreadGroup() + t.isAlive());
        }
        return threadSet.size();
    }
}

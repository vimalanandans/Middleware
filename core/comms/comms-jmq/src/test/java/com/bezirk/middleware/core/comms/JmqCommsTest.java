package com.bezirk.middleware.core.comms;

import com.bezirk.middleware.core.comms.processor.WireMessage;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ch.qos.logback.classic.Level;

import static org.junit.Assert.assertEquals;

public class JmqCommsTest {

    private static final String THREAD_NAME_1 = "T1";
    private static final String THREAD_NAME_2 = "T2";

    //@Test
    public void test() throws InterruptedException {
        Utils.setLogLevel(Level.ERROR);
        int startThreads = Utils.getNumberOfThreadsInSystem(true);
        JmqComms jmqComms = new JmqComms(null, null);
        jmqComms.start();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            jmqComms.shout(("JmqCommsTest " + i).getBytes());
        }
        //Thread.sleep(1000);
        jmqComms.stop();
        Thread.sleep(1000);
        int finalThreads = Utils.getNumberOfThreadsInSystem(true);

        //assertEquals(startThreads, finalThreads);
    }

    //@Test
    public void test1() throws InterruptedException {
        Utils.setLogLevel(Level.ERROR);
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        Thread t1 = new JmqCommsThread(THREAD_NAME_1);
        t1.start();
        Thread t2 = new JmqCommsThread(THREAD_NAME_2);
        t2.start();

        Thread.sleep(2000);

        //stop the threads
        t1.interrupt();
        t2.interrupt();

        //wait for executor service to shutdown
        Thread.sleep(2000);

        t1.join();
        t2.join();
        int finalThreads = Utils.getNumberOfThreadsInSystem(true);

        //assertEquals(startThreads, finalThreads);
    }

    private static class JmqCommsThread extends Thread {
        private final String threadName;
        private int sentMulticastMessages;
        private int recvMulticastMessages;
        private int recvUnicastMessages;

        public JmqCommsThread(String threadName) {
            this.threadName = threadName;
        }

        JmqComms jmqComms;
        ZMQReceiver.OnMessageReceivedListener onMessageReceivedListener = new ZMQReceiver.OnMessageReceivedListener() {
            @Override
            public synchronized boolean processIncomingMessage(String nodeId, byte[] data) {
                String dataString = null;
                try {
                    dataString = new String(data, WireMessage.ENCODING);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (threadName.equalsIgnoreCase(THREAD_NAME_1)) {
                    if (dataString.contains(THREAD_NAME_2)) {
                        recvMulticastMessages++;
                        String returnData = "from1";
                        //System.out.println("got msg from '" + THREAD_NAME_2 + "' with data '" + dataString + "', whispering back '" + returnData + "'");
                        jmqComms.whisper(nodeId, returnData.getBytes());
                    } else if (dataString.contains("from2")) {
                        recvUnicastMessages++;
                        //System.out.println("looks like a response is back");
                    }
                } else if (threadName.equalsIgnoreCase(THREAD_NAME_2)) {
                    if (dataString.contains(THREAD_NAME_1)) {
                        recvMulticastMessages++;
                        String returnData = "from2";
                        //System.out.println("got msg from '" + THREAD_NAME_1 + "' with data '" + dataString + "', whispering back '" + returnData + "'");
                        jmqComms.whisper(nodeId, returnData.getBytes());
                    } else if (dataString.contains("from1")) {
                        recvUnicastMessages++;
                        //System.out.println("looks like a response is back");
                    }
                }
                //jmqComms.whisper(nodeId, data);
                return false;
            }
        };

        @Override
        public void run() {
            jmqComms = new JmqComms(onMessageReceivedListener, null);
            try {
                int i = 0;
                jmqComms.start();

                Thread.sleep(100);
                while (!Thread.currentThread().isInterrupted()) {
                    jmqComms.shout((threadName + i++ + " ").getBytes());
                    sentMulticastMessages++;
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                System.out.println("Caught interrupted exception, shutting down JmqComms");
                jmqComms.stop();
                System.out.println("In thread " + threadName + "\nsentMulticastMessages " + sentMulticastMessages + "\nrecvMulticastMessages = sentUnicastMessages " + recvMulticastMessages + "\nrecvUnicastMessages" + recvUnicastMessages);
                return;
            }
        }
    }
}

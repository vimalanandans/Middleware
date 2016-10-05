package com.bezirk.middleware.core.comms;

import org.junit.Test;

import ch.qos.logback.classic.Level;

import static org.junit.Assert.assertEquals;

public class JmqCommsTest {

    @Test
    public void test() throws InterruptedException {
        Utils.setLogLevel(Level.INFO);
        int startThreads = Utils.getNumberOfThreadsInSystem(true);
        JmqComms jmqComms = new JmqComms(null);
        jmqComms.start();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            jmqComms.shout(("JmqCommsTest " + i).getBytes());
        }
        Thread.sleep(5000);
        jmqComms.stop();

        int finalThreads = Utils.getNumberOfThreadsInSystem(true);
        assertEquals(startThreads, finalThreads);
    }

    @Test
    public void test1() throws InterruptedException {
        Utils.setLogLevel(Level.INFO);
        int startThreads = Utils.getNumberOfThreadsInSystem(false);
        Thread t1 = new JmqCommsThread();
        t1.start();
        Thread t2 = new JmqCommsThread();
        t2.start();

        Thread.sleep(2000);

        //stop the threads
        t1.interrupt();
        t2.interrupt();

        //wait for executor service to shutdown
        Thread.sleep(500);

        t1.join();
        t2.join();
        int finalThreads = Utils.getNumberOfThreadsInSystem(true);
        assertEquals(startThreads, finalThreads);
    }

    private static class JmqCommsThread extends Thread {
        @Override
        public void run() {
            JmqComms jmqComms = new JmqComms(null);
            try {
                int i = 0;
                jmqComms.start();

                Thread.sleep(100);
                while (!Thread.currentThread().isInterrupted()) {
                    jmqComms.shout((this.getName() + i++ + " ").getBytes());
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                System.out.println("Caught interrupted exception, shutting down JmqComms");
                jmqComms.stop();
                return;
            }

        }
    }
}

package com.bezirk.middleware.java.comms;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Utils;
import org.zyre.Zyre;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Tests the request-response pattern where a single requester sends "ping"
 * and the responder replies with "pong"
 */
public class PingPongTest {
    private static final Logger logger = LoggerFactory.getLogger(PingPongTest.class);

    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String GROUP = "global";
    private Requester reqThread;
    private Responder respThread;

    private boolean passed = true;

    @Test
    public void test() throws Exception {
        NativeUtils.loadNativeBinaries();

        reqThread = new Requester();
        respThread = new Responder();

        // start requester, which waits for the responder to JOIN
        reqThread.init();
        reqThread.start();

        // start responder
        respThread.init();
        respThread.start();

        // wait for responder to finish
        respThread.join();

        // wait for requester to finish
        reqThread.join();
        reqThread.destroy();
        respThread.destroy();

        // leave some time for resources to be freed
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     *
     */
    private class Requester extends ZyreThread {

        public void run() {
            String peer;
            // wait for responder to join
            while (true) {
                String msg = zyre.recv();
                HashMap<String, String> map = Utils.parseMsg(msg);
                String event = map.get("event");
                peer = map.get("peer");

                if (event.equals("JOIN")) {
                    if (peer == null) {
                        logger.error("Peer is null");
                        passed = false;
                        return;
                    }
                    logger.info("responder joined: " + peer);
                    break;
                }
            }
            logger.info("sending ping");
            zyre.whisper(peer, PING);

            while (true) {
                String msg = zyre.recv();
                HashMap<String, String> map = Utils.parseMsg(msg);
                String event = map.get("event");

                if (event.equals("WHISPER")) {
                    logger.info("requester received response: " + msg);
                    String text = map.get("message");
                    if (!text.equals(PONG)) {
                        logger.error("Did not receive PONG.  Message was: " + text);
                        passed = false;
                    }
                    break;
                }
            }

        }

    }

    private class Responder extends ZyreThread {

        public void run() {

            logger.info("responder running");
            while (!Thread.currentThread().isInterrupted()) {
                String msg = zyre.recv();
                HashMap<String, String> map = Utils.parseMsg(msg);

                String event = map.get("event");

                if (event.equals("WHISPER")) {
                    logger.info("responder received: " + msg);
                    String text = map.get("message");
                    String peer = map.get("peer");

                    if (!text.equals(PING)) {
                        logger.error("Did not receive PING. Message was: " + text);
                        passed = false;
                    }

                    logger.info("sending pong");
                    zyre.whisper(peer, PONG);
                    break;
                }
            }
        }
    }

    private class ZyreThread extends Thread {

        protected Zyre zyre;

        public void init() {
            zyre = new Zyre();
            zyre.create();
            zyre.join(GROUP);
        }

        public void destroy() {
            zyre.destroy();
            // wait for zyre to close before exiting
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // intentionally blank
            }
        }
    }

}
package org.zyre;

import com.bezirk.comms.NativeUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static javax.xml.bind.DatatypeConverter.*;
import static org.junit.Assert.*;

/**
 * Tests the request-response pattern where a single requester sends "ping"
 * and the responder replies with "pong"
 */
public class PingPongEncTest {

    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String GROUP = "global";
    private static final Logger log = LoggerFactory.getLogger(PingPongEncTest.class);

    static {
        NativeUtils.loadLibs();

    }

    private Requester reqThread;
    private Responder respThread;

    private boolean passed = true;

    @Test
    public void test() throws Exception {

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
                        log.error("Peer is null");
                        passed = false;
                        return;
                    }
                    log.info("responder joined: " + peer);
                    break;
                }
            }
            log.info("sending ping");

            // compress and encode
            byte[] encbytes = Compressor.compress(PING);
            String encstr = printBase64Binary(encbytes);

            zyre.whisper(peer, encstr);

            while (true) {
                String msg = zyre.recv();
                HashMap<String, String> map = Utils.parseMsg(msg);
                String event = map.get("event");

                if (event.equals("WHISPER")) {
                    log.info("requester received response: " + msg);

                    // decode and decompress
                    String text = map.get("message");
                    byte[] textbytes = parseBase64Binary(text);
                    text = Compressor.decompress(textbytes);

                    if (!text.equals(PONG)) {
                        log.error("Did not receive PONG.  Message was: " + text);
                        passed = false;
                    }
                    log.info("received PONG!");
                    break;
                }
            }

        }

    }

    private class Responder extends ZyreThread {

        public void run() {

            log.info("responder running");
            while (!Thread.currentThread().isInterrupted()) {
                String msg = zyre.recv();
                HashMap<String, String> map = Utils.parseMsg(msg);

                String event = map.get("event");

                if (event.equals("WHISPER")) {
                    log.info("responder received: " + msg);
                    String text = map.get("message");

                    // decode and decompress
                    byte[] textbytes = parseBase64Binary(text);
                    text = Compressor.decompress(textbytes);

                    String peer = map.get("peer");

                    if (!text.equals(PING)) {
                        log.error("Did not receive PING. Message was: " + text);
                        passed = false;
                    }

                    log.info("sending pong");

                    // compress and encode
                    byte[] encbytes = Compressor.compress(PONG);
                    String encString = printBase64Binary(encbytes);

                    zyre.whisper(peer, encString);
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

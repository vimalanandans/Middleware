package com.bezirk.comms;

import com.bezirk.processor.CommsProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * ZYRE COMMS JNI IMPLEMENTATION
 */

public class ZyreCommsJni extends Thread {

    public static final Logger log = LoggerFactory.getLogger(ZyreCommsJni.class);
    public static final String BEZIRK_GROUP = "BEZIRK_GROUP";

    static {
        NativeUtils.loadLibs();

    }

    CommsProcessor commsProcessor;
    private Zyre zyre;
    private Map<String, List<String>> peers = new HashMap<String, List<String>>();
    private String group = BEZIRK_GROUP;

    public ZyreCommsJni(CommsProcessor commsProcessor) {

        this.commsProcessor = commsProcessor;

    }

    /**
     * initialize the zyre
     */
    public boolean initZyre() {

        zyre = new Zyre();
        /*try{
			zyre = new Zyre();
		}
		catch (UnsatisfiedLinkError e) {

			log.error("Unable to load zyre comms. ", e);
            return false;
        }*/
        // create the zyre
        zyre.create();

        return true;
    }

    public boolean closeComms() {

        if (zyre != null)
            zyre.destroy();

        // what else do to close comes
        // IS IT OK TO DO THE BELOW?
        zyre = null;

        return false;
    }

    /**
     * start the zyre
     */
    public boolean startZyre() {

        if (zyre != null) {

            // join the group
            zyre.join(getGroup());

            // start the receiver
            start();

            return true;
        }
        return false;
    }

    /**
     * stop the zyre
     */
    public boolean stopZyre() {

        //stop the thread
        interrupt();

        return true;
    }

    // send zyre whisper
    public boolean sendToAllZyre(byte[] msg, boolean isEvent) {
        // in zyre we are sending ctrl and event in same. isEvent is ignored
        String data = new String(msg);

        if (zyre != null) {

            zyre.shout(getGroup(), data);

            log.debug("Multicast size : >> " + data.length());//+ " data >> " + data);

            return true;
        }

        return false;
    }

    // send zyre whisper
    public boolean sendToOneZyre(byte[] msg, String nodeId, boolean isEvent) {

        // in zyre we are sending ctrl and event in same. isEvent is ignored
        String data = new String(msg);
        if (zyre != null) {
            //send to the specific node
            zyre.whisper(nodeId, data);

            //log.debug("Unicast size : >> " + data.length() + " data >> "+data);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (group == null) {
            log.error("group not set");
            return;
        }
        if (zyre == null) {
            log.error("Zyre not set");
            return;
        }
        zyre.join(group);

        while (true) {

            HashMap<String, String> eventMap = receive();

            if (eventMap.isEmpty())
                return;

            String eventType = eventMap.get("event");
            String peer = eventMap.get("peer");
            String group = eventMap.get("group");
            String payload = eventMap.get("message");

            // A Zyre-enabled device enters the network
            if (eventType.equals("ENTER")) {
                handleEnter(peer);
            }
            // Message sent to a particular device
            else if (eventType.equals("WHISPER")) {
                handleWhisper(peer, payload);
            }
            // Message sent to all members of a group
            else if (eventType.equals("SHOUT")) {
                handleShout(peer, group, payload);
            }
            // A device joins a group
            else if (eventType.equals("JOIN")) {
                handleJoin(peer, group);
                logKnownDevices();
            }
            // A device explicitly leaves a group
            else if (eventType.equals("LEAVE")) {
                handleLeave(peer, group);
                logKnownDevices();
            }
            // A device exits the network
            else if (eventType.equals("EXIT")) {
                handleExit(peer);
                logKnownDevices();
            } else {
                System.out.println("unknown event: " + eventType);
            }
        }
    }

    /**
     * handling the revieve
     */
    private HashMap<String, String> receive() {
        String incoming = zyre.recv();

        HashMap<String, String> eventMap = new HashMap<>();

        if ((incoming == null) || incoming.isEmpty())
            return eventMap;

        //log.info("IN Size << " + incoming.length());//+" data << " +incoming);

        if (incoming == null) {// Interrupted
            log.warn("Interrupted during recv()");
            return eventMap;
        }

        if (Thread.interrupted()) {
            log.info("RecvThread exiting");
            return eventMap;
        }


        // Convert the incoming string into a Map
        //eventMap = Utils.parseMsg(incoming);
        eventMap = parseMsg(incoming);

        if (eventMap.isEmpty() || eventMap.get("event") == null) {
            log.info("event map has bytes. parse special : experimental ");
            //  return parseMsgExt(incoming);// to be fixed
            return eventMap;
        }

        return eventMap;
    }


    public HashMap<String, String> parseMsg(String msg) {
		/*
			 * The message is created in the JNI C code with the statement:
			 * snprintf(ret, len, "event::%s|peer::%s|group::%s|message::%s", event, peer, group, message);
			 */

        final String MSG_DELIM = "\\|";  // separates each part of the message
        final String KV_DELIM = "::";    // separates keys from values
        final int NUM_PARTS = 4;
        HashMap<String, String> result = new HashMap<String, String>();
        List<String> pairs = Arrays.asList(msg.split(MSG_DELIM, NUM_PARTS));

        if (pairs.size() != NUM_PARTS) {
            System.err.println("recv() did not return exactly " + NUM_PARTS + " key/value pairs");
            return result;
        }

        for (String pair : pairs) {
            List<String> kv = Arrays.asList(pair.split("::", 2));
            if (kv.size() == 0) {
                // key and value are empty - do nothing
            } else if (kv.size() == 1) {
                // value is null
                result.put(kv.get(0), null);
            } else {
                result.put(kv.get(0), kv.get(1));
            }
        }

        if (result.get("event") == null) {
            return new HashMap<String, String>();
        }

        return result;
    }


    private void logKnownDevices() {
        for (String group : peers.keySet()) {
            log.debug("devices in " + group + " : " + peers.get(group));
        }
    }

    private void handleEnter(String zyreDeviceId) {
        log.info("peer (" + zyreDeviceId + ") entered network");
    }

    private void handleWhisper(String zyreDeviceId, String payload) {

        //log.info("peer (" + zyreDeviceId + ") Whisper to  " + zyreDeviceId + ": " + payload);
        log.info("data size > " + payload.length());

        commsProcessor.processWireMessage(zyreDeviceId, payload);
    }

    private void handleShout(String zyreDeviceId, String group, String payload) {


        //log.info("peer (" + zyreDeviceId + ") shouted to group " + group + ": " + payload);
        log.info("data size > " + payload.length());


        commsProcessor.processWireMessage(zyreDeviceId, payload);


    }


    private void handleJoin(String zyreDeviceId, String group) {
        addPeer(group, zyreDeviceId);
        log.info("peer (" + zyreDeviceId + ") joined: " + group);
    }

    private void handleLeave(String zyreDeviceId, String group) {
        boolean success = removePeer(group, zyreDeviceId);
        log.info("peer (" + zyreDeviceId + ") left " + group + ":" + success);
    }

    private void handleExit(String zyreDeviceId) {
        boolean success = removePeer(zyreDeviceId);
        log.debug("peer (" + zyreDeviceId + ") exited: " + success);
    }

    private boolean addPeer(String group, String zyreDeviceId) {
        List<String> deviceList = peers.get(group);
        if (deviceList == null) {
            deviceList = new ArrayList<String>();
            peers.put(group, deviceList);
        }

        return deviceList.add(zyreDeviceId);
    }

    private boolean removePeer(String zyreDeviceId) {
        if (peers == null || peers.isEmpty()) {
            return false;
        }

        boolean allRemovesSucceeded = true;

        for (String group : peers.keySet()) {
            if (!removePeer(group, zyreDeviceId)) {
                log.debug("remove failed: " + zyreDeviceId);
                allRemovesSucceeded = false;
            }
        }

        return allRemovesSucceeded;
    }

    private boolean removePeer(String group, String zyreDeviceId) {
        peers.get(group);
        List<String> deviceList = peers.get(group);

        if (deviceList == null) {
            return false;
        }

        return deviceList.remove(zyreDeviceId);
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


}


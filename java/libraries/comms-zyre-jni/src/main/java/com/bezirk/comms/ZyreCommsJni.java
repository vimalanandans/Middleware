package com.bezirk.comms;

import com.bezirk.comms.processor.CommsProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * ZYRE COMMS JNI IMPLEMENTATION
 */

public class ZyreCommsJni extends Thread {
    public static final Logger logger = LoggerFactory.getLogger(ZyreCommsJni.class);

    public static final String BEZIRK_GROUP = "BEZIRK_GROUP";

    static {
        NativeUtils.loadNativeBinaries();

    }

    CommsProcessor commsProcessor;
    private Zyre zyre;
    private Map<String, List<String>> peers = new HashMap<>();
    private String group = BEZIRK_GROUP;

    public ZyreCommsJni(CommsProcessor commsProcessor) {

        this.commsProcessor = commsProcessor;

    }

    public ZyreCommsJni(CommsProcessor commsProcessor, String group) {

        this.commsProcessor = commsProcessor;

        if(group != null) // on valid group name replace the default group name
            this.group = group;

    }

    /**
     * initialize the zyre
     */
    public boolean initZyre() {

        //zyre = new Zyre();
        try{
            zyre = new Zyre();
		}
		catch (UnsatisfiedLinkError e) {

			//logger.error("Unable to load zyre comms. ", e);
            logger.error("Unable to load zyre libraries. \n" +
                "Please refer http://developer.bezirk.com/documentation/installation_setup.php");
            return false;
        }
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

        final String data;

        try {
            data = new String(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not available on this system").initCause(e);
        }


        if (zyre != null) {

            zyre.shout(getGroup(), data);

           // logger.debug("Multicast size : >> " + data.length());//+ " data >> " + data);

            return true;
        }else{
            logger.debug("zyre is null");
        }

        return false;
    }

    // send zyre whisper
    public boolean sendToOneZyre(byte[] msg, String nodeId, boolean isEvent) {
        final String data;

        try {
            data = new String(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not available on this system").initCause(e);
        }

        if (zyre != null) {
            //send to the specific node
            zyre.whisper(nodeId, data);

            //logger.debug("Unicast size : >> " + data.length() + " data >> "+data);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (group == null) {
            logger.error("group not set");
            return;
        }
        if (zyre == null) {
            logger.error("Zyre not set");
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
            switch (eventType) {
                case "ENTER":
                    handleEnter(peer);
                    break;
                // Message sent to a particular device
                case "WHISPER":
                    handleWhisper(peer, payload);
                    break;
                // Message sent to all members of a group
                case "SHOUT":
                    handleShout(peer, group, payload);
                    break;
                // A device joins a group
                case "JOIN":
                    handleJoin(peer, group);
                    logKnownDevices();
                    break;
                // A device explicitly leaves a group
                case "LEAVE":
                    handleLeave(peer, group);
                    logKnownDevices();
                    break;
                // A device exits the network
                case "EXIT":
                    handleExit(peer);
                    logKnownDevices();
                    break;
                default:
                    logger.debug("unknown event: " + eventType);
                    break;
            }
        }
    }

    /**
     * handling the revieve
     */
    private HashMap<String, String> receive() {
        String incoming = zyre.recv();

        HashMap<String, String> eventMap = new HashMap<>();

        if ((incoming == null) || incoming.equals(""))
            return eventMap;

        if (Thread.interrupted()) {
            logger.debug("RecvThread exiting");
            return eventMap;
        }


        // Convert the incoming string into a Map
        //eventMap = Utils.parseMsg(incoming);
        eventMap = parseMsg(incoming);

        if (eventMap.isEmpty() || eventMap.get("event") == null) {
            logger.debug("event map has bytes. parse special : experimental ");
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
        HashMap<String, String> result = new HashMap<>();
        List<String> pairs = Arrays.asList(msg.split(MSG_DELIM, NUM_PARTS));

        if (pairs.size() != NUM_PARTS) {
            System.err.println("recv() did not return exactly " + NUM_PARTS + " key/value pairs");
            return result;
        }

        for (String pair : pairs) {
            List<String> kv = Arrays.asList(pair.split(KV_DELIM, 2));
            if (kv.size() == 1) {
                // value is null
                result.put(kv.get(0), null);
            } else if (kv.size() > 1) {
                result.put(kv.get(0), kv.get(1));
            }
        }

        if (result.get("event") == null) {
            return new HashMap<>();
        }

        return result;
    }


    private void logKnownDevices() {
        for (Map.Entry<String, List<String>> entry : peers.entrySet()) {
            if (logger.isDebugEnabled()) logger.debug("devices in {} : {} ", entry.getKey(),
                    entry.getValue());
        }
    }

    private void handleEnter(String zyreDeviceId) {
        logger.debug("peer (" + zyreDeviceId + ") entered network");
    }

    private void handleWhisper(String zyreDeviceId, String payload) {

        //logger.debug("peer (" + zyreDeviceId + ") Whisper to  " + zyreDeviceId + ": " + payload);
        //logger.debug("data size > " + payload.length());

        commsProcessor.processWireMessage(zyreDeviceId, payload);
    }

    private void handleShout(String zyreDeviceId, String group, String payload) {
        //logger.debug("peer (" + zyreDeviceId + ") shouted to group " + group + ": " + payload);
        //logger.debug("data size > " + payload.length());

        commsProcessor.processWireMessage(zyreDeviceId, payload);

    }


    private void handleJoin(String zyreDeviceId, String group) {
        addPeer(group, zyreDeviceId);
        logger.debug("peer (" + zyreDeviceId + ") joined: " + group);
    }

    private void handleLeave(String zyreDeviceId, String group) {
        boolean success = removePeer(group, zyreDeviceId);
        logger.debug("peer (" + zyreDeviceId + ") left " + group + ":" + success);
    }

    private void handleExit(String zyreDeviceId) {
        boolean success = removePeer(zyreDeviceId);
        logger.debug("peer (" + zyreDeviceId + ") exited: " + success);
    }

    private boolean addPeer(String group, String zyreDeviceId) {
        List<String> deviceList = peers.get(group);
        if (deviceList == null) {
            deviceList = new ArrayList<>();
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
                logger.debug("remove failed: " + zyreDeviceId);
                allRemovesSucceeded = false;
            }
        }

        return allRemovesSucceeded;
    }

    private boolean removePeer(String group, String zyreDeviceId) {
        List<String> deviceList = peers.get(group);

        return deviceList != null && deviceList.remove(zyreDeviceId);
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


}


package com.bezirk.comms;

import android.util.Log;

import com.bezirk.processor.CommsProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ZyreCommsHelper {
    public static final Logger logger = LoggerFactory.getLogger(ZyreCommsHelper.class);
    public static final String TAG = ZyreCommsHelper.class.getSimpleName();

    private final ConcurrentMap<String, List<String>> peers;
    private final CommsProcessor commsProcessor;

    public ZyreCommsHelper(ConcurrentMap<String, List<String>> peers, CommsProcessor commsProcessor) {

        this.peers = peers;
        this.commsProcessor = commsProcessor;
    }

    void processEvent(String eventType, String peer, String peerGroup, String payload) {
        // A Zyre-enabled device enters the network
        if (eventType.equals("ENTER")) {
            logger.info("peer (" + peer + ") entered network");

        } else if (eventType.equals("WHISPER")) {
            logger.info("data size > " + payload.length());
            commsProcessor.processWireMessage(peer, payload);

        } else if (eventType.equals("SHOUT")) {
            logger.info("data size > " + payload.length());
            commsProcessor.processWireMessage(peer, payload);

        } else if (eventType.equals("JOIN")) {
            addPeer(peerGroup, peer);
            logger.info("peer (" + peer + ") joined: " + peerGroup);
            logKnownDevices();

        } else if (eventType.equals("LEAVE")) {
            boolean success = removePeer(peerGroup, peer);
            logger.info("peer (" + peer + ") left " + peerGroup + ":" + success);
            logKnownDevices();

        } else if (eventType.equals("EXIT")) {
            boolean isRemovalSuccess = removePeer(peer);
            logger.debug("peer (" + peer + ") exited: " + isRemovalSuccess);
            logKnownDevices();

        } else {
            Log.d(TAG, "unknown event: " + eventType);
        }
    }

    /**
     * handling the revieve
     */
    Map<String, String> receive(Zyre zyre) {
        String incoming = zyre.recv();

        ConcurrentMap<String, String> eventMap = new ConcurrentHashMap<String, String>();

        if ((incoming == null) || incoming.isEmpty())
            return eventMap;

        if (Thread.interrupted()) {
            logger.warn("Interrupted during recv()");
            logger.info("RecvThread exiting");
            return eventMap;
        }
        // Convert the incoming string into a Map
        eventMap = parseMsg(incoming);

        if (eventMap.isEmpty() || eventMap.get("event") == null) {
            logger.info("event map has bytes. parse special : experimental ");
            //  return parseMsgExt(incoming);// to be fixed
            return eventMap;
        }

        return eventMap;
    }

    ConcurrentMap<String, String> parseMsg(String msg) {
        /*
			 * The message is created in the JNI C code with the statement:
			 * snprintf(ret, len, "event::%s|peer::%s|group::%s|message::%s", event, peer, group, message)
			 */

        String MSG_DELIM = "\\|";  // separates each part of the message
        int NUM_PARTS = 4;
        ConcurrentMap<String, String> result = new ConcurrentHashMap<String, String>();
        List<String> pairs = Arrays.asList(msg.split(MSG_DELIM, NUM_PARTS));

        if (pairs.size() != NUM_PARTS) {
            Log.d(TAG, "recv() did not return exactly " + NUM_PARTS + " key/value pairs");
            return result;
        }

        for (String pair : pairs) {
            List<String> keyValueList = Arrays.asList(pair.split("::", 2));
            if (keyValueList.isEmpty()) {
                // key and value are empty - do nothing
            } else {
                int int1 = 1;
                if (keyValueList.size() == int1) {
                    // value is null
                    result.put(keyValueList.get(0), null);
                } else {
                    result.put(keyValueList.get(0), keyValueList.get(int1));
                }
            }
        }

        if (result.get("event") == null) {
            return new ConcurrentHashMap<String, String>();
        }

        return result;
    }

    void logKnownDevices() {
        for (Map.Entry<String, List<String>> entries : peers.entrySet()) {
            logger.debug("devices in " + entries.getKey() + " : " + entries.getValue());
        }
    }

    boolean addPeer(String group, String zyreDeviceId) {
        List<String> deviceList = peers.get(group);
        if (deviceList == null) {
            deviceList = new ArrayList<String>();
            peers.put(group, deviceList);
        }
        return deviceList.add(zyreDeviceId);
    }

    boolean removePeer(String zyreDeviceId) {
        if (peers == null || peers.isEmpty()) {
            return false;
        }

        boolean allRemovesSucceeded = true;

        for (String peerGroup : peers.keySet()) {
            if (!removePeer(peerGroup, zyreDeviceId)) {
                logger.debug("remove failed: " + zyreDeviceId);
                allRemovesSucceeded = false;
            }
        }

        return allRemovesSucceeded;
    }

    boolean removePeer(String group, String zyreDeviceId) {
        peers.get(group);
        List<String> deviceList = peers.get(group);

        if (deviceList == null) {
            return false;
        }

        return deviceList.remove(zyreDeviceId);
    }

}

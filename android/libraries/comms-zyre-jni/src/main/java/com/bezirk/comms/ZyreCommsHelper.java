package com.bezirk.comms;

import android.util.Log;

import com.bosch.upa.uhu.processor.CommsProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AJC6KOR on 12/21/2015.
 */
class ZyreCommsHelper {

    public static final Logger log = LoggerFactory.getLogger(ZyreCommsHelper.class);
    public static final String TAG = ZyreCommsHelper.class.getSimpleName();

    private final ConcurrentMap<String, List<String>> peers;
    private final CommsProcessor commsProcessor;

    public ZyreCommsHelper(ConcurrentMap<String, List<String>> peers, CommsProcessor commsProcessor) {

        this.peers = peers;
        this.commsProcessor = commsProcessor;
    }

    void processEvent(String eventType, String peer, String peerGroup, String payload) {
        // A Zyre-enabled device enters the network
        switch (eventType) {
            case "ENTER":
                log.info("peer (" + peer + ") entered network");
                break;
            case "WHISPER":// Message sent to a particular device
                log.info("data size > " + payload.length());
                commsProcessor.processWireMessage(peer, payload);
                break;
            case "SHOUT":// Message sent to all members of a group
                log.info("data size > " + payload.length());
                commsProcessor.processWireMessage(peer, payload);
                break;
            case "JOIN":// A device joins a group
                addPeer(peerGroup, peer);
                log.info("peer (" + peer + ") joined: " + peerGroup);
                logKnownDevices();
                break;
            case "LEAVE":// A device explicitly leaves a group
                boolean success = removePeer(peerGroup, peer);
                log.info("peer (" + peer + ") left " + peerGroup + ":" + success);
                logKnownDevices();
                break;
            case "EXIT":// A device exits the network
                boolean isRemovalSuccess = removePeer(peer);
                log.debug("peer (" + peer + ") exited: " + isRemovalSuccess);
                logKnownDevices();
                break;
            default:
                Log.d(TAG, "unknown event: " + eventType);
        }
    }

    /** handling the revieve */
    Map<String,String> receive(Zyre zyre) {
        String incoming = zyre.recv();

        ConcurrentMap<String,String> eventMap = new ConcurrentHashMap<>();

        if((incoming == null) || incoming.isEmpty())
            return eventMap;

        if (Thread.interrupted()) {
            log.warn("Interrupted during recv()");
            log.info("RecvThread exiting");
            return eventMap;
        }
        // Convert the incoming string into a Map
        eventMap = parseMsg(incoming);

        if (eventMap.isEmpty() || eventMap.get("event") == null) {
            log.info("event map has bytes. parse special : experimental ");
            //  return parseMsgExt(incoming);// to be fixed
            return eventMap;
        }

        return eventMap;
    }

    ConcurrentMap<String,String> parseMsg(String msg) {
		/*
			 * The message is created in the JNI C code with the statement:
			 * snprintf(ret, len, "event::%s|peer::%s|group::%s|message::%s", event, peer, group, message)
			 */

        String MSG_DELIM = "\\|";  // separates each part of the message
        int NUM_PARTS = 4;
        ConcurrentMap<String,String> result = new ConcurrentHashMap<>();
        List<String> pairs = Arrays.asList(msg.split(MSG_DELIM, NUM_PARTS));

        if (pairs.size() != NUM_PARTS) {
            Log.d(TAG,"recv() did not return exactly " + NUM_PARTS + " key/value pairs");
            return result;
        }

        for (String pair : pairs) {
            List<String> keyValueList = Arrays.asList( pair.split("::", 2) );
            if (keyValueList.isEmpty()) {
                // key and value are empty - do nothing
            }
            else {
                int int1 = 1;
                if (keyValueList.size() == int1) {
                    // value is null
                    result.put(keyValueList.get(0), null);
                }
                else {
                    result.put(keyValueList.get(0), keyValueList.get(int1));
                }
            }
        }

        if (result.get("event") == null) {
            return new ConcurrentHashMap<>();
        }

        return result;
    }

    void logKnownDevices() {
        for(String deviceGroup : peers.keySet()) {
            log.debug("devices in " + deviceGroup + " : " + peers.get(deviceGroup));
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

        for (String peerGroup: peers.keySet()) {
            if ( !removePeer(peerGroup, zyreDeviceId) ) {
                log.debug("remove failed: " + zyreDeviceId);
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

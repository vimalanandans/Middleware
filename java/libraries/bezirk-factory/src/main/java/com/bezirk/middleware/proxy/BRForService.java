package com.bezirk.middleware.proxy;

import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.ServiceIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class BRForService implements BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(BRForService.class);

    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;
    private static final LinkedHashMap<String, Long> duplicateMsgMap = new LinkedHashMap<String, Long>();
    private static final LinkedHashMap<String, Long> duplicateStreamMap = new LinkedHashMap<String, Long>();
    private final HashMap<String, String> activeStreams;
    private final HashMap<String, HashSet<BezirkListener>> eventListenerMap;
    private final HashMap<ZirkId, HashSet<BezirkListener>> sidMap;
    private final HashMap<String, HashSet<BezirkListener>> streamListenerMap;

    public BRForService(HashMap<String, String> activeStreams,
                        HashMap<String, HashSet<BezirkListener>> eventListenerMap,
                        HashMap<ZirkId, HashSet<BezirkListener>> sidMap,
                        HashMap<String, HashSet<BezirkListener>> streamListenerMap) {
        super();
        this.activeStreams = activeStreams;
        this.eventListenerMap = eventListenerMap;
        this.sidMap = sidMap;
        this.streamListenerMap = streamListenerMap;
    }

    @Override
    public void onReceive(ServiceIncomingMessage incomingMessage) {
        if (sidMap.containsKey(incomingMessage.getRecipient())) {
            switch (incomingMessage.getCallbackType()) {
                case "EVENT":
                    if (!(incomingMessage instanceof EventIncomingMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of EventIncomingMessage");
                    }

                    EventIncomingMessage eventCallbackMessage = (EventIncomingMessage) incomingMessage;
                    handleEventCallback(eventCallbackMessage);
                    break;
                case "STREAM_UNICAST":
                    if (!(incomingMessage instanceof StreamIncomingMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of StreamIncomingMessage");
                    }

                    StreamIncomingMessage strmMsg = (StreamIncomingMessage) incomingMessage;
                    handlerStreamUnicastCallback(strmMsg);
                    break;
                case "STREAM_STATUS":
                    if (!(incomingMessage instanceof StreamStatusMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of StreamStatusMessage");
                    }

                    StreamStatusMessage streamStatusCallbackMessage = (StreamStatusMessage) incomingMessage;
                    handleStreamStatusCallback(streamStatusCallbackMessage);
                    break;
                default:
                    logger.error("Unknown incoming message type : " + incomingMessage.getCallbackType());
            }
        }

    }

    /**
     * Handles the Event Callback Message and gives the callback to the services. It is being invoked from
     * Platform specific BezirkCallback implementation.
     *
     * @param eCallbackMessage
     */
    private void handleEventCallback(EventIncomingMessage eCallbackMessage) {
        logger.debug("About to callback sid:" + eCallbackMessage.getRecipient().getZirkId() + " for id:" + eCallbackMessage.msgId);
        //Make a combined sid for sender and recipient
        String combinedSid = eCallbackMessage.senderEndPoint.zirkId.getZirkId() + ":" + eCallbackMessage.getRecipient().getZirkId();
        if (checkDuplicateMsg(combinedSid, eCallbackMessage.msgId)) {
            HashSet<BezirkListener> tempListenersSidMap = sidMap.get(eCallbackMessage.getRecipient());
            HashSet<BezirkListener> tempListenersTopicsMap = eventListenerMap.get(eCallbackMessage.eventTopic);
            if (null != tempListenersSidMap && null != tempListenersTopicsMap) {
                for (BezirkListener invokingListener : tempListenersSidMap) {
                    if (tempListenersTopicsMap.contains(invokingListener)) {
                        Event event = Message.fromJson(eCallbackMessage.serializedEvent, Event.class);
                        invokingListener.receiveEvent(eCallbackMessage.eventTopic,
                                event, eCallbackMessage.senderEndPoint);
                    }
                }
            }
        } else {
            logger.info("Duplicate Event Received");
        }
    }

    /**
     * Handle the stream Unicast callback.This is called from platform specific BezirkCallback Implementation.
     *
     * @param strmMsg streamMessage that will be given back to the services.
     */
    private void handlerStreamUnicastCallback(StreamIncomingMessage strmMsg) {
        if (checkDuplicateStream(strmMsg.sender.zirkId.getZirkId(), strmMsg.localStreamId)) {
            if (streamListenerMap.containsKey(strmMsg.streamTopic)) {
                for (BezirkListener listener : streamListenerMap.get(strmMsg.streamTopic)) {
                    StreamDescriptor streamDescriptor = Message.fromJson(strmMsg.serializedStream, StreamDescriptor.class);
                    listener.receiveStream(strmMsg.streamTopic, streamDescriptor, strmMsg.localStreamId,
                            strmMsg.file, strmMsg.sender);
                }
            } else {
                logger.error("StreamListenerMap does not have a mapped StreamDescriptor");
            }
        } else {
            logger.error("Duplicate StreamDescriptor Request Received");
        }
    }

    /**
     * Handles the StreamDescriptor Status callback and gives the callback to the zirk. This is called from
     * platform specific BezirkCallback Implementation.
     *
     * @param streamStatusCallbackMessage StreamStatusCallback that will be invoked for the services.
     */
    private void handleStreamStatusCallback(StreamStatusMessage streamStatusCallbackMessage) {
        String activeStreamKey = streamStatusCallbackMessage.getRecipient().getZirkId() + streamStatusCallbackMessage.streamId;
        if (activeStreams.containsKey(activeStreamKey)) {
            HashSet<BezirkListener> tempHashSet = streamListenerMap.get(activeStreams.get(activeStreamKey));
            if (tempHashSet != null && !tempHashSet.isEmpty()) {
                for (BezirkListener listener : tempHashSet) {
                    listener.streamStatus(
                            streamStatusCallbackMessage.streamId,
                            ((1 == streamStatusCallbackMessage.streamStatus) ? BezirkListener.StreamStates.END_OF_DATA
                                    : BezirkListener.StreamStates.LOST_CONNECTION));
                }
            }
            activeStreams.remove(activeStreamKey);
        }
    }

    /**
     * This method is used to check if the stream is a duplicate
     */
    private boolean checkDuplicateStream(final String sid, final int streamId) {
        final String key = sid + ":" + streamId;
        final Long currentTime = new Date().getTime();
        if (duplicateStreamMap.containsKey(key)) {
            if (currentTime - duplicateStreamMap.get(key) > TIME_DURATION) {
                duplicateStreamMap.remove(key);
                duplicateStreamMap.put(key, currentTime);
                return true;
            } else {

                return false;
            }
        } else {
            if (duplicateStreamMap.size() < MAX_MAP_SIZE) {
                duplicateStreamMap.put(key, currentTime);
                return true;
            } else {
                duplicateStreamMap.remove(duplicateStreamMap.keySet().iterator().next());
                duplicateStreamMap.put(key, currentTime);
                return true;
            }
        }
    }

    /**
     * This method is used to check if the Event is a duplicate
     */
    private synchronized boolean checkDuplicateMsg(final String sid, final String messageId) {
        final String key = sid + ":" + messageId;
        final Long currentTime = new Date().getTime();
        if (duplicateMsgMap.containsKey(key)) {
            if (currentTime - duplicateMsgMap.get(key) > TIME_DURATION) {
                duplicateMsgMap.remove(key);
                duplicateMsgMap.put(key, currentTime);
                return true;
            } else {
                return false;
            }
        } else {
            if (duplicateMsgMap.size() < MAX_MAP_SIZE) {
                duplicateMsgMap.put(key, currentTime);
                return true;
            } else {
                duplicateMsgMap.remove(duplicateMsgMap.keySet().iterator().next());
                duplicateMsgMap.put(key, currentTime);
                return true;
            }
        }
    }
}

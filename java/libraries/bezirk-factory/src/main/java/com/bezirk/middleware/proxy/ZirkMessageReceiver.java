package com.bezirk.middleware.proxy;

import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.EventIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ZirkMessageReceiver implements BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageReceiver.class);

    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;
    private static final Map<String, Long> duplicateMsgMap = new LinkedHashMap<>();
    private static final Map<String, Long> duplicateStreamMap = new LinkedHashMap<>();

    private final Map<String, String> activeStreams;
    private final Map<String, Set<BezirkListener>> eventListenerMap;
    private final Map<ZirkId, Set<BezirkListener>> sidMap;
    private final Map<String, Set<BezirkListener>> streamListenerMap;

    public ZirkMessageReceiver(Map<String, String> activeStreams,
                               Map<String, Set<BezirkListener>> eventListenerMap,
                               Map<ZirkId, Set<BezirkListener>> sidMap,
                               Map<String, Set<BezirkListener>> streamListenerMap) {
        super();
        this.activeStreams = activeStreams;
        this.eventListenerMap = eventListenerMap;
        this.sidMap = sidMap;
        this.streamListenerMap = streamListenerMap;
    }

    @Override
    public void onReceive(ZirkAction incomingMessage) {
        if (sidMap.containsKey(incomingMessage.getZirkId())) {
            switch (incomingMessage.getAction()) {
                case ACTION_ZIRK_RECEIVE_EVENT:
                    if (!(incomingMessage instanceof EventIncomingMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of EventIncomingMessage");
                    }

                    EventIncomingMessage eventCallbackMessage = (EventIncomingMessage) incomingMessage;
                    handleEventCallback(eventCallbackMessage);
                    break;
                case ACTION_ZIRK_RECEIVE_STREAM:
                    if (!(incomingMessage instanceof StreamIncomingMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of StreamIncomingMessage");
                    }

                    StreamIncomingMessage strmMsg = (StreamIncomingMessage) incomingMessage;
                    handlerStreamUnicastCallback(strmMsg);
                    break;
                case ACTION_ZIRK_RECEIVE_STREAM_STATUS:
                    if (!(incomingMessage instanceof StreamStatusMessage)) {
                        throw new AssertionError("incomingMessage is not an instance of StreamStatusMessage");
                    }

                    StreamStatusMessage streamStatusCallbackMessage = (StreamStatusMessage) incomingMessage;
                    handleStreamStatusCallback(streamStatusCallbackMessage);
                    break;
                default:
                    logger.error("Unimplemented action: {}" + incomingMessage.getAction());
            }
        }
    }

    /**
     * Handles the Event Callback Message and gives the callback to the services. It is being invoked from
     * Platform specific BezirkCallback implementation.
     *
     * @param incomingEvent new event to send up to Zirks registered to receive it
     */
    private void handleEventCallback(EventIncomingMessage incomingEvent) {
        logger.debug("About to callback sid:" + incomingEvent.getZirkId().getZirkId() + " for id:" + incomingEvent.getMsgId());
        //Make a combined sid for sender and recipient
        String combinedSid = incomingEvent.getSenderEndPoint().zirkId.getZirkId() + ":" + incomingEvent.getZirkId().getZirkId();
        if (checkDuplicateMsg(combinedSid, incomingEvent.getMsgId())) {
            Set<BezirkListener> tempListenersSidMap = sidMap.get(incomingEvent.getZirkId());
            Set<BezirkListener> tempListenersTopicsMap = eventListenerMap.get(incomingEvent.getEventTopic());
            if (tempListenersSidMap != null && tempListenersTopicsMap != null) {
                for (BezirkListener invokingListener : tempListenersSidMap) {
                    if (tempListenersTopicsMap.contains(invokingListener)) {
                        Event event = Message.fromJson(incomingEvent.getSerializedEvent(), Event.class);
                        invokingListener.receiveEvent(incomingEvent.getEventTopic(),
                                event, incomingEvent.getSenderEndPoint());
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
        if (checkDuplicateStream(strmMsg.getSender().zirkId.getZirkId(), strmMsg.getLocalStreamId())) {
            if (streamListenerMap.containsKey(strmMsg.getStreamTopic())) {
                for (BezirkListener listener : streamListenerMap.get(strmMsg.getStreamTopic())) {
                    StreamDescriptor streamDescriptor = Message.fromJson(strmMsg.getSerializedStream(), StreamDescriptor.class);
                    listener.receiveStream(strmMsg.getStreamTopic(), streamDescriptor, strmMsg.getLocalStreamId(),
                            strmMsg.getFile(), strmMsg.getSender());
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
        String activeStreamKey = streamStatusCallbackMessage.getZirkId().getZirkId() + streamStatusCallbackMessage.getStreamId();
        if (activeStreams.containsKey(activeStreamKey)) {
            Set<BezirkListener> tempHashSet = streamListenerMap.get(activeStreams.get(activeStreamKey));
            if (tempHashSet != null && !tempHashSet.isEmpty()) {
                for (BezirkListener listener : tempHashSet) {
                    listener.streamStatus(
                            streamStatusCallbackMessage.getStreamId(),
                            ((1 == streamStatusCallbackMessage.getStreamStatus()) ? BezirkListener.StreamStates.END_OF_DATA
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

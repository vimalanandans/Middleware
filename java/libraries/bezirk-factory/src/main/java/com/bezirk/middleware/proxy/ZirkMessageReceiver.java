package com.bezirk.middleware.proxy;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;

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

    private final Map<ZirkId, Set<EventSet.EventReceiver>> eventMap;
    private final Map<String, Set<EventSet.EventReceiver>> eventListenerMap;
    private final Map<ZirkId, Set<StreamSet.StreamReceiver>> streamMap;
    private final Map<String, Set<StreamSet.StreamReceiver>> streamListenerMap;

    public ZirkMessageReceiver(Map<ZirkId, Set<EventSet.EventReceiver>> eventMap,
                               Map<String, Set<EventSet.EventReceiver>> eventListenerMap,
                               Map<ZirkId, Set<StreamSet.StreamReceiver>> streamMap,
                               Map<String, Set<StreamSet.StreamReceiver>> streamListenerMap) {
        super();
        this.eventMap = eventMap;
        this.eventListenerMap = eventListenerMap;
        this.streamMap = streamMap;
        this.streamListenerMap = streamListenerMap;
    }

    @Override
    public void onReceive(ZirkAction incomingMessage) {
        if (eventMap.containsKey(incomingMessage.getZirkId()) ||
                streamMap.containsKey(incomingMessage.getZirkId())) {
            switch (incomingMessage.getAction()) {
                case ACTION_ZIRK_RECEIVE_EVENT:
                    if (!(incomingMessage instanceof UnicastEventAction)) {
                        throw new AssertionError("incomingMessage is not an instance of UnicastEventAction");
                    }

                    UnicastEventAction eventCallbackMessage = (UnicastEventAction) incomingMessage;
                    handleEventCallback(eventCallbackMessage);
                    break;
                case ACTION_ZIRK_RECEIVE_STREAM:
                    if (!(incomingMessage instanceof ReceiveFileStreamAction)) {
                        throw new AssertionError("incomingMessage is not an instance of ReceiveFileStreamAction");
                    }

                    ReceiveFileStreamAction strmMsg = (ReceiveFileStreamAction) incomingMessage;
                    handlerStreamUnicastCallback(strmMsg);
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
    private void handleEventCallback(UnicastEventAction incomingEvent) {
        Event event = Event.fromJson(incomingEvent.getSerializedEvent(), Event.class);
        String eventName = event.getClass().getName();

        BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();

        logger.debug("About to callback zid:" + incomingEvent.getZirkId().getZirkId() + " for id:" + incomingEvent.getMessageId());

        //Make a combined zid for sender and recipient
        String combinedSid = endpoint.zirkId.getZirkId() + ":" + incomingEvent.getZirkId().getZirkId();
        if (checkDuplicateMsg(combinedSid, incomingEvent.getMessageId())) {
            Set<EventSet.EventReceiver> tempListenersEventMap = eventMap.get(incomingEvent.getZirkId());
            Set<EventSet.EventReceiver> tempListenersMessageMap = eventListenerMap.get(eventName);
            if (tempListenersEventMap != null && tempListenersMessageMap != null) {
                for (EventSet.EventReceiver invokingListener : tempListenersEventMap) {
                    if (tempListenersMessageMap.contains(invokingListener)) {
                        invokingListener.receiveEvent(event, endpoint);
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
     * @param incomingStream streamMessage that will be given back to the services.
     */
    private void handlerStreamUnicastCallback(ReceiveFileStreamAction incomingStream) {
        StreamDescriptor streamDescriptor = StreamDescriptor.fromJson(incomingStream.getSerializedStream(),
                StreamDescriptor.class);
        String streamName = streamDescriptor.getClass().getName();

        if (streamListenerMap.containsKey(streamName)) {
            for (StreamSet.StreamReceiver listener : streamListenerMap.get(streamName)) {
                listener.receiveStream(streamDescriptor, incomingStream.getFile(), incomingStream.getSender());
            }
        } else {
            logger.error("StreamListenerMap does not have a mapped StreamDescriptor");
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

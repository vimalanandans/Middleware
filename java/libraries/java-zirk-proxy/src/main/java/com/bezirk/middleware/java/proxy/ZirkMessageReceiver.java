package com.bezirk.middleware.java.proxy;

import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.java.proxy.messagehandler.BroadcastReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ZirkMessageReceiver implements BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageReceiver.class);

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
        if (!eventMap.containsKey(incomingMessage.getZirkId()) &&
                !streamMap.containsKey(incomingMessage.getZirkId())) return;

        switch (incomingMessage.getAction()) {
            case ACTION_ZIRK_RECEIVE_EVENT:
                processEvent((UnicastEventAction) incomingMessage);
                break;
            case ACTION_ZIRK_RECEIVE_STREAM:
                processStream((ReceiveFileStreamAction) incomingMessage);
                break;
            default:
                logger.error("Unimplemented action: {}", incomingMessage.getAction());
        }
    }

    /**
     * Handles the Event Callback Message and gives the callback to the services. It is being invoked from
     * Platform specific BezirkCallback implementation.
     *
     * @param incomingEvent new event to send up to Zirks registered to receive it
     */
    private void processEvent(UnicastEventAction incomingEvent) {
        final Event event = (Event) Event.fromJson(incomingEvent.getSerializedEvent());
        final String eventName = event.getClass().getName();

        if (incomingEvent.isIdentified()) {
            ((IdentifiedEvent) event).setAlias(incomingEvent.getAlias());
            ((IdentifiedEvent) event).setMiddlewareUser(incomingEvent.isMiddlewareUser());
        }

        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();

        final Set<EventSet.EventReceiver> tempEventSet = eventMap.get(incomingEvent.getZirkId());
        final Set<EventSet.EventReceiver> tempMessageSet = eventListenerMap.get(eventName);

        if (tempEventSet != null && tempMessageSet != null) {
            for (EventSet.EventReceiver invokingListener : tempEventSet) {
                if (tempMessageSet.contains(invokingListener)) {
                    invokingListener.receiveEvent(event, endpoint);
                }
            }
        }

    }

    /**
     * Handle the stream Unicast callback.This is called from platform specific BezirkCallback Implementation.
     *
     * @param incomingStream streamMessage that will be given back to the services.
     */
    private void processStream(ReceiveFileStreamAction incomingStream) {
        final StreamDescriptor streamDescriptor = (StreamDescriptor) StreamDescriptor.fromJson(incomingStream.getSerializedStream());
        final String streamName = streamDescriptor.getClass().getName();

        if (streamListenerMap.containsKey(streamName)) {
            for (StreamSet.StreamReceiver listener : streamListenerMap.get(streamName)) {
                listener.receiveStream(streamDescriptor, incomingStream.getFile(), incomingStream.getSender());
            }
        }
    }
}

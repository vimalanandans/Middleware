package com.bezirk.middleware.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZirkMessageReceiver extends BroadcastReceiver {
    private static final int TIME_DURATION = 15000;
    private static final int MAX_MAP_SIZE = 50;
    private static final Map<String, Long> duplicateMsgMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> duplicateStreamMap = new ConcurrentHashMap<>();
    private final String TAG = ZirkMessageReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ZirkAction message;
        try {
            message = (ZirkAction) intent.getSerializableExtra("message");
        } catch (Exception e) { //to prevent app crash due to  java.io.InvalidClassException
            Log.w(TAG, e.getMessage());
            return;
        }

        if (isValidRequest(message.getZirkId())) {
            switch (message.getAction()) {
                case ACTION_ZIRK_RECEIVE_EVENT:
                    processEvent((UnicastEventAction) message);
                    break;
                case ACTION_ZIRK_RECEIVE_STREAM:
                    processStream((ReceiveFileStreamAction) message);
                    break;
                default:
                    Log.e(TAG, "Unimplemented action: " + message.getAction());
            }
        }
    }

    private boolean isValidRequest(ZirkId receivedZirkId) {
        if (ProxyClient.context == null) {
            Log.e(TAG, "Application is not started");
            return false;
        }

        if (!isRequestForCurrentApp(receivedZirkId.getZirkId())) {
            Log.e(TAG, "Intent is not for this Service");
            return false;
        }

        return true;
    }

    private void processEvent(UnicastEventAction incomingEvent) {
        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();
        final String messageId = incomingEvent.getMessageId();

        if (checkDuplicateMsg(endpoint.zirkId.getZirkId(), messageId)) {
            final Event event = (Event) Message.fromJson(incomingEvent.getSerializedEvent());
            final String eventName = event.getClass().getName();

            if (incomingEvent.isIdentified()) {
                ((IdentifiedEvent) event).setAlias(incomingEvent.getAlias());
                ((IdentifiedEvent) event).setMiddlewareUser(incomingEvent.isMiddlewareUser());
            }

            if (ProxyClient.eventSetMap.containsKey(eventName)) {
                final List<EventSet> eventSets = ProxyClient.eventSetMap.get(eventName);
                for (EventSet eventSet : eventSets) {
                    eventSet.getEventReceiver().receiveEvent(event, endpoint);
                }
            }
        } else {
            Log.e(TAG, "Duplicate Message, Dropping message");
        }
    }

    private void processStream(ReceiveFileStreamAction streamMessage) {
        final StreamDescriptor streamDescriptor =
                (StreamDescriptor) Message.fromJson(streamMessage.getSerializedStream());
        //final String streamName = streamDescriptor.getClass().getName();
        final String streamName = streamDescriptor.getStreamActionName();

        if (ProxyClient.streamListenerMap.containsKey(streamName)) {
            final List<StreamSet.StreamReceiver> messageListeners = ProxyClient.streamListenerMap.get(streamName);
            for (StreamSet.StreamReceiver listener : messageListeners) {
                listener.receiveStream(streamDescriptor, streamMessage.getFile(),
                        streamMessage.getSender());
            }
        }
    }

    private boolean checkDuplicateMsg(final String sid, final String messageId) {
        final String key = sid + ":" + messageId;
        final Long currentTime = new Date().getTime();
        if (duplicateMsgMap.containsKey(key)) {
            if (currentTime - duplicateMsgMap.get(key) > TIME_DURATION) {
                duplicateMsgMap.remove(key);
                duplicateMsgMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            duplicateMsgMap.put(key, currentTime);

            if (duplicateMsgMap.size() < MAX_MAP_SIZE) {
                return true;
            } else {
                duplicateMsgMap.remove(duplicateMsgMap.keySet().iterator().next());
                return true;
            }
        }
    }
    
    private boolean isRequestForCurrentApp(final String zirkId) {
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(ProxyClient.context);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equalsIgnoreCase(zirkId)) {
                return true;
            }
        }

        return false;
    }
}

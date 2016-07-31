package com.bezirk.middleware.proxy.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

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
        ZirkAction message = (ZirkAction) intent.getSerializableExtra("message");

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

    private void processEvent(UnicastEventAction eventMessage) {
        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) eventMessage.getEndpoint();
        final String messageId = eventMessage.getMessageId();

        if (checkDuplicateMsg(endpoint.zirkId.getZirkId(), messageId)) {
            final Event event = Message.fromJson(eventMessage.getSerializedEvent(), Event.class);
            final String eventName = event.getClass().getName();

            if (ProxyClient.eventListenerMap.containsKey(eventName)) {
                final List<EventSet.EventReceiver> messageListeners = ProxyClient.eventListenerMap.get(eventName);
                for (EventSet.EventReceiver listener : messageListeners) {
                    listener.receiveEvent(event, endpoint);
                }
            }
        } else {
            Log.e(TAG, "Duplicate Message, Dropping message");
        }
    }

    private void processStream(ReceiveFileStreamAction streamMessage) {
        final StreamDescriptor streamDescriptor =
                Message.fromJson(streamMessage.getSerializedStream(), StreamDescriptor.class);
        final String streamName = streamDescriptor.getClass().getName();

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

    private boolean checkDuplicateStream(final String sid, final int streamId) {
        final String key = sid + ":" + streamId;
        final Long currentTime = new Date().getTime();
        if (duplicateStreamMap.containsKey(key)) {
            if (currentTime - duplicateStreamMap.get(key) > TIME_DURATION) {
                duplicateStreamMap.remove(key);
                duplicateStreamMap.put(key, currentTime);
                return true;
            } else
                return false;
        } else {
            duplicateStreamMap.put(key, currentTime);

            if (duplicateStreamMap.size() < MAX_MAP_SIZE) {
                return true;
            } else {
                duplicateStreamMap.remove(duplicateStreamMap.keySet().iterator().next());
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

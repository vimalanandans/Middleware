package com.bezirk.middleware.proxy.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProxyClient implements Bezirk {
    protected static final Map<String, List<EventSet.EventReceiver>> eventListenerMap = new ConcurrentHashMap<>();
    protected static final Map<String, List<StreamSet.StreamReceiver>> streamListenerMap = new ConcurrentHashMap<>();
    protected static final Map<Short, String> activeStreams = new ConcurrentHashMap<>();

    private static final String TAG = ProxyClient.class.getSimpleName();
    private static final String COMPONENT_NAME = "com.bezirk.controlui";
    private static final String SERVICE_PKG_NAME = "com.bezirk.starter.MainService";
    private static final ComponentName RECEIVING_COMPONENT = new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME);

    protected static Context context;
    private final ZirkId zirkId;
    private short streamFactory;

    public ProxyClient(Context context, ZirkId zirkId) {
        ProxyClient.context = context;
        this.zirkId = zirkId;
    }

    private static boolean sendBezirkIntent(ZirkAction action) {
        return sendBezirkIntent(context, action);
    }

    private static boolean sendBezirkIntent(Context context, ZirkAction action) {
        final Intent intent = new Intent();
        intent.setComponent(RECEIVING_COMPONENT);
        final String actionName = action.getAction().getName();
        intent.setAction(actionName);
        intent.putExtra(actionName, action);

        if (context.startService(intent) == null) {
            Log.e(TAG, "Failed to send intent for action: " + actionName +
                    ". Is the middleware running?");

            return false;
        }

        return true;
    }

    public static ZirkId registerZirk(Context context, final String zirkName) {
        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
        }

        Log.d(TAG, "Registering Zirk: " + zirkName);

        final SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
        String zirkIdAsString = shrdPref.getString(zirkName, null);
        if (null == zirkIdAsString) {
            zirkIdAsString = UUID.randomUUID().toString();
            Log.d(TAG, "ZirkId-> " + zirkIdAsString);

            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString(zirkName, zirkIdAsString);
            editor.commit();
        }

        final ZirkId zirkId = new ZirkId(zirkIdAsString);

        if (sendBezirkIntent(context, new RegisterZirkAction(zirkId, zirkName))) {
            Log.d(TAG, "Registered Zirk: " + zirkName);
            return zirkId;
        }

        return null;
    }

    @Override
    public void unregisterZirk() {
        Log.d(TAG, "Unregister request for zirkID: " + zirkId.getZirkId());

        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equalsIgnoreCase(zirkId.getZirkId())) {
                Log.d(TAG, "Unregistering zirk: " + entry.getKey());
                SharedPreferences.Editor editor = shrdPref.edit();
                editor.remove(entry.getKey());
                editor.apply();
                unsubscribe(null);
                break;
            }
        }
    }

    @Override
    public void subscribe(final MessageSet messageSet) {
        if (messageSet instanceof EventSet) {
            EventSet.EventReceiver listener = ((EventSet) messageSet).getEventReceiver();
            addMessagesToMap(messageSet, eventListenerMap, listener);
        } else if (messageSet instanceof StreamSet) {
            StreamSet.StreamReceiver listener = ((StreamSet) messageSet).getStreamReceiver();
            addMessagesToMap(messageSet, streamListenerMap, listener);
        } else {
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        sendBezirkIntent(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_SUBSCRIBE, zirkId, messageSet));
    }

    private void addMessagesToMap(MessageSet messageSet, Map<String,
            List<EventSet.EventReceiver>> listenerMap, EventSet.EventReceiver listener) {
        for (String messageName : messageSet.getMessages()) {
            if (listenerMap.containsKey(messageName)) {
                List<EventSet.EventReceiver> zirkList = listenerMap.get(messageName);
                if (zirkList.contains(listener)) {
                    throw new IllegalArgumentException("The assigned listener is already in use for " + messageName);
                } else {
                    zirkList.add(listener);
                }
            } else {
                List<EventSet.EventReceiver> listenerList = new ArrayList<>();
                listenerList.add(listener);
                listenerMap.put(messageName, listenerList);
            }
        }
    }

    private void addMessagesToMap(MessageSet messageSet, Map<String,
            List<StreamSet.StreamReceiver>> listenerMap, StreamSet.StreamReceiver listener) {
        for (String messageName : messageSet.getMessages()) {
            if (listenerMap.containsKey(messageName)) {
                List<StreamSet.StreamReceiver> zirkList = listenerMap.get(messageName);
                if (!zirkList.contains(listener)) {
                    zirkList.add(listener);
                } else {
                    throw new IllegalArgumentException("The assigned listener is already in use for " + messageName);
                }
            } else {
                List<StreamSet.StreamReceiver> listenerList = new ArrayList<>();
                listenerList.add(listener);
                listenerMap.put(messageName, listenerList);
            }
        }
    }

    @Override
    public boolean unsubscribe(final MessageSet messageSet) {
        return sendBezirkIntent(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE, zirkId,
                messageSet));
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location("null/null/null")), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        sendBezirkIntent(new SendMulticastEventAction(zirkId, recipient, event));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        Log.d(TAG, "Zirk sending event: " + event.getClass().getName());

        sendBezirkIntent(new UnicastEventAction(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT,
                zirkId, recipient, event));
    }

    @Override
    public void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }

    @Override
    public void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);

        activeStreams.put(streamId, streamDescriptor.getClass().getName());

        sendBezirkIntent(new SendFileStreamAction(zirkId, recipient, streamDescriptor, file));
    }

    @Override
    public void setLocation(Location location) {
        sendBezirkIntent(new SetLocationAction(zirkId, location));
    }
}
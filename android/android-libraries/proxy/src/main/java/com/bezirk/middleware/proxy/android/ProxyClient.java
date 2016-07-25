package com.bezirk.middleware.proxy.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.BezirkActions;
import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SendUnicastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.actions.ZirkAction;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProxyClient implements Bezirk {
    protected static final Map<String, List<BezirkListener>> eventListenerMap = new ConcurrentHashMap<>();
    protected static final Map<String, List<BezirkListener>> streamListenerMap = new ConcurrentHashMap<>();
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

    private static boolean sendBezirkIntent(String actionName, ZirkAction action) {
        return sendBezirkIntent(context, actionName, action);
    }

    private static boolean sendBezirkIntent(Context context, String actionName, ZirkAction action) {
        final Intent intent = new Intent();
        intent.setComponent(RECEIVING_COMPONENT);
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

        if (sendBezirkIntent(context, BezirkActions.ACTION_BEZIRK_REGISTER.getName(),
                new RegisterZirkAction(zirkId, zirkName))) {
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
    public void subscribe(final ProtocolRole protocolRole, final BezirkListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Must subscribe with a non-null listener");
        }

        if (protocolRole.getEventTopics() != null)
            addTopicsToMap(protocolRole.getEventTopics(), eventListenerMap, listener, "Event");
        if (protocolRole.getStreamTopics() != null)
            addTopicsToMap(protocolRole.getStreamTopics(), streamListenerMap, listener, "StreamDescriptor");

        sendBezirkIntent(BezirkActions.ACTION_BEZIRK_SUBSCRIBE.getName(),
                new SubscriptionAction(zirkId, protocolRole));
    }

    private void addTopicsToMap(String[] topics, Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type) {
        for (String topic : topics) {
            if (topic == null || topic.isEmpty()) {
                continue;
            }

            if (listenerMap.containsKey(topic)) {
                addListener(listenerMap, listener, type, topic);
            } else {
                List<BezirkListener> regServiceList = new ArrayList<>();
                regServiceList.add(listener);
                listenerMap.put(topic, regServiceList);
            }
        }
    }

    private void addListener(Map<String, List<BezirkListener>> listenerMap, BezirkListener listener, String type, String topic) {
        List<BezirkListener> zirkList = listenerMap.get(topic);
        if (zirkList.contains(listener)) {
            Log.w(TAG, type + " already registered with the " + type + "Label " + topic);
        } else {
            zirkList.add(listener);
        }
    }

    @Override
    public boolean unsubscribe(final ProtocolRole protocolRole) {
        return sendBezirkIntent(BezirkActions.ACTION_BEZIRK_UNSUBSCRIBE.getName(),
                new SubscriptionAction(zirkId, protocolRole));
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location(null)), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        sendBezirkIntent(BezirkActions.ACTION_SERVICE_SEND_MULTICAST_EVENT.getName(),
                new SendMulticastEventAction(zirkId, recipient, event));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        Log.d(TAG, "Zirk sending event: " + event.topic);

        sendBezirkIntent(BezirkActions.ACTION_SERVICE_SEND_UNICAST_EVENT.getName(),
                new SendUnicastEventAction(zirkId, recipient, event));
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);

        activeStreams.put(streamId, streamDescriptor.topic);

        if (sendBezirkIntent(BezirkActions.ACTION_BEZIRK_PUSH_UNICAST_STREAM.getName(),
                new SendFileStreamAction(zirkId, recipient, streamDescriptor, streamId, file))) {
            return streamId;
        }

        return 0;
    }

    @Override
    public void setLocation(Location location) {
        sendBezirkIntent(BezirkActions.ACTION_BEZIRK_SET_LOCATION.getName(),
                new SetLocationAction(zirkId, location));
    }
}
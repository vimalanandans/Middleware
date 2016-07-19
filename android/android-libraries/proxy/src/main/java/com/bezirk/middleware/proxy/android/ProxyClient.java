package com.bezirk.middleware.proxy.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.BezirkActions;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.google.gson.Gson;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ProxyClient implements Bezirk {
    static final int TIME_DURATION = 15000;
    static final int MAX_MAP_SIZE = 50;
    static final ConcurrentMap<String, List<BezirkListener>> eventListenerMap = new ConcurrentHashMap<String, List<BezirkListener>>();
    static final ConcurrentMap<String, List<BezirkListener>> streamListenerMap = new ConcurrentHashMap<String, List<BezirkListener>>();
    static final ConcurrentMap<Short, String> activeStreams = new ConcurrentHashMap<Short, String>();
    static final ConcurrentMap<String, Long> duplicateMsgMap = new ConcurrentHashMap<String, Long>();
    static final ConcurrentMap<String, Long> duplicateStreamMap = new ConcurrentHashMap<String, Long>();
    //Pipe Listener Map -- pipeId : listener
    static final ConcurrentMap<String, BezirkListener> pipeListenerMap = new ConcurrentHashMap<String, BezirkListener>();
    public static final String ACTION_BEZIRK_REGISTER = "REGISTER";
    private static final String ACTION_SERVICE_SEND_MULTICAST_EVENT = "MULTICAST_EVENT";
    private static final String ACTION_SERVICE_SEND_UNICAST_EVENT = "UNICAST_EVENT";
    private static final String ACTION_BEZIRK_SUBSCRIBE = "SUBSCRIBE";
    private static final String ACTION_BEZIRK_UNSUBSCRIBE = "UNSUBSCRIBE";
    private static final String ACTION_BEZIRK_SETLOCATION = "LOCATION";
    private static final String ACTION_BEZIRK_PUSH_UNICAST_STREAM = "UNICAST_STREAM";
    private static final String ACTION_BEZIRK_PUSH_MULTICAST_STREAM = "MULTICAST_STREAM";
    private static final String COMPONENT_NAME = "com.bezirk.controlui";
    private static final String SERVICE_PKG_NAME = "com.bezirk.starter.MainService";
    private static final ProxyClientHelper PROXY_CLIENT_HELPER = new ProxyClientHelper();
    static Context context;
    static BezirkListener DiscoveryListener;
    static int discoveryCount; // keep track of Discovery Id
    private static final String TAG = ProxyClient.class.getSimpleName();
    private short streamFactory;

    private ZirkId zirkId;

    public ProxyClient(Context context, ZirkId zirkId) {
        ProxyClient.context = context;
        this.zirkId = zirkId;
    }

    public static ZirkId registerZirk(Context context, final String zirkName) {
        Log.i(TAG, "RegisteringService: " + zirkName);
        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
        }

        // TODO: if the zirk id is uninstalled then owner device shows cached and new one.

        final SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
        String zirkIdAsString = shrdPref.getString(zirkName, null);
        if (null == zirkIdAsString) {
            // UUID for zirk id
            zirkIdAsString = UUID.randomUUID().toString();

            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString(zirkName, zirkIdAsString);
            editor.commit();
        }

        ZirkId zirkId = new ZirkId(zirkIdAsString);
        Log.d(TAG, "ZirkId-> " + zirkIdAsString); // Remove this line
        // Send the Intent to the BezirkStack
        String serviceIdKEY = "zirkId";
        String serviceNameKEY = "serviceName";
        Intent registerIntent = new Intent();

        ComponentName componentName = new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME);
        registerIntent.setComponent(componentName);
        registerIntent.setAction(ACTION_BEZIRK_REGISTER);
        registerIntent.putExtra(serviceIdKEY, new Gson().toJson(zirkId));
        registerIntent.putExtra(serviceNameKEY, zirkName);

        ComponentName retName = context.startService(registerIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk installed?");
            return null;
        } else {
            Log.i(TAG, "Registration request is triggered to Bezirk for Service :" + zirkName);
        }

        return zirkId;
    }

/* Old unregisterZirk method available in commit ID : 53012cbff5b00847b765ac59efc0c5b9cfb5cd33 */

    //TODO: Test this implementation
    @Override
    public void unregisterZirk() {
        Log.i(TAG, "Unregister request for serviceID: " + ((ZirkId) zirkId).getZirkId());

        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            //find and delete the entry corresponding to this zirkId
            if (entry.getValue().toString().equalsIgnoreCase(((ZirkId) zirkId).getZirkId())) {
                Log.i(TAG, "Unregistering zirk: " + entry.getKey());
                SharedPreferences.Editor editor = shrdPref.edit();
                editor.remove(entry.getKey());
                editor.apply();
                unsubscribe(null);
            }
        }
    }

    @Override
    public void subscribe(final ProtocolRole protocolRole, final BezirkListener listener) {
        isRequestValid(zirkId, protocolRole, listener);

        PROXY_CLIENT_HELPER.addTopicsToMap(protocolRole.getEventTopics(), eventListenerMap, listener, "Event");
        PROXY_CLIENT_HELPER.addTopicsToMap(protocolRole.getStreamTopics(), streamListenerMap, listener, "Stream");
        // Send the intent
        Intent subscribeIntent = new Intent();
        subscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        subscribeIntent.setAction(ACTION_BEZIRK_SUBSCRIBE);
        subscribeIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        SubscribedRole subRole = new SubscribedRole(protocolRole);
        subscribeIntent.putExtra("protocol", subRole.getSubscribedProtocolRole());
        ComponentName retName = context.startService(subscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    private void isRequestValid(ZirkId subscriber, ProtocolRole pRole, BezirkListener listener) {
        if (!StringValidatorUtil.areValidStrings(pRole.getRoleName()) || null == listener || null == subscriber) {
            throw new IllegalArgumentException("Check for ProtocolRole/ BezirkListener/ZirkId for null or empty values");
        }

        if ((null == pRole.getEventTopics()) && (null == pRole.getStreamTopics())) {
            throw new IllegalArgumentException("ProtocolRole doesn't have any Events/Streams to subscribe");
        }
    }

    @Override
    public boolean unsubscribe(final ProtocolRole protocolRole) {
        Intent unSubscribeIntent = new Intent();
        unSubscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unSubscribeIntent.setAction(ACTION_BEZIRK_UNSUBSCRIBE);
        unSubscribeIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        String pRoleAsString = (null == protocolRole) ? null : (new SubscribedRole(protocolRole).getSubscribedProtocolRole());
        unSubscribeIntent.putExtra("protocol", pRoleAsString);

        ComponentName retName = context.startService(unSubscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return false;
        }

        return true;
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location(null)), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        if (event == null) {
            throw new IllegalArgumentException("Cannot send a null event");
        }

        Intent multicastEventIntent = new Intent();
        multicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastEventIntent.setAction(ACTION_SERVICE_SEND_MULTICAST_EVENT);
        multicastEventIntent.putExtra("zirkId", new Gson().toJson(zirkId));

        multicastEventIntent.putExtra("address", recipient.toJson());
        multicastEventIntent.putExtra("multicastEvent", event.toJson());

        ComponentName retName = context.startService(multicastEventIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        if (event == null) {
            throw new IllegalArgumentException("Cannot send a null event");
        }

        Intent unicastEventIntent = new Intent();
        unicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unicastEventIntent.setAction(ACTION_SERVICE_SEND_UNICAST_EVENT);
        unicastEventIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        unicastEventIntent.putExtra("receiverSep", new Gson().toJson(recipient));
        unicastEventIntent.putExtra("eventMsg", event.toJson());
        ComponentName retName = context.startService(unicastEventIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    @Override
    public short sendStream(ZirkEndPoint recipient, Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(streamId, stream.topic);
        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) recipient;

        final Intent multicastStreamIntent = new Intent();
        multicastStreamIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastStreamIntent.setAction(ACTION_BEZIRK_PUSH_MULTICAST_STREAM);
        multicastStreamIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        multicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        multicastStreamIntent.putExtra("stream", stream.toJson());
        multicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = context.startService(multicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return 0;
        }
        Log.d(TAG, "StreamId-> " + streamId);
        return streamId;

    }

    @Override
    public short sendStream(ZirkEndPoint recipient, Stream stream, File file) {
        if (recipient == null) {
            throw new IllegalArgumentException("Cannot send a stream to a null recipient");
        }

        if (stream == null || stream.topic.isEmpty()) {
            throw new IllegalArgumentException("Null or empty stream specified when sending " +
                    "a file");
        }
        if (file == null) {
            throw new IllegalArgumentException("Cannot send a null file");
        }

        if (!file.exists()) {
            Log.e(TAG, "Cannot send file stream. File not found: " + file.getPath());
            return (short) -1;
        }
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);

        activeStreams.put(streamId, stream.topic);

        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) recipient;

        final Intent unicastStreamIntent = new Intent();
        unicastStreamIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unicastStreamIntent.setAction(ACTION_BEZIRK_PUSH_UNICAST_STREAM);
        unicastStreamIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        unicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        unicastStreamIntent.putExtra("stream", stream.toJson());
        unicastStreamIntent.putExtra("filePath", file);
        unicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = context.startService(unicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return 0;
        }
        Log.d(TAG, "StreamId-> " + streamId);
        return streamId;
    }

    @Override
    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot set a null location");
        }

        Intent locationIntent = new Intent();
        locationIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        locationIntent.setAction(ACTION_BEZIRK_SETLOCATION);
        locationIntent.putExtra("zirkId", new Gson().toJson(zirkId));
        locationIntent.putExtra("locationData", new Gson().toJson(location));
        context.startService(locationIntent);
    }
}

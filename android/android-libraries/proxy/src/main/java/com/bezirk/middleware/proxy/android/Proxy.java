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
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.pipe.policy.ext.BezirkPipePolicy;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.google.gson.Gson;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Proxy implements Bezirk {
    static final int TIME_DURATION = 15000;
    static final int MAX_MAP_SIZE = 50;
    static final ConcurrentMap<String, List<BezirkListener>> eventListenerMap = new ConcurrentHashMap<String, List<BezirkListener>>();
    static final ConcurrentMap<String, List<BezirkListener>> streamListenerMap = new ConcurrentHashMap<String, List<BezirkListener>>();
    static final ConcurrentMap<Short, String> activeStreams = new ConcurrentHashMap<Short, String>();
    static final ConcurrentMap<String, Long> duplicateMsgMap = new ConcurrentHashMap<String, Long>();
    static final ConcurrentMap<String, Long> duplicateStreamMap = new ConcurrentHashMap<String, Long>();
    //Pipe Listener Map -- pipeId : listener
    static final ConcurrentMap<String, BezirkListener> pipeListenerMap = new ConcurrentHashMap<String, BezirkListener>();
    private static final String ACTION_BEZIRK_REGISTER = "REGISTER";
    private static final String ACTION_SERVICE_DISCOVER = "DISCOVER";
    private static final String ACTION_SERVICE_SEND_MULTICAST_EVENT = "MULTICAST_EVENT";
    private static final String ACTION_SERVICE_SEND_UNICAST_EVENT = "UNICAST_EVENT";
    private static final String ACTION_BEZIRK_SUBSCRIBE = "SUBSCRIBE";
    private static final String ACTION_BEZIRK_UNSUBSCRIBE = "UNSUBSCRIBE";
    private static final String ACTION_BEZIRK_SETLOCATION = "LOCATION";
    private static final String ACTION_BEZIRK_PUSH_UNICAST_STREAM = "UNICAST_STREAM";
    private static final String ACTION_BEZIRK_PUSH_MULTICAST_STREAM = "MULTICAST_STREAM";
    private static final String COMPONENT_NAME = "com.bosch.upa.uhu.controlui";
    private static final String SERVICE_PKG_NAME = "com.bosch.upa.uhu.starter.MainService";
    private static final ProxyHelper proxyHelper = new ProxyHelper();
    static Context mContext;
    static BezirkListener DiscoveryListener;
    static int discoveryCount; // keep track of Discovery Id
    private final String TAG = Proxy.class.getSimpleName();
    private short streamFactory;

    public Proxy(Context context) {
        mContext = context;

    }

    @Override
    public ZirkId registerZirk(final String zirkName) {
        Log.i(TAG, "RegisteringService: " + zirkName);
        if (zirkName == null) {
            Log.e(TAG, "Service name Cannot be null during Registration");
            return null;
        }

        // TODO: if the zirk id is uninstalled then owner device shows cached and new one.

        final SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String serviceIdAsString = shrdPref.getString(zirkName, null);
        if (null == serviceIdAsString) {
            // UUID for zirk id
            serviceIdAsString = UUID.randomUUID().toString();

            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString(zirkName, serviceIdAsString);
            editor.commit();
        }

        final BezirkZirkId serviceId = new BezirkZirkId(serviceIdAsString);
        Log.d(TAG, "BezirkZirkId-> " + serviceIdAsString); // Remove this line
        // Send the Intent to the BezirkStack
        String serviceIdKEY = "zirkId";
        String serviceNameKEY = "serviceName";
        Intent registerIntent = new Intent();

        ComponentName componentName = new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME);
        registerIntent.setComponent(componentName);
        registerIntent.setAction(ACTION_BEZIRK_REGISTER);
        registerIntent.putExtra(serviceIdKEY, new Gson().toJson(serviceId));
        registerIntent.putExtra(serviceNameKEY, zirkName);

        ComponentName retName = mContext.startService(registerIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk installed?");
            return null;
        } else {
            Log.i(TAG, "Registration request is triggered to Bezirk for Service :" + zirkName);
        }
        return serviceId;
    }

/* Old unregisterZirk method available in commit ID : 53012cbff5b00847b765ac59efc0c5b9cfb5cd33 */

    //TODO: Test this implementation
    @Override
    public void unregisterZirk(final ZirkId zirkId) {
        Log.i(TAG, "Unregister request for serviceID: " + ((BezirkZirkId) zirkId).getBezirkZirkId());

        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            //find and delete the entry corresponding to this zirkId
            if (entry.getValue().toString().equalsIgnoreCase(((BezirkZirkId) zirkId).getBezirkZirkId())) {
                Log.i(TAG, "Unregistering zirk: " + entry.getKey());
                SharedPreferences.Editor editor = shrdPref.edit();
                editor.remove(entry.getKey());
                editor.apply();
                unsubscribe(zirkId, null);
            }
        }
    }

    @Override
    public void subscribe(final ZirkId subscriber, final ProtocolRole protocolRole, final BezirkListener listener) {
        if (!isRequestValid(subscriber, protocolRole, listener)) {
            return;
        }

        proxyHelper.addTopicsToMap(protocolRole.getEventTopics(), eventListenerMap, listener, "Event");
        proxyHelper.addTopicsToMap(protocolRole.getStreamTopics(), streamListenerMap, listener, "Stream");
        // Send the intent
        Intent subscribeIntent = new Intent();
        subscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        subscribeIntent.setAction(ACTION_BEZIRK_SUBSCRIBE);
        subscribeIntent.putExtra("zirkId", new Gson().toJson(subscriber));
        SubscribedRole subRole = new SubscribedRole(protocolRole);
        subscribeIntent.putExtra("protocol", subRole.getSubscribedProtocolRole());
        ComponentName retName = mContext.startService(subscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    private boolean isRequestValid(ZirkId subscriber, ProtocolRole pRole, BezirkListener listener) {
        if (!StringValidatorUtil.areValidStrings(pRole.getRoleName()) || null == listener || null == subscriber) {
            Log.e(TAG, "Check for ProtocolRole/ BezirkListener/ZirkId for null or empty values");
            return false;
        }
        if ((null == pRole.getEventTopics()) && (null == pRole.getStreamTopics())) {
            Log.e(TAG, "ProtocolRole doesn't have any Events/Streams to subscribe");
            return false;
        }
        return true;
    }

    @Override
    public boolean unsubscribe(final ZirkId subscriber, final ProtocolRole protocolRole) {
        Intent unSubscribeIntent = new Intent();
        unSubscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unSubscribeIntent.setAction(ACTION_BEZIRK_UNSUBSCRIBE);
        unSubscribeIntent.putExtra("zirkId", new Gson().toJson(subscriber));
        String pRoleAsString = (null == protocolRole) ? null : (new SubscribedRole(protocolRole).getSubscribedProtocolRole());
        unSubscribeIntent.putExtra("protocol", pRoleAsString);

        ComponentName retName = mContext.startService(unSubscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return false;
        }

        return true;
    }

    @Override
    public void sendEvent(ZirkId sender, RecipientSelector recipient, Event event) {
        // Check for sending the target!
        if (null == event || null == sender) {
            Log.e(TAG, "Check for null in target or Event or sender");
            return;
        }
        Intent multicastEventIntent = new Intent();
        multicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastEventIntent.setAction(ACTION_SERVICE_SEND_MULTICAST_EVENT);
        multicastEventIntent.putExtra("zirkId", new Gson().toJson(sender));

        multicastEventIntent.putExtra("address", recipient.toJson());
        multicastEventIntent.putExtra("multicastEvent", event.toJson());

        ComponentName retName = mContext.startService(multicastEventIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    @Override
    public void sendEvent(ZirkId sender, ZirkEndPoint recipient, Event event) {

        if (null == recipient || null == event || null == sender) {
            Log.e(TAG, "Check for null in receiver or Event or sender");
            return;
        }

        Intent unicastEventIntent = new Intent();
        unicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unicastEventIntent.setAction(ACTION_SERVICE_SEND_UNICAST_EVENT);
        unicastEventIntent.putExtra("zirkId", new Gson().toJson(sender));
        unicastEventIntent.putExtra("receiverSep", new Gson().toJson(recipient));
        unicastEventIntent.putExtra("eventMsg", event.toJson());
        ComponentName retName = mContext.startService(unicastEventIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    @Override
    public short sendStream(ZirkId sender, ZirkEndPoint recipient, Stream stream, PipedOutputStream dataStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(streamId, stream.topic);
        BezirkZirkEndPoint recipientSEP = (BezirkZirkEndPoint) recipient;

        final Intent multicastStreamIntent = new Intent();
        multicastStreamIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastStreamIntent.setAction(ACTION_BEZIRK_PUSH_MULTICAST_STREAM);
        multicastStreamIntent.putExtra("zirkId", new Gson().toJson(sender));
        multicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        multicastStreamIntent.putExtra("stream", stream.toJson());
        multicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = mContext.startService(multicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return 0;
        }
        Log.d(TAG, "StreamId-> " + streamId);
        return streamId;

    }

    @Override
    public short sendStream(ZirkId sender, ZirkEndPoint recipient, Stream stream, File file) {

        if (null == recipient || null == stream || !StringValidatorUtil.areValidStrings(stream.topic)) {
            Log.e(TAG, "Check for null values in sendStream()/ Topic might be Empty.");
            return (short) -1;
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
        unicastStreamIntent.putExtra("zirkId", new Gson().toJson(sender));
        unicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        unicastStreamIntent.putExtra("stream", stream.toJson());
        unicastStreamIntent.putExtra("filePath", file);
        unicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = mContext.startService(unicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
            return 0;
        }
        Log.d(TAG, "StreamId-> " + streamId);
        return streamId;
    }

    @Override
    /**
     * Currently the behavior of this method is:
     *   1. register the pipe with the PipeManager, or
     *   2. if the pipe already exists, overwrite an existing pipe with the same uri
     */
    public void requestPipeAuthorization(ZirkId requester, Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut, BezirkListener listener) {
        //Update Listener Map
        final String pipeId = UUID.randomUUID().toString();
        pipeListenerMap.put(pipeId, listener);

        Intent addPipe = new Intent();
        addPipe.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        addPipe.setAction(BezirkActions.ACTION_PIPE_REQUEST);
        addPipe.putExtra(BezirkActions.KEY_PIPE_NAME, pipe.getName());
        addPipe.putExtra(BezirkActions.KEY_PIPE_REQ_ID, pipeId);
        addPipe.putExtra(BezirkActions.KEY_SENDER_ZIRK_ID, new Gson().toJson(requester));

        addPipe.putExtra(BezirkActions.KEY_PIPE_CLASS, pipe.getClass().getCanonicalName());

        // Add Pipe Policys
        addPipe.putExtra(BezirkActions.KEY_PIPE_POLICY_IN, allowedIn == null ? null : new BezirkPipePolicy(allowedIn).toJson());
        addPipe.putExtra(BezirkActions.KEY_PIPE_POLICY_OUT, allowedOut == null ? null : new BezirkPipePolicy(allowedOut).toJson());

        Log.i(TAG, "Sending intent for pipe class: " + pipe.getClass().getCanonicalName());

        Log.d(TAG, "addCloudPipe() Sending intent: " + addPipe.getAction());

        ComponentName retName = mContext.startService(addPipe);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Bezirk Service. returning null for zirk id. Is Bezirk this installed?");
        }
    }

    @Override
    public void getPipePolicy(Pipe pipe, BezirkListener listener) {
        // Need not do now
        Log.w(TAG, "getPipePolicy() not implemented yet");
    }

    @Override
    public void discover(ZirkId zirk, RecipientSelector scope, ProtocolRole protocolRole, long timeout, int maxResults, BezirkListener listener) {

        if (null == zirk || null == listener) {
            Log.e(TAG, "ZirkId/BezirkListener is null");
            return;
        }

        DiscoveryListener = listener;
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;

        Intent discoverIntent = new Intent();
        discoverIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        discoverIntent.setAction(ACTION_SERVICE_DISCOVER);
        discoverIntent.putExtra("zirkId", new Gson().toJson(zirk));
        discoverIntent.putExtra("address", new Gson().toJson(scope));
        discoverIntent.putExtra("protocolRole", new SubscribedRole(protocolRole).getSubscribedProtocolRole());
        discoverIntent.putExtra("timeout", timeout);
        discoverIntent.putExtra("maxDiscovered", maxResults);
        discoverIntent.putExtra("discoveryId", discoveryCount);
        mContext.startService(discoverIntent);
        Log.i(TAG, "Discovery Request to BezirkStack");
    }

    @Override
    public void setLocation(ZirkId zirk, Location location) {
        if (null == location) {
            Log.e(TAG, "Location is null or Empty, Services cannot set the location as Null");
            return;
        }
        Intent locationIntent = new Intent();
        locationIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        locationIntent.setAction(ACTION_BEZIRK_SETLOCATION);
        locationIntent.putExtra("zirkId", new Gson().toJson(zirk));
        locationIntent.putExtra("locationData", new Gson().toJson(location));
        mContext.startService(locationIntent);
    }


}

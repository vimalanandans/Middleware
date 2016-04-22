package com.bezirk.middleware.proxy.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.actions.UhuActions;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.serialization.AddressSerializer;
import com.bezirk.pipe.policy.ext.UhuPipePolicy;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Proxy implements Bezirk {
    static final int TIME_DURATION = 15000;
    static final int MAX_MAP_SIZE = 50;
    static final ConcurrentMap<String, ArrayList<BezirkListener>> eventListenerMap = new ConcurrentHashMap<String, ArrayList<BezirkListener>>();
    static final ConcurrentMap<String, ArrayList<BezirkListener>> streamListenerMap = new ConcurrentHashMap<String, ArrayList<BezirkListener>>();
    static final ConcurrentMap<Short, String> activeStreams = new ConcurrentHashMap<Short, String>();
    static final ConcurrentMap<String, Long> duplicateMsgMap = new ConcurrentHashMap<String, Long>();
    static final ConcurrentMap<String, Long> duplicateStreamMap = new ConcurrentHashMap<String, Long>();
    //Pipe Listener Map -- pipeId : listener
    static final ConcurrentMap<String, BezirkListener> pipeListenerMap = new ConcurrentHashMap<String, BezirkListener>();
    private static final String ACTION_UHU_REGISTER = "REGISTER";
    private static final String ACTION_SERVICE_DISCOVER = "DISCOVER";
    private static final String ACTION_SERVICE_SEND_MULTICAST_EVENT = "MULTICAST_EVENT";
    private static final String ACTION_SERVICE_SEND_UNICAST_EVENT = "UNICAST_EVENT";
    private static final String ACTION_UHU_SUBSCRIBE = "SUBSCRIBE";
    private static final String ACTION_UHU_UNSUBSCRIBE = "UNSUBSCRIBE";
    private static final String ACTION_UHU_SETLOCATION = "LOCATION";
    private static final String ACTION_UHU_PUSH_UNICAST_STREAM = "UNICAST_STREAM";
    private static final String ACTION_UHU_PUSH_MULTICAST_STREAM = "MULTICAST_STREAM";
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
    public ServiceId registerService(final String serviceName) {
        Log.i(TAG, "RegisteringService: " + serviceName);
        if (serviceName == null) {
            Log.e(TAG, "Service name Cannot be null during Registration");
            return null;
        }

        // TODO: if the service id is uninstalled then owner device shows cached and new one.

        final SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String serviceIdAsString = shrdPref.getString(serviceName, null);
        if (null == serviceIdAsString) {
            // UUID for service id
            serviceIdAsString = UUID.randomUUID().toString();

            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString(serviceName, serviceIdAsString);
            editor.commit();
        }

        final UhuServiceId serviceId = new UhuServiceId(serviceIdAsString);
        Log.d(TAG, "UhuServiceId-> " + serviceIdAsString); // Remove this line
        // Send the Intent to the UhuStack
        String serviceIdKEY = "serviceId";
        String serviceNameKEY = "serviceName";
        Intent registerIntent = new Intent();

        ComponentName componentName = new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME);
        registerIntent.setComponent(componentName);
        registerIntent.setAction(ACTION_UHU_REGISTER);
        registerIntent.putExtra(serviceIdKEY, new Gson().toJson((UhuServiceId) serviceId));
        registerIntent.putExtra(serviceNameKEY, serviceName);

        ComponentName retName = mContext.startService(registerIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu installed?");
            return null;
        } else {
            Log.i(TAG, "Registration request is triggered to Uhu for Service :" + serviceName);
        }
        return serviceId;
    }

/* Old unregisterService method available in commit ID : 53012cbff5b00847b765ac59efc0c5b9cfb5cd33 */

    //TODO: Test this implementation
    @Override
    public void unregisterService(final ServiceId serviceId) {
        Log.i(TAG, "Unregister request for serviceID: " + ((UhuServiceId) serviceId).getUhuServiceId());

        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Map<String, ?> keys = shrdPref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            //find and delete the entry corresponding to this serviceId
            if (entry.getValue().toString().equalsIgnoreCase(((UhuServiceId) serviceId).getUhuServiceId())) {
                Log.i(TAG, "Unregistering service: " + entry.getKey());
                SharedPreferences.Editor editor = shrdPref.edit();
                editor.remove(entry.getKey());
                editor.apply();
                unsubscribe(serviceId, null);
            }
        }
    }

    @Override
    public void subscribe(final ServiceId subscriber, final ProtocolRole pRole, final BezirkListener listener) {
        if (!isRequestValid(subscriber, pRole, listener)) {
            return;
        }

        proxyHelper.addTopicsToMap(pRole.getEventTopics(), eventListenerMap, listener, "Event");
        proxyHelper.addTopicsToMap(pRole.getStreamTopics(), streamListenerMap, listener, "Stream");
        // Send the intent
        Intent subscribeIntent = new Intent();
        subscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        subscribeIntent.setAction(ACTION_UHU_SUBSCRIBE);
        subscribeIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) subscriber));
        SubscribedRole subRole = new SubscribedRole(pRole);
        subscribeIntent.putExtra("protocol", subRole.getSubscribedProtocolRole());
        ComponentName retName = mContext.startService(subscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return;
        }
    }

    private boolean isRequestValid(ServiceId subscriber, ProtocolRole pRole, BezirkListener listener) {
        if (!StringValidatorUtil.areValidStrings(pRole.getProtocolName()) || null == listener || null == subscriber) {
            Log.e(TAG, "Check for ProtocolRole/ UhuListener/ServiceId for null or empty values");
            return false;
        }
        if ((null == pRole.getEventTopics()) && (null == pRole.getStreamTopics())) {
            Log.e(TAG, "ProtocolRole doesn't have any Events/ Streams to subscribe");
            return false;
        }
        return true;
    }

    @Override
    public void unsubscribe(final ServiceId subscriber, final ProtocolRole pRole) {
        Intent unSubscribeIntent = new Intent();
        unSubscribeIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unSubscribeIntent.setAction(ACTION_UHU_UNSUBSCRIBE);
        unSubscribeIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) subscriber));
        String pRoleAsString = (null == pRole) ? null : (new SubscribedRole(pRole).getSubscribedProtocolRole());
        unSubscribeIntent.putExtra("protocol", pRoleAsString);

        ComponentName retName = mContext.startService(unSubscribeIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return;
        }
    }

    @Override
    public void sendEvent(ServiceId sender, Address target, Event event) {
        // Check for sending the target!
        if (null == event || null == sender) {
            Log.e(TAG, "Check for null in target or Event or sender");
            return;
        }
        Intent multicastEventIntent = new Intent();
        multicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastEventIntent.setAction(ACTION_SERVICE_SEND_MULTICAST_EVENT);
        multicastEventIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) sender));

        multicastEventIntent.putExtra("address", new AddressSerializer().toJson(target));
        multicastEventIntent.putExtra("multicastEvent", event.serialize());

        ComponentName retName = mContext.startService(multicastEventIntent);

        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return;
        }
    }

    @Override
    public void sendEvent(ServiceId sender, ServiceEndPoint receiver, Event event) {

        if (null == receiver || null == event || null == sender) {
            Log.e(TAG, "Check for null in receiver or Event or sender");
            return;
        }

        Intent unicastEventIntent = new Intent();
        unicastEventIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unicastEventIntent.setAction(ACTION_SERVICE_SEND_UNICAST_EVENT);
        unicastEventIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) sender));
        unicastEventIntent.putExtra("receiverSep", new Gson().toJson((UhuServiceEndPoint) receiver));
        unicastEventIntent.putExtra("eventMsg", event.serialize());
        ComponentName retName = mContext.startService(unicastEventIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return;
        }
    }

    @Override
    public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream stream, PipedOutputStream pipedOutputStream) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);
        activeStreams.put(streamId, stream.topic);
        UhuServiceEndPoint recipientSEP = (UhuServiceEndPoint) receiver;

        final Intent multicastStreamIntent = new Intent();
        multicastStreamIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        multicastStreamIntent.setAction(ACTION_UHU_PUSH_MULTICAST_STREAM);
        multicastStreamIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) sender));
        multicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        multicastStreamIntent.putExtra("stream", stream.serialize());
        multicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = mContext.startService(multicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return 0;
        }
        Log.d(TAG, "StreamId-> " + streamId);
        return streamId;

    }

    @Override
    public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream stream, String filePath) {

        if (null == receiver || null == stream || !StringValidatorUtil.areValidStrings(filePath, stream.topic)) {
            Log.e(TAG, "Check for null values in sendStream()/ Topic might be Empty.");
            return (short) -1;
        }

        File tempFile = new File(filePath);
        if (!tempFile.exists()) {
            Log.e(TAG, " No file found at the location: " + filePath);
            return (short) -1;
        }
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);

        activeStreams.put(streamId, stream.topic);

        UhuServiceEndPoint recipientSEP = (UhuServiceEndPoint) receiver;

        final Intent unicastStreamIntent = new Intent();
        unicastStreamIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        unicastStreamIntent.setAction(ACTION_UHU_PUSH_UNICAST_STREAM);
        unicastStreamIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) sender));
        unicastStreamIntent.putExtra("receiverSEP", new Gson().toJson(recipientSEP));
        unicastStreamIntent.putExtra("stream", stream.serialize());
        unicastStreamIntent.putExtra("filePath", filePath);
        unicastStreamIntent.putExtra("localStreamId", streamId);
        ComponentName retName = mContext.startService(unicastStreamIntent);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
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
    public void requestPipe(ServiceId requester, Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut, BezirkListener listener) {
        //Update Listener Map
        final String pipeId = UUID.randomUUID().toString();
        pipeListenerMap.put(pipeId, listener);

        Intent addPipe = new Intent();
        addPipe.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        addPipe.setAction(UhuActions.ACTION_PIPE_REQUEST);
        addPipe.putExtra(UhuActions.KEY_PIPE_NAME, pipe.getName());
        addPipe.putExtra(UhuActions.KEY_PIPE_REQ_ID, pipeId);
        addPipe.putExtra(UhuActions.KEY_SENDER_SERVICE_ID, new Gson().toJson((UhuServiceId) requester));

        if (pipe instanceof CloudPipe) {
            addPipe.putExtra(UhuActions.KEY_PIPE_URI, ((CloudPipe) pipe).getURI().toString());
        }

        addPipe.putExtra(UhuActions.KEY_PIPE_CLASS, pipe.getClass().getCanonicalName());

        // Add Pipe Policys
        addPipe.putExtra(UhuActions.KEY_PIPE_POLICY_IN, allowedIn == null ? null : new UhuPipePolicy(allowedIn).serialize());
        addPipe.putExtra(UhuActions.KEY_PIPE_POLICY_OUT, allowedOut == null ? null : new UhuPipePolicy(allowedOut).serialize());

        Log.i(TAG, "Sending intent for pipe class: " + pipe.getClass().getCanonicalName());

        Log.d(TAG, "addCloudPipe() Sending intent: " + addPipe.getAction());

        ComponentName retName = mContext.startService(addPipe);
        if (retName == null) {
            Log.e(TAG, "Unable to start the Uhu Service. returning null for service id. Is Uhu this installed?");
            return;
        }
    }

    @Override
    public void getPipePolicy(Pipe pipe, BezirkListener listener) {
        // Need not do now
        Log.w(TAG, "getPipePolicy() not implemented yet");
    }

    @Override
    public void discover(ServiceId service, Address scope, ProtocolRole pRole, long timeout, int maxDiscovered, BezirkListener listener) {

        if (null == service || null == listener) {
            Log.e(TAG, "ServiceId/UhuListener is null");
            return;
        }

        DiscoveryListener = listener;
        discoveryCount = (++discoveryCount) % Integer.MAX_VALUE;

        Intent discoverIntent = new Intent();
        discoverIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        discoverIntent.setAction(ACTION_SERVICE_DISCOVER);
        discoverIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) service));
        discoverIntent.putExtra("address", new Gson().toJson(scope));
        discoverIntent.putExtra("pRole", new SubscribedRole(pRole).getSubscribedProtocolRole());
        discoverIntent.putExtra("timeout", timeout);
        discoverIntent.putExtra("maxDiscovered", maxDiscovered);
        discoverIntent.putExtra("discoveryId", discoveryCount);
        mContext.startService(discoverIntent);
        Log.i(TAG, "Discovery Request to UhuStack");
    }

    @Override
    public void setLocation(ServiceId service, Location location) {
        if (null == location) {
            Log.e(TAG, "Location is null or Empty, Services cannot set the location as Null");
            return;
        }
        Intent locationIntent = new Intent();
        locationIntent.setComponent(new ComponentName(COMPONENT_NAME, SERVICE_PKG_NAME));
        locationIntent.setAction(ACTION_UHU_SETLOCATION);
        locationIntent.putExtra("serviceId", new Gson().toJson((UhuServiceId) service));
        locationIntent.putExtra("locationData", new Gson().toJson(location));
        mContext.startService(locationIntent);
    }


}

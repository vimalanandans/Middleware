package com.bezirk.middleware.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.identity.IdentityManager;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.middleware.messages.StreamSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProxyClient implements Bezirk {
    private static final String TAG = ProxyClient.class.getName();

    protected static final Map<String, List<StreamSet.StreamReceiver>> streamListenerMap = new ConcurrentHashMap<>();
    protected static final Map<String, List<EventSet>> eventSetMap = new ConcurrentHashMap<>();

    protected static Context context;
    private final ZirkId zirkId;
    private final IdentityManager identityManager;
    private short streamFactory;
    private static IntentSender intentSender;

    public ProxyClient(ZirkId zirkId) {

        // Bind to remote identity management service
//        Intent intent = new Intent();
//        intent.setComponent(RECEIVING_COMPONENT);
//        boolean boundService = context.bindService(intent,
//                ClientIdentityManagerAdapter.remoteConnection, Context.BIND_AUTO_CREATE);
//        Log.d(TAG, "Binding to identity management service status: "+ boundService);

        this.zirkId = zirkId;
        this.identityManager = new ClientIdentityManagerAdapter();
    }


    public static ZirkId registerZirk(@NotNull final Context context, final String zirkName, final IntentSender intentSender) {
        ProxyClient.context = context;
        ProxyClient.intentSender = intentSender;

        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
        }

        Log.d(TAG, "Registering Zirk is: " + zirkName);

        final SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (null != shrdPref) {
            Log.d(TAG, "shrdPref is not null");
        } else {
            Log.d(TAG, "shredPref is null");
        }
        String zirkIdAsString = shrdPref.getString(zirkName, null);
        Log.d(TAG, "zirkIdAsString is " + zirkIdAsString);
        if (null == zirkIdAsString) {
            zirkIdAsString = UUID.randomUUID().toString();
            Log.d(TAG, "ZirkId-> " + zirkIdAsString);

            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString(zirkName, zirkIdAsString);
            editor.commit();
        }

        final ZirkId zirkId = new ZirkId(zirkIdAsString);

        if (intentSender.sendBezirkIntent(new RegisterZirkAction(zirkId, zirkName))) {
            Log.d(TAG, "Registered Zirk: " + zirkName);
            return zirkId;
        }

        return zirkId;
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
                //TODO add unsubscription code for subsriptions of a zirk
                //unsubscribe(null);
                break;
            }
        }
    }

    @Override
    public void subscribe(final MessageSet messageSet) {
        Log.d(TAG, "subscribe method of ProxyClient");
        if (messageSet instanceof EventSet) {
            Log.d(TAG, "messageSet instanceof EventSet in ProxyClient");
            //EventSet.EventReceiver listener = ((EventSet) messageSet).getEventReceiver();
            addMessagesToMap((EventSet) messageSet, eventSetMap);
        } else if (messageSet instanceof StreamSet) {
            Log.d(TAG, "messageSet instanceof StreamSet in ProxyClient");
            StreamSet.StreamReceiver listener = ((StreamSet) messageSet).getStreamReceiver();
            addMessagesToMap(messageSet, streamListenerMap, listener);
        } else {
            Log.d(TAG, "messageSet is unKnown:  in ProxyClient");
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        intentSender.sendBezirkIntent(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_SUBSCRIBE, zirkId, messageSet));
    }

    private void addMessagesToMap(EventSet eventSet, Map<String,
            List<EventSet>> listenerMap) {
        for (String messageName : eventSet.getMessages()) {
            if (listenerMap.containsKey(messageName)) {
                List<EventSet> eventSetList = listenerMap.get(messageName);
                if (eventSetList.contains(eventSet)) {
                    throw new IllegalArgumentException("The eventSet is already in use for " + messageName);
                } else {
                    eventSetList.add(eventSet);
                }
            } else {
                List<EventSet> eventSetList = new ArrayList<>();
                eventSetList.add(eventSet);
                listenerMap.put(messageName, eventSetList);
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
        if (messageSet == null) {
            Log.w(TAG, "Unsubscribing from all MessageSet(s) using null is currently not implemented. To unsubscribe from all subscriptions, call this method by passing the reference to the each MessageSet");
            return false;
        }
        for (List<EventSet> eventSets : eventSetMap.values()) {
            if (eventSets.contains(messageSet)) {
                eventSets.remove(messageSet);
            }
        }
        return intentSender.sendBezirkIntent(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE, zirkId,
                messageSet));
    }

    @Override
    public void sendEvent(Event event) {
        Log.d(TAG, "sendEvent One....");
        sendEvent(new RecipientSelector(new Location("null/null/null")), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        intentSender.sendBezirkIntent(new SendMulticastEventAction(zirkId, recipient, event,
                (event instanceof IdentifiedEvent)));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        intentSender.sendBezirkIntent(new UnicastEventAction(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT,
                zirkId, recipient, event, (event instanceof IdentifiedEvent)));
    }

    @Override
    public void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }


    @Override
    public void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor) {
        short streamId = (short) ((streamFactory++) % Short.MAX_VALUE);

        intentSender.sendBezirkStreamIntent(context, new SendFileStreamAction(zirkId, recipient, /*streamDescriptor,*/ streamId, streamDescriptor.getClass().getName()), streamDescriptor);

    }

    @Override
    public void setLocation(Location location) {
        intentSender.sendBezirkIntent(new SetLocationAction(zirkId, location));
    }

}
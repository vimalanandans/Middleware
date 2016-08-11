package com.bezirk.middleware.proxy;

import com.bezirk.actions.BezirkAction;
import com.bezirk.actions.RegisterZirkAction;
import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.SetLocationAction;
import com.bezirk.actions.SubscriptionAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.componentManager.ComponentManager;
import com.bezirk.datastorage.ProxyPersistence;
import com.bezirk.datastorage.ProxyRegistry;
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
import com.bezirk.proxy.ProxyServer;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.proxy.messagehandler.ZirkMessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProxyClient implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(ProxyClient.class);

    private static final Map<ZirkId, Set<EventSet.EventReceiver>> eventMap = new HashMap<>();
    private static final Map<ZirkId, Set<StreamSet.StreamReceiver>> streamMap = new HashMap<>();
    private static final Map<String, Set<EventSet.EventReceiver>> eventListenerMap = new HashMap<>();
    private static final Map<String, Set<StreamSet.StreamReceiver>> streamListenerMap = new HashMap<>();
    private static final BroadcastReceiver brForService = new ZirkMessageReceiver(
            eventMap, eventListenerMap, streamMap, streamListenerMap);
    private static final ZirkMessageHandler bezirkPcCallback = new ZirkMessageHandler(brForService);
    private static  ComponentManager componentManager = null;
    private static ProxyServer proxy;
    private static ProxyPersistence proxyPersistence;
    private static ProxyRegistry proxyRegistry = null;

    static void createMiddleware(String messageGroup) {
        if(componentManager == null) {
            componentManager = new ComponentManager(bezirkPcCallback, messageGroup);
            componentManager.start();
            proxy = componentManager.getProxyServer();
            proxyPersistence = componentManager.getBezirkProxyPersistence();
            try {
                proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error loading ProxyRegistry", e);
                System.exit(-1);
            }
        }

    }

    private ZirkId zirkId;

    public ProxyClient(String messageGroup) {
        createMiddleware(messageGroup);
    }

    public boolean registerZirk(String zirkName) {
        String zirkIdAsString = proxyRegistry.getBezirkServiceId(zirkName);

        if (zirkIdAsString == null) {
            zirkIdAsString = UUID.randomUUID().toString();
            proxyRegistry.updateBezirkZirkId(zirkName, zirkIdAsString);
            try {
                proxyPersistence.persistBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error in persisting the information", e);
                return false;
            }
        }

        if (logger.isTraceEnabled()) logger.trace("Zirk-Id: {}", zirkIdAsString);

        zirkId = new ZirkId(zirkIdAsString);
        proxy.registerZirk(new RegisterZirkAction(zirkId, zirkName));

        return true;
    }

    @Override
    public void unregisterZirk() {
        proxyRegistry.deleteBezirkZirkId(zirkId.getZirkId());
        try {
            proxyPersistence.persistBezirkProxyRegistry();
        } catch (Exception e) {
            logger.error("Error in persisting the information", e);
        }
        proxy.unregister(new RegisterZirkAction(zirkId, null));
    }

    @Override
    public void subscribe(final MessageSet messageSet) {
        if (messageSet instanceof EventSet) {
            EventSet.EventReceiver listener = ((EventSet) messageSet).getEventReceiver();
            addTopicsToMaps(zirkId, messageSet, listener, eventListenerMap);
        } else if (messageSet instanceof StreamSet) {
            StreamSet.StreamReceiver listener = ((StreamSet) messageSet).getStreamReceiver();
            addTopicsToMaps(zirkId, messageSet, listener, streamListenerMap);
        } else {
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        proxy.subscribe(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_SUBSCRIBE, zirkId,
                messageSet));
    }

    private void addTopicsToMaps(final ZirkId subscriber, final MessageSet messageSet,
                                 final EventSet.EventReceiver listener,
                                 Map<String, Set<EventSet.EventReceiver>> listenerMap) {
        for (String messageName : messageSet.getMessages()) {
            if (eventMap.containsKey(subscriber)) {
                eventMap.get(subscriber).add(listener);
            } else {
                Set<EventSet.EventReceiver> listeners = new HashSet<>();
                listeners.add(listener);
                eventMap.put(subscriber, listeners);
            }

            if (listenerMap.containsKey(messageName)) {
                final Set<EventSet.EventReceiver> zirkList = listenerMap.get(messageName);
                if (zirkList.contains(listener)) {
                    throw new IllegalArgumentException("The assigned listener is already in use for "
                            + messageName);
                } else {
                    zirkList.add(listener);
                }
            } else {
                Set<EventSet.EventReceiver> regServiceList = new HashSet<>();
                regServiceList.add(listener);
                listenerMap.put(messageName, regServiceList);
            }
        }
    }

    private void addTopicsToMaps(final ZirkId subscriber, final MessageSet messageSet,
                                 final StreamSet.StreamReceiver listener,
                                 Map<String, Set<StreamSet.StreamReceiver>> listenerMap) {
        for (String messageName : messageSet.getMessages()) {
            if (streamMap.containsKey(subscriber)) {
                streamMap.get(subscriber).add(listener);
            } else {
                Set<StreamSet.StreamReceiver> listeners = new HashSet<>();
                listeners.add(listener);
                streamMap.put(subscriber, listeners);
            }

            if (listenerMap.containsKey(messageName)) {
                final Set<StreamSet.StreamReceiver> zirkList = listenerMap.get(messageName);
                if (zirkList.contains(listener)) {
                    throw new IllegalArgumentException("The assigned listener is already in use for "
                            + messageName);
                } else {
                    zirkList.add(listener);
                }
            } else {
                Set<StreamSet.StreamReceiver> regServiceList = new HashSet<>();
                regServiceList.add(listener);
                listenerMap.put(messageName, regServiceList);
            }
        }
    }

    @Override
    public boolean unsubscribe(MessageSet messageSet) {
        return proxy.unsubscribe(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE, zirkId,
                messageSet));
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location("null/null/null")), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        proxy.sendEvent(new SendMulticastEventAction(zirkId, recipient, event,
                (event instanceof IdentifiedEvent)));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        proxy.sendEvent(new UnicastEventAction(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT,
                zirkId, recipient, event, (event instanceof IdentifiedEvent)));
    }

    @Override
    public void sendStream(ZirkEndPoint recipient,
                           StreamDescriptor streamDescriptor, PipedOutputStream dataStream) {
        throw new UnsupportedOperationException("Calling sendStream with a PipedOutputStream is current unimplemented.");
    }

    @Override
    public void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file) {
        proxy.sendStream(new SendFileStreamAction(zirkId, recipient, streamDescriptor, file));
    }

    @Override
    public void setLocation(Location location) {
        proxy.setLocation(new SetLocationAction(zirkId, location));
    }

    @Override
    public IdentityManager getIdentityManager() {
        return proxy.getIdentityManager();
    }
}

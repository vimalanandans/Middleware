/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.java.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.RegisterZirkAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.SetLocationAction;
import com.bezirk.middleware.core.actions.SubscriptionAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.datastorage.ProxyPersistence;
import com.bezirk.middleware.core.datastorage.ProxyRegistry;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.core.proxy.ProxyServer;
import com.bezirk.middleware.java.ComponentManager;
import com.bezirk.middleware.java.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.middleware.java.proxy.messagehandler.ZirkMessageHandler;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.streaming.Stream;
import com.bezirk.middleware.streaming.StreamController;

public class ProxyClient implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(ProxyClient.class);

    private static final Map<ZirkId, Set<EventSet.EventReceiver>> eventMap = new HashMap<>();
    private static final Map<String, Set<EventSet.EventReceiver>> eventListenerMap = new HashMap<>();
    private static final BroadcastReceiver brForService = new ZirkMessageReceiver(
            eventMap, eventListenerMap);
    private static final ZirkMessageHandler bezirkPcCallback = new ZirkMessageHandler(brForService);
    private static ComponentManager componentManager;
    private static ProxyServer proxy;
    private static ProxyPersistence proxyPersistence;
    private static ProxyRegistry proxyRegistry;
    private static boolean started;
    private ZirkId zirkId;

    static synchronized void start(@NotNull final Config config) {
        if (componentManager == null) {
            componentManager = new ComponentManager(bezirkPcCallback, config);
            componentManager.start();
            proxy = componentManager.getProxyServer();
            proxyPersistence = componentManager.getBezirkProxyPersistence();
            try {
                proxyRegistry = proxyPersistence.loadBezirkProxyRegistry();
            } catch (Exception e) {
                logger.error("Error loading ProxyRegistry", e);
                System.exit(-1);
            }
            started = true;
        }
    }

    static synchronized void stop() {
        if (started) {
            componentManager.stop();
        }
    }

    static synchronized boolean isStarted() {
        return started;
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

        logger.trace("Zirk-Id: {}", zirkIdAsString);

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
        } else {
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        proxy.subscribe(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_SUBSCRIBE, zirkId,
                messageSet));
    }

    private static void addTopicsToMaps(@NotNull final ZirkId subscriber, @NotNull final MessageSet messageSet,
                                 @NotNull final EventSet.EventReceiver listener,
                                 @NotNull final Map<String, Set<EventSet.EventReceiver>> listenerMap) {
        for (String messageName : messageSet.getMessages()) {
            if (eventMap.containsKey(subscriber)) {
                eventMap.get(subscriber).add(listener);
            } else {
                final Set<EventSet.EventReceiver> listeners = new HashSet<>();
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
                final Set<EventSet.EventReceiver> regServiceList = new HashSet<>();
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
                event instanceof IdentifiedEvent));

    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        proxy.sendEvent(new UnicastEventAction(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT,
                zirkId, recipient, event, event instanceof IdentifiedEvent));
    }

    @Override
    public void setLocation(Location location) {
        proxy.setLocation(new SetLocationAction(zirkId, location));
    }

    @Override
    public StreamController sendStream(Stream streamRequest) {
        throw new UnsupportedOperationException("feature to be implemented.");
    }

    @Override
    public void subscribeToStreamReceiver(Stream.StreamEventReceiver streamEventReceiver) {
        throw new UnsupportedOperationException("feature to be implemented.");
    }
}

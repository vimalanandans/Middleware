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
package com.bezirk.middleware.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
import com.bezirk.middleware.core.proxy.ProxyServer;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyClient implements Bezirk {
    private static final Logger logger = LoggerFactory.getLogger(ProxyClient.class);
    /**
     * Stores the list of <code>EventSet</code>(s) associated with each zirk.
     * [Key -&gt; Value] = [ZirkId -&gt; [List of EventSets]]
     */
    protected static final Map<ZirkId, List<EventSet>> zirkEventSubsciptionsMap = new ConcurrentHashMap<>();
    /**
     * Stores the list of <code>EventSet</code>(s) associated with each eventTopic. Typically used for incoming events.
     * [Key -&gt; Value] = [eventTopic -&gt; [List of EventSets]]
     */
    protected static final Map<String, List<EventSet>> eventSubscriptionsMap = new ConcurrentHashMap<>();
    protected static Context context;
    private static ProxyServer proxyServer;
    private final ZirkId zirkId;

    public ProxyClient(ZirkId zirkId) {
        this.zirkId = zirkId;
    }

    protected static void registerProxyServer(@NotNull final ProxyServer proxyServer){
        ProxyClient.proxyServer = proxyServer;
    }

    public static ZirkId registerZirk(@NotNull final Context context, final String zirkName) {
        ProxyClient.context = context;

        if (zirkName == null) {
            throw new IllegalArgumentException("Cannot register a Zirk with a null name");
        }

        logger.debug("Attempting to register Zirk: " + zirkName);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences == null) {
            throw new IllegalStateException("Cannot register new Zirk because shared preferences " +
                    "could not be retrieved");
        }

        String zirkIdAsString = sharedPreferences.getString(zirkName, null);
        logger.debug("zirkIdAsString from sharedPreferences: " + zirkIdAsString);
        if (zirkIdAsString == null) {
            zirkIdAsString = UUID.randomUUID().toString();
            logger.debug("Generated zirkId: " + zirkIdAsString);

            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(zirkName, zirkIdAsString);
            editor.commit();
        }

        final ZirkId zirkId = new ZirkId(zirkIdAsString);
        proxyServer.registerZirk(new RegisterZirkAction(zirkId, zirkName));
        return zirkId;
    }

    @Override
    public void unregisterZirk() {
        logger.debug("Unregister request for zirkID: " + zirkId.getZirkId());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equalsIgnoreCase(zirkId.getZirkId())) {
                logger.debug("Unregistering zirk: " + entry.getKey());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(entry.getKey());
                editor.apply();
                break;
            }
        }
    }

    @Override
    public void subscribe(final MessageSet messageSet) {
        logger.debug("subscribe method of ProxyClient");
        if (messageSet instanceof EventSet) {
            logger.debug("messageSet instanceof EventSet in ProxyClient");
            addEventSet((EventSet) messageSet);
        } else {
            logger.debug("messageSet is unKnown:  in ProxyClient");
            throw new AssertionError("Unknown MessageSet type: " +
                    messageSet.getClass().getSimpleName());
        }

        proxyServer.subscribe(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_SUBSCRIBE,
                zirkId, messageSet));
    }

    private void addEventSet(final EventSet eventSet) {
        if (zirkEventSubsciptionsMap.containsKey(zirkId)) {
            zirkEventSubsciptionsMap.get(zirkId).add(eventSet);
        } else {
            List<EventSet> eventSets = new ArrayList<>();
            eventSets.add(eventSet);
            zirkEventSubsciptionsMap.put(zirkId, eventSets);
        }

        for (String messageName : eventSet.getMessages()) {
            if (eventSubscriptionsMap.containsKey(messageName)) {
                List<EventSet> eventSetList = eventSubscriptionsMap.get(messageName);
                if (eventSetList.contains(eventSet)) {
                    throw new IllegalArgumentException("The eventSet is already in use for " +
                            messageName);
                } else {
                    eventSetList.add(eventSet);
                }
            } else {
                List<EventSet> eventSetList = new ArrayList<>();
                eventSetList.add(eventSet);
                eventSubscriptionsMap.put(messageName, eventSetList);
            }
        }
    }

    @Override
    public boolean unsubscribe(final MessageSet messageSet) {
        if (messageSet == null) {
            return false;
        }

        if (messageSet instanceof EventSet) {
            for (List<EventSet> eventSets : eventSubscriptionsMap.values()) {
                if (eventSets.contains(messageSet)) {
                    eventSets.remove(messageSet);
                }
            }
            for (List<EventSet> eventSets : zirkEventSubsciptionsMap.values()) {
                if (eventSets.contains(messageSet)) {
                    eventSets.remove(messageSet);
                }
            }
        }
        proxyServer.unsubscribe(new SubscriptionAction(BezirkAction.ACTION_BEZIRK_UNSUBSCRIBE, zirkId, messageSet));
        return true;
    }

    @Override
    public void sendEvent(Event event) {
        sendEvent(new RecipientSelector(new Location("null/null/null")), event);
    }

    @Override
    public void sendEvent(RecipientSelector recipient, Event event) {
        proxyServer.sendEvent(new SendMulticastEventAction(zirkId, recipient, event,
                event instanceof IdentifiedEvent));
    }

    @Override
    public void sendEvent(ZirkEndPoint recipient, Event event) {
        proxyServer.sendEvent(new UnicastEventAction(BezirkAction.ACTION_ZIRK_SEND_UNICAST_EVENT,
                zirkId, recipient, event, event instanceof IdentifiedEvent));
    }

    @Override
    public void setLocation(Location location) {
        proxyServer.setLocation(new SetLocationAction(zirkId, location));
    }

}

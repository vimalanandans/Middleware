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

import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.java.proxy.messagehandler.BroadcastReceiver;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class ZirkMessageReceiver implements BroadcastReceiver {
    private final Map<ZirkId, Set<EventSet.EventReceiver>> eventMap;
    private final Map<String, Set<EventSet.EventReceiver>> eventListenerMap;

    public ZirkMessageReceiver(@NotNull final Map<ZirkId, Set<EventSet.EventReceiver>> eventMap,
                               @NotNull final Map<String, Set<EventSet.EventReceiver>> eventListenerMap) {
        super();
        this.eventMap = eventMap;
        this.eventListenerMap = eventListenerMap;
    }

    @Override
    public void onReceive(@NotNull final ZirkAction incomingMessage) {
        if (!eventMap.containsKey(incomingMessage.getZirkId())) {
            return;
        }

        if (BezirkAction.ACTION_ZIRK_RECEIVE_EVENT.equals(incomingMessage.getAction())) {
            processEvent((UnicastEventAction) incomingMessage);
        }
    }

    /**
     * Handles the Event Callback Message and gives the callback to the services. It is being invoked from
     * Platform specific BezirkCallback implementation.
     *
     * @param incomingEvent new event to send up to Zirks registered to receive it
     */
    private void processEvent(@NotNull final UnicastEventAction incomingEvent) {
        final Event event = (Event) Event.fromJson(incomingEvent.getSerializedEvent());
        final String eventName = event.getClass().getName();

        if (incomingEvent.isIdentified()) {
            ((IdentifiedEvent) event).setAlias(incomingEvent.getAlias());
            ((IdentifiedEvent) event).setMiddlewareUser(incomingEvent.isMiddlewareUser());
        }

        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();

        final Set<EventSet.EventReceiver> tempEventSet = eventMap.get(incomingEvent.getZirkId());
        final Set<EventSet.EventReceiver> tempMessageSet = eventListenerMap.get(eventName);

        if (tempEventSet != null && tempMessageSet != null) {
            for (EventSet.EventReceiver invokingListener : tempEventSet) {
                if (tempMessageSet.contains(invokingListener)) {
                    invokingListener.receiveEvent(event, endpoint);
                }
            }
        }
    }
}

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bezirk.middleware.core.actions.StreamAction;
import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.core.actions.ZirkAction;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.IdentifiedEvent;
import com.bezirk.middleware.messages.StreamEvent;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.streaming.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ZirkMessageReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ZirkMessageReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        final ZirkAction message;
        try {
            message = (ZirkAction) intent.getSerializableExtra("message");
        } catch (Exception e) {
            // java.io.InvalidClassException is thrown when there is a difference in the serialVersionUID,
            // see http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html
            // As the exception is not thrown by intent.getSerializableExtra(),
            // it is caught using Exception
            logger.warn("Failed to read serialized message from intent", e);
            return;
        }

        if (isValidRequest(message.getZirkId()) && BezirkAction.ACTION_ZIRK_RECEIVE_EVENT.equals(message.getAction())) {
            processEvent((UnicastEventAction) message);
        } else if(BezirkAction.ACTION_ZIRK_RECEIVE_STREAM.equals(message.getAction())){
            processStream((StreamAction) message);
        }else{
            logger.error("Unimplemented action: {}", message.getAction());
        }

    }

    private boolean isValidRequest(ZirkId receivedZirkId) {
        if (ProxyClient.context == null) {
            logger.error("Application is not started");
            return false;
        }

        if (!isRequestForCurrentApp(receivedZirkId.getZirkId())) {
            logger.error("Intent is not for this Service");
            return false;
        }

        return true;
    }

    /**
     * Gives a callback with updated event status to the receiver.
     * @param incomingEvent incoming Unicast event
     */
    private void processEvent(UnicastEventAction incomingEvent) {
        final Event event = (Event) Event.fromJson(incomingEvent.getSerializedEvent());
        final String eventName = event.getClass().getName();

        if (incomingEvent.isIdentified()) {
            ((IdentifiedEvent) event).setAlias(incomingEvent.getAlias());
            ((IdentifiedEvent) event).setMiddlewareUser(incomingEvent.isMiddlewareUser());
        }

        final BezirkZirkEndPoint endpoint = (BezirkZirkEndPoint) incomingEvent.getEndpoint();

        final List<EventSet> subscriptionsForZirk = ProxyClient.zirkEventSubsciptionsMap.get(incomingEvent.getZirkId());
        final List<EventSet> subscriptionsForEvent = ProxyClient.eventSubscriptionsMap.get(eventName);

        if (subscriptionsForZirk != null && subscriptionsForEvent != null) {
            for (EventSet eventSet : subscriptionsForZirk) {
                if (subscriptionsForEvent.contains(eventSet)) {
                    eventSet.getEventReceiver().receiveEvent(event, endpoint);
                }
            }
        }
    }

    /**
     *  Gives a callback with updated stream status to the receiver.
     * @param incomingStreamEvent incoming stream event
     */
    private void processStream(StreamAction incomingStreamEvent){
        final Short streamID = incomingStreamEvent.getStreamId();

        if (ProxyClient.streamSetMap.containsKey(streamID)) {
            final Stream.StreamEventReceiver receiver = ProxyClient.streamSetMap.get(streamID);
            if(receiver != null){
                final StreamEvent streamEvent = new StreamEvent(incomingStreamEvent.getStreamStatus(), incomingStreamEvent.getStreamId());
                receiver.receiveStreamEvent(streamEvent);
            }else{
                logger.error("receiver object is null for stream request in stream map for Stream ID {}", streamID);
            }
        }else{
            logger.error("Stream Map does not contain stream request for Stream ID {}", streamID);
        }

    }

    private boolean isRequestForCurrentApp(final String zirkId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProxyClient.context);
        Map<String, ?> keys = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equalsIgnoreCase(zirkId)) {
                return true;
            }
        }
        return false;
    }
}

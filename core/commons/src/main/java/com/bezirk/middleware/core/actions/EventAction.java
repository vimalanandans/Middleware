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
package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public abstract class EventAction extends ZirkAction {

    private static final long serialVersionUID = 7767357774933115392L;
    private final String serializedEvent;
    private final String messageId;
    private final String eventName;

    public EventAction(ZirkId zirkId, Event event) {
        super(zirkId);

        if (event == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        eventName = event.getClass().getName();
        serializedEvent = event.toJson();
        this.messageId = event.toString();
    }

    public EventAction(ZirkId zirkId, String serializedEvent, String msgId, String eventName) {
        super(zirkId);

        if (serializedEvent == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        this.serializedEvent = serializedEvent;
        this.messageId = msgId;
        this.eventName = eventName;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getEventName() {
        return eventName;
    }
}

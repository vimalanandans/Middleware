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

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class UnicastEventAction extends EventAction {

    private static final long serialVersionUID = -772143748078804070L;
    private final BezirkAction action;
    private final ZirkEndPoint endpoint;
    private boolean isIdentified = false;
    private Alias alias;
    private boolean isMiddlewareUser = false;

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint, Event event) {
        super(zirkId, event);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint, Event event,
                              boolean isIdentified) {
        this(action, zirkId, endpoint, event);

        this.isIdentified = isIdentified;
    }

    public boolean isMiddlewareUser() {
        return isMiddlewareUser;
    }

    public void setMiddlewareUser(boolean middlewareUser) {
        isMiddlewareUser = middlewareUser;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint,
                              String serializedEvent, String messageId, String eventName) {
        super(zirkId, serializedEvent, messageId, eventName);

        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }

        this.action = action;
        this.endpoint = endpoint;
    }

    public UnicastEventAction(BezirkAction action, ZirkId zirkId, ZirkEndPoint endpoint,
                              String serializedEvent, String messageId, String eventName,
                              boolean isIdentified) {
        this(action, zirkId, endpoint, serializedEvent, messageId, eventName);

        this.isIdentified = isIdentified;
    }

    public ZirkEndPoint getEndpoint() {
        return endpoint;
    }

    public boolean isIdentified() {
        return isIdentified;
    }

    public Alias getAlias() {
        return alias;
    }

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    @Override
    public BezirkAction getAction() {
        return action;
    }
}

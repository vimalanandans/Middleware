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

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class SendMulticastEventAction extends EventAction {

    private static final long serialVersionUID = 5208841186626658098L;
    private final RecipientSelector selector;
    private boolean isIdentified = false;
    private Alias alias;

    public SendMulticastEventAction(ZirkId zirkId, RecipientSelector selector, Event event) {
        super(zirkId, event);

        if (selector == null) {
            throw new IllegalArgumentException("Cannot send an event to a null recipient. You " +
                    "probably want to use sendEvent(Event)");
        }

        this.selector = selector;
    }

    public SendMulticastEventAction(ZirkId zirkId, RecipientSelector selector, Event event,
                                    boolean isIdentified) {
        this(zirkId, selector, event);

        this.isIdentified = isIdentified;
    }

    public RecipientSelector getRecipientSelector() {
        return selector;
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
        return BezirkAction.ACTION_ZIRK_SEND_MULTICAST_EVENT;
    }
}

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
package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * This is generic ledger which works only between comms without encryption.
 * this is meant for comms layer diagnostic tests / performance tests etc.
 */
public class MessageLedger implements Ledger {
    private BezirkZirkEndPoint sender;
    private BezirkZirkEndPoint recipient;
    // serialized message
    private String message;

    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    public void setSender(BezirkZirkEndPoint sender) {
        this.sender = sender;
    }

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(BezirkZirkEndPoint recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

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

public class UnicastControlMessage extends ControlMessage {

    private BezirkZirkEndPoint recipient;

    /**
     * Empty Constructor required for gson.fromJson
     */
    public UnicastControlMessage() {
        // Empty Constructor required for gson.fromJson
    }

    /**
     * Used if you want to send a custom key
     * Generally only used with responses
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereId      the sphere-id
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit, String key) {
        super(sender, sphereId, discriminator, retransmit, key);
        this.recipient = recipient;
    }

    /**
     * Used if you want the stack to auto-generate the key
     * Generally only used with requests
     *
     * @param sender        the sender-end-point
     * @param recipient     the recipient-end-point
     * @param sphereId      the sphere-id
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     */
    public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId,
                                 Discriminator discriminator, Boolean retransmit) {
        super(sender, sphereId, discriminator, retransmit);
        this.recipient = recipient;
    }

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

}

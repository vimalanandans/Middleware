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

public class MulticastControlMessage extends ControlMessage {

    /**
     * Empty Constructor required for gson.fromJson
     */
    public MulticastControlMessage() {
        // Empty Constructor required for gson.fromJson
    }

    /**
     * Used if you want to send a custom key
     * Generally only used with responses
     * This constructor may not be used currently: leaving it here for later
     *
     * @param sender        the sender-end-point
     * @param sphereId      This is the sphereId
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    public MulticastControlMessage(BezirkZirkEndPoint sender, String sphereId,
                                   Discriminator discriminator, String key) {
        //Notice last boolean is set to true : This is because all multicasts are retransmitted
        super(sender, sphereId, discriminator, true, key);
    }

    /**
     * Used if you want the stack to auto-generate the key
     * Generally only used with requests
     *
     * @param sender        the sender-end-point
     * @param sphereId      This is the sphereId
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     */
    public MulticastControlMessage(BezirkZirkEndPoint sender, String sphereId,
                                   Discriminator discriminator) {
        //Notice last boolean is set to true : This is because all multicasts are retransmitted
        super(sender, sphereId, discriminator, true);
    }

}

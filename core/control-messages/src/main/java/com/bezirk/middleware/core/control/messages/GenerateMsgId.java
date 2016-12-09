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
 * This class is used to generate messageIds for both Events and Control messages on behalf of the Bezirk Stack
 */
public final class GenerateMsgId {

    private static final String KEY_SEPARATOR = ":";
    private static int evtId = 0;
    private static int ctrlId = 0;

    private GenerateMsgId() {
        //Utility class should have a private constructor
    }

    /**
     * This method will be invoked by ProxyForServices to set the msgId of each of the Event going on the wire.
     *
     * @return the msgId
     * Example - 2:192.168.1.124:abc123423
     */
    public static String generateEvtId(BezirkZirkEndPoint sep) {
        if (1024 == evtId) {
            evtId = 0;
        }
        return (++evtId + KEY_SEPARATOR + sep.device + KEY_SEPARATOR + sep.zirkId.getZirkId());
    }

    /**
     * This method will be invoked by ProxyForServices to set the msgId of each of the Event going on the wire.
     *
     * @return the ctrlId
     */
    public static int generateCtrlId() {
        if (1024 == ctrlId) {
            ctrlId = 0;
        }
        return ++ctrlId;
    }
}

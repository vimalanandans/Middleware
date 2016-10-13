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
package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * Platform independent API's used by PubSubBroker for validating the responses by the control channel.
 */
interface PubSubBrokerControlReceiver {

    /**
     * Checks if the StreamDescriptor is registered by the Zirk zirkId to streamTopic.
     *
     * @param streamName - StreamDescriptor name of the stream Descriptor
     * @param serviceId  - ZirkId of the Zirk
     * @return true if registered, false otherwise.
     */
    boolean isStreamTopicRegistered(final String streamName, final ZirkId serviceId);

    /**
     * Returns the Location of the Zirk.
     *
     * @param serviceId ZirkId of the Zirk whose location needs to be known
     * @return Location if exists, null if the zirk is not registered
     */
    Location getLocationForZirk(final ZirkId serviceId);
}

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
package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import java.util.ArrayList;
import java.util.List;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of messages
 * FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryResponse extends MulticastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.SPHERE_DISCOVERY_RESPONSE;
    private final List<ZirkId> services;

    /**
     * Constructor
     *
     * @param scannedSphereId sphere to be created at the recipient device
     * @param services        Services that need to join the target sphere
     */

    public SphereDiscoveryResponse(String scannedSphereId,
                                   List<ZirkId> services, BezirkZirkEndPoint sender) {
        super(sender, scannedSphereId, discriminator);
        this.services = new ArrayList<>(services);

    }

    /**
     * @return the services
     */
    public final List<ZirkId> getServices() {
        return new ArrayList<>(services);
    }


}

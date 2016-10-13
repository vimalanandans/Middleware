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
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of services to be catched
 */
public class CatchResponse extends MulticastControlMessage {
    private static final ControlMessage.Discriminator discriminator = ControlMessage.Discriminator.CATCH_RESPONSE;
    private BezirkDeviceInfo inviterSphereDeviceInfo;
    private String catcherSphereId;
    private String catcherDeviceId;

    public CatchResponse(BezirkZirkEndPoint sender, String catcherSphereId, String catcherDeviceId,
                         BezirkDeviceInfo inviterSphereDeviceInfo) {
        super(sender, catcherSphereId, discriminator);
        // null checks for sender and catcherSphereId added here because call to the
        // super method has to be the first line in a constructor.
        if (catcherSphereId == null || inviterSphereDeviceInfo == null || catcherDeviceId == null || sender == null) {
            throw new IllegalArgumentException("Paramters of the constructor have to be non-null");
        }
        this.catcherSphereId = catcherSphereId;
        this.inviterSphereDeviceInfo = inviterSphereDeviceInfo;
        this.catcherDeviceId = catcherDeviceId;

    }

    /**
     * @return the services of the scanned sphere as response
     */
    public final BezirkDeviceInfo getInviterSphereDeviceInfo() {
        return inviterSphereDeviceInfo;
    }

    public String getCatcherSphereId() {
        return catcherSphereId;
    }

    public String getCatcherDeviceId() {
        return catcherDeviceId;
    }
}

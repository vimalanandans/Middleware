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

public class ShareRequest extends MulticastControlMessage {

    private static final Discriminator discriminator = ControlMessage.Discriminator.SHARE_REQUEST;
    /**
     * Important note and open point : If we have a owner sphere S [owned by the
     * current device] with local services S1 and S2 and external services S3
     * and S4[in other devices], when we get S to this function, we currently
     * ensure that only S1 and S2 are added since this device only owns these 2
     * services
     * <p>
     * Due to this currently only one BezirkDeviceInfo is required. If we need to
     * extend the concept, we could move towards a List of BezirkDeviceInfo's
     * </p>
     */
    private final BezirkDeviceInfo bezirkDeviceInfo;
    private final String sharerSphereId;

    /**
     * @param shortCode        short code of the device sharing its sphere - Has to be non-null.
     * @param bezirkDeviceInfo - Has to be non-null.
     * @param sender           - Has to be non-null.
     * @param sharerSphereId:  sphereId of the sphere which is sharing its services, required
     *                         in order to complete the process when the SHARE_RESPONSE is
     *                         received. Would not be needed if all the devices with their
     *                         services are sent back to the device requesting to the share
     *                         the services. In order to add the services from the sphereId
     *                         which is sharing the services into the new sphere. Has to be non-null.
     */
    public ShareRequest(String shortCode, BezirkDeviceInfo bezirkDeviceInfo, BezirkZirkEndPoint sender,
                        String sharerSphereId) {
        super(sender, shortCode, discriminator);
        // null checks for sender and shortCode added here because call to the
        // super method has to be the first line in a constructor.
        if (shortCode == null || bezirkDeviceInfo == null || sender == null || sharerSphereId == null) {
            throw new IllegalArgumentException("Paramesters of the constructor have to be non-null");
        }
        this.bezirkDeviceInfo = bezirkDeviceInfo;
        this.sharerSphereId = sharerSphereId;
    }

    /**
     * @return the bezirkDeviceInfo
     */
    public final BezirkDeviceInfo getBezirkDeviceInfo() {
        return bezirkDeviceInfo;
    }

    public String getSharerSphereId() {
        return sharerSphereId;
    }

}

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
 * // new message SphereCatchRequest Control message for sphere control
 * messages/sphere management
 */
public class CatchRequest extends MulticastControlMessage {
    // private sphere sphereInformation;


    private static final ControlMessage.Discriminator discriminator = ControlMessage.Discriminator.CATCH_REQUEST;

    private final BezirkDeviceInfo bezirkDeviceInfo;
    private final String sphereExchangeData; // generate 'catch' sphere qr code
    // as string
    private final String catcherSphereId; // sphere Id which wished to catch the
    // services of temp sphere

    /**
     * @param sender             - has to be non-null
     * @param inviterShortCode   short code of the inviting sphere. Has to be non-null.
     * @param catcherSphereId    sphereId catching the services from the inviting sphere. Has to be non-null.
     * @param bezirkDeviceInfo   zirk and device information of the sphere catching the. Has to be non-null.
     *                           services
     * @param sphereExchangeData sphere information of the sphere catching the services. Has to be non-null.
     */
    public CatchRequest(BezirkZirkEndPoint sender, String inviterShortCode, String catcherSphereId,
                        BezirkDeviceInfo bezirkDeviceInfo, String sphereExchangeData) {
        super(sender, inviterShortCode, discriminator);
        // null checks for sender and inviterShortCode added here because call to the
        // super method has to be the first line in a constructor.
        if (catcherSphereId == null || bezirkDeviceInfo == null || sphereExchangeData == null
                || sender == null || inviterShortCode == null) {
            throw new IllegalArgumentException("Paramters of the constructor have to be non-null");
        }
        this.bezirkDeviceInfo = bezirkDeviceInfo;
        this.sphereExchangeData = sphereExchangeData;
        this.catcherSphereId = catcherSphereId;
    }

    /**
     * @return the sphereVitals as qr code
     */
    public final String getSphereExchangeData() {
        return sphereExchangeData;
    }

    /**
     * @return the services of catching device
     */
    public final BezirkDeviceInfo getBezirkDeviceInfo() {
        return bezirkDeviceInfo;
    }

    /**
     * @return the catching sphere id
     */
    public final String getCatcherSphereId() {
        return catcherSphereId;
    }
}

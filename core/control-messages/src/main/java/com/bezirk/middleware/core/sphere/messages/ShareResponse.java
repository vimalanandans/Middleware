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
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

public class ShareResponse extends UnicastControlMessage {

    private static final Discriminator discriminator = ControlMessage.Discriminator.SHARE_RESPONSE;
    private final BezirkDeviceInfo bezirkDeviceInfo;
    private final String sphereExchangeDataString;
    private final String sharerSphereId;

    /**
     * @param sender                    - Has to be non-null.
     * @param recipient-                Has to be non-null.
     * @param uniqueKey-                Has to be non-null.
     * @param shareCode-                Has to be non-null.
     * @param bezirkDeviceInfo-            Has to be non-null.
     * @param sphereExchangeDataString- Has to be non-null.
     * @param sharerSphereId-           Has to be non-null.
     */
    public ShareResponse(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String uniqueKey, String shareCode,
                         BezirkDeviceInfo bezirkDeviceInfo, String sphereExchangeDataString, String sharerSphereId) {
        super(sender, recipient, shareCode, discriminator, true, uniqueKey);
        // null checks for sender, recipient, shareCode and uniqueKey added here because call to the
        // super method has to be the first line in a constructor.
        if (bezirkDeviceInfo == null || sphereExchangeDataString == null || sharerSphereId == null
                || sender == null || recipient == null || shareCode == null || uniqueKey == null) {
            throw new IllegalArgumentException("Parameters of the constructor have to be non-null");
        }
        this.bezirkDeviceInfo = bezirkDeviceInfo;
        this.sphereExchangeDataString = sphereExchangeDataString;
        this.sharerSphereId = sharerSphereId;
    }

    /**
     * @return the sphereVitals as qr code
     */
    public final String getSphereExchangeDataString() {
        return sphereExchangeDataString;
    }

    /**
     * @return the services of catching device
     */
    public final BezirkDeviceInfo getBezirkDeviceInfo() {
        return bezirkDeviceInfo;
    }

    public String getSharerSphereId() {
        return sharerSphereId;
    }

}

/**
 *
 */
package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;

/**
 * @author rishabh
 */
public class ShareResponse extends UnicastControlMessage {

    private final static Discriminator discriminator = ControlMessage.Discriminator.ShareResponse;
    private final UhuDeviceInfo uhuDeviceInfo;
    private final String sphereExchangeDataString;
    private final String sharerSphereId;

    /**
     * @param sender                    - Has to be non-null.
     * @param recipient-                Has to be non-null.
     * @param uniqueKey-                Has to be non-null.
     * @param shareCode-                Has to be non-null.
     * @param uhuDeviceInfo-            Has to be non-null.
     * @param sphereExchangeDataString- Has to be non-null.
     * @param sharerSphereId-           Has to be non-null.
     */
    public ShareResponse(UhuZirkEndPoint sender, UhuZirkEndPoint recipient, String uniqueKey, String shareCode,
                         UhuDeviceInfo uhuDeviceInfo, String sphereExchangeDataString, String sharerSphereId) {
        super(sender, recipient, shareCode, discriminator, true, uniqueKey);
        // null checks for sender, recipient, shareCode and uniqueKey added here because call to the
        // super method has to be the first line in a constructor.
        if (uhuDeviceInfo == null || sphereExchangeDataString == null || sharerSphereId == null
                || sender == null || recipient == null || shareCode == null || uniqueKey == null) {
            throw new IllegalArgumentException("Paramters of the constructor have to be non-null");
        }
        this.uhuDeviceInfo = uhuDeviceInfo;
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
    public final UhuDeviceInfo getUhuDeviceInfo() {
        return uhuDeviceInfo;
    }

    public String getSharerSphereId() {
        return sharerSphereId;
    }

}

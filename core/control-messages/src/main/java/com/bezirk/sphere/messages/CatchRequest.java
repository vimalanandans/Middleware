package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

/**
 * // new message SphereCatchRequest Control message for Sphere control
 * messages/Sphere management
 */
public class CatchRequest extends com.bezirk.control.messages.MulticastControlMessage {
    // private Sphere sphereInformation;


    private final static ControlMessage.Discriminator discriminator = ControlMessage.Discriminator.CatchRequest;

    private final UhuDeviceInfo uhuDeviceInfo;
    private final String sphereExchangeData; // generate 'catch' sphere qr code
    // as string
    private final String catcherSphereId; // Sphere Id which wished to catch the
    // services of temp sphere

    /**
     * @param sender             - has to be non-null
     * @param inviterShortCode   short code of the inviting sphere. Has to be non-null.
     * @param catcherSphereId    sphereId catching the services from the inviting sphere. Has to be non-null.
     * @param uhuDeviceInfo      service and device information of the sphere catching the. Has to be non-null.
     *                           services
     * @param sphereExchangeData sphere information of the sphere catching the services. Has to be non-null.
     */
    public CatchRequest(UhuServiceEndPoint sender, String inviterShortCode, String catcherSphereId,
                        UhuDeviceInfo uhuDeviceInfo, String sphereExchangeData) {
        super(sender, inviterShortCode, discriminator);
        // null checks for sender and inviterShortCode added here because call to the
        // super method has to be the first line in a constructor.
        if (catcherSphereId == null || uhuDeviceInfo == null || sphereExchangeData == null
                || sender == null || inviterShortCode == null) {
            throw new IllegalArgumentException("Paramters of the constructor have to be non-null");
        }
        this.uhuDeviceInfo = uhuDeviceInfo;
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
    public final UhuDeviceInfo getUhuDeviceInfo() {
        return uhuDeviceInfo;
    }

    /**
     * @return the catching sphere id
     */
    public final String getCatcherSphereId() {
        return catcherSphereId;
    }
}

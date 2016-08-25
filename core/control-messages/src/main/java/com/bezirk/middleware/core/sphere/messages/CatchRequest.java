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


    private final static ControlMessage.Discriminator discriminator = ControlMessage.Discriminator.CatchRequest;

    private final BezirkDeviceInfo bezirkDeviceInfo;
    private final String sphereExchangeData; // generate 'catch' sphere qr code
    // as string
    private final String catcherSphereId; // sphere Id which wished to catch the
    // services of temp sphere

    /**
     * @param sender             - has to be non-null
     * @param inviterShortCode   short code of the inviting sphere. Has to be non-null.
     * @param catcherSphereId    sphereId catching the services from the inviting sphere. Has to be non-null.
     * @param bezirkDeviceInfo      zirk and device information of the sphere catching the. Has to be non-null.
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

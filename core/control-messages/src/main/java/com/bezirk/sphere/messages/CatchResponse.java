package com.bezirk.sphere.messages;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of services to be catched
 */
public class CatchResponse extends com.bezirk.control.messages.MulticastControlMessage {
    private final static com.bezirk.control.messages.ControlMessage.Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.CatchResponse;
    private BezirkDeviceInfo inviterSphereDeviceInfo;
    private String catcherSphereId;
    private String catcherDeviceId;

    /**
     * Constructor
     *
     * @param sphereID          - Has to be non-null.
     *                          Join sphere where the scanned services to be added
     * @param sphereInformation Information about the sphere to be joined. Has to be non-null.
     * @param services          Services that need to join the target sphere. Has to be non-null.
     */

    public CatchResponse(BezirkZirkEndPoint sender, String catcherSphereId, String catcherDeviceId, BezirkDeviceInfo inviterSphereDeviceInfo) {
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

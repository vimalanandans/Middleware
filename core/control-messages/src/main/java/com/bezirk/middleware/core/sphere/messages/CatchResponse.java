package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of services to be catched
 */
public class CatchResponse extends MulticastControlMessage {
    private final static ControlMessage.Discriminator discriminator = ControlMessage.Discriminator.CATCH_RESPONSE;
    private BezirkDeviceInfo inviterSphereDeviceInfo;
    private String catcherSphereId;
    private String catcherDeviceId;

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

/**
 *
 */
package com.bezirk.control.messages.discovery;

import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezrik.network.UhuNetworkUtilities;

/**
 * @author Rishabh Gulati
 */
public final class SphereDiscoveryResponse extends com.bezirk.control.messages.UnicastControlMessage {

    private static final Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.SphereDiscoveryResponse;
    //ZirkEndPoint for this response only contains deviceID
    private static final BezirkZirkEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(null);
    private final Integer reqDiscoveryId;
    private UhuSphereInfo uhuSphereInfo;


    public SphereDiscoveryResponse(BezirkZirkEndPoint recipient, String sphereId, String reqKey, int discId) {
        super(sender, recipient, sphereId, discriminator, false, reqKey);
        //TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;
    }


    /**
     * @return the uhuSphereInfo
     */
    public final UhuSphereInfo getUhuSphereInfo() {
        return uhuSphereInfo;
    }


    /**
     * @param uhuSphereInfo the uhuSphereInfo to set
     */
    public final void setUhuSphereInfo(UhuSphereInfo uhuSphereInfo) {
        this.uhuSphereInfo = uhuSphereInfo;
    }


    public Integer getReqDiscoveryId() {
        return reqDiscoveryId;
    }

}

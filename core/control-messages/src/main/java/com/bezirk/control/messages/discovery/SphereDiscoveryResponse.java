/**
 *
 */
package com.bezirk.control.messages.discovery;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.network.BezirkNetworkUtilities;

/**
 * @author Rishabh Gulati
 */
public final class SphereDiscoveryResponse extends com.bezirk.control.messages.UnicastControlMessage {

    private static final Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.SphereDiscoveryResponse;
    //ZirkEndPoint for this response only contains deviceID
    private static final BezirkZirkEndPoint sender = BezirkNetworkUtilities.getServiceEndPoint(null);
    private final Integer reqDiscoveryId;
    private BezirkSphereInfo bezirkSphereInfo;


    public SphereDiscoveryResponse(BezirkZirkEndPoint recipient, String sphereId, String reqKey, int discId) {
        super(sender, recipient, sphereId, discriminator, false, reqKey);
        //TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;
    }


    /**
     * @return the bezirkSphereInfo
     */
    public final BezirkSphereInfo getBezirkSphereInfo() {
        return bezirkSphereInfo;
    }


    /**
     * @param bezirkSphereInfo the bezirkSphereInfo to set
     */
    public final void setBezirkSphereInfo(BezirkSphereInfo bezirkSphereInfo) {
        this.bezirkSphereInfo = bezirkSphereInfo;
    }


    public Integer getReqDiscoveryId() {
        return reqDiscoveryId;
    }

}

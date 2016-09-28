package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * This message is used to respond to a SignedControlMessage(discriminator : BEZIRK_SPHERE_LEAVE)
 *
 * @author Rishab Gulati
 */
public class MemberLeaveResponse extends UnicastControlMessage {
    //private final int requestId;
    private static final Discriminator discriminator = ControlMessage.Discriminator.MEMBER_LEAVE_RESPONSE;
    //private final BezirkZirkEndPoint recipient; //initiator (owner of the sphere)
    private final boolean signatureVerified;
    private final boolean removedSuccessfully;
    private final ZirkId serviceId;
    private final String sphere_Name;

    /**
     * Used for sending unicast response back to the requesting member
     */
    public MemberLeaveResponse(String sphereID, int requestId, boolean signatureVerified, boolean removedSuccessfully, BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, ZirkId serviceId, String sphere_Name, String key) {

        super(sender, recipient, sphereID, discriminator, false, key);
        //public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereName,
        //String discriminator, Boolean retransmit, String key){
        this.signatureVerified = signatureVerified;
        this.removedSuccessfully = removedSuccessfully;
        this.serviceId = serviceId;
        this.sphere_Name = sphere_Name;
        //this.requestId = requestId;
    }

    public boolean isRemovedSuccessfully() {
        return removedSuccessfully;
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

    public String getSphere_Name() {
        return sphere_Name;
    }

    public boolean isSignatureVerified() {
        return signatureVerified;
    }

//    public int getRequestId() {
//        return requestId;
//    }

}

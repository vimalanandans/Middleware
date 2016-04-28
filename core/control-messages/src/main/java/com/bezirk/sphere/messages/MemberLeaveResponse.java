package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

/**
 * This message is used to respond to a SignedControlMessage(discriminator : BezirkSphereLeave)
 *
 * @author Rishab Gulati
 */
public class MemberLeaveResponse extends UnicastControlMessage {
    //private final int requestId;
    private final static Discriminator discriminator = ControlMessage.Discriminator.MemberLeaveResponse;
    //private final BezirkZirkEndPoint recipient; //initiator (owner of the sphere)
    private final boolean signatureVerified;
    private final boolean removedSuccessfully;
    private final BezirkZirkId serviceId;
    private final String sphere_Name;

    /**
     * Used for sending unicast response back to the requesting member
     *
     * @param sphereID
     * @param requestId
     * @param signatureVerified
     * @param removedSuccessfully
     * @param recipient
     * @param serviceId
     * @param sphere_Name
     */
    public MemberLeaveResponse(String sphereID, int requestId, boolean signatureVerified, boolean removedSuccessfully, BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, BezirkZirkId serviceId, String sphere_Name, String key) {

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

    public BezirkZirkId getServiceId() {
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

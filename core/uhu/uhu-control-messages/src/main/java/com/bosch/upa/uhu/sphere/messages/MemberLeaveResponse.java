package com.bosch.upa.uhu.sphere.messages;

import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * Created by GUR1PI on 8/6/2014.
 */

/**
 * This message is used to respond to a SignedControlMessage(discriminator : UhuSphereLeave)
 */
public class MemberLeaveResponse extends UnicastControlMessage{
    //private final UhuServiceEndPoint recipient; //initiator (owner of the sphere)
    private final boolean signatureVerified;
    private final boolean removedSuccessfully;
    private final UhuServiceId serviceId;
    private final String sphere_Name;
    //private final int requestId;
	private final static Discriminator discriminator = ControlMessage.Discriminator.MemberLeaveResponse;
    
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
    public MemberLeaveResponse(String sphereID, int requestId, boolean signatureVerified, boolean removedSuccessfully, UhuServiceEndPoint sender, UhuServiceEndPoint recipient, UhuServiceId serviceId, String sphere_Name, String key){
    	
        super(sender, recipient, sphereID, discriminator, false, key);
        //public UnicastControlMessage(UhuServiceEndPoint sender, UhuServiceEndPoint recipient, String sphereName,
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
    
    public UhuServiceId getServiceId() {
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

package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * Created by GUR1PI on 8/6/2014.
 */

/**
 * This message is used to initiate 'leave' from a member of the sphere owning a service it shared with owner of the same sphere
 */
public class MemberLeaveRequest extends MulticastControlMessage{
    private final UhuServiceId serviceId;
    //private final String sphere_Name;
	private final static Discriminator discriminator = ControlMessage.Discriminator.MemberLeaveRequest;
    
    /**
     * Used with for multicasting the leave request by the member of a sphere
     * @param sphereID
     * @param serviceId
     * @param sphere_Name
     * @param sender
     */
    public MemberLeaveRequest(String sphereID, UhuServiceId serviceId, String sphere_Name, UhuServiceEndPoint sender){        
    	super(sender, sphereID, discriminator);
        this.serviceId = serviceId;
        //this.sphere_Name = sphere_Name;        
    }

    public UhuServiceId getServiceId() {
        return serviceId;
    }

//    public String getSphere_Name() {
//        return sphere_Name;
//    }

}

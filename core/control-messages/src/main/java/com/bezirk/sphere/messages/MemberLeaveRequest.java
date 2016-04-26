package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

/**
 * This message is used to initiate 'leave' from a member of the sphere owning a service it shared
 * with owner of the same sphere
 *
 * @author Rishab Gulati
 */
public class MemberLeaveRequest extends MulticastControlMessage {
    //private final String sphere_Name;
    private final static Discriminator discriminator = ControlMessage.Discriminator.MemberLeaveRequest;
    private final UhuZirkId serviceId;

    /**
     * Used with for multicasting the leave request by the member of a sphere
     *
     * @param sphereID
     * @param serviceId
     * @param sphere_Name
     * @param sender
     */
    public MemberLeaveRequest(String sphereID, UhuZirkId serviceId, String sphere_Name, UhuZirkEndPoint sender) {
        super(sender, sphereID, discriminator);
        this.serviceId = serviceId;
        //this.sphere_Name = sphere_Name;        
    }

    public UhuZirkId getServiceId() {
        return serviceId;
    }

//    public String getSphere_Name() {
//        return sphere_Name;
//    }

}

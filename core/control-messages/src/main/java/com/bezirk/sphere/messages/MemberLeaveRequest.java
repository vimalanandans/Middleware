package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

/**
 * This message is used to initiate 'leave' from a member of the sphere owning a zirk it shared
 * with owner of the same sphere
 *
 * @author Rishab Gulati
 */
public class MemberLeaveRequest extends MulticastControlMessage {
    //private final String sphere_Name;
    private final static Discriminator discriminator = ControlMessage.Discriminator.MemberLeaveRequest;
    private final ZirkId serviceId;

    /**
     * Used with for multicasting the leave request by the member of a sphere
     */
    public MemberLeaveRequest(String sphereID, ZirkId serviceId, String sphere_Name, BezirkZirkEndPoint sender) {
        super(sender, sphereID, discriminator);
        this.serviceId = serviceId;
        //this.sphere_Name = sphere_Name;        
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

//    public String getSphere_Name() {
//        return sphere_Name;
//    }

}

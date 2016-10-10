package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * This message is used to initiate 'leave' from a member of the sphere owning a zirk it shared
 * with owner of the same sphere
 *
 * @author Rishab Gulati
 */
public class MemberLeaveRequest extends MulticastControlMessage {
    //private final String sphere_Name;
    private static final Discriminator discriminator = ControlMessage.Discriminator.MEMBER_LEAVE_REQUEST;
    private final ZirkId serviceId;

    /**
     * Used with for multicasting the leave request by the member of a sphere
     */
    public MemberLeaveRequest(String sphereID, ZirkId serviceId, String sphereName, BezirkZirkEndPoint sender) {
        super(sender, sphereID, discriminator);
        this.serviceId = serviceId;
        //this.sphereName = sphereName;
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

//    public String getSphereName() {
//        return sphereName;
//    }

}

package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

/**
 * This control message is issued by owners to ask other services (mainly members) to leave
 */
public class BezirkSphereLeave extends UnicastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.BEZIRK_SPHERE_LEAVE;
    private final ZirkId serviceId;
    private final long time;

    /**
     * Used for sending leave request to a member
     */
    public BezirkSphereLeave(String sphereID, ZirkId serviceId, BezirkZirkEndPoint sender,
                             BezirkZirkEndPoint recipient) {
        super(sender, recipient, sphereID, discriminator, true);
        //public UnicastControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereName,
        //String discriminator, Boolean retransmit, String key){
        this.serviceId = serviceId;
        this.time = System.currentTimeMillis();
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to fromJson into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return gson.fromJson(json, dC);
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

    public long getTime() {
        return time;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}

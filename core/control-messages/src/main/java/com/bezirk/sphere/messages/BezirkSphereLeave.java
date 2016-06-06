package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

/**
 * This control message is issued by owners to ask other services (mainly members) to leave
 *
 * @author Rishabh Gulati
 */
public class BezirkSphereLeave extends com.bezirk.control.messages.UnicastControlMessage {
    private final static Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.BezirkSphereLeave;
    private final ZirkId serviceId;
    private final long time;

    /**
     * Used for sending leave request to a member
     *
     * @param sphereID
     * @param serviceId
     * @param recipient
     */
    public BezirkSphereLeave(String sphereID, ZirkId serviceId, BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient) {
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

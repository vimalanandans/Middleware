package com.bosch.upa.uhu.sphere.messages;

import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

/**
 * This control message is issued by owners to ask other services (mainly members) to leave
 * @author Rishabh Gulati
 *
 */
public class UhuSphereLeave extends UnicastControlMessage {
    private final UhuServiceId serviceId;
    private final long time;
	private final static Discriminator discriminator = ControlMessage.Discriminator.UhuSphereLeave;

    /**
     * Used for sending leave request to a member
     * @param sphereID
     * @param serviceId
     * @param recipient
     */
    public UhuSphereLeave(String sphereID, UhuServiceId serviceId, UhuServiceEndPoint sender, UhuServiceEndPoint recipient){
        super(sender, recipient, sphereID, discriminator, true);
        //public UnicastControlMessage(UhuServiceEndPoint sender, UhuServiceEndPoint recipient, String sphereName,
		//String discriminator, Boolean retransmit, String key){
        this.serviceId = serviceId;
        this.time = System.currentTimeMillis();
    }
    
    public UhuServiceId getServiceId() {
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

    /**     *
     * @param json The Json String that is to be deserialized
     * @param dC class to deserialize into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, dC);
    }


}

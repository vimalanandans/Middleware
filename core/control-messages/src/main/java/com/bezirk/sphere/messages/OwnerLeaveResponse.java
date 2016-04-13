package com.bezirk.sphere.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;


/**
 * Created by GUR1PI on 8/3/2014.
 */
//TODO Move to new package, since this is an object used for encapsulating information to be sent as a signed messages. The control message which uses this object is SignedControlMessage.

public class OwnerLeaveResponse {
    //private final UhuServiceEndPoint recipient; //TODO check if redundant field is actually needed for further verification of the signed message
    private final boolean removedSuccessfully;
    private final String sphereID; //TODO check if redundant field is actually needed for further verification of the signed message
    private final UhuServiceId serviceId;
    private final long time;

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerLeaveResponse.class);

    public OwnerLeaveResponse(String sphereID, UhuServiceId serviceId, UhuServiceEndPoint recipient, boolean removedSuccessfully){
        this.sphereID = sphereID;
        this.serviceId = serviceId;
        //this.recipient = recipient;
        this.removedSuccessfully = removedSuccessfully;
        this.time = System.currentTimeMillis();
        LOGGER.debug("Time when message was created : "+time);
    }

    public String getSphereID() {
        return sphereID;
    }

    public UhuServiceId getServiceId() {
        return serviceId;
    }

    public long getTime() {
        return time;
    }

//    public UhuServiceEndPoint getRecipient() {
//        return recipient;
//    }

    public boolean isRemovedSuccessfully() {
        return removedSuccessfully;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     *
     * @param json The Json String that is to be deserialized
     * @param C class to deserialize into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return gson.fromJson(json, dC);
    }


}
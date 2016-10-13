package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Move to new package, since this is an object used for encapsulating information to be sent as a
// signed messages. The control message which uses this object is SignedControlMessage.

public class OwnerLeaveResponse {
    private static final Logger logger = LoggerFactory.getLogger(OwnerLeaveResponse.class);

    //TODO check if redundant field is actually needed for further verification of the signed message
    //private final BezirkZirkEndPoint recipient;
    private final boolean removedSuccessfully;
    //TODO check if redundant field is actually needed for further verification of the signed message
    private final String sphereID;
    private final ZirkId serviceId;
    private final long time;

    public OwnerLeaveResponse(String sphereID, ZirkId serviceId, BezirkZirkEndPoint recipient,
                              boolean removedSuccessfully) {
        this.sphereID = sphereID;
        this.serviceId = serviceId;
        //this.recipient = recipient;
        this.removedSuccessfully = removedSuccessfully;
        this.time = System.currentTimeMillis();

        if (logger.isDebugEnabled()) logger.debug("Time when message was created : {}", time);
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC    class to fromJson into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return gson.fromJson(json, dC);
    }

    public String getSphereID() {
        return sphereID;
    }

    public ZirkId getServiceId() {
        return serviceId;
    }

    public long getTime() {
        return time;
    }

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


}

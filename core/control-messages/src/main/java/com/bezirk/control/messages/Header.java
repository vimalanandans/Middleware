package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;

/**
 * Header contains information needed by comms to route messages
 */
public class Header {
    // Open Fields
    private String sphereName = null; //Don't touch
    private BezirkZirkEndPoint senderSEP = null; // Change to ZirkEndPoint sender
    private String uniqueMsgId = null;
    private String topic = null; //Get topic(=label) from Event

    public Header() {
    }

    public Header(String sphereName, BezirkZirkEndPoint senderSEP, String uniqueMsgId, String topic) {
        this.sphereName = sphereName;
        this.senderSEP = senderSEP;
        this.uniqueMsgId = uniqueMsgId;
        this.topic = topic;
    }

    /**
     * @return the messageId which is generated per message sent. Every message is assigned with a unique identifier
     */
    public String getUniqueMsgId() {
        return uniqueMsgId;
    }

    /**
     * @param messageId the messageId which is generated per message sent. Every message is assigned with a unique identifier
     */
    public void setUniqueMsgId(String messageId) {
        this.uniqueMsgId = messageId;
    }

    /**
     * @return the name of the sphere
     */
    public String getSphereName() {
        return sphereName;
    }

    /**
     * @param sphereName the name of the sphere
     */
    public void setSphereName(String sphereName) {
        this.sphereName = sphereName;
    }

    /**
     * @return the senderId of the message
     */
    public BezirkZirkEndPoint getSenderSEP() {
        return senderSEP;
    }

    /**
     * @param senderSEP the senderId of the message. Usually there is a function that retrieves
     *                  the hostId and this is used to set the senderId
     */
    public void setSenderSEP(BezirkZirkEndPoint senderSEP) {
        this.senderSEP = senderSEP;
    }

    /**
     * @return messageType (Eg:sphereJoin, discovery,..) or messageLabel (Event labels)
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @param msgTypeOrLabel messageType (Eg:sphereJoin, discovery,..) or messageLabel (Event labels)
     */
    public void setTopic(String msgTypeOrLabel) {
        this.topic = msgTypeOrLabel;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}

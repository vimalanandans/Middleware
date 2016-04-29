/**
 * @author Vijet Badigannavar (bvijet@in.bosch.com)
 * @modified 2/17/2015
 */
package com.bezirk.remotelogging;

import com.google.gson.Gson;

/**
 * This class defines the Bezirk Logging Message that are used by the Platforms for Remote Logging.
 */
public class BezirkLoggingMessage {
    /**
     * sphere Name on which the Remote Logging Message is sent.
     */
    public String sphereName = null;
    /**
     * Unique key associated with the Message
     */
    public String timeStamp = null;
    /**
     * Sender of the Log message
     */
    public String sender = null;
    /**
     * Recipient of the Log Message
     */
    public String recipient = null;
    /**
     * Unique Id that is for each message that is sent or received
     */
    public String uniqueMsgId = null;
    /**
     * Topic of the message i,e is sent
     */
    public String topic = null;
    /**
     * Type of a Message (EVENT-SEND, EVENT-RECEIVE, CONTROL-MESSAGE-SEND, CONTROL-MESSAGE-RECEIVE)
     */
    public String typeOfMessage = null;
    /**
     * Version of the Logging Message
     */
    public String version = null;

    public BezirkLoggingMessage() {
        //	 Empty Constructor used by Json to toJson Deserialize
    }

    /**
     * Construct the Logging Message to be sent on the wire. This will be called in BezirkCommsSend,
     * EventListenerUtility,ControlListenerUtility
     *
     * @param sphereName    Name of the sphere
     * @param uniqueKey     MsgId of the Message
     * @param sender        Sender IP
     * @param recipient     Recipient Ip, null in case of multicast
     * @param uniqueMsgId   MsgId of the message that is sent or received
     * @param topic         topic of the Message, discriminator in case of Control Message
     * @param typeOfMessage one of (EVENT-SEND, EVENT-RECEIVE, CONTROL-MESSAGE-SEND, CONTROL-MESSAGE-RECEIVE)
     * @param version       Version of the Logging Message
     */
    public BezirkLoggingMessage(String sphereName, String timeStamp,
                                String sender, String recipient, String uniqueMsgId, String topic,
                                String typeOfMessage, String version) {
        super();
        this.sphereName = sphereName;
        this.timeStamp = timeStamp;
        this.sender = sender;
        this.recipient = recipient;
        this.uniqueMsgId = uniqueMsgId;
        this.topic = topic;
        this.typeOfMessage = typeOfMessage;
        this.version = version;
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to fromJson into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return gson.fromJson(json, dC);
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}

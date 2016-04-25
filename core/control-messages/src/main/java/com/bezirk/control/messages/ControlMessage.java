package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.google.gson.Gson;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         <p/>
 *         This is the super-class for all control messages.
 *         <p/>
 *         For example: {@link com.bezirk.control.messages.discovery.DiscoveryRequest}, {@link com.bezirk.control.messages.discovery.DiscoveryResponse}, {@link com.bezirk.sphere.UhuSphereMessage} are subclasses of {@link ControlMessage}
 */
public class ControlMessage {
    private String sphereId = "";
    private Integer messageId = new Integer(-1); //message Id check if used
    private Discriminator discriminator;
    private String uniqueKey = "";
    private Boolean retransmit = true;
    private UhuServiceEndPoint sender;
    public ControlMessage() {
        // Empty Constructor required for gson.fromJSON
    }

    /**
     * This constructor is used if you want to explicitly parse the UniqueKey
     *
     * @param sender        the sender-end-point
     * @param sphereName    the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    {@value true} if the message is to be re-transmitted
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    protected ControlMessage(UhuServiceEndPoint sender, String sphereId,
                             Discriminator discriminator, Boolean retransmit, String key) {
        this.sender = sender;
        this.sphereId = sphereId;
        this.messageId = GenerateMsgId.generateCtrlId();
        this.discriminator = discriminator;
        this.uniqueKey = key;
        this.retransmit = retransmit;
    }

    /**
     * This constructor is used when you want to auto-generate the uniqueKey
     *
     * @param sender        the sender-end-point
     * @param sphereName    the sphere-id
     * @param discriminator the message discriminator Eg: DiscoveryRequest, StreamResponse
     * @param retransmit    {@value true} if the message is to be re-transmitted
     */
    protected ControlMessage(UhuServiceEndPoint sender, String sphereId,
                             Discriminator discriminator, Boolean retransmit) {
        this.sender = sender;
        this.sphereId = sphereId;
        this.messageId = GenerateMsgId.generateCtrlId();
        this.discriminator = discriminator;
        this.retransmit = retransmit;
        //Auto-Generate Unique Key
        this.uniqueKey = sender.device + ":" + sphereId + ":" + this.messageId;
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to fromJSON into
     * @return object of class type C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, dC);
    }

    /**
     * General norm for this field is
     * For requests: true
     * For responses: false
     *
     * @return true if msg is to be retransmitted
     */
    public Boolean getRetransmit() {
        return retransmit;
    }

    /**
     * For requests: this key must be used to Hash Requests that are pending and also to map duplicates
     * For responses: this key must be directly borrowed from Requests and sent back
     *
     * @return Unique key to identify a specific control message instance
     */
    public String getUniqueKey() {
        return uniqueKey;
    }

    /**
     * @return the Id of the sphere
     */
    public String getSphereId() {
        return sphereId;
    }

    /**
     * @return the message id
     */
    public Integer getMessageId() {
        return messageId;
    }


    /**
     * The discriminator is of type String and is used to distinguish between control Messages such as Discovery, Sphere , Streaming etc.
     *
     * @return the control Message discriminator
     */
    public Discriminator getDiscriminator() {
        return discriminator;
    }


    /**
     * Returns the Sender of the Control Message
     *
     * @return ServiceEndPoint of the Sender
     */
    public UhuServiceEndPoint getSender() {
        return sender;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public enum Discriminator {
        DiscoveryRequest, DiscoveryResponse, MemberLeaveRequest, MemberLeaveResponse,
        OwnerLeaveResponse, UhuSphereLeave, ShareResponse, CatchRequest,
        CatchResponse, ShareRequest,
        StreamRequest, StreamResponse, SphereDiscoveryRequest, SphereDiscoveryResponse,
        LoggingServiceMessage, RTCControlMessage,
        MaxCtrlMsgId // add the new command before MaxCtrlMsgId
    }
}

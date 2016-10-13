/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;

/**
 * This is the super-class for all control messages.
 */
public class ControlMessage {
    private String sphereId = "";
    private Integer messageId = -1; //message Id check if used
    private Discriminator discriminator;
    private String uniqueKey = "";
   // private Boolean retransmit = true;
    private BezirkZirkEndPoint sender;

    public ControlMessage() {
        // Empty Constructor required for gson.fromJson
    }

    /**
     * This constructor is used if you want to explicitly parse the UniqueKey
     *
     * @param sender        the sender-end-point
     * @param sphereId    the sphere-id
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     * @param key           UniqueKey that is used to match responses to corresponding requests
     */
    protected ControlMessage(BezirkZirkEndPoint sender, String sphereId,
                             Discriminator discriminator, Boolean retransmit, String key) {
        this.sender = sender;
        this.sphereId = sphereId;
        this.messageId = GenerateMsgId.generateCtrlId();
        this.discriminator = discriminator;
        this.uniqueKey = key;
     //   this.retransmit = retransmit;
    }

    /**
     * This constructor is used when you want to auto-generate the uniqueKey
     *
     * @param sender        the sender-end-point
     * @param sphereId    the sphere-id
     * @param discriminator the message discriminator Eg: DISCOVERY_REQUEST, STREAM_RESPONSE
     * @param retransmit    <code>true</code> if the message is to be re-transmitted
     */
    protected ControlMessage(BezirkZirkEndPoint sender, String sphereId,
                             Discriminator discriminator, Boolean retransmit) {
        this.sender = sender;
        this.sphereId = sphereId;
        this.messageId = GenerateMsgId.generateCtrlId();
        this.discriminator = discriminator;
    //    this.retransmit = retransmit;
        //Auto-Generate Unique Key
        this.uniqueKey = sender.device + ":" + sphereId + ":" + this.messageId;
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

    /**
     * General norm for this field is
     * For requests: true
     * For responses: false
     *
     * @return true if msg is to be retransmitted
     */
//    public Boolean getRetransmit() {
//        return retransmit;
//    }

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
     * The discriminator is of type String and is used to distinguish between control Messages
     * such as Discovery, sphere , Streaming etc.
     *
     * @return the control Message discriminator
     */
    public Discriminator getDiscriminator() {
        return discriminator;
    }


    /**
     * Returns the Sender of the Control Message
     *
     * @return ZirkEndPoint of the Sender
     */
    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    public void setSphereId(String sphereId) {
        this.sphereId = sphereId;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public enum Discriminator {
        DISCOVERY_REQUEST, DISCOVERY_RESPONSE, MEMBER_LEAVE_REQUEST, MEMBER_LEAVE_RESPONSE,
        OWNER_LEAVE_RESPONSE, BEZIRK_SPHERE_LEAVE, SHARE_RESPONSE, CATCH_REQUEST,
        CATCH_RESPONSE, SHARE_REQUEST,
        STREAM_REQUEST, STREAM_RESPONSE, SPHERE_DISCOVERY_REQUEST, SPHERE_DISCOVERY_RESPONSE,
        LOGGING_SERVICE_MESSAGE, RTC_CONTROL_MESSAGE,
        MAX_CTRL_MSG_ID // add the new command before MAX_CTRL_MSG_ID
    }
}

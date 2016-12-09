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
package com.bezirk.middleware.core.remotelogging;

import com.google.gson.Gson;

/**
 * This class defines the Remote Logging Message that are used by the Platforms for Remote Logging.
 */
public class RemoteLoggingMessage {
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
     * Type of a Message (EVENT-SEND, EVENT-RECEIVE, CONTROL-MESSAGE-SEND, CONTROL-MESSAGE-RECEIVE)
     */
    public String typeOfMessage = null;
    /**
     * Version of the Logging Message
     */
    public String version = null;

    public RemoteLoggingMessage() {
        //	 Empty Constructor used by Json to toJson Deserialize
    }

    /**
     * Construct the Logging Message to be sent on the wire. This will be called in BezirkCommsSend,
     * EventListenerUtility,ControlListenerUtility
     *
     * @param sphereName    Name of the sphere
     * @param timeStamp     timeStamp of the Message
     *                      // * @param sender        Sender IP
     * @param recipient     Recipient Ip, null in case of multicast
     * @param uniqueMsgId   MsgId of the message that is sent or received
     * @param typeOfMessage one of (EVENT-SEND, EVENT-RECEIVE, CONTROL-MESSAGE-SEND, CONTROL-MESSAGE-RECEIVE)
     * @param version       Version of the Logging Message
     */
    public RemoteLoggingMessage(String sphereName, String timeStamp,
                                String sender, String recipient, String uniqueMsgId,
                                String typeOfMessage, String version) {
        super();
        this.sphereName = sphereName;
        this.timeStamp = timeStamp;
        this.sender = sender;
        this.recipient = recipient;
        this.uniqueMsgId = uniqueMsgId;
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

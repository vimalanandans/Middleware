/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.messages;

import com.google.gson.Gson;

/**
 * Superclass of all messages that services may exchange over Bezirk. Defines JSON (de)serialization.
 */
public class Message {

    /**
     * Hint about the sender's protocol-specific expectations recipients should meet.
     */
    public Flag flag;

    /**
     * Discriminatory property for all messages. Should be set by leaf classes.
     * Subscription ultimately translates to topics, which are then used by Bezirk during reception
     * of published messages for matching them with recipients.
     */
    public String topic;

    /**
     * Intended to help services match requests to (asynchronous) replies, this property is passed,
     * but ignored by Bezirk. Should be set by requester services (e.g. circular counter), and
     * echoed back by responder services.
     * <p/>
     * May be ignored for notices.
     */
    public String msgId;

    protected Message() {
        // Better to have protected constructor rather than abstract class since there are no abstract methods
    }

    /**
     * @param json The Json String that is to be deserialized
     * @param dC   class to deserialize into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class<C> dC) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, dC);
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Provides the message's recipient(s) with an indication of the intent of the
     * message and their duty to reply.
     */
    public enum Flag {
        /**
         * Indicate to the recipient(s) that the message does not require a reply.
         */
        NOTICE,
        /**
         * Indicate to the recipient(s) that the message expects a reply.
         */
        REQUEST,
        /**
         * Indicate to the recipient(s) that the message is a reply to a <code>REQUEST</code>.
         */
        REPLY
    }

}

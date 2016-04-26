/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
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

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.ZirkId;
import com.google.gson.Gson;

/**
 * Base class for all message types Zirks may exchange using the Bezirk middleware. This class
 * implements the serialization routines required to toJson/fromJson a message for
 * transfer/reception.
 */
public abstract class Message {
    private static final Gson gson = new Gson();

    /**
     * Hint about how the sender expects the recipient(s) to handle the message. This is typically
     * set by the concrete implementation class's constructor.
     *
     * @see #topic
     * @see Message.Flag
     */
    public Flag flag;

    /**
     * The pub-sub topic for this message. Topics are defined by
     * {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}. A Zirk subscribes to
     * certain topics by using
     * {@link com.bezirk.middleware.Bezirk#subscribe(ZirkId, ProtocolRole, BezirkListener)} to
     * subscribe to a role. When the Bezirk middleware receives a message, it forwards that
     * message on to any registered Zirk that is subscribed to the role defining the topic.
     * The concrete implementation of a message specifies the topic, which should usually be the
     * implementation class's simple name. For example:
     * <pre>
     * public class TemperatureReadEvent implements Event {
     *     public TemperatureReadEvent() {
     *         super(Flag.NOTICE, TemperatureReadEvent.class.getSimpleName());
     *     }
     * }
     * </pre>
     */
    public String topic;

    /**
     * Intended to help Zirks match messages with {@link #flag flags} set to {@link Message.Flag#REQUEST}
     * with their corresponding {@link Message.Flag#REPLY reply} when the reply is received by a
     * {@link BezirkListener}. The middleware does not use this property internally.
     * The Zirk sending the request should set this ID, and the responding Zirk should echo it in
     * the corresponding reply.
     * <p>
     * This property may be ignored for notices.
     * </p>
     */
    public String msgId;

    /**
     * Serialize the message to a JSON string.
     *
     * @return JSON representation of the message
     */
    public String toJson() {
        return gson.toJson(this);
    }


    /**
     * Deserialize the <code>json</code> string to create an object of type <code>objectType</code>.
     * This method can be used to toJson a message as follows:
     * <p>
     * <code>TemperatureReadEvent tempReadEvent = Event.fromJson(event, TemperatureReadEvent.class);</code>.
     * </p>
     *
     * @param <C>        the type of the object represented by <code>json</code>, set by
     *                   <code>objectType</code>
     * @param json       the JSON String that is to be deserialized
     * @param objectType the type of the object represented by <code>json</code>
     * @return an object of type <code>objectType</code> deserialized from <code>json</code>
     */
    public static <C> C fromJson(String json, Class<C> objectType) {
        return gson.fromJson(json, objectType);
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

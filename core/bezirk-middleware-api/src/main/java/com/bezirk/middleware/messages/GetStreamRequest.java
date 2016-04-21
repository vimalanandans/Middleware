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


/**
 * This event class represents a request for a stream to be returned as a response to a service.  
 * This is needed in Uhu TEMPORARILY to support the initial integration of the CloudPipe feature.
 */
public class GetStreamRequest extends Event {

    public static final String TOPIC = GetStreamRequest.class.getSimpleName();
    protected String subTopic = null;

    public GetStreamRequest(String subTopic) {
        super(Flag.REQUEST, TOPIC);
        this.subTopic = subTopic;
    }

    public static GetStreamRequest deserialize(String serializedMessage) {
        return Event.deserialize(serializedMessage, GetStreamRequest.class);
    }

    public String getSubTopic() {
        return subTopic;
    }

}

package com.bezirk.UhUbasics.protocols.oneway;

import com.bezirk.Proxy.Event;
import com.bezirk.Proxy.IndoorLocation;
import com.bezirk.Proxy.ServiceEndPoint;

/**
 * This Event is used to send a one way (aka "notice") greeting
 *
 * @author Adam Wynne
 */
public class HelloNoticeEvent extends Event {

    /**
     * String that represents the topic that this event is sent to
     */
    public static final String TOPIC = "hello-notice-topic";

    private String greeting = null;

    public HelloNoticeEvent(IndoorLocation at, String sphere, ServiceEndPoint senderEndpoint) {
        // Create an event with a null sender ServiceEndPoint because we
        // don't expect a response.
        super(TOPIC, at, sphere, senderEndpoint);
    }

    /**
     * used to send a greeting as a string, such as "hello"
     */
    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}

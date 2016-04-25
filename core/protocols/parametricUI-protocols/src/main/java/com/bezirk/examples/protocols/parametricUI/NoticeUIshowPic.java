/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.middleware.messages.Event;

/**
 * @see com.bezirk.API.messages.Event
 */
public class NoticeUIshowPic extends Event {

    /**
     * Useful for static references to the event topic, e.g. protocol definitions
     */
    public static final String TOPIC = NoticeUIshowPic.class.getSimpleName();

    // Payload
    private String picURL;

    /**
     * A notice to display the specified picture
     *
     * @param picURL
     */
    public NoticeUIshowPic(String picURL) {
        // set UhuMessage properties: useful for discrimination/deserialization
        super(Flag.NOTICE, TOPIC);
        // set event payload: useful for the consumer services
        this.picURL = picURL;
    }

    /**
     * Use instead of the generic UhuMessage.fromJSON()
     *
     * @param json
     * @return
     */
    public static NoticeUIshowPic deserialize(String json) {
        return Event.fromJSON(json, NoticeUIshowPic.class);
    }

    public String getPicURL() {
        return picURL;
    }
}

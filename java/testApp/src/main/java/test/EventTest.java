package test;

import com.bezirk.middleware.messages.Event;

/**
 * Created by SEV1PI on 7/6/2016.
 */
public class EventTest extends Event {
    /**
     * The concrete implementation of an <code>Event</code> must specify the event's flag
     * and topic. Message flags and topics are documented in {@link Message}.
     *
     * @param flag  flag to mark the intent of this event
     * @param topic the pub-sub topic for this event
     */
    /**
     * Useful for static references to the event topic, e.g. protocol definitions
     */
    public static final String TOPIC = EventTest.class.getSimpleName();


    public EventTest(Flag flag, String topic) {
        super(flag.NOTICE, TOPIC);
    }
}

package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;

import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

/**
 * Class used to create a set of {@link Event events} to subscribe to. Extend this set to design an
 * interface declaring the events to subscribe to, otherwise directly instantiate it to subscribe
 * to a small set of ad hoc events. See {@link MessageSet} for examples.
 */
public class EventSet extends MessageSet {

    private static final long serialVersionUID = 3824727820246453131L;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EventSet.class);
    private transient EventReceiver receiver;

    @SafeVarargs
    public EventSet(Class<? extends Event>... e) {
        super(e);
    }

    /**
     * Get the listener that will be notified when an <code>Event</code> in this set is received.
     *
     * @return the listener that will be notified when an Event in this set is received, or
     * <code>null</code> if one is not set
     */
    public EventReceiver getEventReceiver() {
        return receiver;
    }

    /**
     * Set the listener that will be notified when an <code>Event</code> in this set is received
     * after subscription. Set this listener before calling
     * {@link com.bezirk.middleware.Bezirk#subscribe(MessageSet)}, otherwise events
     * may be missed.
     * <pre>
     *     // Create the Event set
     *     EventSet es = new EventSet(HvacObservation.class);
     *
     *     // Set the listener before subscribing to the set
     *     es.setEventReceiver((event, sender) -&gt; {
     *        HvacObservation o = (HvacObservation)event;
     *        // Do something with the HvacObservation
     *     });
     *
     *     bezirk.subscribe(es);
     *
     *     // If we set the event listener here instead, we might miss messages we expected
     *     // to receive
     * </pre>
     *
     * @param receiver the listener to notify when an Event in this set is received, or
     *                 <code>null</code> to remove an existing listener
     */
    public void setEventReceiver(EventReceiver receiver) {
            logger.debug("Inside setEventReceiver method");
        this.receiver = receiver;
    }

    /**
     * Interface implemented by observers of an <code>EventSet</code> that want to be notified when
     * an event in this set is received.
     */
    public interface EventReceiver {
        /**
         * Called to notify the subscriber that a new event was received.
         *
         * @param event  the received event
         * @param sender the sender of the event
         */
        void receiveEvent(Event event, ZirkEndPoint sender);
    }
}
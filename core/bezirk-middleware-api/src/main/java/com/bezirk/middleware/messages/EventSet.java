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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to create a set of {@link Event events} to subscribe to. Extend this set to design an
 * interface declaring the events to subscribe to; otherwise, directly instantiate it to subscribe
 * to a small set of ad hoc events. See {@link MessageSet} for examples.
 */
public class EventSet extends MessageSet {
    private static final long serialVersionUID = 3824727820246453131L;
    private static final Logger logger = LoggerFactory.getLogger(EventSet.class);
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

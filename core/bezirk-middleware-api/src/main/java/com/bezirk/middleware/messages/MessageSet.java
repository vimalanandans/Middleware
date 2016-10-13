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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Base class for creating sets of messages that a Zirk wants to receive. Bezirk uses topic-based
 * publish and subscribe to enable Zirks to scalably communicate with loose coupling. In Bezirk, a
 * message's topic is its type (e.g. <code>com.example.messages.measurement.HeartBeatMeasurement</code>).
 * A Zirk creates a message set or re-uses an existing one. A Zirk begins receiving messages after
 * passing a set to {@link com.bezirk.middleware.Bezirk#subscribe(MessageSet)}.
 * <p>
 * A message set can be used to either define an interface specifying the messages a Zirk wants and
 * expects to receive or to create ad hoc sets of messages to receive. Creating an interface
 * communicates the design intent of the message set in a clear and re-usable way. For example,
 * a set of lighting related messages could be created to be re-used by any Zirk that wants to
 * work with lights. On the other hand, when working with a small set of ad-hoc messages, designing
 * a re-usable interface can be unnecessary and cumbersome.  You can choose whichever suits your needs.
 * </p><p>
 * To create an interface, Zirk creators should extend the message set class that matches the type
 * of message(s) they want to receive, such as an {@link EventSet}. For example:
 * </p>
 * <pre>
 *     public class CarObservationEvents extends EventSet {
 *         public CarObservationEvents() {
 *              super(HvacObservation.class, SeatObservation.class,
 *              MirrorObservation.class, BrakeObservation.class,
 *              RadioObservation.class,
 *              /* imagine many more events here *&#47;);
 *         }
 *     }
 * </pre>
 * <p>
 * Subscribe to this set using:
 * </p>
 * <pre>
 *     CarObservationEvents o = new CarObservationEvents();
 *
 *     o.setEventReceiver((event, sender) -&gt; {
 *         if (event instanceof HvacObservation) {
 *             HvacObservation o = (HvacObservation) event;
 *             // ...
 *         } else if (event instanceof SeatObservation) {
 *             SeatObservation o = (SeatObservation) event;
 *             // ...
 *         } // ...
 *     });
 *
 *     bezirk.subscribe(o);
 * </pre>
 * <p>
 * Any Zirk developer that wants to subscribe to receive car observations can simply re-use this
 * implementation for their own subscription.
 * </p><p>
 * However, directly instantiating a set is easy and lightweight for cases where a Zirk wants to
 * receive a small, ad hoc, or unrelated set of messages:
 * </p>
 * <pre>
 *     EventSet es = new EventSet(SeatObservation.class, NoiseLevelMeasurement.class,
 *         HeartBeatMeasurement.class);
 *
 *     es.setEventReceiver(/* ... *&#47;);
 *
 *     bezirk.subscribe(es);
 * </pre>
 *
 * @see Event
 */
public abstract class MessageSet implements Serializable {
    private static final long serialVersionUID = -6232492956322531924L;
    private final Set<String> messageClassList = new HashSet<>();
    private final String setId;

    @SafeVarargs
    public MessageSet(Class<? extends Message>... m) {
        for (Class<? extends Message> messageClass : m) {
            messageClassList.add(messageClass.getName());
        }
        this.setId = UUID.randomUUID().toString();
    }

    /**
     * The specific pub-sub topics any Zirk subscribed to this role will subscribe to. In
     * particular, a Zirk will receive any <code>Event</code> or <code>StreamDescriptor</code> sent in its
     * subnet whose topic is listed in the <code>Set</code> returned by this method.
     *
     * @return the set of topics this role subscribes to
     */
  /*  public Set<Class<? extends Message>> getMessages() {
        Set<Class<? extends Message>> messages = new HashSet<>();

        for (String className : messageClassList) {
            try {
                Class<?> messageClass = Class.forName(className);
                messages.add((Class<? extends Message>) messageClass);
            } catch (ClassNotFoundException cfne) {
                throw new IllegalArgumentException("The class for a message specified in a set is" +
                        " missing: " + className);
            }
        }

        return messages;
    } */
    public Set<String> getMessages() {
        return messageClassList;
    }

    public String getSetId() {
        return setId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageSet that = (MessageSet) o;

        return setId.equals(that.setId);

    }

    @Override
    public int hashCode() {
        return setId.hashCode();
    }
}

package com.bezirk.middleware;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.MessageSet;

/**
 * The API for registering Zirks, sending messages, and subscribing to messages sent by other Zirks.
 * Zirks fetch this API using the following code:
 * <br>
 * <pre>
 * import com.bezirk.middleware.Bezirk;
 * import com.bezirk.middleware.java.proxy.BezirkMiddleware;
 *
 * // ...
 *
 *          BezirkMiddleware.initialize();
 *          Bezirk bezirk = BezirkMiddleware.registerZirk("Zirk Name Here");
 *
 * // ...
 *
 * </pre>
 *
 * That sample uses the {@link com.bezirk.middleware.java.proxy.BezirkMiddleware Java SE} lifecycle
 * API. There is also a version for {@link com.bezirk.middleware.android.BezirkMiddleware Android}.
 */
public interface Bezirk {
    /**
     * Undo the effects of registering the Zirk using
     * <code>BezirkMiddleware.registerZirk(String)</code>.
     */
    void unregisterZirk();

    /**
     * Begin receiving the events or streams included in <code>messageSet</code>. Translating
     * the concept to the pub-sub model, this method is how Zirks subscribe to topics.
     *
     * @param messageSet a declaration of the messages the registered Zirk will receive
     */
    void subscribe(MessageSet messageSet);

    /**
     * Unsubscribe a Zirk from a particular message set. This method undoes the effects of
     * {@link #subscribe(MessageSet)}. After this method finishes, the
     * Zirk will no longer receive messages included in <code>messageSet</code> unless
     * the messages also appear in a different set the Zirk is subscribed to. This
     * method does nothing if the Zirk was not subscribed to the set.
     *
     * @param messageSet set to unsubscribe from
     * @return <code>true</code> if <code>subscriber</code> was unsubscribed from the
     * set as a result of this method call
     */
    boolean unsubscribe(MessageSet messageSet);

    /**
     * Publish an event to all Zirks in the sender's subnet subscribed to the event.
     *
     * @param event the <code>Event</code> being sent
     */
    void sendEvent(Event event);

    /**
     * Publish an event to all Zirks in the sender's subnet subscribed to the event that also
     * meet the requirements set by a {@link com.bezirk.middleware.addressing.RecipientSelector}.
     * The set of recipients can be narrowed using <code>recipient</code> if a semantic address is
     * specified.
     *
     * @param recipient the {@link RecipientSelector} specifying the sent event's recipients within
     *                  a subnet
     * @param event     the <code>Event</code> being sent
     */
    void sendEvent(RecipientSelector recipient, Event event);

    /**
     * Publish an event with one specific recipient.
     *
     * @param recipient intended recipient, as extracted from a received message
     * @param event     the <code>Event</code> being sent
     */
    void sendEvent(ZirkEndPoint recipient, Event event);

    /*
     * Inform the Bezirk middleware of the Zirk's {@link com.bezirk.middleware.addressing.Location}.
     * This method is useful when the Zirk controls a device that is in a location distinct from
     * the device that is executing the Zirk, or when it is useful to send a message to the Zirk
     * only when it is in a certain location. For addressing purposes, a {@link RecipientSelector}
     * must be created, initialized with a <code>Location</code>, used when sending an event to make
     * use of a Zirk's <code>location</code>.
     *
     * @param location the physical location of the Thing <code>Zirk</code> controls
     */
    void setLocation(Location location);
}

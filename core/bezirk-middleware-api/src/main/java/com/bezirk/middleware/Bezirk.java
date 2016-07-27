/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bezirk.middleware;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.messages.StreamDescriptor;

import java.io.File;
import java.io.PipedOutputStream;


/**
 * The API for registering Zirks, sending messages, and subscribing to messages sent by other Zirks.
 * Zirks fetch this API using the following code:
 * <br>
 * <pre>
 * import com.bezirk.middleware.Bezirk;
 * import com.bezirk.middleware.proxy.Factory;
 *
 * // ...
 *
 *          Bezirk bezirk = Factory.registerZirk("Zirk Name Here");
 *
 * // ...
 *
 * </pre>
 */
public interface Bezirk {
    /**
     * Undo the effects of registering the Zirk using a <code>Factory</code> and remove all
     * subscriptions as if {@link #unsubscribe(MessageSet)} were called with <code>null</code> as
     * the <code>MessageSet</code>.
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
     * method does nothing if the Zirk was not subscribed to the set. Specifying <code>null</code>
     * for the set unsubscribes the Zirk from all sets it is subscribed to.
     *
     * @param messageSet set to unsubscribe from, or <code>null</code> to remove all subscriptions
     * @return <code>true</code> if <code>subscriber</code> was unsubscribed from at least one
     * set as a result of this method call
     */
    boolean unsubscribe(MessageSet messageSet);

    /**
     * Publish an event to all Zirks in the sender's sphere(s) subscribed to the event.
     *
     * @param event the <code>Event</code> being sent
     */
    void sendEvent(Event event);

    /**
     * Publish an event to all Zirks in the sender's sphere(s) subscribed to the event that also
     * meet the requirements set by a {@link com.bezirk.middleware.addressing.RecipientSelector}.
     * The set of recipients can be narrowed using <code>recipient</code> if a semantic address is
     * specified, or broadened if a pipe is specified.
     *
     * @param recipient the {@link RecipientSelector} specifying the sent event's recipients within
     *                  a sphere
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

    /**
     * Publish a {@link StreamDescriptor} with one specific recipient. The
     * channel's properties are described by <code>stream</code>. The transmitted data is supplied by
     * writes to <code>dataStream</code>. To send a file use
     * {@link #sendStream(ZirkEndPoint, StreamDescriptor, File)}. Streams sent using this
     * method are assumed to be incremental (see {@link StreamDescriptor#isIncremental()}).
     *
     * @param recipient  intended recipient, as extracted from a received message
     * @param streamDescriptor     communication channel's descriptor
     * @param dataStream io stream where the outgoing data will be written into by this method's
     *                   caller. Internally, the Bezirk middleware will read data from the
     *                   <code>dataStream</code> in a thread-safe manner by creating a
     *                   <code>PipedInputStream</code> linked to <code>dataStream</code>
     */
    void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor,
                    PipedOutputStream dataStream);

    /**
     * Publish a file with one specific recipient. This method is identical to
     * {@link Bezirk#sendStream(ZirkEndPoint, StreamDescriptor, PipedOutputStream)}, except this
     * version is intended to send a specific file instead of general data. Streams sent using this
     * method are assumed to be non-incremental (see {@link StreamDescriptor#isIncremental()}).
     *
     * @param recipient intended recipient, as extracted from a received message
     * @param streamDescriptor    communication channel's descriptor
     * @param file      the file whose contents will be sent using the <code>stream</code>
     */
    void sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file);

    /**
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

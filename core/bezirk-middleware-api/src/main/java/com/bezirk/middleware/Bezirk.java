package com.bezirk.middleware;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;

import java.io.File;
import java.io.PipedOutputStream;


/**
 * The API for registering Zirks, sending messages, and discovering other Zirks. Zirks fetch
 * this API using the following code:
 * <br>
 * <pre>
 * import com.bezirk.api.Bezirk;
 * import com.bezirk.middleware.proxy.Factory;
 *
 * // ...
 *
 *          Bezirk bezirk = Factory.registerZirk("Zirk Name Here");
 *
 * // ...
 *
 * </pre>
 *
 * @see BezirkListener
 */
public interface Bezirk {
    /**
     * Undo the effects of registering the Zirk using a <code>Factory</code> and removes all
     * subscriptions as if {@link #unsubscribe(ProtocolRole)} were called with <code>null</code> as
     * the <code>ProtocolRole</code>.
     */
    public void unregisterZirk();

    /**
     * Register a Zirk <code>listener</code> to receive all events and streams whose topics are
     * defined by <code>protocolRole</code>. This is a declaration to the middleware and other Zirks
     * that the registered Zirk will fill the role defined by <code>protocolRole</code>. Translating
     * the concept to the pub-sub model, this method is how Zirks subscribe to topics.
     *
     * @param protocolRole a declaration of the role the registered Zirk will play, meaning the
     *                     Zirk should receive all messages whose topic is defined in this role
     * @param listener     recipient of notifications issued from the Bezirk middleware when events
     */
    public void subscribe(ProtocolRole protocolRole, BezirkListener listener);

    /**
     * Unsubscribe a Zirk from a particular role. This method undoes the effects of
     * {@link #subscribe(ProtocolRole, BezirkListener)}. After this method finishes, the
     * Zirk will no longer receive messages for topics defined in <code>protocolRole</code>. This
     * method does nothing if the defined <code>subscriber</code> was not subscribed to the role.
     * Specifying <code>null</code> for the role unsubscribes the Zirk from all roles it is
     * subscribed to.
     *
     * @param protocolRole role to unsubscribe from, or <code>null</code> to remove all subscriptions
     * @return <code>true</code> if <code>subscriber</code> was unsubscribed from at least one
     * role as a result of this method call
     */
    public boolean unsubscribe(ProtocolRole protocolRole);

    /**
     * Publish an event to all Zirks in the sender's sphere(s) subscribed to the event's topic.
     *
     * @param event the <code>Event</code> being sent
     * @see BezirkListener#receiveEvent(String, Event, ZirkEndPoint)
     */
    public void sendEvent(Event event);

    /**
     * Publish an event to all Zirks in the sender's sphere(s) subscribed to the event's topic.
     * The set of recipients can be narrowed using <code>recipient</code> if a semantic address is
     * specified, or broadened if a pipe is specified.
     *
     * @param recipient the {@link RecipientSelector} specifying the sent event's recipients
     * @param event     the <code>Event</code> being sent
     * @see BezirkListener#receiveEvent(String, Event, ZirkEndPoint)
     */
    public void sendEvent(RecipientSelector recipient, Event event);

    /**
     * Publish an event with one specific recipient.
     *
     * @param recipient intended recipient, as extracted from a received message
     * @param event     the <code>Event</code> being sent
     * @see BezirkListener#receiveEvent(String, Event, ZirkEndPoint)
     */
    public void sendEvent(ZirkEndPoint recipient, Event event);

    /**
     * Publish a {@link StreamDescriptor} with one specific recipient. The
     * channel's properties are described by <code>stream</code>. The transmitted data is supplied by
     * writes to <code>dataStream</code>. To send a file use
     * {@link #sendStream(ZirkEndPoint, StreamDescriptor, File)}. Streams sent using this
     * method are assumed to be incremental (see {@link StreamDescriptor#isIncremental()}).
     *
     * @param recipient  intended recipient, as extracted from a received message
     * @param dataStream io stream where the outgoing data will be written into by this method's
     *                   caller. Internally, the Bezirk middleware will read data from the
     *                   <code>dataStream</code> in a thread-safe manner by creating a
     *                   <code>PipedInputStream</code> linked to <code>dataStream</code>
     * @return Bezirk middleware-generated id for the stream, which will be referred to in
     * {@link BezirkListener#streamStatus(short, BezirkListener.StreamStates)}
     * @see BezirkListener#receiveStream(String, StreamDescriptor, short, java.io.InputStream, ZirkEndPoint)
     * @see BezirkListener#receiveStream(String, StreamDescriptor, short, File, ZirkEndPoint)
     */
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor,
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
     * @return Bezirk middleware-generated id for the stream, which will be referred to in
     * {@link BezirkListener#streamStatus(short, BezirkListener.StreamStates)}
     * @see BezirkListener#receiveStream(String, StreamDescriptor, short, java.io.InputStream, ZirkEndPoint)
     * @see BezirkListener#receiveStream(String, StreamDescriptor, short, File, ZirkEndPoint)
     */
    public short sendStream(ZirkEndPoint recipient, StreamDescriptor streamDescriptor, File file);

    /**
     * Inform the Bezirk middleware of the Zirk's {@link com.bezirk.middleware.addressing.Location}.
     * This method is useful when the Zirk controls a device that is in a location distinct from
     * the device that is executing the Zirk. The location is used whenever an
     * {@link RecipientSelector} is used (e.g. when sending an event,
     * discovering Zirks subscribed to a role, etc.).
     *
     * @param location the physical location of the Thing <code>Zirk</code> controls
     */
    public void setLocation(Location location);
}

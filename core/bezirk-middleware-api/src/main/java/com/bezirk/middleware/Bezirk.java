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

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;

import java.io.File;
import java.io.PipedOutputStream;


/**
 * The API for registering Zirks, sending messages, and discovering other Zirks. Zirks fetch
 * this API using the following code:
 * <br>
 * <pre>
 * import com.bezirk.api.Bezirk;
 * import com.bezirk.proxy.Factory;
 *
 * // ...
 *
 *          Bezirk bezirk = Factory.getInstance();
 *
 * // ...
 *
 * </pre>
 *
 * @see BezirkListener
 */
public interface Bezirk {
    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available the user,
     * thus allowing her to place it in a sphere to interact with other Zirks. This method must
     * be called before any other API method can be called because it returns the ID required by
     * most other API methods.
     *
     * @param zirkName the name of the Zirk being registered, as defined by the Zirk
     *                 developer/vendor
     * @return a middleware-defined ID for the registered Zirk, or <code>null</code> if a Zirk with
     * the name specified by <code>zirkName</code> is already registered. The returned ID is
     * required to use most other API methods
     */
    public ZirkId registerZirk(String zirkName);

    /**
     * Undo the effects of {@link #registerZirk(String)} and removes all subscriptions as if
     * {@link #unsubscribe(ZirkId, ProtocolRole)} were called with <code>null</code> as the
     * <code>ProtocolRole</code>.
     *
     * @param zirkId id for the registered Zirk, returned by {@link #registerZirk(String)}
     */
    public void unregisterZirk(ZirkId zirkId);

    /**
     * Register a Zirk <code>listener</code> to receive all events and streams whose topics are
     * defined by <code>protocolRole</code>. This is a declaration to the middleware and other Zirks
     * that the registered Zirk will fill the role defined by <code>protocolRole</code>. Translating
     * the concept to the pub-sub model, this method is how Zirks subscribe to topics.
     *
     * @param subscriber   id for subscribing Zirk, returned by {@link #registerZirk(String)}
     * @param protocolRole a declaration of the role the registered Zirk will play, meaning the
     *                     Zirk should receive all messages whose topic is defined in this role
     * @param listener     recipient of notifications issued from the Bezirk middleware when events
     *                     and streams associated with this protocol role are received
     */
    public void subscribe(ZirkId subscriber, ProtocolRole protocolRole, BezirkListener listener);

    /**
     * Unsubscribe a Zirk from a particular role. This method undoes the effects of
     * {@link #subscribe(ZirkId, ProtocolRole, BezirkListener)}. After this method finishes, the
     * Zirk will no longer receive messages for topics defined in <code>protocolRole</code>. This
     * method does nothing if the defined <code>subscriber</code> was not subscribed to the role.
     * Specifying <code>null</code> for the role unsubscribes the Zirk from all roles it is
     * subscribed to.
     *
     * @param subscriber   id for registered Zirk, as returned by {@link #registerZirk(String)}
     * @param protocolRole role to unsubscribe from, or <code>null</code> to remove all subscriptions
     * @return <code>true</code> if <code>subscriber</code> was unsubscribed from at least one
     * role as a result of this method call
     */
    public boolean unsubscribe(ZirkId subscriber, ProtocolRole protocolRole);

    /**
     * Publish an event to all Zirks in the sender's sphere(s) subscribed to the event's topic.
     * The set of recipients can be narrowed using <code>receiver</code> if a semantic address is
     * specified, or broadened if a pipe is specified.
     *
     * @param sender   id of Zirk sending the event, as returned by {@link #registerZirk(String)}
     * @param receiver the {@link Address} of the sent event's recipients
     * @param event    the <code>Event</code> being sent
     * @see BezirkListener#receiveEvent(String, String, ZirkEndPoint)
     */
    public void sendEvent(ZirkId sender, Address receiver, Event event);

    /**
     * Publish an event with one specific recipient.
     *
     * @param sender   id of Zirk sending the event, returned by {@link #registerZirk(String)}
     * @param receiver intended recipient, as extracted from a received message, or as discovered
     *                 by calling
     *                 {@link #discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
     * @param event    the <code>Event</code> being sent
     * @see BezirkListener#receiveEvent(String, String, ZirkEndPoint)
     */
    public void sendEvent(ZirkId sender, ZirkEndPoint receiver, Event event);

    /**
     * Publish a {@link com.bezirk.middleware.messages.Stream} with one specific recipient. The
     * channel's properties are described by <code>stream</code>. The transmitted data is supplied by
     * writes to <code>dataStream</code>. To send a file use
     * {@link #sendStream(ZirkId, ZirkEndPoint, Stream, File)}. Streams sent using this
     * method are assumed to be incremental (see {@link Stream#isIncremental()}).
     *
     * @param sender     id of Zirk sending the stream, as returned by {@link #registerZirk(String)}
     * @param receiver   intended recipient, as extracted from a received message, or as discovered
     *                   by calling
     *                   {@link #discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
     * @param stream     communication channel's descriptor
     * @param dataStream io stream where the outgoing data will be written into by this method's
     *                   caller. Internally, the Bezirk middleware will read data from the
     *                   <code>dataStream</code> in a thread-safe manner by creating a
     *                   <code>PipedInputStream</code> linked to <code>dataStream</code>
     * @return Bezirk middelware-generated id for the stream, which will be referred to in
     * {@link BezirkListener#streamStatus(short, BezirkListener.StreamStates)}
     * @see BezirkListener#receiveStream(String, String, short, java.io.InputStream, ZirkEndPoint)
     * @see BezirkListener#receiveStream(String, String, short, File, ZirkEndPoint)
     */
    public short sendStream(ZirkId sender, ZirkEndPoint receiver, Stream stream,
                            PipedOutputStream dataStream);

    /**
     * Publish a file with one specific recipient. This method is identical to
     * {@link Bezirk#sendStream(ZirkId, ZirkEndPoint, Stream, PipedOutputStream)}, except this
     * version is intended to send a specific file instead of general data. Streams sent using this
     * method are assumed to be non-incremental (see {@link Stream#isIncremental()}).
     *
     * @param sender   id of Zirk sending the stream, as returned by {@link #registerZirk(String)}
     * @param receiver intended recipient, as extracted from a received message, or as discovered
     *                 by calling
     *                 {@link #discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
     * @param stream   communication channel's descriptor
     * @param file     the file whose contents will be sent using the <code>stream</code>
     * @return Bezirk middelware-generated id for the stream, which will be referred to in
     * {@link BezirkListener#streamStatus(short, BezirkListener.StreamStates)}
     * @see BezirkListener#receiveStream(String, String, short, java.io.InputStream, ZirkEndPoint)
     * @see BezirkListener#receiveStream(String, String, short, File, ZirkEndPoint)
     */
    public short sendStream(ZirkId sender, ZirkEndPoint receiver, Stream stream, File file);

    /**
     * Request the use of a {@link com.bezirk.middleware.addressing.Pipe} to send data outside of the
     * <code>requester</code>'s sphere(s). A pipe may break the confidentiality property established
     * by spheres, thus the user must authorize the use of the pipe. The authorization process is
     * initiated via this request. Pipes are governed by security policies, specified by
     * <code>allowedIn</code> and <code>allowedOut</code>, which restrict the messages that can flow
     * into and out of the sphere via the pipe to just those messages belonging to explicitly listed
     * {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles's}.
     *
     * @param requester  id of Zirk requesting the pipe, as returned by {@link #registerZirk(String)}
     * @param pipe       the pipe the Zirk wants permission to use
     * @param allowedIn  specification of message roles allowed to flow into the requester's sphere(s)
     *                   via the pipe. If <code>null</code>, no security policy is imposed
     * @param allowedOut specification of message roles allowed to flow out of the requester's sphere(s)
     *                   via the pipe. If <code>null</code>, no security policy is imposed
     * @param listener   recipient of notifications issued by the Bezirk middleware when the user
     *                   authorizes the creation of a pipe
     * @see BezirkListener#pipeGranted(Pipe, PipePolicy, PipePolicy)
     */
    public void requestPipeAuthorization(ZirkId requester, Pipe pipe, PipePolicy allowedIn,
                                         PipePolicy allowedOut, BezirkListener listener);

    /**
     * Fetch the security policies governing an authorized <code>pipe</code>. If the pipe is not
     * authorized, this method does nothing. Use
     * {@link #requestPipeAuthorization(ZirkId, Pipe, PipePolicy, PipePolicy, BezirkListener)} to request
     * the authority to use a pipe.
     *
     * @param pipe     the authorized pipe whose policy we want to retrieve
     * @param listener recipient of notifications issued by the Bezirk middleware when
     *                 <code>pipe</code>'s policies is discovered
     * @see BezirkListener#pipeGranted(Pipe, PipePolicy, PipePolicy)
     */
    public void getPipePolicy(Pipe pipe, BezirkListener listener);

    /**
     * Find Zirks subscribed to <code>protocolRole</code> within <code>zirk</code>'s sphere(s).
     * The <code>listener</code> will be notified when a set of results is ready. The set of results
     * is considered ready after all of the Zirks within the relevant sphere(s) that are subscribed to
     * the role are found, the <code>timeout</code> elapses, or the number of discovered Zirks in the
     * result set hits <code>maxResults</code>. The discovery of Zirks subscribed to a role can
     * be further constrained to just the set of Zirks described by <code>scope</code> (see
     * {@link com.bezirk.middleware.addressing.Location}).
     * <p>
     * Internally, the Bezirk middleware issues one discovery request at a time. If this method
     * is called multiple times in short succession, a new discovery request will occur only after
     * the first request is fulfilled. Each new discovery request will be fulfilled in the order they
     * were received as soon as the currently executing request finishes.
     * </p>
     *
     * @param zirk         id of Zirk discovering Zirks subscribed to <code>protocolRole</code>, as
     *                     returned by {@link #registerZirk(String)}
     * @param scope        semantic address to further constrain the discovery of Zirks fulfilling
     *                     <code>protocolRole</code>
     * @param protocolRole the role that Zirks we want to discover are subscribed to, or
     *                     <code>null</code> to discover all Zirks in <code>zirk</code>'s
     *                     spheres
     * @param timeout      max time in milliseconds that the Bezirk middleware can wait for replies
     *                     to discovery requests
     * @param maxResults   number of Zirks the Bezirk middleware is to wait for
     * @param listener     recipient of notifications issued by the Bezirk middleware when the set
     *                     of discovered Zirks is complete
     * @see BezirkListener#discovered(java.util.Set)
     * @see DiscoveredZirk
     * @see #setLocation(ZirkId, Location)
     */
    public void discover(ZirkId zirk, Address scope, ProtocolRole protocolRole, long timeout,
                         int maxResults, BezirkListener listener);

    /**
     * Inform the Bezirk middleware of the Zirk's {@link com.bezirk.middleware.addressing.Location}.
     * This method is useful when the Zirk controls a device that is in a location distrinct from
     * the device that is executing the Zirk. The location is used whenever an
     * {@link com.bezirk.middleware.addressing.Address} is used (e.g. when sending an event,
     * discovering Zirks subscribed to a role, etc.).
     *
     * @param zirk     id of Zirk whose location is being set, as returned by {@link #registerZirk(String)}
     * @param location the physical location of the Thing <code>Zirk</code> controlss
     */
    public void setLocation(ZirkId zirk, Location location);
}

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

import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * Interface implemented by all Zirks that will receive messages, discover Zirks, or use pipes.
 * The implementor of this interface will be notified when observable operations requested in
 * {@link Bezirk} are completed or reach an interesting state.
 * <p>
 * {@link com.bezirk.middleware.messages.Event Events} and
 * {@link com.bezirk.middleware.messages.Stream Streams} are received by Zirks subscribed to
 * specific topics. Zirks subscribe to topics by subscribing to a
 * {@link com.bezirk.middleware.messages.ProtocolRole} using
 * {@link Bezirk#subscribe(ProtocolRole, BezirkListener)}. It may make sense to
 * structure your code by implementing a <code>BezirkListener</code> for each role your
 * Zirk subscribes to.
 * </p>
 *
 * @see Bezirk
 */
public interface BezirkListener {
    /**
     * Called by the Bezirk middleware when an <code>event</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     *
     * @param topic  the event's topic. Topics are defined by
     *               {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param event  the received event. Use <code>topic</code> to determine the more specific type
     *               of the event if you need to cast it to access custom class members.
     * @param sender the Zirk that sent the event
     */
    public void receiveEvent(String topic, Event event, ZirkEndPoint sender);

    /**
     * Called by the Bezirk middleware when a <code>stream</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link Stream#isIncremental()}
     * </p>
     *
     * @param topic       the event's topic. Topics are defined by
     *                    {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param stream      the received stream descriptor. Use <code>topic</code> to determine the
     *                    more specific type of the descriptor if you need to cast it to access
     *                    custom class members.
     * @param streamId    Bezirk middleware-generated id for the stream, used to refer to the stream in
     *                    {@link #streamStatus(short, StreamStates)}
     * @param inputStream input stream containing the received data
     * @param sender      the Zirk that sent the stream
     */
    public void receiveStream(String topic, Stream stream, short streamId, InputStream inputStream,
                              ZirkEndPoint sender);

    /**
     * Called by the Bezirk middleware when a <code>stream</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link Stream#isIncremental()} as <code>false</code>.
     * </p>
     *
     * @param topic    the event's topic. Topics are defined by
     *                 {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param stream   the received stream descriptor. Use <code>topic</code> to determine the
     *                 more specific type of the descriptor if you need to cast it to access
     *                 custom class members.
     * @param streamId Bezirk middleware-generated id for the stream, used to refer to the stream in
     *                 {@link #streamStatus(short, StreamStates)}
     * @param file     the received file
     * @param sender   the Zirk that sent the stream
     */
    public void receiveStream(String topic, Stream stream, short streamId, File file,
                              ZirkEndPoint sender);

    /**
     * Called by the Bezirk middleware if something unexpected happens to the stream referred to
     * by <code>streamId</code>, or when an incremental stream closes.
     *
     * @param streamId as returned by {@link Bezirk#sendStream(ZirkEndPoint, Stream, java.io.PipedOutputStream)}
     *                 or received in {@link #receiveStream(String, Stream, short, InputStream, ZirkEndPoint)}
     * @param status   the status of the stream referenced by <code>streamId</code>
     */
    public void streamStatus(short streamId, StreamStates status);

    /**
     * Called by the Bezirk middleware when a user grants or denies a Zirk authorization to use a pipe.
     * Pipe authorization is requested using
     * {@link Bezirk#requestPipeAuthorization(Pipe, PipePolicy, PipePolicy, BezirkListener)}.
     *
     * @param pipe       the pipe the user authorized the Zirk implementing this interface
     *                   to use, or <code>null</code> if authorization was denied
     * @param allowedIn  specification of message roles allowed to flow into the requester's sphere(s)
     *                   via the pipe. If <code>null</code>, no security policy is imposed
     * @param allowedOut specification of message roles allowed to flow out of the requester's sphere(s)
     *                   via the pipe. If <code>null</code>, no security policy is imposed
     */
    public void pipeGranted(Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut);

    /**
     * Called by the Bezirk middleware if something unexpected happens to <code>pipe</code>.
     *
     * @param pipe   as passed in {@link #pipeGranted(Pipe, PipePolicy, PipePolicy)}
     * @param status the new status of <code>pipe</code>
     */
    public void pipeStatus(Pipe pipe, PipeStates status);

    /**
     * Called by the Bezirk middleware when a discovery request issued using
     * {@link Bezirk#discover(RecipientSelector, ProtocolRole, long, int, BezirkListener)}
     * completes.
     *
     * @param zirkSet a set of Zirks discovered as subscribing to a particular
     *                {@link com.bezirk.middleware.messages.ProtocolRole}.
     */
    public void discovered(Set<DiscoveredZirk> zirkSet);

    /**
     * Unexpected states a stream can be in.
     */
    public enum StreamStates {
        LOST_CONNECTION, END_OF_DATA
    }

    /**
     * Unexpected states a pipe can be in.
     */
    public enum PipeStates {
        LOST_CONNECTION
    }
}

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

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;

import java.io.File;
import java.io.InputStream;

/**
 * Interface implemented by all Zirks that will receive messages, discover Zirks, or use pipes.
 * The implementor of this interface will be notified when observable operations requested in
 * {@link Bezirk} are completed or reach an interesting state.
 * <p>
 * {@link com.bezirk.middleware.messages.Event Events} and
 * {@link StreamDescriptor Streams} are received by Zirks subscribed to
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
     * Called by the Bezirk middleware when a <code>streamDescriptor</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link StreamDescriptor#isIncremental()}
     * </p>
     *
     * @param topic       the event's topic. Topics are defined by
     *                    {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param streamDescriptor      the received streamDescriptor descriptor. Use <code>topic</code> to determine the
     *                    more specific type of the descriptor if you need to cast it to access
     *                    custom class members.
     * @param streamId    Bezirk middleware-generated id for the streamDescriptor, used to refer to the streamDescriptor in
     *                    {@link #streamStatus(short, StreamStates)}
     * @param inputStream input streamDescriptor containing the received data
     * @param sender      the Zirk that sent the streamDescriptor
     */
    public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream,
                              ZirkEndPoint sender);

    /**
     * Called by the Bezirk middleware when a <code>streamDescriptor</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link StreamDescriptor#isIncremental()} as <code>false</code>.
     * </p>
     *
     * @param topic    the event's topic. Topics are defined by
     *                 {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param streamDescriptor   the received streamDescriptor descriptor. Use <code>topic</code> to determine the
     *                 more specific type of the descriptor if you need to cast it to access
     *                 custom class members.
     * @param streamId Bezirk middleware-generated id for the streamDescriptor, used to refer to the streamDescriptor in
     *                 {@link #streamStatus(short, StreamStates)}
     * @param file     the received file
     * @param sender   the Zirk that sent the streamDescriptor
     */
    public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file,
                              ZirkEndPoint sender);

    /**
     * Called by the Bezirk middleware if something unexpected happens to the stream referred to
     * by <code>streamId</code>, or when an incremental stream closes.
     *
     * @param streamId as returned by {@link Bezirk#sendStream(ZirkEndPoint, StreamDescriptor, java.io.PipedOutputStream)}
     *                 or received in {@link #receiveStream(String, StreamDescriptor, short, InputStream, ZirkEndPoint)}
     * @param status   the status of the stream referenced by <code>streamId</code>
     */
    public void streamStatus(short streamId, StreamStates status);

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

/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;

import java.io.InputStream;
import java.util.Set;

/**
 * Interface implemented by all Zirks that will receive messages, discover services, or use pipes.
 * The implementor of this interface will be notified when observable operations requested in
 * {@link Bezirk} are completed or reach an interesting state.
 * <p>
 * {@link com.bezirk.middleware.messages.Event Events} and
 * {@link com.bezirk.middleware.messages.Stream Streams} are received by Zirks subscribed to
 * specific topics. Zirks subscribe to topics by subscribing to a
 * {@link com.bezirk.middleware.messages.ProtocolRole} using
 * {@link Bezirk#subscribe(ServiceId, ProtocolRole, BezirkListener)}. It may make sense to
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
     * @param event  the received event serialized as a JSON string
     * @param sender the Zirk that sent the event
     */
    public void receiveEvent(String topic, String event, ServiceEndPoint sender);

    /**
     * Called by the Bezirk middleware when a <code>stream</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link Stream#isIncremental()}
     * </p>
     *
     * @param topic       the event's topic. Topics are defined by
     *                    {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param stream      the received stream's descriptor serialized as JSON string
     * @param streamId    Bezirk middleware-generated id for the stream, used to refer to the stream in
     *                    {@link #streamStatus(short, StreamStates)}
     * @param inputStream inputstream containing the received data
     * @param sender      the Zirk that sent the stream
     */
    public void receiveStream(String topic, String stream, short streamId, InputStream inputStream,
                              ServiceEndPoint sender);

    /**
     * Called by the Bezirk middleware when a <code>stream</code> arrives whose topic the Zirk
     * implementing this interface is subscribed to.
     * <p>
     * This callback is for streams marked {@link Stream#isIncremental()} as <code>false</code>.
     * </p>
     *
     * @param topic    the event's topic. Topics are defined by
     *                 {@link com.bezirk.middleware.messages.ProtocolRole ProtocolRoles}
     * @param stream   the received stream's descriptor serialized as JSON string
     * @param streamId Bezirk middleware-generated id for the stream, used to refer to the stream in
     *                 {@link #streamStatus(short, StreamStates)}
     * @param filePath the received file
     * @param sender   the Zirk that sent the stream
     */
    public void receiveStream(String topic, String stream, short streamId, String filePath,
                              ServiceEndPoint sender);

    /**
     * Called by the Bezirk middleware if something unexpected happens to the stream referred to
     * by <code>streamId</code>, or when an incremental stream closes.
     *
     * @param streamId as returned by {@link Bezirk#sendStream(ServiceId, ServiceEndPoint, Stream, java.io.PipedOutputStream)}
     *                 or received in {@link #receiveStream(String, String, short, InputStream, ServiceEndPoint)}
     * @param status   the status of the stream referenced by <code>streamId</code>
     */
    public void streamStatus(short streamId, StreamStates status);

    /**
     * Called by the Bezirk middleware when a user grants or denies a Zirk authorization to use a pipe.
     * Pipe authorization is requested using
     * {@link Bezirk#requestPipeAuthorization(ServiceId, Pipe, PipePolicy, PipePolicy, BezirkListener)}.
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
     * {@link Bezirk#discover(ServiceId, Address, ProtocolRole, long, int, BezirkListener)}
     * completes.
     *
     * @param serviceSet a set of services discovered as subscribing to a particular
     *                   {@link com.bezirk.middleware.messages.ProtocolRole}.
     */
    public void discovered(Set<DiscoveredService> serviceSet);

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

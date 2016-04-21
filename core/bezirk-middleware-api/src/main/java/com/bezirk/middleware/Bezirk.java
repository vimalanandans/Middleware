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
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;

import java.io.PipedOutputStream;


/**
 * Platform-independent API offered by bezirk middleware to services (normally by means of a
 * platform-specific proxy.)
 *
 * @see BezirkListener
 */
public interface Bezirk {

    /**
     * Makes the service known to Bezirk middleware, so that the user may then associate the service
     * with one or more Spheres.
     * Should be called once by each service before any other call on this API.
     *
     * @param myName as defined by the service developer/vendor
     * @return Bezirk middelware-defined id for the registering service, or NULL if Bezirk
     * middelware blocks a second registration for the same name.
     */
    public ServiceId registerService(String myName);

    /**
     * Undoes the effects of {@link #registerService(String)} and removes all subscriptions:
     * same as calling {@link #unsubscribe(ServiceId, ProtocolRole)} with NULL in the ProtocolRole
     *
     * @param myId as returned by {@link #registerService(String)}
     */
    public void unregisterService(ServiceId myId);

    /**
     * Registers a service listener for all events and streams defined in the protocol role.
     *
     * @param subscriber id as returned by {@link #registerService(String)}
     * @param pRole      specific protocol role that the service will play
     * @param listener   object for the callbacks issued by Bezirk middelware upon reception of
     *                   events and streams associated with this protocol role
     */
    public void subscribe(ServiceId subscriber, ProtocolRole pRole, BezirkListener listener);

    /**
     * Removes the effects of subscribe(...). Does nothing if no subscription is found.
     *
     * @param subscriber id as returned by {@link #registerService(String)}
     * @param pRole      specific protocol to remove, or NULL to remove all subscriptions for the service
     */
    public void unsubscribe(ServiceId subscriber, ProtocolRole pRole);

    /**
     * Publishes an event over a target semantic address.
     *
     * @param sender id as returned by {@link #registerService(String)}
     * @param target semantic address
     * @param event  being published
     * @see BezirkListener#receiveEvent(String, String, ServiceEndPoint)
     */
    public void sendEvent(ServiceId sender, Address target, Event event);

    /**
     * (Asynchronous) Unicast an event to an intended receiver.
     *
     * @param sender   id as returned by {@link #registerService(String)}
     * @param receiver intended recipient, as extracted from a received message, or as resulted from discovery
     * @param event    being unicast
     * @see BezirkListener#receiveEvent(String, String, ServiceEndPoint)
     */
    public void sendEvent(ServiceId sender, ServiceEndPoint receiver, Event event);

    /**
     * Unicast a stream to an intended receiver. The stream is described by s and fed by data written into p.
     *
     * @param sender   id as returned by {@link #registerService(String)}
     * @param receiver intended recipient, as extracted from a request message, or as resulted from discovery
     * @param s        descriptor of the stream
     * @param p        stream where the outgoing data will be written into by the invoker of this method.
     *                 Bezirk middelware will read data from p in a thread-safe way by creating a PipedInputStream linked to p
     * @return Bezirk middelware-generated id for the stream, which will be referred to in {@link BezirkListener#streamStatus(short, BezirkListener.StreamConditions)}
     * @see BezirkListener#receiveStream(String, String, short, java.io.InputStream, ServiceEndPoint)
     * @see BezirkListener#receiveStream(String, String, short, String, ServiceEndPoint)
     */
    public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream s, PipedOutputStream p);

    /**
     * Same as {@link Bezirk#sendStream(ServiceId, ServiceEndPoint, Stream, PipedOutputStream)} but taking a file as the source of data.
     */
    public short sendStream(ServiceId sender, ServiceEndPoint receiver, Stream s, String filePath);

    /**
     * Requests a pipe p to be added to all Spheres this service is a member of.
     *
     * @param requester  id as returned by {@link #registerService(String)}
     * @param p
     * @param allowedIn  protocols allowed to flow from the pipe into the sphere.  No constraint on the protocols, if NULL.
     * @param allowedOut protocols allowed to flow from the sphere into the pipe.  No constraint on the protocols, if NULL.
     * @param listener   object for the callback issued by Bezirk middelware once the pipe is verified by the user
     * @see BezirkListener#pipeGranted(Pipe, PipePolicy, PipePolicy)
     */
    public void requestPipe(ServiceId requester, Pipe p, PipePolicy allowedIn, PipePolicy allowedOut, BezirkListener listener);

    /**
     * Requests being informed of the policy for p.
     *
     * @param p
     * @param listener object for the callback issued by Bezirk middelware
     * @see BezirkListener#pipeGranted(Pipe, PipePolicy, PipePolicy)
     */
    public void getPipePolicy(Pipe p, BezirkListener listener);

    /**
     * Requests the discovery of services which support pRole, within the spheres that the service is a member of.
     * Bezirk middelware will issue a callback with the results once either timeout expires or the number of replies reaches maxDiscovered.
     * For each service, Bezirk middelware manages one outstanding request at a time:
     * a service may issue another discover request once the results of the previous request no longer matter.
     *
     * @param service       id as returned by {@link #registerService(String)}
     * @param scope         address for discovery
     * @param pRole         specific role to discover, or NULL if discovering all services within the Sphere(s) the invoker is a member of
     * @param timeout       max time Bezirk middelware is to wait for replies to come in
     * @param maxDiscovered number of replies that Bezirk middelware is to wait for
     * @param listener      object for the callback issued by Bezirk middelware upon gathering of discovery results
     * @see BezirkListener#discovered(java.util.Set)
     * @see #setLocation(ServiceId, Location)
     */
    public void discover(ServiceId service, Address scope, ProtocolRole pRole, long timeout, int maxDiscovered, BezirkListener listener);

    /**
     * Informs Bezirk middelware of the location of the service, when distinct from the location of the host device: useful for proxy devices, e.g. CCU.
     * It may be invoked each time a mobile service changes location.
     * The location of a service is used to match location constraints during discovery and semantic addressing of events.
     *
     * @param service  id as returned by {@link #registerService(String)}
     * @param location
     */
    public void setLocation(ServiceId service, Location location);
}

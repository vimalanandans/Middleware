/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.middleware;

import java.io.InputStream;
import java.util.Set;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.middleware.addressing.DiscoveredService;

/**
 * Services that subscribe to a protocol role, or that request discovery, must designate an object that implements this interface.
 */
public interface IBezirkListener {
	
	public enum StreamConditions {LOST_CONNECTION, END_OF_DATA}
	public enum PipeConditions {LOST_CONNECTION}
	
	/**
	 * Callback issued by Bezirk middleware each time an event arrives that matches a subscription.
	 * 
	 * @param topic as string
	 * @param event serialized as string
	 * @param sender of the event: may be used to unicast a reply back to the sender
	 *
	 */
	public void receiveEvent(String topic, String event, ServiceEndPoint sender);

	/**
	 * Callback issued by Bezirk middleware each time a stream arrives that matches a subscription.
	 * This callback is for streams marked {@link Stream#isIncremental()}
	 *  
	 * @param topic as string
	 * @param stream serialized as string
	 * @param streamId Bezirk middleware-generated id for the stream, which will be referred to in {@link #streamStatus(short, StreamConditions)}
	 * @param f inputstream containing the data to be processed
	 * @param sender of the stream: may be used to unicast a reply back to the sender
	 *
	 */
	public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender);

	/**
	 * Same as {@link #receiveStream(String, String, short, InputStream, ServiceEndPoint)} but where the incoming data is offered in a file.
	 * This callback is for streams that set {@link Stream#isIncremental()} to false.
	 */
	public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender);
	
	/**
	 * Callback issued by Bezirk middleware if something unexpected happens, or when an incremental stream closes.
	 * 
	 * @param streamId as returned by {@link IBezirk#sendStream(ServiceId, ServiceEndPoint, Stream, java.io.PipedOutputStream)}
	 * or passed in {@link #receiveStream(String, String, short, InputStream, ServiceEndPoint)}
	 * @param status
	 */
	public void streamStatus(short streamId, StreamConditions status);

	/**
	 * @param p the pipe, or NULL if denied entirely
	 * @param allowedIn protocols allowed to flow from the pipe into the sphere.  No constraint on the protocols, if NULL.
	 * @param allowedOut protocols allowed to flow from the sphere into the pipe.  No constraint on the protocols, if NULL.
	 * @param granted True if the pipe was granted
	 */
	public void pipeGranted(Pipe p, PipePolicy allowedIn, PipePolicy allowedOut);

	// The above will be changed in next version to:
	//public void pipeGranted(boolean granted, Pipe p, PipePolicy allowedIn, PipePolicy allowedOut);

	/**
	 * Callback issued by Bezirk middleware if something unexpected happens.
	 * @param p as passed in {@link #pipeGranted(Pipe, PipePolicy, PipePolicy)}
	 */
	public void pipeStatus(Pipe p, PipeConditions status );
	
	/**
	 * Callback issued by Bezirk middleware once the outstanding {@link IBezirk#discover(ServiceId, Address, ProtocolRole, long, int, IBezirkListener)} runs its course
	 * 
	 * @param serviceSet
	 */
	public void discovered(Set<DiscoveredService> serviceSet);
	
}

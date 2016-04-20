package com.bezirk.control.messages.discovery;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class DiscoveryRequest extends MulticastControlMessage {
	private long timeout = 0;
	private int maxDiscovered = 0;
	private Location location;
	private SubscribedRole protocol;
	private final int discoveryId;	
	private final static Discriminator discriminator = ControlMessage.Discriminator.DiscoveryRequest;
	
	/**
	 * 
	 * @param sphereId
	 * @param sender
	 * @param location
	 * @param protocol
	 * @param discoveryId This id maps to the original request that then gets 
	 * split into different control messages, one per sphere
	 * @param timeout
	 * @param maxDiscovered
	 */
	public DiscoveryRequest(String sphereId, UhuServiceEndPoint sender, Location location, SubscribedRole protocol, int discoveryId, long timeout, int maxDiscovered){
		super(sender, sphereId, discriminator);
		this.location = location;
		this.protocol = protocol;
		this.discoveryId = discoveryId;
		this.timeout = timeout;
		this.maxDiscovered = maxDiscovered;
	}

	public long getTimeout() {
		return timeout;
	}



	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}



	public int getMaxDiscovered() {
		return maxDiscovered;
	}



	public void setMaxDiscovered(int maxDiscovered) {
		this.maxDiscovered = maxDiscovered;
	}


	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public SubscribedRole getProtocol() {
		return protocol;
	}

	public void setProtocol(SubscribedRole protocol) {
		this.protocol = protocol;
	}

	public int getDiscoveryId() {
		return discoveryId;
	}

	
	
}

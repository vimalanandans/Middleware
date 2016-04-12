package com.bezirk.sphere.messages;

import java.util.List;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;


/**
 *  
 *    Control message for SphereCatchDiscoveryResponse to send the list of messages
 * 		FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryResponse extends MulticastControlMessage {
	private List<UhuServiceId> services;
	private final static Discriminator discriminator = ControlMessage.Discriminator.SphereDiscoveryResponse;
	/**
	 * Constructor
	 * 
	 * @param sphereID
	 *            Sphere to be created at the recipient device
	 * @param sphereInformation
	 *            Information about the sphere to be joined
	 * @param services
	 *            Services that need to join the target sphere
	 */

	public SphereDiscoveryResponse(String scannedSphereId,
			List<UhuServiceId> services, UhuServiceEndPoint sender) {
		super(sender, scannedSphereId, discriminator);
		this.services = services;		

	}
	
	/**
	 * @return the services
	 */
	public final List<UhuServiceId> getServices() {
		return services;
	}
	

}

package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;


/**
 *  
 *    Control message for SphereDiscoveryRequest
 * 		FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryRequest extends MulticastControlMessage {
	
	private final static Discriminator discriminator = ControlMessage.Discriminator.SphereDiscoveryRequest;
	

	/**
	 * Constructor
	 * 
	 * @param sphereID
	 *            Sphere id to be scanned for services (target)
	 * @param sphereInformation
	 *            Information about the sphere to be joined
	 * @param services
	 *            Services that need to join the target sphere
	 */

	public SphereDiscoveryRequest(String scanSphereId,
			UhuServiceEndPoint sender) {
		super(sender, scanSphereId, discriminator);	
	}


	

}

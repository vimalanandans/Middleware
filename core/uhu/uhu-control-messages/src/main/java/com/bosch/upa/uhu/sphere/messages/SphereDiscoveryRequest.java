package com.bosch.upa.uhu.sphere.messages;

import java.util.List;

import com.bosch.upa.uhu.api.objects.SphereVitals;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.MulticastControlMessage;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


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

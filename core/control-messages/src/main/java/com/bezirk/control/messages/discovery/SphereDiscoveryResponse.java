/**
 * 
 */
package com.bezirk.control.messages.discovery;

import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

/**
 * @author Rishabh Gulati
 *
 */
public final class SphereDiscoveryResponse extends com.bezirk.control.messages.UnicastControlMessage {

	private UhuSphereInfo uhuSphereInfo;	
	private final Integer reqDiscoveryId;
	private static final Discriminator discriminator = com.bezirk.control.messages.ControlMessage.Discriminator.SphereDiscoveryResponse;
	//ServiceEndPoint for this response only contains deviceID
	private static final UhuServiceEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(null);
	
	
	public SphereDiscoveryResponse(UhuServiceEndPoint recipient, String sphereId,  String reqKey, int discId){
		super(sender, recipient, sphereId, discriminator, false, reqKey);
		//TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;        
	}
		
	

	/**
	 * @return the uhuSphereInfo
	 */
	public final UhuSphereInfo getUhuSphereInfo() {
		return uhuSphereInfo;
	}



	/**
	 * @param uhuSphereInfo the uhuSphereInfo to set
	 */
	public final void setUhuSphereInfo(UhuSphereInfo uhuSphereInfo) {
		this.uhuSphereInfo = uhuSphereInfo;
	}



	public Integer getReqDiscoveryId() {
		return reqDiscoveryId;
	}

}

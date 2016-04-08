/**
 * 
 */
package com.bosch.upa.uhu.control.messages.discovery;

import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;

/**
 * @author Rishabh Gulati
 *
 */
public final class SphereDiscoveryResponse extends UnicastControlMessage{

	private UhuSphereInfo uhuSphereInfo;	
	private final Integer reqDiscoveryId;
	private static final Discriminator discriminator = ControlMessage.Discriminator.SphereDiscoveryResponse;
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

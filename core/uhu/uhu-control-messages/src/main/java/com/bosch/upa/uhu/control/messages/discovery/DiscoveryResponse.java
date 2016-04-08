package com.bosch.upa.uhu.control.messages.discovery;

import java.util.ArrayList;
import java.util.List;

import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;


public class DiscoveryResponse extends UnicastControlMessage {
	private final List<UhuDiscoveredService> serviceList;
	private final Integer reqDiscoveryId;
	private static final Discriminator discriminator = ControlMessage.Discriminator.DiscoveryResponse;
	//ServiceEndPoint for this response only contains deviceID
	private static final UhuServiceEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(null);
	
	
	public DiscoveryResponse(UhuServiceEndPoint recipient, String sphereId,  String reqKey, int discId){
		super(sender, recipient, sphereId, discriminator, false, reqKey);
		//TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;
        this.serviceList = new ArrayList<UhuDiscoveredService>();
	}

	public List<UhuDiscoveredService> getServiceList() {
		return serviceList;
	}

	public Integer getReqDiscoveryId() {
		return reqDiscoveryId;
	}

}

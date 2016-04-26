package com.bezirk.discovery;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ControlLedger;
import DiscoveryRequest;
import UhuNetworkUtilities;
import UhuZirkEndPoint;
import UhuZirkId;
import UhuValidatorUtility;*/

/**
 * FIXME: Unused module. removed it - Vimal
 **/

/*public class SphereDiscoverySender {
    private static final Logger logger =LoggerFactory.getLogger(SphereDiscoverySender.class);

	
	public SphereDiscoverySender(final String sphere, final int discoveryId, final long timeout, final int maxDiscovered) throws Exception{
		 if(!UhuValidatorUtility.checkForString(sphere)){
	            logger.error( "sphere name is null, Dropping discovery request from User");
	            throw new IllegalArgumentException("sphere name is null, Dropping discovery request from User");
	        }
	        final String serviceIdStr = "______SPHERESCANNER#2";
	        final UhuZirkId serviceId = new UhuZirkId(serviceIdStr);
	        final ControlLedger transControlMessage = new ControlLedger();
	        final UhuZirkEndPoint sender = UhuNetworkUtilities.getZirkEndPoint(serviceId);
	        final DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphere,sender,null,null,discoveryId,timeout,maxDiscovered);
	        transControlMessage.setMessage(discoveryRequest);
	        transControlMessage.setSphereId(sphere);
	        transControlMessage.setSerializedMessage(transControlMessage.getMessage().toJson());

            // FIXME commented out because no one is using  this module - Vimal
	        // MessageQueueManager.getControlSenderQueue().addToQueue(transControlMessage);

	        //Add discovery record
	        DiscoveryLabel dLabel = new DiscoveryLabel(sender, discoveryId,true);
	        DiscoveryRecord discoveryRecord = new DiscoveryRecord(timeout, maxDiscovered);
	        DiscoveryProcessor.getDiscovery().addRequest(dLabel, discoveryRecord);
	}

}*/

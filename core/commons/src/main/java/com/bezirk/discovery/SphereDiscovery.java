package com.bezirk.discovery;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.sphere.api.IUhuSphereDiscovery;
import com.bezirk.util.UhuValidatorUtility;

/**
 * Created by Rishabh Gulati on 12/19/2014.
 * modified by Vimal
 */
public class SphereDiscovery {

    private static final Logger log = LoggerFactory.getLogger(SphereDiscovery.class);

    private final ConcurrentHashMap<DiscoveryLabel, SphereDiscoveryRecord> discoveredMap;
    
    //TODO change the name
    //private final IUhuSphereDiscovery sphereDiscoveryHandler;

    public SphereDiscovery(IUhuSphereDiscovery sphereDiscoveryHandler) {
        discoveredMap = new ConcurrentHashMap<DiscoveryLabel, SphereDiscoveryRecord>();
       // this.sphereDiscoveryHandler = sphereDiscoveryHandler;
    }

    public ConcurrentHashMap<DiscoveryLabel, SphereDiscoveryRecord> getDiscoveredMap() throws InterruptedException {
        synchronized (this) {
			while (discoveredMap.keySet().size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					log.warn("Discovery Queue Interrupted");
					throw e;
				}
			}
			return discoveredMap;
		}
    }

    public void remove(DiscoveryLabel dlabel) {
        synchronized (this) {
			discoveredMap.remove(dlabel);
			log.info("Discovery removed > " + dlabel.getRequester().device);
		}
    }

    public void addRequest(DiscoveryLabel dlabel, SphereDiscoveryRecord disc) {
        synchronized (this) {
			discoveredMap.put(dlabel, disc);
			log.info("Discovery request added > "
					+ dlabel.getRequester().device);
			//  notifyAll();
		}
    }

    public boolean addResponse(SphereDiscoveryResponse response) {
    	
        synchronized (this) {
			if (UhuValidatorUtility.checkUhuServiceEndPoint(response
					.getRecipient())) {
				
				final DiscoveryLabel dLabel = new DiscoveryLabel(
						response.getRecipient(), response.getReqDiscoveryId());
				final SphereDiscoveryRecord discRecord = discoveredMap.get(dLabel);
				if (UhuValidatorUtility.isObjectNotNull(discRecord)) {
					//update sphere discovery record
					discRecord.updateSet(response.getUhuSphereInfo(),
							response.getSender());
					notifyAll();
					/* final long currentTime = new Date().getTime();
					 
					 if (discRecord.getDiscoveredSetSize() >= discRecord.getMax() || currentTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
					 	log.debug("Timeout for sphere discovery, Size of UhuSphereInfos discovered : "+discRecord.getSphereServices().size());                    
					 	sphereDiscoveryHandler.processDiscoveredSphereInfo(discRecord.getSphereServices(), discRecord.getSphereId());                    
					     
					     discoveredMap.remove(dLabel);
					     log.info("Discovery response added > " + dLabel.getRequester().device);
					     return true;
					 }*/
					return true;

				} else

				{
					log.warn("Nothing to add " + "discovery is not pending");
					return false;
				}

			}else{
				
				return false;
			}
			
		}
    }
}
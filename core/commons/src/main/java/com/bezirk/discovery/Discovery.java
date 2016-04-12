package com.bezirk.discovery;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.util.UhuValidatorUtility;
import com.google.gson.Gson;

public class Discovery {
    private static final Logger log = LoggerFactory.getLogger(Discovery.class);

    private final ConcurrentHashMap<DiscoveryLabel,DiscoveryRecord> discoveredMap;

    private final Gson gson = new Gson();

    public Discovery(){
        discoveredMap = new ConcurrentHashMap<DiscoveryLabel,DiscoveryRecord>();
    }

    public ConcurrentHashMap<DiscoveryLabel,DiscoveryRecord> getDiscoveredMap() throws InterruptedException {
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

    public void remove(DiscoveryLabel dlabel){
        synchronized (this) {
			discoveredMap.remove(dlabel);
		}
    }

    public void addRequest(DiscoveryLabel dlabel, DiscoveryRecord disc){
        synchronized (this) {
			discoveredMap.put(dlabel, disc);
			notifyAll();
		}
    }

    public boolean addResponse(DiscoveryResponse response){
        synchronized (this) {
			if (UhuValidatorUtility.checkUhuServiceEndPoint(response
					.getRecipient())) {
				final DiscoveryLabel dLabel = new DiscoveryLabel(
						response.getRecipient(), response.getReqDiscoveryId());
				final DiscoveryRecord discRecord = discoveredMap.get(dLabel);
				if (UhuValidatorUtility.isObjectNotNull(discRecord)) {
					//update discovered services list
					discRecord.updateList(response.getServiceList());
					final long currentTime = new Date().getTime();
					if (discRecord.getDiscoveredListSize() >= discRecord.getMax()
							|| currentTime - discRecord.getCreationTime() >= discRecord
									.getTimeout()) {
						DiscoveryIncomingMessage callbackMessage = new DiscoveryIncomingMessage(
								dLabel.getRequester().serviceId,
								gson.toJson(discRecord.getList()),
								dLabel.getDiscoveryId(), dLabel.isSphereDiscovery());
						UhuCompManager.getplatformSpecificCallback()
								.onDiscoveryIncomingMessage(callbackMessage);
						discoveredMap.remove(dLabel);
						return true;
					}
					return true;
				} else {
					log.warn("Nothing to add " + "discovery is not pending");
					return false;
				}
				
				
			}else{
				
				return false;
			}
			

		}
    }
}

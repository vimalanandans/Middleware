package com.bezirk.sphere.discovery;

import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.pubsubbroker.discovery.DiscoveryLabel;
import com.bezirk.sphere.api.BezirkSphereDiscovery;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rishabh Gulati on 12/19/2014.
 * modified by Vimal
 */
public class SphereDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(SphereDiscovery.class);

    private final Map<DiscoveryLabel, SphereDiscoveryRecord> discoveredMap;

    //TODO change the name
    //private final BezirkSphereDiscovery sphereDiscoveryHandler;

    public SphereDiscovery(BezirkSphereDiscovery sphereDiscoveryHandler) {
        discoveredMap = new ConcurrentHashMap<DiscoveryLabel, SphereDiscoveryRecord>();
        // this.sphereDiscoveryHandler = sphereDiscoveryHandler;
    }

    public ConcurrentHashMap<DiscoveryLabel, SphereDiscoveryRecord> getDiscoveredMap() throws InterruptedException {
        synchronized (this) {
            while (discoveredMap.keySet().size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.warn("Discovery Queue Interrupted");
                    throw e;
                }
            }
            return (ConcurrentHashMap) discoveredMap;
        }
    }

    public void remove(DiscoveryLabel discoveryLabel) {
        synchronized (this) {
            discoveredMap.remove(discoveryLabel);
            logger.info("Discovery removed > " + discoveryLabel.getRequester().device);
        }
    }

    public void addRequest(DiscoveryLabel dlabel, SphereDiscoveryRecord disc) {
        synchronized (this) {
            discoveredMap.put(dlabel, disc);
            logger.info("Discovery request added > "
                    + dlabel.getRequester().device);
            //  notifyAll();
        }
    }

    public boolean addResponse(SphereDiscoveryResponse response) {

        synchronized (this) {
            if (BezirkValidatorUtility.checkBezirkZirkEndPoint(response
                    .getRecipient())) {

                final DiscoveryLabel dLabel = new DiscoveryLabel(
                        response.getRecipient(), response.getReqDiscoveryId());
                final SphereDiscoveryRecord discRecord = discoveredMap.get(dLabel);
                if (BezirkValidatorUtility.isObjectNotNull(discRecord)) {
                    //update sphere discovery record
                    discRecord.updateSet(response.getBezirkSphereInfo(),
                            response.getSender());
                    notifyAll();
                    /* final long currentTime = new Date().getTime();
					 
					 if (discRecord.getDiscoveredSetSize() >= discRecord.getMax() || currentTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
					 	logger.debug("Timeout for sphere discovery, Size of BezirkSphereInfo discovered : "+discRecord.getSphereZirks().size());
					 	sphereDiscoveryHandler.processDiscoveredSphereInfo(discRecord.getSphereZirks(), discRecord.getSphereId());
					     
					     discoveredMap.remove(dLabel);
					     logger.info("Discovery response added > " + dLabel.getRequester().device);
					     return true;
					 }*/
                    return true;

                } else

                {
                    logger.warn("Nothing to add " + "discovery is not pending");
                    return false;
                }

            } else {

                return false;
            }

        }
    }
}

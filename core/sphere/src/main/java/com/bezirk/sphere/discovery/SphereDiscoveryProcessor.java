package com.bezirk.sphere.discovery;

import com.bezirk.comms.Comms;
import com.bezirk.sphere.api.SphereDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rishabh Gulati
 */
public class SphereDiscoveryProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SphereDiscoveryProcessor.class);

    private static com.bezirk.sphere.discovery.SphereDiscovery discovery;
    private final SphereDiscovery sphereDiscoveryHandler;
    private Boolean running = false;
    // private Comms comms = null;

    public SphereDiscoveryProcessor(SphereDiscovery sphereDiscoveryHandler, Comms comms) {
        this.sphereDiscoveryHandler = sphereDiscoveryHandler;
        // this.comms = comms;
    }

    public static com.bezirk.sphere.discovery.SphereDiscovery getDiscovery() {
        return discovery;
    }

    public static void setDiscovery(com.bezirk.sphere.discovery.SphereDiscovery discovery) {
        SphereDiscoveryProcessor.discovery = discovery;
    }

    @Override
    public void run() {
        logger.info("DiscoveryProcessor has started");
        running = true;
        while (running) {
            //Stop by Interrupting Thread
            if (Thread.currentThread().isInterrupted()) {
                stop();
                continue;
            }
//			//Copy a list of pending discoveries
            CopyOnWriteArrayList<com.bezirk.pubsubbroker.discovery.DiscoveryLabel> discoveryLabels;

            try {
                discoveryLabels = new CopyOnWriteArrayList<com.bezirk.pubsubbroker.discovery.DiscoveryLabel>(SphereDiscoveryProcessor.discovery.getDiscoveredMap().keySet());
            } catch (InterruptedException e) {
                this.stop();
                continue;
            }

            for (com.bezirk.pubsubbroker.discovery.DiscoveryLabel discoveryLabel : discoveryLabels) {
                SphereDiscoveryRecord discRecord;
                try {
                    discRecord = discovery.getDiscoveredMap().get(discoveryLabel);
                } catch (InterruptedException e) {
                    this.stop();
                    return;
                }
                //Check if request is still in pending map
                //if not then continue iterating
                if (discRecord == null) {
                    continue;
                }
                checkRequestTimeOutAndInvokeRequestor(discoveryLabel,
                        discRecord);
            }
        }

    }

    private void checkRequestTimeOutAndInvokeRequestor(com.bezirk.pubsubbroker.discovery.DiscoveryLabel discoveryLabel,
                                                       SphereDiscoveryRecord discRecord) {
        long curTime = new Date().getTime();
        //If discovery request has timed out
        //	Invoke the requester with the discovered and drop the request
        if (curTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
            logger.debug("Timeout for sphere discovery, Size of BezirkSphereInfo discovered : " + discRecord.getSphereZirks().size());
            if (sphereDiscoveryHandler != null) {

                sphereDiscoveryHandler.processDiscoveredSphereInfo(discRecord.getSphereZirks(), discRecord.getSphereId());
            }

            SphereDiscoveryProcessor.discovery.remove(discoveryLabel);
            logger.info("Discovery response added > " + discoveryLabel.getRequester().device);
        } else { // time not exceeded wait for some time before trying so that thread over run
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.error("wait interrupted", e);

            }
        }
    }

    public void stop() {
        running = false;
        logger.info("DiscoveryProcessor has stopped");
    }
}

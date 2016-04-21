package com.bezirk.discovery;

import com.bezirk.comms.IUhuComms;
import com.bezirk.sphere.api.IUhuSphereDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Rishabh Gulati on 12/19/2014.
 */
public class SphereDiscoveryProcessor implements Runnable {


    private static final Logger log = LoggerFactory.getLogger(SphereDiscoveryProcessor.class);
    private static SphereDiscovery discovery;
    private final IUhuSphereDiscovery sphereDiscoveryHandler;
    private Boolean running = false;
    // private IUhuComms uhuComms = null;

    public SphereDiscoveryProcessor(IUhuSphereDiscovery sphereDiscoveryHandler, IUhuComms uhuComms) {
        this.sphereDiscoveryHandler = sphereDiscoveryHandler;
        // this.uhuComms = uhuComms;
    }

    public static SphereDiscovery getDiscovery() {
        return discovery;
    }

    public static void setDiscovery(SphereDiscovery discovery) {
        SphereDiscoveryProcessor.discovery = discovery;
    }

    @Override
    public void run() {
        log.info("DiscoveryProcessor has started");
        running = true;
        while (running) {
            //Stop by Interrupting Thread
            if (Thread.currentThread().isInterrupted()) {
                stop();
                continue;
            }
//			//Copy a list of pending discoveries
            CopyOnWriteArrayList<DiscoveryLabel> dlabelList;

            try {
                dlabelList = new CopyOnWriteArrayList<DiscoveryLabel>(SphereDiscoveryProcessor.discovery.getDiscoveredMap().keySet());
            } catch (InterruptedException e) {
                this.stop();
                continue;
            }

            Iterator<DiscoveryLabel> it = dlabelList.iterator();

            while (it.hasNext()) {
                DiscoveryLabel dlbl = it.next();
                SphereDiscoveryRecord discRecord;
                try {
                    discRecord = discovery.getDiscoveredMap().get(dlbl);
                } catch (InterruptedException e) {
                    this.stop();
                    return;
                }
                //Check if request is still in pending map
                //if not then continue iterating
                if (discRecord == null) {
                    continue;
                }
                checkRequestTimeOutAndInvokeRequestor(dlbl,
                        discRecord);
            }
        }

    }

    private void checkRequestTimeOutAndInvokeRequestor(DiscoveryLabel dlbl,
                                                       SphereDiscoveryRecord discRecord) {
        long curTime = new Date().getTime();
        //If discovery request has timed out
        //	Invoke the requester with the discovered and drop the request
        if (curTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
            log.debug("Timeout for sphere discovery, Size of UhuSphereInfos discovered : " + discRecord.getSphereServices().size());
            if (sphereDiscoveryHandler != null) {

                sphereDiscoveryHandler.processDiscoveredSphereInfo(discRecord.getSphereServices(), discRecord.getSphereId());
            }

            SphereDiscoveryProcessor.discovery.remove(dlbl);
            log.info("Discovery response added > " + dlbl.getRequester().device);
        } else { // time not exceeded wait for some time before trying so that thread over run
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.error("wait interrupted", e);

            }
        }
    }

    public void stop() {
        running = false;
        log.info("DiscoveryProcessor has stopped");
    }
}

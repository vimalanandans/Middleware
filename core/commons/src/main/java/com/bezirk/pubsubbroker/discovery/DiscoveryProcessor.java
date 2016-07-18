package com.bezirk.pubsubbroker.discovery;

import com.bezirk.BezirkCompManager;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryProcessor.class);
    private static Discovery discovery;
    private Boolean running = false;

    public static Discovery getDiscovery() {
        return discovery;
    }

    public static void setDiscovery(Discovery discovery) {
        DiscoveryProcessor.discovery = discovery;
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
            CopyOnWriteArrayList<DiscoveryLabel> discoveryLabels;
            try {
                discoveryLabels = new CopyOnWriteArrayList<DiscoveryLabel>(DiscoveryProcessor.discovery.getDiscoveredMap().keySet());
            } catch (InterruptedException e) {
                this.stop();
                continue;
            }

            for (DiscoveryLabel discoveryLabel : discoveryLabels) {
                com.bezirk.pubsubbroker.discovery.DiscoveryRecord discRecord;
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
                long curTime = new Date().getTime();
                //If discovery request has timed out
                //	Invoke the requester with the discovered and drop the request
                if (curTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
                    final Gson gson = new Gson();
                    DiscoveryIncomingMessage callbackMessage = new DiscoveryIncomingMessage(discoveryLabel.getRequester().zirkId,
                            gson.toJson(discRecord.getList()), discoveryLabel.getDiscoveryId(), discoveryLabel.isSphereDiscovery());
                    getDiscovery().messageHandler.onDiscoveryIncomingMessage(callbackMessage);
                    DiscoveryProcessor.discovery.remove(discoveryLabel);
                }
            }
        }

    }

    public void stop() {
        running = false;
        logger.info("DiscoveryProcessor has stopped");
    }
}

package com.bezirk.pubsubbroker.discovery;


import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.proxy.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.util.ValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

public class Discovery {
    private static final Logger logger = LoggerFactory.getLogger(Discovery.class);

    private final ConcurrentMap<DiscoveryLabel, com.bezirk.pubsubbroker.discovery.DiscoveryRecord> discoveredMap;

    private final Gson gson = new Gson();

    public MessageHandler messageHandler;

    public Discovery() {
        discoveredMap = new ConcurrentHashMap<DiscoveryLabel, com.bezirk.pubsubbroker.discovery.DiscoveryRecord>();
    }

    public ConcurrentHashMap<DiscoveryLabel, com.bezirk.pubsubbroker.discovery.DiscoveryRecord> getDiscoveredMap() throws InterruptedException {
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

    public void remove(DiscoveryLabel dlabel) {
        synchronized (this) {
            discoveredMap.remove(dlabel);
        }
    }

    public void addRequest(DiscoveryLabel discoveryLabel, com.bezirk.pubsubbroker.discovery.DiscoveryRecord disc, MessageHandler msgHandler) {
        synchronized (this) {
            discoveredMap.put(discoveryLabel, disc);

            this.messageHandler = msgHandler;

            notifyAll();
        }
    }

    public boolean addResponse(DiscoveryResponse response) {
        synchronized (this) {
            if (ValidatorUtility.checkBezirkZirkEndPoint(response
                    .getRecipient())) {
                final DiscoveryLabel dLabel = new DiscoveryLabel(
                        response.getRecipient(), response.getReqDiscoveryId());
                final com.bezirk.pubsubbroker.discovery.DiscoveryRecord discRecord = discoveredMap.get(dLabel);
                if (ValidatorUtility.isObjectNotNull(discRecord)) {
                    //update discovered services list
                    discRecord.updateList(response.getZirkList());
                    final long currentTime = new Date().getTime();
                    if (discRecord.getDiscoveredListSize() >= discRecord.getMax()
                            || currentTime - discRecord.getCreationTime() >= discRecord
                            .getTimeout()) {
                        DiscoveryIncomingMessage callbackMessage = new DiscoveryIncomingMessage(
                                dLabel.getRequester().zirkId,
                                gson.toJson(discRecord.getList()),
                                dLabel.getDiscoveryId(), dLabel.isSphereDiscovery());
                        messageHandler.onDiscoveryIncomingMessage(callbackMessage);
                        discoveredMap.remove(dLabel);
                        return true;
                    }
                    return true;
                } else {
                    logger.warn("Nothing to add discovery is not pending");
                    return false;
                }


            } else {

                return false;
            }


        }
    }
}

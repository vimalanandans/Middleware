package com.bezirk.discovery;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class DiscoveryProcessor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DiscoveryProcessor.class);
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
                dlabelList = new CopyOnWriteArrayList<DiscoveryLabel>(DiscoveryProcessor.discovery.getDiscoveredMap().keySet());
            } catch (InterruptedException e) {
                this.stop();
                continue;
            }

            Iterator<DiscoveryLabel> it = dlabelList.iterator();
            while (it.hasNext()) {
                DiscoveryLabel dlbl = it.next();
                DiscoveryRecord discRecord;
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
                long curTime = new Date().getTime();
                //If discovery request has timed out
                //	Invoke the requestor with the discovered and drop the request
                if (curTime - discRecord.getCreationTime() >= discRecord.getTimeout()) {
                    final Gson gson = new Gson();
                    DiscoveryIncomingMessage callbackMessage = new DiscoveryIncomingMessage(dlbl.getRequester().zirkId, gson.toJson(discRecord.getList()), dlbl.getDiscoveryId(), dlbl.isSphereDiscovery());
                    UhuCompManager.getplatformSpecificCallback().onDiscoveryIncomingMessage(callbackMessage);
                    DiscoveryProcessor.discovery.remove(dlbl);
                }
            }
        }

    }

    public void stop() {
        running = false;
        log.info("DiscoveryProcessor has stopped");
    }
}

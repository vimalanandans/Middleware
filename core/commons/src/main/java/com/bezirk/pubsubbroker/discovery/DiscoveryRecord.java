package com.bezirk.pubsubbroker.discovery;

import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class DiscoveryRecord {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryRecord.class);

    private final int max;
    private final long timeout;
    //public DiscoveryResponse response;
    private final HashSet<BezirkDiscoveredZirk> list;
    private final long createTime;

    //*** public DiscoveryRecord(timeout, max)
    public DiscoveryRecord(long timeout, int max) {
        this.max = max;
        this.timeout = timeout;
        this.createTime = new Date().getTime();
        list = new HashSet<BezirkDiscoveredZirk>();
    }

    public int getMax() {
        return max;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getCreationTime() {
        return createTime;
    }

    public int getDiscoveredListSize() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public HashSet<BezirkDiscoveredZirk> getList() {
        return list;
    }

    public void updateList(List<BezirkDiscoveredZirk> discoveredZirks) {
        for (BezirkDiscoveredZirk zirk : discoveredZirks) {
            if (!this.list.contains(zirk)) { //Check if ZirkEndPoint Exists
                logger.debug("Updating discList w SED-{}:{}", zirk.zirk.device, zirk.zirk.zirkId.getZirkId());
                this.list.add(zirk);
            }

        }
    }

}


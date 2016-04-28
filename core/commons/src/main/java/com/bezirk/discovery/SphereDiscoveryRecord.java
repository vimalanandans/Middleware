package com.bezirk.discovery;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class SphereDiscoveryRecord {
    private static final Logger logger = LoggerFactory.getLogger(SphereDiscoveryRecord.class);

    private final int max;
    private final long timeout;
    // Key is used for identifying the device sending discovery
    // response. Can be replaced with an alternative identifier. Currently the
    // device field from BezirkZirkEndPoint is used
    private final HashMap<String, BezirkSphereInfo> sphereInfoMap;
    private final long createTime;
    private final String sphereId;

    // *** public DiscoveryRecord(timeout, max)
    public SphereDiscoveryRecord(String sphereId, long timeout, int max) {
        this.sphereId = sphereId;
        this.max = max;
        this.timeout = timeout;
        this.createTime = new Date().getTime();
        sphereInfoMap = new HashMap<String, BezirkSphereInfo>();
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

    public String getSphereId() {
        return sphereId;
    }

    public int getDiscoveredSetSize() {
        if (sphereInfoMap != null) {
            return sphereInfoMap.size();
        }
        return 0;
    }

    // TODO: change to a single BezirkSphereInfo object instead of returning a list
    // of BezirkSphereInfo
    public HashSet<BezirkSphereInfo> getSphereZirks() {
        // BezirkSphereInfo uhuSphereInfo = null;
        // if (sphereInfoMap.size() != 0) {
        // copy an existing entry using the copy constructor
        // uhuSphereInfo = new
        // BezirkSphereInfo(sphereInfoMap.values().iterator().next());
        // for (BezirkSphereInfo info : sphereInfoMap.values()) {
        // if(uhuSphereInfo.getDeviceList().)
        // }
        // }
        return new HashSet<BezirkSphereInfo>(sphereInfoMap.values());
    }

    public void updateSet(BezirkSphereInfo bezirkSphereInfo,
                          BezirkZirkEndPoint bezirkZirkEndPoint) {
        // updating if the value already exists to get the latest version
        sphereInfoMap.put(bezirkZirkEndPoint.device, bezirkSphereInfo);
        //printMap();
    }

    private void printMap() {
        logger.debug("----------------------- Discovered Information Status -----------------------");
        for (Entry<String, BezirkSphereInfo> entry : sphereInfoMap
                .entrySet()) {
            logger.debug("Device: " + entry.getKey());
            logger.debug("Information: " + entry.getValue());
            logger.debug("-----------------------");

        }
    }

}

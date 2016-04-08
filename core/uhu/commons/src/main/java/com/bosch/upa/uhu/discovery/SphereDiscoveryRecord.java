package com.bosch.upa.uhu.discovery;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;

public class SphereDiscoveryRecord {
	private static final Logger log = LoggerFactory
			.getLogger(SphereDiscoveryRecord.class);

	private final int max;
	private final long timeout;
	// Key is used for identifying the device sending discovery
	// response. Can be replaced with an alternative identifier. Currently the
	// device field from UhuServiceEndPoint is used
	private final HashMap<String, UhuSphereInfo> sphereInfoMap;
	private final long createTime;
	private final String sphereId;

	// *** public DiscoveryRecord(timeout, max)
	public SphereDiscoveryRecord(String sphereId, long timeout, int max) {
		this.sphereId = sphereId;
		this.max = max;
		this.timeout = timeout;
		this.createTime = new Date().getTime();
		sphereInfoMap = new HashMap<String, UhuSphereInfo>();
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

	public String getSphereId(){
		return sphereId;
	}
	
	public int getDiscoveredSetSize() {
		if (sphereInfoMap != null) {
			return sphereInfoMap.size();
		}
		return 0;
	}

	// TODO: change to a single UhuSphereInfo object instead of returning a list
	// of UhuSphereInfo
	public HashSet<UhuSphereInfo> getSphereServices() {
		// UhuSphereInfo uhuSphereInfo = null;
		// if (sphereInfoMap.size() != 0) {
		// copy an existing entry using the copy constructor
		// uhuSphereInfo = new
		// UhuSphereInfo(sphereInfoMap.values().iterator().next());
		// for (UhuSphereInfo info : sphereInfoMap.values()) {
		// if(uhuSphereInfo.getDeviceList().)
		// }
		// }
		return new HashSet<UhuSphereInfo>(sphereInfoMap.values());
	}

	public void updateSet(UhuSphereInfo uhuSphereInfo,
			UhuServiceEndPoint uhuServiceEndPoint) {
		// updating if the value already exists to get the latest version
		sphereInfoMap.put(uhuServiceEndPoint.device, uhuSphereInfo);
		//printMap();
	}

	private void printMap() {
		log.debug("----------------------- Discovered Information Status -----------------------");
		for (Entry<String, UhuSphereInfo> entry : sphereInfoMap
				.entrySet()) {
			log.debug("Device: " + entry.getKey());
			log.debug("Information: " + entry.getValue());
			log.debug("-----------------------");

		}
	}

}

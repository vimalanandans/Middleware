package com.bezirk.discovery;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.proxy.api.impl.UhuDiscoveredService;


public class DiscoveryRecord {
	private static final Logger log =LoggerFactory.getLogger(DiscoveryRecord.class);

	private final int max;
	private final long timeout;
	//public DiscoveryResponse response;
	private final HashSet<UhuDiscoveredService> list;
	private final long createTime;
	//*** public DiscoveryRecord(timeout, max)
	public DiscoveryRecord(long timeout, int max){
		this.max = max;
		this.timeout = timeout;
		this.createTime = new Date().getTime();
		list = new HashSet<UhuDiscoveredService>();
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

	public int getDiscoveredListSize (){
		if(list !=null){
			return list.size();
		}
		return 0;
	}

	public HashSet<UhuDiscoveredService> getList() {
		return list;
	}

	public void updateList(List<UhuDiscoveredService> list){
		Iterator<UhuDiscoveredService> it = list.iterator();
		while(it.hasNext()){
			UhuDiscoveredService curServ = it.next();
			if(!this.list.contains(curServ)){ //Check if ServiceEndPoint Exists
				log.debug("Updating discList w SED-"+curServ.service.device+":"+curServ.service.serviceId.getUhuServiceId());
				this.list.add(curServ);
			}
			
		}
	}
	
}


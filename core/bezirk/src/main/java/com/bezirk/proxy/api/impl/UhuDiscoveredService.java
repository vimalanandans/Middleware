package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Location;
/**
 * @author: Joao de Sousa (CR/RTC3-NA)
 * With contributions from:
 * Mansimar Aneja (CR/RTC3.1-NA)
 * Vijet Badigannavar (RBEI/EST PJ-SPS)
 * Samarjit Das (CR/RTC3.1-NA)
 * Cory Henson (CR/RTC3.1-NA)
 * Sunil Kumar Meena (RBEI/EST1)
 * Adam Wynne (CR/RTC3.1-NA)
 * Jan Zibuschka (CR/AEA3)
 */
import com.bezirk.middleware.addressing.ServiceEndPoint;


/**
 * A tuple that characterizes a discovered service.
 */
public class UhuDiscoveredService implements DiscoveredService{
	public UhuServiceEndPoint service;
	public String name;
	public String pRole;
	public Location location;	


    public UhuDiscoveredService (){
    	//Empty constructor needed for gson.deserialze
    }

    public UhuDiscoveredService(UhuServiceEndPoint service, String sName, String pRole, Location location){
        this.service = service;
        this.name = sName;
        this.pRole = pRole;
        this.location = location;
    }

	@Override
	public ServiceEndPoint getServiceEndPoint() {
		return this.service;
	}

	@Override
	public String getServiceName() {
		return this.name ;
	}

	@Override
	public String getProtocol() {
		return this.pRole;
	}

	@Override
	public Location getLocation() {
		return this.location;
	}



    @Override
   	public int hashCode() {
       	String s = service.device +":"+ service.serviceId.toString();
   		return s == null ?  0 : s.hashCode();
   	}
    
    @Override
	public boolean equals(Object obj){
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if(obj instanceof UhuDiscoveredService){
			UhuDiscoveredService serv = (UhuDiscoveredService) obj;
			if (this.service.equals(serv.service)){
				return true;
			}
		}			
		return false;
	}

}

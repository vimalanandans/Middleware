package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;

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


/**
 * A tuple that characterizes a discovered service.
 */
public class UhuDiscoveredZirk implements DiscoveredZirk {
    public UhuZirkEndPoint service;
    public String name;
    public String pRole;
    public Location location;


    public UhuDiscoveredZirk() {
        //Empty constructor needed for gson.deserialze
    }

    public UhuDiscoveredZirk(UhuZirkEndPoint service, String sName, String pRole, Location location) {
        this.service = service;
        this.name = sName;
        this.pRole = pRole;
        this.location = location;
    }

    @Override
    public ZirkEndPoint getZirkEndPoint() {
        return this.service;
    }

    @Override
    public String getZirkName() {
        return this.name;
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
        String s = service.device + ":" + service.serviceId.toString();
        return s == null ? 0 : s.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof UhuDiscoveredZirk) {
            UhuDiscoveredZirk serv = (UhuDiscoveredZirk) obj;
            if (this.service.equals(serv.service)) {
                return true;
            }
        }
        return false;
    }

}

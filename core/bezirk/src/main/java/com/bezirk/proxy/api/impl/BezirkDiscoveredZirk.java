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
 * A tuple that characterizes a discovered zirk.
 */
public class BezirkDiscoveredZirk implements DiscoveredZirk {
    public BezirkZirkEndPoint zirk;
    public String name;
    public String pRole;
    public Location location;


    public BezirkDiscoveredZirk() {
        //Empty constructor needed for gson.deserialze
    }

    public BezirkDiscoveredZirk(BezirkZirkEndPoint zirk, String sName, String pRole, Location location) {
        this.zirk = zirk;
        this.name = sName;
        this.pRole = pRole;
        this.location = location;
    }

    @Override
    public ZirkEndPoint getZirkEndPoint() {
        return this.zirk;
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
        String s = zirk.device + ":" + zirk.zirkId.toString();
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
        if (obj instanceof BezirkDiscoveredZirk) {
            BezirkDiscoveredZirk serv = (BezirkDiscoveredZirk) obj;
            if (this.zirk.equals(serv.zirk)) {
                return true;
            }
        }
        return false;
    }

}

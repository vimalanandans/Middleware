package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkEndPoint;

public class UhuZirkEndPoint implements ZirkEndPoint {
    public String device;
    public UhuZirkId serviceId;

    public UhuZirkEndPoint(String d, UhuZirkId serviceId) {
        device = d;
        this.serviceId = serviceId;
    }

    public UhuZirkEndPoint(UhuZirkId serviceId) {
        device = null;
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        String s = this.device + ":" + this.serviceId.toString();
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
        if (obj instanceof UhuZirkEndPoint) {
            UhuZirkEndPoint curEp = (UhuZirkEndPoint) obj;
            return this.device.equals(curEp.device) && this.serviceId.equals(curEp.serviceId);
        }
        return false;
    }

    public UhuZirkId getUhuServiceId() {
        return serviceId;
    }
}

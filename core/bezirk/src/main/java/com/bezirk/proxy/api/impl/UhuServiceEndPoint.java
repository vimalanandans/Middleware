package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ServiceEndPoint;

public class UhuServiceEndPoint implements ServiceEndPoint {
    public String device;
    public UhuServiceId serviceId;

    public UhuServiceEndPoint(String d, UhuServiceId serviceId) {
        device = d;
        this.serviceId = serviceId;
    }

    public UhuServiceEndPoint(UhuServiceId serviceId) {
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
        if (obj instanceof UhuServiceEndPoint) {
            UhuServiceEndPoint curEp = (UhuServiceEndPoint) obj;
            return this.device.equals(curEp.device) && this.serviceId.equals(curEp.serviceId);
        }
        return false;
    }

    public UhuServiceId getUhuServiceId() {
        return serviceId;
    }
}

package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkEndPoint;

import java.io.Serializable;

public class BezirkZirkEndPoint implements ZirkEndPoint, Serializable {
    public String device;
    public final ZirkId zirkId;

    public BezirkZirkEndPoint(String device, ZirkId zirkId) {
        this.device = device;
        this.zirkId = zirkId;
    }

    public BezirkZirkEndPoint(ZirkId zirkId) {
        device = null;
        this.zirkId = zirkId;
    }

    @Override
    public int hashCode() {
        String s = this.device + ":" + this.zirkId.toString();
        return s.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof BezirkZirkEndPoint) {
            BezirkZirkEndPoint curEp = (BezirkZirkEndPoint) obj;
            return this.device.equals(curEp.device) && this.zirkId.equals(curEp.zirkId);
        }
        return false;
    }

    public ZirkId getBezirkZirkId() {
        return zirkId;
    }
}
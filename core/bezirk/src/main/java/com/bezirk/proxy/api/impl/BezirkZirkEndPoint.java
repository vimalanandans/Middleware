package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkEndPoint;

public class BezirkZirkEndPoint implements ZirkEndPoint {
    public String device;
    public final BezirkZirkId zirkId;

    public BezirkZirkEndPoint(String device, BezirkZirkId zirkId) {
        this.device = device;
        this.zirkId = zirkId;
    }

    public BezirkZirkEndPoint(BezirkZirkId zirkId) {
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

    public BezirkZirkId getBezirkZirkId() {
        return zirkId;
    }
}
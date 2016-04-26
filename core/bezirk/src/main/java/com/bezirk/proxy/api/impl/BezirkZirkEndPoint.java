package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkEndPoint;

public class BezirkZirkEndPoint implements ZirkEndPoint {
    public String device;
    public BezirkZirkId zirkId;

    public BezirkZirkEndPoint(String d, BezirkZirkId zirkId) {
        device = d;
        this.zirkId = zirkId;
    }

    public BezirkZirkEndPoint(BezirkZirkId zirkId) {
        device = null;
        this.zirkId = zirkId;
    }

    @Override
    public int hashCode() {
        String s = this.device + ":" + this.zirkId.toString();
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

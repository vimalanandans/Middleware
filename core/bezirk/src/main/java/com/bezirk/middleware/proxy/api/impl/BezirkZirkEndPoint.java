package com.bezirk.middleware.proxy.api.impl;

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

    public ZirkId getBezirkZirkId() {
        return zirkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BezirkZirkEndPoint that = (BezirkZirkEndPoint) o;

        if (!device.equals(that.device)) return false;
        if (!zirkId.equals(that.zirkId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = device.hashCode();
        result = 31 * result + zirkId.hashCode();
        return result;
    }
}
package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkId;

import java.io.Serializable;

public final class UhuZirkId implements ZirkId, Serializable {
    private final String uhuServiceId;
    private String uhuEventId;

    public UhuZirkId(String serviceId) {
        this.uhuServiceId = serviceId;
    }

    public UhuZirkId(String serviceId, String uhuEventId) {
        this.uhuServiceId = serviceId;
        this.uhuEventId = uhuEventId;
    }

    public String getUhuServiceId() {
        return uhuServiceId;
    }

    public String getUhuEventId() {
        return uhuEventId;
    }

    @Override
    public String toString() {
        return "UhuZirkId{" +
                "uhuServiceId='" + uhuServiceId + '\'' +
                "uhuEventId='" + uhuEventId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((uhuEventId == null) ? 0 : uhuEventId.hashCode());
        result = prime * result
                + ((uhuServiceId == null) ? 0 : uhuServiceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UhuZirkId other = (UhuZirkId) obj;
        if (uhuEventId == null) {
            if (other.uhuEventId != null)
                return false;
        } else if (!uhuEventId.equals(other.uhuEventId))
            return false;
        if (uhuServiceId == null) {
            if (other.uhuServiceId != null)
                return false;
        } else if (!uhuServiceId.equals(other.uhuServiceId))
            return false;
        return true;
    }


}
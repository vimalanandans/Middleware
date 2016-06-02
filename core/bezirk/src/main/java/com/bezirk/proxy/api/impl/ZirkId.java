package com.bezirk.proxy.api.impl;

import java.io.Serializable;

public final class ZirkId implements Serializable {
    private final String xirkId;
    private String bezirkEventId;

    public ZirkId(String zirkId) {
        this.xirkId = zirkId;
    }

    public ZirkId(String zirkId, String bezirkEventId) {
        this.xirkId = zirkId;
        this.bezirkEventId = bezirkEventId;
    }

    public String getZirkId() {
        return xirkId;
    }

    public String getBezirkEventId() {
        return bezirkEventId;
    }

    @Override
    public String toString() {
        return "ZirkId{" +
                "xirkId='" + xirkId + '\'' +
                "bezirkEventId='" + bezirkEventId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((bezirkEventId == null) ? 0 : bezirkEventId.hashCode());
        result = prime * result
                + ((xirkId == null) ? 0 : xirkId.hashCode());
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
        ZirkId other = (ZirkId) obj;
        if (bezirkEventId == null) {
            if (other.bezirkEventId != null)
                return false;
        } else if (!bezirkEventId.equals(other.bezirkEventId))
            return false;
        if (xirkId == null) {
            if (other.xirkId != null)
                return false;
        } else if (!xirkId.equals(other.xirkId))
            return false;
        return true;
    }
}
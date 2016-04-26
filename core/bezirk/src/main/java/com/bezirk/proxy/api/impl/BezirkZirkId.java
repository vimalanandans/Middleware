package com.bezirk.proxy.api.impl;

import com.bezirk.middleware.addressing.ZirkId;

import java.io.Serializable;

public final class BezirkZirkId implements ZirkId, Serializable {
    private final String bezirkZirkId;
    private String bezirkEventId;

    public BezirkZirkId(String zirkId) {
        this.bezirkZirkId = zirkId;
    }

    public BezirkZirkId(String zirkId, String bezirkEventId) {
        this.bezirkZirkId = zirkId;
        this.bezirkEventId = bezirkEventId;
    }

    public String getBezirkZirkId() {
        return bezirkZirkId;
    }

    public String getBezirkEventId() {
        return bezirkEventId;
    }

    @Override
    public String toString() {
        return "BezirkZirkId{" +
                "bezirkZirkId='" + bezirkZirkId + '\'' +
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
                + ((bezirkZirkId == null) ? 0 : bezirkZirkId.hashCode());
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
        BezirkZirkId other = (BezirkZirkId) obj;
        if (bezirkEventId == null) {
            if (other.bezirkEventId != null)
                return false;
        } else if (!bezirkEventId.equals(other.bezirkEventId))
            return false;
        if (bezirkZirkId == null) {
            if (other.bezirkZirkId != null)
                return false;
        } else if (!bezirkZirkId.equals(other.bezirkZirkId))
            return false;
        return true;
    }
}
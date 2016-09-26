package com.bezirk.middleware.proxy.api.impl;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class ZirkId implements Serializable {

    private static final long serialVersionUID = -6695395311323807495L;
    private final String zirkId;
    private String bezirkEventId;

    public ZirkId(@NotNull String zirkId) {
        this.zirkId = zirkId;
    }

    public ZirkId(@NotNull String zirkId, String bezirkEventId) {
        this.zirkId = zirkId;
        this.bezirkEventId = bezirkEventId;
    }

    @NotNull
    public String getZirkId() {
        return zirkId;
    }

    public String getBezirkEventId() {
        return bezirkEventId;
    }

    @Override
    public String toString() {
        return "ZirkId{" +
                "zirkId='" + zirkId + '\'' +
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
                + ((zirkId == null) ? 0 : zirkId.hashCode());
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
        if (zirkId == null) {
            if (other.zirkId != null)
                return false;
        } else if (!zirkId.equals(other.zirkId))
            return false;
        return true;
    }
}
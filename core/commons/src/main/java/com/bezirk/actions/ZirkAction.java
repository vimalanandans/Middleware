package com.bezirk.actions;

import com.bezirk.proxy.api.impl.ZirkId;

import java.io.Serializable;

public abstract class ZirkAction implements Serializable {
    private final ZirkId zirkId;

    public ZirkAction(ZirkId zirkId) {
        if (zirkId == null && zirkId.getZirkId() != null && !zirkId.getZirkId().isEmpty()) {
            throw new IllegalArgumentException("zirkId must be set to a non-null and non-empty ID");
        }

        this.zirkId = zirkId;
    }

    public ZirkId getZirkId() {
        return zirkId;
    }
}

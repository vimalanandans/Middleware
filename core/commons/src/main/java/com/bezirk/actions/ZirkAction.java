package com.bezirk.actions;

import com.bezirk.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public abstract class ZirkAction implements Serializable {
    private final ZirkId zirkId;

    public ZirkAction(@NotNull ZirkId zirkId) {
        if (zirkId.getZirkId().isEmpty()) {
            throw new IllegalArgumentException("zirkId must be set to a non-empty ID");
        }

        this.zirkId = zirkId;
    }

    @NotNull
    public ZirkId getZirkId() {
        return zirkId;
    }

    public abstract BezirkAction getAction();
}

package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;

public abstract class ZirkAction extends Action {
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


}

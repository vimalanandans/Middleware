package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class SetLocationAction extends ZirkAction {

    private static final long serialVersionUID = -3253426082978826042L;
    private final Location location;

    public SetLocationAction(ZirkId zirkId, Location location) {
        super(zirkId);

        if (location == null) {
            throw new IllegalArgumentException("local must be non-null");
        }

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_BEZIRK_SET_LOCATION;
    }
}

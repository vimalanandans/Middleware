package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

public class RegisterZirkAction extends ZirkAction {

    private static final long serialVersionUID = 3741338959971855300L;
    private final String zirkName;

    public RegisterZirkAction(ZirkId zirkId, String zirkName) {
        super(zirkId);

//        if (zirkName == null || zirkName.isEmpty()) {
//            throw new IllegalArgumentException("zirkName must be set to a non-empty string.");
//        }

        this.zirkName = zirkName;
    }

    public String getZirkName() {
        return zirkName;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_BEZIRK_REGISTER;
    }
}

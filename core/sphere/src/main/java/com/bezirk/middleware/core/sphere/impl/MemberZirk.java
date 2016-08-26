package com.bezirk.middleware.core.sphere.impl;

import java.util.HashSet;

/**
 * @author Rishabh Gulati
 */
public final class MemberZirk extends com.bezirk.middleware.core.sphere.impl.Zirk {

    private static final long serialVersionUID = -8152511737806997740L;

    public MemberZirk(String serviceName, String ownerDeviceId, HashSet<String> sphereSet) {
        super(serviceName, ownerDeviceId, sphereSet);
    }
}

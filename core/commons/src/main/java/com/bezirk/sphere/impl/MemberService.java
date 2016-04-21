package com.bezirk.sphere.impl;

import java.util.HashSet;

/**
 * @author Rishabh Gulati
 */
public final class MemberService extends Service {

    private static final long serialVersionUID = -8152511737806997740L;

    public MemberService(String serviceName, String ownerDeviceId, HashSet<String> sphereSet) {
        super(serviceName, ownerDeviceId, sphereSet);
    }
}

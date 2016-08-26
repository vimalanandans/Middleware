/**
 *
 */
package com.bezirk.middleware.core.sphere.impl;

import java.util.HashSet;

/**
 * @author Rishabh Gulati
 */
public final class OwnerZirk extends com.bezirk.middleware.core.sphere.impl.Zirk {

    /**
     *
     */
    private static final long serialVersionUID = 933087926226939892L;

    public OwnerZirk(String serviceName, String ownerDeviceId, HashSet<String> sphereSet) {
        super(serviceName, ownerDeviceId, sphereSet);
    }
}

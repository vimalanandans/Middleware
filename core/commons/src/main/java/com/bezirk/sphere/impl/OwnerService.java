/**
 *
 */
package com.bezirk.sphere.impl;

import java.util.HashSet;

/**
 * @author Rishabh Gulati
 */
public final class OwnerService extends Service {

    /**
     *
     */
    private static final long serialVersionUID = 933087926226939892L;

    public OwnerService(String serviceName, String ownerDeviceId, HashSet<String> sphereSet) {
        super(serviceName, ownerDeviceId, sphereSet);
    }
}

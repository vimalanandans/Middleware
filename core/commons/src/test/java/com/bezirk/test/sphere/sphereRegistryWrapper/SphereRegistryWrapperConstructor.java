/**
 *
 */
package com.bezirk.test.sphere.sphereRegistryWrapper;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.ISpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ICryptoInternals;
import com.bezirk.sphere.impl.SphereRegistryWrapper;

import org.junit.Test;

public class SphereRegistryWrapperConstructor {

    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static ISpherePersistence spherePersistence = null;
    private static SphereRegistry registry = null;
    private static UPADeviceInterface upaDevice = null;
    private ICryptoInternals crypto = null;

    /**
     * Test the behavior of the SphereRegistryWrapper constructor when null objects are passed.
     * <br>The constructor is expected to throw an IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullRegistryAndNullSpherePersistenceShouldThrowException() {
        sphereRegistryWrapper = new SphereRegistryWrapper(registry, spherePersistence, upaDevice, crypto, null, null);
    }

}

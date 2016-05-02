/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.CryptoInternals;
import com.bezirk.sphere.impl.SphereRegistryWrapper;

import org.junit.Test;

public class SphereRegistryWrapperConstructor {

    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SpherePersistence spherePersistence = null;
    private static SphereRegistry registry = null;
    private static UPADeviceInterface upaDevice = null;
    private CryptoInternals crypto = null;

    /**
     * Test the behavior of the SphereRegistryWrapper constructor when null objects are passed.
     * <br>The constructor is expected to throw an IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullRegistryAndNullSpherePersistenceShouldThrowException() {
        sphereRegistryWrapper = new SphereRegistryWrapper(registry, spherePersistence, upaDevice, crypto, null, null);
    }

}

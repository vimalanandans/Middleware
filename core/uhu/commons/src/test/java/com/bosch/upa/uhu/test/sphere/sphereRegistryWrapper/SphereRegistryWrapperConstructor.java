/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper;

import org.junit.Test;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.ICryptoInternals;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;

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
	@Test (expected=IllegalArgumentException.class)
	public void nullRegistryAndNullSpherePersistenceShouldThrowException() {
		sphereRegistryWrapper = new SphereRegistryWrapper(registry, spherePersistence, upaDevice, crypto, null, null);
	}
	
}

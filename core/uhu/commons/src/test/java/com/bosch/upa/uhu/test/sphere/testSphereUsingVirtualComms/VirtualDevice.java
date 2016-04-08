/**
 * Class to create a virtual device with its own deviceID, sphere, services and registry.
 */
package com.bosch.upa.uhu.test.sphere.testSphereUsingVirtualComms;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.comms.IUhuCommsLegacy;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.impl.CatchProcessor;
import com.bosch.upa.uhu.sphere.impl.OwnerService;
import com.bosch.upa.uhu.sphere.impl.Service;
import com.bosch.upa.uhu.sphere.impl.ShareProcessor;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.sphere.impl.UhuSphere;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.bosch.upa.uhu.test.sphere.testUtilities.SpherePropertiesMock;

@SuppressWarnings("deprecation")
public class VirtualDevice {
	private static final Logger log = LoggerFactory.getLogger(VirtualCommsManager.class);	
	public SphereRegistryWrapper sphereRegistryWrapper;
	public SphereRegistry sphereRegistry;
	public UPADeviceInterface upaDevice;
	public ISphereConfig sphereConfig;
	public CryptoEngine cryptoEngine;
	public UhuSphere uhuSphere;
	public ShareProcessor shareProcessor;
	public CatchProcessor catchProcessor;
	
	public String sphereId = null;
	
	// to keep track of the number of devices being created and also use it in sphere, device and service names.
	static int virtualDeviceNumber = 0;
	
	public VirtualDevice(IUhuCommsLegacy uhuComms) throws Exception {
		sphereConfig = new SpherePropertiesMock();
		
		upaDevice = new Device(virtualDeviceNumber);
		sphereRegistry = new SphereRegistry();
		cryptoEngine = new CryptoEngine(sphereRegistry);
		uhuSphere = new UhuSphere(cryptoEngine, upaDevice, sphereRegistry);
		
		//Create mock SpherePersistence object for registry
		ISpherePersistence spherePersistence= mock(ISpherePersistence.class);
		when(spherePersistence.loadSphereRegistry()).thenReturn(sphereRegistry);
		Mockito.doNothing().when(spherePersistence).persistSphereRegistry();
		
		//Prepare the SphereRegistryWrapper object for sphere
		sphereRegistryWrapper = new SphereRegistryWrapper(sphereRegistry, spherePersistence, upaDevice, cryptoEngine, null, sphereConfig);		
		sphereRegistryWrapper.init();
		
		uhuSphere.initSphere(spherePersistence, uhuComms, null, sphereConfig);
		
		// Prepare the sphere in the given device and also add services to it.
		sphereId = prepareSphereAndReturnSphereId(sphereRegistryWrapper, upaDevice, Integer.toString(virtualDeviceNumber));
		log.info("Created sphere with sphere ID " + sphereId + "\n");
		
		Field spField = uhuSphere.getClass().getDeclaredField("shareProcessor");
		spField.setAccessible(true);
		shareProcessor = (ShareProcessor) spField.get(uhuSphere);
		
		Field cpField = uhuSphere.getClass().getDeclaredField("catchProcessor");
		cpField.setAccessible(true);
		catchProcessor = (CatchProcessor) cpField.get(uhuSphere);
		
		virtualDeviceNumber++;
	}
	
	public final String prepareSphereAndReturnSphereId(SphereRegistryWrapper sphereRegistryWrapper, UPADeviceInterface upaDevice, String virtualDeviceNumber) throws Exception {
		String OWNER_SERVICE_NAME_1 = "OWNER_SERVICE_NAME_" + virtualDeviceNumber + "1";
		String OWNER_SERVICE_NAME_2 = "OWNER_SERVICE_NAME_" + virtualDeviceNumber + "2";
        String OWNER_SPHERE_NAME = "OWNER_SPHERE_NAME_" + virtualDeviceNumber;
        
		// create owner sphere and service
		String sphereId = sphereRegistryWrapper.createSphere(OWNER_SPHERE_NAME, null, null);
		Sphere sphere = sphereRegistryWrapper.getSphere(sphereId);

		// create service1
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		Service service1 = new OwnerService(OWNER_SERVICE_NAME_1, upaDevice.getDeviceId(), sphereSet1);
		UhuServiceId OWNER_SERVICE_ID_1 = new UhuServiceId(OWNER_SERVICE_NAME_1);
		sphereRegistryWrapper.addService(OWNER_SERVICE_ID_1.getUhuServiceId(), service1);

		// create service2
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(sphereId);
		Service service2 = new OwnerService(OWNER_SERVICE_NAME_2, upaDevice.getDeviceId(), sphereSet2);
		UhuServiceId OWNER_SERVICE_ID_2 = new UhuServiceId(OWNER_SERVICE_NAME_2);
		sphereRegistryWrapper.addService(OWNER_SERVICE_ID_2.getUhuServiceId(), service2);

		LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();

		// create list of services for the sphere
		ArrayList<UhuServiceId> services = new ArrayList<>();
		services.add(OWNER_SERVICE_ID_1);
		services.add(OWNER_SERVICE_ID_2);

		// add services to the deviceServices map for the sphere
		deviceServices.put(upaDevice.getDeviceId(), services);
		sphere.setDeviceServices(deviceServices);
		sphereRegistryWrapper.persist();
		return sphereId;
	}


}

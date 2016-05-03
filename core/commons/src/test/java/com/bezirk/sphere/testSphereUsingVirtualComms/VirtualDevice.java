/**
 * Class to create a virtual device with its own deviceID, sphere, services and registry.
 */
package com.bezirk.sphere.testSphereUsingVirtualComms;

import com.bezirk.comms.BezirkCommsLegacy;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.Zirk;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.sphere.testUtilities.SpherePropertiesMock;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
public class VirtualDevice {
    private static final Logger logger = LoggerFactory.getLogger(VirtualCommsManager.class);

    // to keep track of the number of devices being created and also use it in sphere, device and zirk names.
    static int virtualDeviceNumber = 0;
    public SphereRegistryWrapper sphereRegistryWrapper;
    public SphereRegistry sphereRegistry;
    public BezirkDeviceInterface upaDevice;
    public ISphereConfig sphereConfig;
    public CryptoEngine cryptoEngine;
    public BezirkSphere bezirkSphere;
    public ShareProcessor shareProcessor;
    public CatchProcessor catchProcessor;
    public String sphereId = null;

    public VirtualDevice(BezirkCommsLegacy bezirkComms) throws Exception {
        sphereConfig = new SpherePropertiesMock();

        upaDevice = new Device(virtualDeviceNumber);
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        bezirkSphere = new BezirkSphere(cryptoEngine, upaDevice, sphereRegistry);

        //Create mock SpherePersistence object for registry
        SpherePersistence spherePersistence = mock(SpherePersistence.class);
        when(spherePersistence.loadSphereRegistry()).thenReturn(sphereRegistry);
        Mockito.doNothing().when(spherePersistence).persistSphereRegistry();

        //Prepare the SphereRegistryWrapper object for sphere
        sphereRegistryWrapper = new SphereRegistryWrapper(sphereRegistry, spherePersistence, upaDevice, cryptoEngine, null, sphereConfig);
        sphereRegistryWrapper.init();

        bezirkSphere.initSphere(spherePersistence, bezirkComms, null, sphereConfig);

        // Prepare the sphere in the given device and also add services to it.
        sphereId = prepareSphereAndReturnSphereId(sphereRegistryWrapper, upaDevice, Integer.toString(virtualDeviceNumber));
        logger.info("Created sphere with sphere ID " + sphereId + "\n");

        Field spField = bezirkSphere.getClass().getDeclaredField("shareProcessor");
        spField.setAccessible(true);
        shareProcessor = (ShareProcessor) spField.get(bezirkSphere);

        Field cpField = bezirkSphere.getClass().getDeclaredField("catchProcessor");
        cpField.setAccessible(true);
        catchProcessor = (CatchProcessor) cpField.get(bezirkSphere);

        virtualDeviceNumber++;
    }

    public final String prepareSphereAndReturnSphereId(SphereRegistryWrapper sphereRegistryWrapper, BezirkDeviceInterface upaDevice, String virtualDeviceNumber) {
        String OWNER_SERVICE_NAME_1 = "OWNER_SERVICE_NAME_" + virtualDeviceNumber + "1";
        String OWNER_SERVICE_NAME_2 = "OWNER_SERVICE_NAME_" + virtualDeviceNumber + "2";
        String OWNER_SPHERE_NAME = "OWNER_SPHERE_NAME_" + virtualDeviceNumber;

        // create owner sphere and zirk
        String sphereId = sphereRegistryWrapper.createSphere(OWNER_SPHERE_NAME, null, null);
        Sphere sphere = sphereRegistryWrapper.getSphere(sphereId);

        // create zirk1
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);
        Zirk zirk1 = new OwnerZirk(OWNER_SERVICE_NAME_1, upaDevice.getDeviceId(), sphereSet1);
        BezirkZirkId OWNER_SERVICE_ID_1 = new BezirkZirkId(OWNER_SERVICE_NAME_1);
        sphereRegistryWrapper.addService(OWNER_SERVICE_ID_1.getBezirkZirkId(), zirk1);

        // create zirk2
        HashSet<String> sphereSet2 = new HashSet<>();
        sphereSet2.add(sphereId);
        Zirk zirk2 = new OwnerZirk(OWNER_SERVICE_NAME_2, upaDevice.getDeviceId(), sphereSet2);
        BezirkZirkId OWNER_SERVICE_ID_2 = new BezirkZirkId(OWNER_SERVICE_NAME_2);
        sphereRegistryWrapper.addService(OWNER_SERVICE_ID_2.getBezirkZirkId(), zirk2);

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();

        // create list of services for the sphere
        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(OWNER_SERVICE_ID_1);
        services.add(OWNER_SERVICE_ID_2);

        // add services to the deviceServices map for the sphere
        deviceServices.put(upaDevice.getDeviceId(), services);
        sphere.setDeviceServices(deviceServices);
        sphereRegistryWrapper.persist();
        return sphereId;
    }


}

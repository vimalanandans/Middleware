///**
// * 
// */
//package com.bosch.test.sphere.sphereRegistryWrapper.sphere;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.UUID;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import UPADeviceInterface;
//import UhuSphereInfo;
//import SphereRegistry;
//import UhuServiceId;
//import ISphereConfig;
//import UhuSphereType;
//import ISphereConfig.Mode;
//import MemberSphere;
//import OwnerSphere;
//import Sphere;
//import SphereRegistryWrapper;
//import MockSetUpUtility;
//import SphereTestUtility;
//
///**
// * @author rishabh
// *
// */
//public class GetSpheres {
//
//    private static SphereRegistryWrapper sphereRegistryWrapper;
//    private static SphereRegistry registry;
//    private static SphereTestUtility sphereTestUtility;
//    private static ISphereConfig sphereConfig;
//    private static UPADeviceInterface upaDevice;
//    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
//    private static final Logger logger = LoggerFactory.getLogger(GetSpheres.class);
//
//    /**
//     * @throws java.lang.Exception
//     */
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        logger.info("***** Setting up SphereRegistryWrapper:GetSpheres TestCase *****");
//        mockSetUp.setUPTestEnv();
//        registry = mockSetUp.registry;
//        upaDevice = mockSetUp.upaDevice;
//        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
//        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
//        sphereConfig = mockSetUp.sphereConfig;
//    }
//
//    /**
//     * @throws java.lang.Exception
//     */
//    @AfterClass
//    public static void tearDownAfterClass() throws Exception {
//        logger.info("***** Shutting down SphereRegistryWrapper:GetSpheres TestCase *****");
//        mockSetUp.destroyTestSetUp();
//    }
//
//    /**
//     * @throws java.lang.Exception
//     */
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    /**
//     * @throws java.lang.Exception
//     */
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    /**
//     * Get the sphere info of the spheres present in the registry
//     */
//    @Test
//    public final void validSpheres() {
//
//        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
//        Sphere defaultSphere = sphereRegistryWrapper.getSphere(defaultSphereId);
//        String defaultSphereName = defaultSphere.getSphereName();
//        String defaultSphereType = defaultSphere.getSphereType();
//
//        // Create owner sphere and add it to registry
//        String sphereId1 = UUID.randomUUID().toString();
//        String sphereName1 = sphereTestUtility.OWNER_SPHERE_NAME_1;
//        String sphereType1 = UhuSphereType.UHU_SPHERE_TYPE_HOME;
//        Sphere ownerSphere = new OwnerSphere(sphereName1, sphereTestUtility.DEVICE_1.getDeviceId(), sphereType1);
//        registry.spheres.put(sphereId1, ownerSphere);
//
//        // Create another sphere - a member sphere, and add it to the registry
//        String sphereId2 = UUID.randomUUID().toString();
//        String sphereName2 = sphereTestUtility.MEMBER_SPHERE_NAME_1;
//        String sphereType2 = UhuSphereType.UHU_SPHERE_TYPE_HOME;
//        // Set of devices
//        HashSet<String> devices = new HashSet<>();
//        devices.add(sphereTestUtility.DEVICE_2.getDeviceId());
//        // Map of device id and service ids
//        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
//        ArrayList<UhuServiceId> services = new ArrayList<>();
//		services.add(sphereTestUtility.MEMBER_SERVICE_ID_1);
//		deviceServices.put(upaDevice.getDeviceId(), services);
//		
//		defaultSphere.setDeviceServices(deviceServices);
//        Sphere memberSphere = new MemberSphere(sphereName2, sphereType2, devices, deviceServices, false);
//        registry.spheres.put(sphereId2, memberSphere);
//
//        
//        UhuSphereInfo defaultInfo = new UhuSphereInfo(defaultSphereId, defaultSphereName, defaultSphereType, null,
//                null);
//        UhuSphereInfo sphereInfo1 = new UhuSphereInfo(sphereId1, sphereName1, sphereType1, null, null);
//        UhuSphereInfo sphereInfo2 = new UhuSphereInfo(sphereId2, sphereName2, sphereType2, null, null);
//        List<UhuSphereInfo> createdSphereInfo = new ArrayList<>();
//        createdSphereInfo.add(defaultInfo);
//        createdSphereInfo.add(sphereInfo1);
//        createdSphereInfo.add(sphereInfo2);
//
//        List<UhuSphereInfo> retrievedSpheres = (List<UhuSphereInfo>) sphereRegistryWrapper.getSpheres();
//        System.out.println(retrievedSpheres.size());
//        int j = 0; // for iterating over created spheres, variable 'i' depends
//                   // on size of stored spheres which can vary based on the mode
//                   // i.e. dev or prop
//        for (int i = 0; i < retrievedSpheres.size(); i++) {
//            // for skipping the development sphere
//            if (sphereConfig.getMode() == Mode.DEV && i == 1) {
//                continue;
//            }
//            UhuSphereInfo retrieved = retrievedSpheres.get(i);
//            UhuSphereInfo created = createdSphereInfo.get(j++);
//            assertEquals(created.getSphereID(), retrieved.getSphereID());
//            assertEquals(created.getSphereName(), retrieved.getSphereName());
//            assertEquals(created.getSphereType(), retrieved.getSphereType());
//        }
//    }
//}

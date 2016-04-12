///**
// * 
// */
//package com.bosch.test.sphere.sphereRegistryWrapper.sphere;
//
//import static org.junit.Assert.assertTrue;
//
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
//import SphereRegistry;
//import UhuSphereType;
//import OwnerSphere;
//import SphereRegistryWrapper;
//import Device;
//import MockSetUpUtility;
//import SphereTestUtility;
//
///**
// * @author rishabh
// *
// */
//public class GetDefaultSphereId {
//
//	private static SphereRegistryWrapper sphereRegistryWrapper;
//	private static SphereRegistry registry;
//	private static SphereTestUtility sphereTestUtility;
//	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
//	private static final Logger log = LoggerFactory.getLogger(GetDefaultSphereId.class);
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		log.info("***** Setting up SphereRegistryWrapper:GetDefaultSphereId TestCase *****");
//		
//		// required for reseting the device no's to 0 for each test class
//		// considering the device no for testing
//		Device.reset();
//		
//		mockSetUp.setUPTestEnv();
//		registry = mockSetUp.registry;
//		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
//		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		log.info("***** Shutting down SphereRegistryWrapper:GetDefaultSphereId TestCase *****");
//		mockSetUp.destroyTestSetUp();
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getDefaultSphereId()}.
//	 * <br>Test validity of default sphere id
//	 */
//	@Test
//	public final void validDefaultSphereId() {
//		System.out.println("received: " + sphereRegistryWrapper.getDefaultSphereId());
//		System.out.println("created: " + createDefaultSphereId());
//		assertTrue(sphereRegistryWrapper.getDefaultSphereId().equals(createDefaultSphereId()));
//	}
//
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getDefaultSphereId()}.
//	 * <br>Test validity of default sphere id when another sphere is added to the
//	 * registry
//	 */
//	@Test
//	public final void validDefaultSphereIdWithAnotherSphere() {
//		registry.spheres.put(UUID.randomUUID().toString(), new OwnerSphere());
//		assertTrue(sphereRegistryWrapper.getDefaultSphereId().equals(createDefaultSphereId()));
//	}
//
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getDefaultSphereId()}.
//	 * 
//	 * <br>Test validity of default sphere id when another default sphere is added
//	 * using sphere type parameter
//	 */
//	@Test
//	public final void validDefaultSphereIdAnotherDefaultSphere() {
//		String sphereId = sphereRegistryWrapper.createSphere(sphereTestUtility.OWNER_SPHERE_NAME_2,
//				UhuSphereType.UHU_SPHERE_TYPE_DEFAULT, null);
//		assert(sphereId != null);
//		// validate that the default sphere is still original default sphere
//		assertTrue(sphereRegistryWrapper.getDefaultSphereId().equals(createDefaultSphereId()));
//	}
//
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getDefaultSphereId()}.
//	 * 
//	 * <br>construct the sphereId which is expected to be formed for the default
//	 * sphere based on the mock Device class
//	 * 
//	 * @return
//	 */
//	private final String createDefaultSphereId() {
//		String deviceName = Device.DEVICE_NAME + Device.DEVICE_NO;
//		String deviceId = Device.DEVICE_ID + Device.DEVICE_NO;
//		String deviceIdSubString = deviceId.substring(deviceId.length() - 5, deviceId.length());
//		String defaultSphereName = "Sphere-" + deviceName + "-" + deviceIdSubString;
//		String defaultSphereId = defaultSphereName + deviceId;
//		return defaultSphereId;
//	}
//
//
//}

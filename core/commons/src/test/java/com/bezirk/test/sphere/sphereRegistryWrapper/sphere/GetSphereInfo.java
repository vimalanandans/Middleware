///**
// * 
// */
//package com.bosch.test.sphere.sphereRegistryWrapper.sphere;
//
//import static org.junit.Assert.*;
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
//public class GetSphereInfo {
//
//	private static SphereRegistryWrapper sphereRegistryWrapper;
//	private static SphereRegistry registry;
//	private static SphereTestUtility sphereTestUtility;
//	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
//	private static final Logger logger = LoggerFactory.getLogger(GetSphereInfo.class);
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		log.info("***** Setting up SphereRegistryWrapper:GetSphereInfo TestCase *****");
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
//		log.info("***** Shutting down SphereRegistryWrapper:GetSphereInfo TestCase *****");
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
//	 * Test method for {@link SphereRegistryWrapper#getSphereInfo(String)}.
//	 * 
//	 * <br>Check the validity of the sphere information retrieved from UhuSphereInfo object
//	 */
//	@Test
//	public final void validSphere() {
//		String sphereId = UUID.randomUUID().toString();
//		Sphere ownerSphere = new OwnerSphere(sphereTestUtility.OWNER_SPHERE_NAME_1,
//				sphereTestUtility.DEVICE_1.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_HOME);
//		registry.spheres.put(sphereId, ownerSphere);
//
//		assertEquals(UhuSphereType.UHU_SPHERE_TYPE_HOME, sphereRegistryWrapper.getSphereInfo(sphereId).getSphereType());
//		assertEquals(sphereTestUtility.OWNER_SPHERE_NAME_1, sphereRegistryWrapper.getSphereInfo(sphereId).getSphereName());
//		assertEquals(sphereId, sphereRegistryWrapper.getSphereInfo(sphereId).getSphereID());
//		
//	}
//	
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getSphereInfo(String)}.
//	 * 
//	 * <br>Test the behavior of getSphereInfo method when null string is passed to it.
//	 * getSphereInfo method is expected to return null.
//	 */
//	@Test
//	public final void nullSphereIdShouldRetunNull() {
//		String sphereId = null;
//		assertNull(sphereRegistryWrapper.getSphereInfo(sphereId));
//	}
//	
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getSphereInfo(String)}.
//	 * 
//	 * <br> If the sphere Id is of a temporary member sphere, the the method will return null.
//	 */
//	@Test
//	public final void temporarySphereReturnsNull() {
//		String sphereId = UUID.randomUUID().toString();
//		Sphere tempMemberSphere = new MemberSphere(sphereTestUtility.MEMBER_SPHERE_NAME_1, UhuSphereType.UHU_SPHERE_TYPE_HOME, null, null, true);
//		registry.spheres.put(sphereId, tempMemberSphere);
//
//		assertNull(sphereRegistryWrapper.getSphereInfo(sphereId));
//		
//	}
//	
//	/**
//	 * Test method for {@link SphereRegistryWrapper#getSphereInfo(String)}.
//	 * 
//	 * <br>Test the behavior of getSphereInfo method when the sphere is not in registry.
//	 * getSphereInfo method is expected to return null.
//	 */
//	@Test
//	public final void sphereNotInRegistryRetunsNull() {
//		String sphereId = UUID.randomUUID().toString();
//		assertNull(sphereRegistryWrapper.getSphereInfo(sphereId));
//	}
//	
//
//}

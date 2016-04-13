package com.bezirk.sphere.control.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This testCase verifies the SphereInformation POJO by retrieving the field values using getters.
 * 
 * @author AJC6KOR
 *
 */
public class SphereInformationTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(SphereInformationTest.class);

	private static final String creatorDeviceId = "CREATOR_DEVICE_ID";
	private static final boolean owner= true;
	private static final byte[] ownerPrivateKeyBytes= "OWNER_PRIVATE_KEY".getBytes();
	private static final byte[] ownerPublicKeyBytes="OWNER_PUBLIC_KEY".getBytes();
	private static final byte[] sphereKey="SPHERE_KEY".getBytes();
	private static final String sphereName="Home";
	private static final boolean temporarySphere =false;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up SphereInformationTest TestCase *****");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down SphereInformationTest TestCase *****");
	}

	@Test
	public void testSphereInformation() {
		
		com.bezirk.sphere.control.Objects.SphereInformation sphereInformation =prepareSphereInformation();
		assertEquals("CreatorDeviceId not equal to the set value.",creatorDeviceId, sphereInformation.getCreatorDeviceId());
		assertEquals("SphereName not equal to the set value.",sphereName, sphereInformation.getSphereName());
		assertTrue("OwnerSphere is not considered as owner.", sphereInformation.isOwner());
		assertFalse("Owner sphere is considered as temporary sphere.", sphereInformation.isTemporarySphere());

		
	}

	private com.bezirk.sphere.control.Objects.SphereInformation prepareSphereInformation() {
		com.bezirk.sphere.control.Objects.SphereInformation sphereInformation = new com.bezirk.sphere.control.Objects.SphereInformation();
		sphereInformation.setCreatorDeviceId(creatorDeviceId);
		sphereInformation.setOwner(owner);
		sphereInformation.setOwnerPrivateKeyBytes(ownerPrivateKeyBytes);
		sphereInformation.setOwnerPublicKeyBytes(ownerPublicKeyBytes);
		sphereInformation.setSphereKey(sphereKey);
		sphereInformation.setSphereName(sphereName);
		sphereInformation.setTemporarySphere(temporarySphere);
		return sphereInformation;
	}
}
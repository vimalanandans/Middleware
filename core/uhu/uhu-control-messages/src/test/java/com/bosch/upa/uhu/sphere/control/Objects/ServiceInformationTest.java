package com.bosch.upa.uhu.sphere.control.Objects;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This testCase verifies the ServiceInformation POJO by retrieving the field values using getters.
 * 
 * @author AJC6KOR
 *
 */
public class ServiceInformationTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(ServiceInformationTest.class);

	private static final String sphereId = "TestSphere";
	private static final ServiceVitals serviceVitals = new ServiceVitals();
	private static final HashSet<String> sphereSet = new HashSet<>();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up ServiceInformationTest TestCase *****");
		sphereSet.add(sphereId);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down ServiceInformationTest TestCase *****");
	}

	
	@Test
	public void testServiceInformation() {
		
		ServiceInformation serviceInformation =prepareServiceInformation();
		assertEquals("SphereVitals not equal to the set value.",serviceVitals, serviceInformation.getServiceVitals());
		assertEquals("SphereSet not equal to the set value.",sphereSet, serviceInformation.getSphereSet());
		
	}

	private ServiceInformation prepareServiceInformation() {
		ServiceInformation serviceInformation = new ServiceInformation();
		serviceInformation.setServiceVitals(serviceVitals );
		serviceInformation.setSphereSet(sphereSet );
		return serviceInformation;
	}
}

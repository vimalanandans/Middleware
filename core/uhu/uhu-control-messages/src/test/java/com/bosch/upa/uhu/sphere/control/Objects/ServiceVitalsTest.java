package com.bosch.upa.uhu.sphere.control.Objects;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This testCase verifies the ServiceVitals POJO by retrieving the field values using getters.
 * 
 * @author AJC6KOR
 *
 */
public class ServiceVitalsTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(ServiceVitalsTest.class);
	
	private static final String ownerDeviceID ="TESTDEVICe";
	private static final String serviceName="ServiceA";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up ServiceVitalsTest TestCase *****");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down ServiceVitalsTest TestCase *****");
	}


	
	@Test
	public void testServiceVitals() {
		
		ServiceVitals serviceVitals = prepareServiceVitals();
		
		assertEquals("OwnerDeviceID not equal to the set value.",ownerDeviceID, serviceVitals.getOwnerDeviceID());
		assertEquals("ServiceName not equal to the set value.",serviceName, serviceVitals.getServiceName());
		
	}

	private ServiceVitals prepareServiceVitals() {
		ServiceVitals serviceVitals = new ServiceVitals();
		serviceVitals.setOwnerDeviceID(ownerDeviceID);
		serviceVitals.setServiceName(serviceName);
		return serviceVitals;
	}
}

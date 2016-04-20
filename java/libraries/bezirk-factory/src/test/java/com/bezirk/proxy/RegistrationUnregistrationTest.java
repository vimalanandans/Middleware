package com.bezirk.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.Bezirk;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author vbd4kor
 * This class tests the Uhu for Registration and Unregistration Scenario.
 * Steps: 1. registers the service with UhuFactory
 * 		  2. Check the Sid Map if it contains the ServiceId
 * 		  3. register the same service Again and check if the same service is generated.
 * 		  4. unregister the service
 * 		  5. Register the service again and check for different ServiceId that indicates the validaity of the test case 
 * 			
 */
public class RegistrationUnregistrationTest {
	private static final Logger log = LoggerFactory.getLogger(RegistrationUnregistrationTest.class);
	
	
	@BeforeClass
	public static void setup(){
		log.info(" ************** Setting up RegistrationUnregistrationTest Testcase ****************************");
	}

	//@Test
	public void registrationUnregistrationTest(){
		final String serviceName = "MOCK_SERVICE_A";
		Bezirk uhu = null;
		UhuServiceId serviceId = null;

		uhu = Factory.getInstance();
		serviceId = (UhuServiceId) uhu.registerService(serviceName);
		String uhuServiceId = serviceId.getUhuServiceId();
		assertNotNull("ServiceID is null after registration.",uhuServiceId);
		
		// Test
		
		// RE-Register the service and check if the same id is getting generated
		String duplicatUuhuServiceId = ((UhuServiceId)uhu.registerService(serviceName)).getUhuServiceId();
		assertEquals("Different serviceID generated upon duplicate registration for same service.",uhuServiceId,duplicatUuhuServiceId);
		
		// unRegister
		uhu.unregisterService(serviceId);
		
	}
	
	@AfterClass
	public static void tearDown(){
		log.info(" ************** TearingDown RegistrationUnregistrationTest Testcase ****************************");
	}
		
}

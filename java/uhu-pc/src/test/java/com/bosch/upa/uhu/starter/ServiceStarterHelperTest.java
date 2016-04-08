package com.bosch.upa.uhu.starter;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.IUhuCommsLegacy;
import com.bosch.upa.uhu.device.UhuDevice;
import com.bosch.upa.uhu.device.UhuDeviceType;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.sphere.api.IUhuSphereAPI;
import com.bosch.upa.uhu.util.MockSetUpUtilityForUhuPC;

/**
 * This testcase verifies the methods in ServiceStarterHelper class.
 * 
 * @author AJC6KOR
 *
 */
public class ServiceStarterHelperTest {
	
	private static final MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		mockSetUP.setUPTestEnv();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		mockSetUP.destroyTestSetUp();

	}
	
	@Test
	public void test() {
		
	testInitSphere();

	testConfigureUhuDevice();

	
	}

	/**
	 * Positive Testcase :
	 * 
	 * UhuSphere should be non null after successful invocation of initSphere in MainService.
	 * 
	 */
	private void testInitSphere() {

		ServiceStarterHelper helper = new ServiceStarterHelper();
		UPADeviceInterface uhuDevice= mockSetUP.getUpaDevice();
		RegistryPersistence registryPersistence=mockSetUP.getRegistryPersistence();
		IUhuComms commsLegacy = Mockito.mock(IUhuCommsLegacy.class);
		IUhuSphereAPI uhuSphere =helper.initSphere(uhuDevice, registryPersistence, commsLegacy);
		
		assertNotNull("UhuSphere is not initialized. ",uhuSphere);

	}

	/**
	 * Positive Testcase :
	 * 
	 * This covers two scenarios : 
	 * 
	 * a) UhuConfig Display Enabled  
	 * 
	 *  	In this case the uhudevice type should be set as PC upon device configuration.
	 * 
	 * b) UhuConfig Display disabled 
	 * 
	 *  	In this case the uhudevice type should be set as EMBEDDED_KIT upon device configuration.
	 * 
	 */
	private void testConfigureUhuDevice() {
		ServiceStarterHelper helper = new ServiceStarterHelper();
		UhuConfig uhuConfig = new UhuConfig();
		
		UhuDevice uhuDevice = helper.configureUhuDevice(uhuConfig);
		
		assertNotNull("UhuDevice is null after configuragtion. ",uhuDevice);
		
		assertNotNull("UhuDeviceType is null after configuragtion. ",uhuDevice.getDeviceType());
		
		assertEquals("Uhu Device Type is not configured to PC when display is enabled.",UhuDeviceType.UHU_DEVICE_TYPE_PC,uhuDevice.getDeviceType());
		
		assertNotNull("UhuDeviceLocation is null after configuragtion. ",uhuDevice.getDeviceLocation());

		
		uhuConfig.setDisplayEnable("false");
		
		uhuDevice = helper.configureUhuDevice(uhuConfig);
		
		assertEquals("Uhu Device Type is not configured to EMBEDDED KIT when display is disabled.",UhuDeviceType.UHU_DEVICE_TYPE_EMBEDDED_KIT,uhuDevice.getDeviceType());
	}

}

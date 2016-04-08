package com.bosch.upa.uhu.starter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.NetworkInterface;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.upa.uhu.comms.UhuCommsPC;
import com.bosch.upa.uhu.util.MockSetUpUtilityForUhuPC;

/**
 * This testcase verifies the working of UhuPCNetwork Utility
 * 
 * @author AJC6KOR
 *
 */
public class UhuPCNetworkUtilTest {
	
	private static MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		System.clearProperty("InterfaceName");

	}

	@Test
	public void test(){
		
		testFetchNetworkInterface();
		
	}
	
	/**
	 * Positive Testcase :
	 * 
	 * Network interface is fetched after initializing UhuCommsPC. 
	 * Utility should return interface successfully. 
	 */
	private void testFetchNetworkInterface() {
		
		UhuPCNetworkUtil uhuPCNetworkUtil = new UhuPCNetworkUtil();
		UhuConfig uhuConfig = new UhuConfig();
		
		UhuCommsPC.init();
		NetworkInterface intf =null;
		try {
			intf = uhuPCNetworkUtil.fetchNetworkInterface(uhuConfig );
		} catch (Exception e) {
			fail("Unable to fetch network interface. "+e.getMessage());
		}
		assertNotNull("Unable to fetch network interface.",intf);
		
	}
	
}

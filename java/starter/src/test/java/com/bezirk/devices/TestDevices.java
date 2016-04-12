package com.bezirk.devices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;

public class TestDevices {
	
	private final static Logger LOG = LoggerFactory.getLogger(TestDevices.class);
	UPADeviceForPC upaDeviceForPC;
	@Before
	public void setUp() throws Exception {
		upaDeviceForPC = new UPADeviceForPC();
		
	}

	@Test
	public void testLoadProperties() {
		try {
			Properties props = UPADeviceForPC.loadProperties();
			LOG.info("printing properties...");
			for (Object key : props.keySet()) {
				LOG.info("key: " + key + " prop: " + props.getProperty((String)key));
			}
		} 
		catch (Exception e) {
			fail("Exception in fetching upadeviceforPC properties. "+e.getMessage());
		}
	}
	@Test(expected=Exception.class)
	public void testLoadPropertiesWhenPropNull() throws Exception{
		UPADeviceForPC.loadProperties("upadevice_empty.properties");
	}
	
	@Test(expected=Exception.class)
	public void testLoadPropertiesWhenPropFileEmpty() throws Exception{
		UPADeviceForPC.loadProperties("upadevice_noProps.properties");
	}
	
	@Test
	public void testGetDeviceName(){
		Location location = new Location("FirstFloor", "lab", "pantry");
		DeviceDetails deviceDetails = new DeviceDetails();
		deviceDetails.setDeviceLocation(location);
		deviceDetails.setDeviceName("UHU-PC1");
		deviceDetails.setDeviceId("TestDevID1234");
		assertNotNull("Device name is null", upaDeviceForPC.getDeviceName());		
	}
	
	@Test
	public void testGetDeviceLocation(){
		//get default location value from properties file
		assertNotNull("Device location is null", upaDeviceForPC.getDeviceLocation());
		
		Location location = new Location("Floor1/null/null");
		assertEquals("Location is not matching default location.",location, upaDeviceForPC.getDeviceLocation());

	}
	
	@Test
	public void testSetDeviceLocation(){
		Location loc = new Location("floor1", "kitchen", "Refrigerator");
		//DeviceLocation  set when deviceDetails is not null.
		assertTrue("Unable to set location for device.",upaDeviceForPC.setDeviceLocation(loc));
	}
	
	@Test
	public void testGetDeviceID(){
		assertNotNull("Device id is null", upaDeviceForPC.getDeviceId());		
	}
	
/*	@Test(expected = Exception.class)  
	public void testStoreProperties() throws IOException{
		Properties props = new Properties();
		UPADeviceForPC.storeProperties(props);
	}
	*/
	

	
}

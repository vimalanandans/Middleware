package com.bezirk.protocols.penguin.v01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.bezirk.protocols.specific.userproxy.ClearData;

/**
 * This testcase verifies the ClearData event by setting the properties and retrieving them after deserialization.
 * 
 * @author RHR8KOR
 *
 */
public class ClearDataTest {


	@Test
	public void test() {
		
		ClearData clearData = new ClearData();
		clearData.setClearObservation(true);
		clearData.setClearPreference(true);
		clearData.setUser("BOB");
		
		String serializedData = clearData.serialize();
		
		ClearData clearDataSer = ClearData.deserialize(serializedData);
		assertEquals("User name is not same as the set value","BOB", clearDataSer.getUser());		
		assertTrue("ClearData ClearPreference is false", clearData.isClearPreference());
		assertTrue("ClearData clearObservation is false", clearData.isClearObservation());

 	}

}

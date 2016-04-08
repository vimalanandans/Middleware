package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.bezirk.services.light.protocol.HueVocab.Policy;
import com.bezirk.api.addressing.Location;

/**
 * This testcase verifies the ConfigurePolicy event by setting the properties and retrieving them.
 * 
 * @author RHR8KOR
 *
 */
public class ConfigurePolicyTest {

	
	@Test
	public void test() {
		
		/**
		 * Junit For ConfigurePolicy.java 
		 */
		
		Location loc = new Location("Office", "Floor", "Canteen");
		Policy policy = HueVocab.Policy.KOH;
		
		ConfigurePolicy configurePolicy = new ConfigurePolicy(loc,policy,"Bright Light");
	
		
		
		assertEquals("Floor", configurePolicy.getLocation().getArea());
		assertEquals("KOH", configurePolicy.getPolicy().toString());
		assertEquals("Bright Light", configurePolicy.getPresenceSentivity());
		assertEquals("No-King", configurePolicy.getKing());
		configurePolicy.setKing("One-King");
		assertEquals("One-King", configurePolicy.getKing());
		
		
		/**
		 * Junit for ActuateBulb.java
		 * 
		 */
		
		Set<Integer> lightNum = new HashSet<Integer>();
		lightNum.add(6);
		lightNum.add(10);
		ActuateBulb actuateBulb = new ActuateBulb(lightNum, HueVocab.Commands.ON);
		
		assertTrue("Light Number does'nt matches with the added set.", actuateBulb.getLightNumber().contains(6));
		assertEquals("ON", actuateBulb.getCommand().toString());		
		actuateBulb.setColor(HueVocab.Color.RED);
		assertEquals("RED", actuateBulb.getColor().toString());
		assertTrue("ActuateBulb (Integer lightNumber, HueVocab.Commands command) not called",actuateBulb.getById());
		assertNull("Location is not null",actuateBulb.getLocation());
		assertEquals("ActuateBulb", ActuateBulb.TOPIC);
		assertEquals(2, actuateBulb.getLightNumber().size());
		
		actuateBulb = null;
		
		
		actuateBulb = new ActuateBulb(loc, HueVocab.Commands.TOGGLE);
		assertEquals("Office", actuateBulb.getLocation().getRegion().toString());
		assertEquals("TOGGLE", actuateBulb.getCommand().toString());
		assertFalse("ActuateBulb (Location location, HueVocab.Commands command) not called",actuateBulb.getById());
		assertNull("Light number is not null", actuateBulb.getLightNumber());
		
 	}

}

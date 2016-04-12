/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.api.addressing;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the Address by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class AddressTest {

	@Test
	public void test() {
				

		Location loc = new Location("OFFICE1/BLOCK1/FLOOR1");
		Address address = new Address(loc );
		
		assertEquals("Location is not equal to the set value.",loc,address.getLocation());
		
		loc = new Location("OFFICE1/BLOCK1/FLOOR2");
		
		assertNotEquals("Location in address is equal to another invalid location.",loc,address.getLocation());
		
		com.bezirk.api.addressing.Pipe pipe = new Pipe();
		address = new Address(loc, pipe , true);
		
		assertEquals("Pipe is not equal to the set value.",pipe,address.getPipe());
		assertTrue("Is Local target is false.",address.isLocalTargeted());
		
		address = new Address(loc, pipe , false);
		assertFalse("Is Local target is true.",address.isLocalTargeted());

	}

}

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
package com.bezirk.middleware.addressing;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 *	 This testcase verifies the CloudPipe by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class CloudPipeTest {

	@Test
	public void test() {

		String name="TestPipe";
		URI uri=null;
		try {
			uri = new URI("http://test.com");
		} catch (URISyntaxException e) {
			
			fail("Exception in creating uri."+e.getMessage());
		}
		com.bezirk.middleware.addressing.CloudPipe cloudPipe = new CloudPipe(name, uri);
		
		String serializedCloudPipe = cloudPipe.serialize();
		CloudPipe deserializedCloudPipe = CloudPipe.deserialize(serializedCloudPipe, CloudPipe.class);
		
		assertEquals("Pipe name is not equal to the set value.",name,deserializedCloudPipe.getName());
		assertEquals("Pipe uri is not equal to the set value.",uri,deserializedCloudPipe.getURI());
		
		CloudPipe testCloudPipe = new CloudPipe(name, uri);
		assertTrue("Pipes with same uri are not considered equal.",testCloudPipe.equals(cloudPipe));
		
		try {
			uri = new URI("http://testPipe.com");
		} catch (URISyntaxException e) {
			
			fail("Exception in creating uri."+e.getMessage());
		}
		testCloudPipe = new CloudPipe(name, uri);
		assertFalse("Pipes with different uri are considered equal.",testCloudPipe.equals(cloudPipe));
		
		
	}

}

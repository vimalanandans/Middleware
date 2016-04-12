package com.bezirk.test.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;


import org.junit.Test;

import com.bezirk.commons.UhuVersion;

/**
 * @author ajc6kor
 *
 */
public class UhuVersionTest {

	@Test
	public void test() {

		assertNotNull("UhuVersion cannot be retrieved.",UhuVersion.UHU_VERSION);
		assertNotEquals("UhuVersion cannot be retrieved.","1.1",UhuVersion.UHU_VERSION);

		
	}

}

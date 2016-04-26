package com.bezirk.common;

import com.bezirk.commons.UhuVersion;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ajc6kor
 */
public class UhuVersionTest {

    @Test
    public void test() {

        assertNotNull("UhuVersion cannot be retrieved.", UhuVersion.UHU_VERSION);
        assertNotEquals("UhuVersion cannot be retrieved.", "1.1", UhuVersion.UHU_VERSION);


    }

}

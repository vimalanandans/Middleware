package com.bezirk.common;

import com.bezirk.commons.BezirkVersion;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ajc6kor
 */
public class BezirkVersionTest {

    @Test
    public void test() {

        assertNotNull("BezirkVersion cannot be retrieved.", BezirkVersion.BEZIRK_VERSION);
        assertNotEquals("BezirkVersion cannot be retrieved.", "1.1", BezirkVersion.BEZIRK_VERSION);


    }

}

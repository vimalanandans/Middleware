/**
 *
 */
package com.bezirk.sphere;

import com.bezirk.sphere.impl.SphereProperties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * @author rishabh
 */
@Deprecated
public class SpherePropertiesTest {
    private static final Logger logger = LoggerFactory.getLogger(SpherePropertiesTest.class);

    SphereProperties sphereProperties;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        sphereProperties = new SphereProperties();
        sphereProperties.init();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Commenting out as we are not using property files anymore. Eventually Test class should be removed once preferences feature is finalised.
     */
    //@Test
    public final void testSetDefaultSphereName() {
        String temp = "DefaultSphere";
        assertTrue(sphereProperties.setDefaultSphereName(temp));
        assertTrue(sphereProperties.getDefaultSphereName().equalsIgnoreCase(temp));
        // set the defaultSphereName back to empty string
        sphereProperties.setDefaultSphereName("");
    }

}

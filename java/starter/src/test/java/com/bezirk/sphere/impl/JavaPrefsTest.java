package com.bezirk.sphere.impl;

import com.bezirk.sphere.api.BezirkDevMode;
import com.bezirk.sphere.api.SpherePrefs;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Rishabh Gulati
 */
public class JavaPrefsTest {

    private static final Logger logger = LoggerFactory.getLogger(JavaPrefsTest.class);
    private JavaPrefs javaPrefs;

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
        javaPrefs = new JavaPrefs();
        javaPrefs.init();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        //revert to default mode value
        boolean modeReset = (SpherePrefs.DEVELOPMENT_SPHERE_MODE_DEFAULT_VALUE) ? javaPrefs.setMode(BezirkDevMode.Mode.ON) : javaPrefs.setMode(BezirkDevMode.Mode.OFF);

        //revert to default sphere name value
        boolean defNameReset = javaPrefs.setDefaultSphereName(SpherePrefs.DEFAULT_SPHERE_NAME_DEFAULT_VALUE);

        if (modeReset && defNameReset) {
            logger.debug("tear down successful");
        }
    }

    @Test
    public final void testSetGetDevMode() {
        boolean setMode = javaPrefs.setMode(BezirkDevMode.Mode.ON);
        logger.debug("Set " + SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY + " --> " + setMode);

        BezirkDevMode.Mode getMode = javaPrefs.getMode();
        logger.debug("Get " + SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY + " --> " + getMode);

        assertTrue(setMode);
        assertEquals(BezirkDevMode.Mode.ON, getMode);

        try {
            logger.debug("Get " + SpherePrefs.DEVELOPMENT_SPHEREKEY_KEY + " --> " + new String(javaPrefs.getSphereKey(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {

        }
    }

    @Test
    public final void testSetGetDefaultSphereName() {
        logger.debug("Set " + SpherePrefs.DEFAULT_SPHERE_NAME_KEY + " --> " + javaPrefs.setDefaultSphereName("Test name"));
        logger.debug("Get " + SpherePrefs.DEVELOPMENT_SPHERE_NAME_KEY + " --> " + javaPrefs.getSphereName());
    }

    @Test
    public final void testGetDevSphereId() {
        String devSphereId = javaPrefs.getSphereId();
        logger.debug("Get " + SpherePrefs.DEVELOPMENT_SPHERE_ID_KEY + " --> " + devSphereId);
        assertEquals(SpherePrefs.DEVELOPMENT_SPHERE_ID_DEFAULT_VALUE, devSphereId);
    }

}



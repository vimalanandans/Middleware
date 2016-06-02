package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.proxy.api.impl.ZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author vbd4kor
 *         This class tests the Bezirk for Registration and Unregistration Scenario.
 *         Steps: 1. registers the zirk with BezirkFactory
 *         2. Check the Sid Map if it contains the ZirkId
 *         3. register the same zirk Again and check if the same zirk is generated.
 *         4. unregister the zirk
 *         5. Register the zirk again and check for different ZirkId that indicates the validaity of the test case
 */
public class RegistrationUnregistrationTest {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationUnregistrationTest.class);

    @BeforeClass
    public static void setup() {
        logger.info(" ************** Setting up RegistrationUnregistrationTest Testcase ****************************");
    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** TearingDown RegistrationUnregistrationTest Testcase ****************************");
    }

    //@Test
    public void registrationUnregistrationTest() {
        final String serviceName = "MOCK_SERVICE_A";
        Bezirk bezirk = null;

        bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
        assertTrue("Failed to register Zirk", bezirk.registerZirk(serviceName));
        assertFalse("Was able to register Zirk name twice.", bezirk.registerZirk(serviceName));

        // unRegister
        bezirk.unregisterZirk();

    }

}

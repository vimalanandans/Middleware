package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author vbd4kor
 *         This class tests the Bezirk for Registration and Unregistration Scenario.
 *         Steps: 1. registers the zirk with UhuFactory
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
        BezirkZirkId zirkId = null;

        bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
        zirkId = (BezirkZirkId) bezirk.registerZirk(serviceName);
        String bezirkZirkId = zirkId.getBezirkZirkId();
        assertNotNull("ServiceID is null after registration.", bezirkZirkId);

        // Test

        // RE-Register the zirk and check if the same id is getting generated
        String duplicateBezirkZirkId = ((BezirkZirkId) bezirk.registerZirk(serviceName)).getBezirkZirkId();
        assertEquals("Different zirkID generated upon duplicate registration for same zirk.", bezirkZirkId, duplicateBezirkZirkId);

        // unRegister
        bezirk.unregisterZirk(zirkId);

    }

}

package com.bezirk.middleware.core.sphere.control.Objects;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the ServiceVitals POJO by retrieving the field values using getters.
 */
public class ServiceVitalsTest {
    private static final Logger logger = LoggerFactory.getLogger(ServiceVitalsTest.class);

    private static final String ownerDeviceID = "TESTDEVICe";
    private static final String serviceName = "ServiceA";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up ServiceVitalsTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down ServiceVitalsTest TestCase *****");
    }


    @Test
    public void testServiceVitals() {

        com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals serviceVitals = prepareServiceVitals();

        assertEquals("OwnerDeviceID not equal to the set value.", ownerDeviceID, serviceVitals.getOwnerDeviceID());
        assertEquals("ServiceName not equal to the set value.", serviceName, serviceVitals.getServiceName());

    }

    private com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals prepareServiceVitals() {
        com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals serviceVitals = new com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals();
        serviceVitals.setOwnerDeviceID(ownerDeviceID);
        serviceVitals.setServiceName(serviceName);
        return serviceVitals;
    }
}

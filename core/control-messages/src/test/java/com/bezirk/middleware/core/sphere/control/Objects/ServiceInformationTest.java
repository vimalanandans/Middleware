package com.bezirk.middleware.core.sphere.control.Objects;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the ServiceInformation POJO by retrieving the field values using getters.
 *
 * @author AJC6KOR
 */
public class ServiceInformationTest {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInformationTest.class);

    private static final String sphereId = "TestSphere";
    private static final com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals serviceVitals = new com.bezirk.middleware.core.sphere.control.Objects.ServiceVitals();
    private static final HashSet<String> sphereSet = new HashSet<>();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up ServiceInformationTest TestCase *****");
        sphereSet.add(sphereId);

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down ServiceInformationTest TestCase *****");
    }


    @Test
    public void testServiceInformation() {

        com.bezirk.middleware.core.sphere.control.Objects.ServiceInformation serviceInformation = prepareServiceInformation();
        assertEquals("SphereVitals not equal to the set value.", serviceVitals, serviceInformation.getServiceVitals());
        assertEquals("SphereSet not equal to the set value.", sphereSet, serviceInformation.getSphereSet());

    }

    private com.bezirk.middleware.core.sphere.control.Objects.ServiceInformation prepareServiceInformation() {
        com.bezirk.middleware.core.sphere.control.Objects.ServiceInformation serviceInformation = new ServiceInformation();
        serviceInformation.setServiceVitals(serviceVitals);
        serviceInformation.setSphereSet(sphereSet);
        return serviceInformation;
    }
}

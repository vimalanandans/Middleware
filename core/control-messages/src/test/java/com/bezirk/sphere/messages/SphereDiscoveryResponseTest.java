package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This testCase verifies the SphereDiscoveryResponse by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class SphereDiscoveryResponseTest {
    private static final Logger logger = LoggerFactory.getLogger(SphereDiscoveryResponseTest.class);

    private static final ZirkId serviceAId = new ZirkId("ServiceA");
    private static final ZirkId serviceBId = new ZirkId("ServiceB");

    private static List<ZirkId> services = new ArrayList<>();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up SphereDiscoveryResponseTest TestCase *****");
        services.add(serviceAId);
        services.add(serviceBId);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down SphereDiscoveryResponseTest TestCase *****");
    }

    @Test
    public void testSphereDiscoveryResponse() {

        String scannedSphereId = "SCANNEDID";
        ZirkId serviceId = new ZirkId("ServiceA");
        BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
        com.bezirk.sphere.messages.SphereDiscoveryResponse sphereDiscoveryRequest = new com.bezirk.sphere.messages.SphereDiscoveryResponse(scannedSphereId, services, sender);
        String serializedMessage = sphereDiscoveryRequest.serialize();
        com.bezirk.sphere.messages.SphereDiscoveryResponse deserializedSphereDiscoveryResponse = com.bezirk.sphere.messages.SphereDiscoveryResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.SphereDiscoveryResponse.class);
        assertEquals("Deserialized Response services are not equal to original sphere discovery response services", services, deserializedSphereDiscoveryResponse.getServices());

    }

}

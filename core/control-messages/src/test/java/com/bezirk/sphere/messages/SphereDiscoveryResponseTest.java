package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

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

    private static final Logger log = LoggerFactory
            .getLogger(SphereDiscoveryResponseTest.class);


    private static final UhuZirkId serviceAId = new UhuZirkId("ServiceA");
    private static final UhuZirkId serviceBId = new UhuZirkId("ServiceB");

    private static List<UhuZirkId> services = new ArrayList<>();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up SphereDiscoveryResponseTest TestCase *****");
        services.add(serviceAId);
        services.add(serviceBId);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down SphereDiscoveryResponseTest TestCase *****");
    }

    @Test
    public void testSphereDiscoveryResponse() {

        String scannedSphereId = "SCANNEDID";
        UhuZirkId serviceId = new UhuZirkId("ServiceA");
        UhuZirkEndPoint sender = new UhuZirkEndPoint(serviceId);
        com.bezirk.sphere.messages.SphereDiscoveryResponse sphereDiscoveryRequest = new com.bezirk.sphere.messages.SphereDiscoveryResponse(scannedSphereId, services, sender);
        String serializedMessage = sphereDiscoveryRequest.serialize();
        com.bezirk.sphere.messages.SphereDiscoveryResponse deserializedSphereDiscoveryResponse = com.bezirk.sphere.messages.SphereDiscoveryResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.SphereDiscoveryResponse.class);
        assertEquals("Deserialized Response services are not equal to original sphere discovery response services", services, deserializedSphereDiscoveryResponse.getServices());

    }

}

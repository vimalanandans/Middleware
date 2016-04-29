package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the SphereDiscoveryRequest by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class SphereDiscoveryRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(SphereDiscoveryRequestTest.class);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up SphereDiscoveryRequestTest TestCase *****");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down SphereDiscoveryRequestTest TestCase *****");
    }

    @Test
    public void testSphereDiscoveryRequest() {

        String scanSphereId = "SCANNEDID";
        BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
        BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
        com.bezirk.sphere.messages.SphereDiscoveryRequest sphereDiscoveryRequest = new com.bezirk.sphere.messages.SphereDiscoveryRequest(scanSphereId, sender);
        String serializedMessage = sphereDiscoveryRequest.serialize();
        com.bezirk.sphere.messages.SphereDiscoveryRequest deserializedSphereDiscoveryRequest = com.bezirk.sphere.messages.SphereDiscoveryRequest.deserialize(serializedMessage, com.bezirk.sphere.messages.SphereDiscoveryRequest.class);
        assertEquals("Deserialized Request MessageId not equal to original sphere discovery request", sphereDiscoveryRequest.getMessageId(), deserializedSphereDiscoveryRequest.getMessageId());

    }

}

package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testCase verifies the OwnerLeaveResponse by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class OwnerLeaveResponseTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OwnerLeaveResponseTest.class);

    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkId serviceBId = new BezirkZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceBId);


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        LOGGER.info("***** Setting up OwnerLeaveResponseTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        LOGGER.info("***** Shutting down OwnerLeaveResponseTest TestCase *****");
    }


    @Test
    public void testOwnerLeaveResponse() {

        com.bezirk.sphere.messages.OwnerLeaveResponse ownerLeaveResponse = new com.bezirk.sphere.messages.OwnerLeaveResponse(sphereId, serviceId, recipient, true);
        String serializedMessage = ownerLeaveResponse.serialize();
        com.bezirk.sphere.messages.OwnerLeaveResponse deserializedOwnerLeaveResponse = com.bezirk.sphere.messages.OwnerLeaveResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.OwnerLeaveResponse.class);
        assertEquals("SphereID not equal to the set value.", sphereId, deserializedOwnerLeaveResponse.getSphereID());
        assertEquals("ServiceID not equal to the set value.", serviceId, deserializedOwnerLeaveResponse.getServiceId());
        assertTrue("IsRemovedSuccessfully not equal to the set value.", deserializedOwnerLeaveResponse.isRemovedSuccessfully());
        assertEquals("Time not equal after deserialization.", ownerLeaveResponse.getTime(), deserializedOwnerLeaveResponse.getTime());

    }


}

package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the MemberLeaveRequest by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class MemberLeaveRequestTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MemberLeaveRequestTest.class);

    private static final String sphereId = "TestSphere";
    private static final String sphereName = "Test";
    private static final UhuZirkId serviceId = new UhuZirkId("ServiceA");
    private static final UhuZirkEndPoint sender = new UhuZirkEndPoint(serviceId);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        LOGGER.info("***** Setting up MemberLeaveRequestTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        LOGGER.info("***** Shutting down MemberLeaveRequestTest TestCase *****");
    }


    @Test
    public void testMemberLeaveRequest() {

        com.bezirk.sphere.messages.MemberLeaveRequest memberLeaveRequest = new com.bezirk.sphere.messages.MemberLeaveRequest(sphereId, serviceId, sphereName, sender);
        String serializedMessage = memberLeaveRequest.serialize();
        com.bezirk.sphere.messages.MemberLeaveRequest deserializedMemberLeaveRequest = com.bezirk.sphere.messages.MemberLeaveRequest.deserialize(serializedMessage, com.bezirk.sphere.messages.MemberLeaveRequest.class);
        assertEquals("ZirkId not equal to the set value.", serviceId, deserializedMemberLeaveRequest.getServiceId());


    }

}

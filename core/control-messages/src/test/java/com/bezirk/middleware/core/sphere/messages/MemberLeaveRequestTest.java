package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the MEMBER_LEAVE_REQUEST by retrieving the field values after deserialization.
 */
public class MemberLeaveRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(MemberLeaveRequestTest.class);

    private static final String sphereId = "TestSphere";
    private static final String sphereName = "Test";
    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);

    @BeforeClass
    public static void setUpBeforeClass() {
        logger.info("***** Setting up MemberLeaveRequestTest TestCase *****");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        logger.info("***** Shutting down MemberLeaveRequestTest TestCase *****");
    }


    @Test
    public void testMemberLeaveRequest() {
        com.bezirk.middleware.core.sphere.messages.MemberLeaveRequest memberLeaveRequest = new com.bezirk.middleware.core.sphere.messages.MemberLeaveRequest(sphereId, serviceId, sphereName, sender);
        String serializedMessage = memberLeaveRequest.serialize();
        com.bezirk.middleware.core.sphere.messages.MemberLeaveRequest deserializedMemberLeaveRequest = com.bezirk.middleware.core.sphere.messages.MemberLeaveRequest.deserialize(serializedMessage, com.bezirk.middleware.core.sphere.messages.MemberLeaveRequest.class);
        assertEquals("ZirkId not equal to the set value.", serviceId, deserializedMemberLeaveRequest.getServiceId());
    }

}

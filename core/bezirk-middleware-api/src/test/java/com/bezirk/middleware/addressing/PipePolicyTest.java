/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.ProtocolRole;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the PipePolicy by setting the properties and retrieving them after deserialization.
 * This alse includes tests for verifying equals check.
 *
 * @author AJC6KOR
 */
public class PipePolicyTest {
    @Test
    public void test() {
        ProtocolRole pRole = new MockProtocolRole();

        Map<String, String> reasonMap = new HashMap<>();
        String protocolName = "TestProtocol";
        String reason = "Used for testing";
        reasonMap.put(protocolName, reason);

        PipePolicy pipePolicy = new MockPipePolicy();
        pipePolicy.setReasonMap(reasonMap);
        pipePolicy.addAllowedProtocol(pRole, reason);

        String serializedPipePolicy = pipePolicy.toJson();

        PipePolicy deserializedPipePolicy = PipePolicy.fromJson(serializedPipePolicy, MockPipePolicy.class);

        assertEquals("ReasonMap is not equal to the set value.", reasonMap, deserializedPipePolicy.getReasonMap());
        assertTrue("Test Protocol name is missing in pipe policy protocol names.", deserializedPipePolicy.getProtocolNames().contains(protocolName));
        assertEquals("Unable to retrieve reason for test protocol from pipe policy.", reason, deserializedPipePolicy.getReason(protocolName));
        assertFalse("Test Protocol is considered as authorized.", deserializedPipePolicy.isAuthorized(protocolName));

        assertEquals("ReasonMap is not equal to the set value.", reasonMap, deserializedPipePolicy.getReasonMap());
        protocolName = "InvalidProtocol";
        assertFalse("Unkown Protocol name is present in pipe policy protocol names.", deserializedPipePolicy.getProtocolNames().contains(protocolName));
        reason = "Testing negative reason";
        assertNotEquals("Reason for test protocol from pipe policy matches with unkown reason.", reason, deserializedPipePolicy.getReason(protocolName));


        PipePolicy tempPipePolicy = new MockPipePolicy();
        assertFalse("PipePolicies with different reason maps are considered equal.", tempPipePolicy.equals(pipePolicy));
        tempPipePolicy.setReasonMap(reasonMap);
        assertTrue("PipePolicies with same reason maps are considered unequal.", tempPipePolicy.equals(pipePolicy));

        assertFalse("PipePolicy and protocol are conidered equal", pipePolicy.equals(pRole));
    }

    private class MockPipePolicy extends PipePolicy {
        public boolean isAuthorized(String protocolRoleName) {
            return false;
        }
    }
}

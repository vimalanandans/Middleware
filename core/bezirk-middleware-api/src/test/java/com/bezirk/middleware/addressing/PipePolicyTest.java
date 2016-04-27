package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.ProtocolRole;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
